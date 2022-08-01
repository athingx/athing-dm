package io.github.athingx.athing.dm.thing.builder;

import io.github.athingx.athing.dm.thing.ThingDm;
import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;
import io.github.athingx.athing.dm.thing.impl.ThingDmImpl;
import io.github.athingx.athing.dm.thing.impl.bind.*;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpCall;
import io.github.athingx.athing.thing.api.op.OpData;
import io.github.athingx.athing.thing.api.op.OpGroupBinding;
import io.github.athingx.athing.thing.api.op.OpReply;

import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.function.CompletableFutureFn.tryCatchComplete;

/**
 * 设备模型构造器
 */
public class ThingDmBuilder {

    private ThingDmOption option = new ThingDmOption();

    /**
     * 设备模型参数
     *
     * @param option 参数
     * @return this
     */
    public ThingDmBuilder option(ThingDmOption option) {
        this.option = option;
        return this;
    }

    /**
     * 构造设备模型
     *
     * @param thing 设备
     * @return 设备模型构造
     */
    public CompletableFuture<ThingDm> build(Thing thing) {
        final OpGroupBinding group = thing.op().binding();
        final ThingDmCompContainer container = new ThingDmCompContainer(thing.path());

        // 批量绑定
        group.bindFor(new BindForPropertySet(thing, container));
        group.bindFor(new BindForServiceAsync(thing, container));
        group.bindFor(new BindForForServiceSync(thing, container));

        // 绑定事件上报呼叫
        final CompletableFuture<OpCall<OpData, OpReply<Void>>> eCallerFuture
                = new BindForForEventCaller(thing, option)
                .bindFor(group);

        // 绑定属性上报呼叫
        final CompletableFuture<OpCall<OpData, OpReply<Void>>> pCallerFuture
                = new BindForForPropertyCaller(thing, option)
                .bindFor(group);

        // 提交绑定
        return group
                .commit()
                .thenCompose(bind -> tryCatchComplete(() -> new ThingDmImpl(
                        thing, bind, container,
                        eCallerFuture.get(),
                        pCallerFuture.get()
                )));
    }

}
