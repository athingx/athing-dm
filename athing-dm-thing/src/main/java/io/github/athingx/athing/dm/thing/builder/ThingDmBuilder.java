package io.github.athingx.athing.dm.thing.builder;

import io.github.athingx.athing.dm.thing.ThingDm;
import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;
import io.github.athingx.athing.dm.thing.impl.ThingDmImpl;
import io.github.athingx.athing.dm.thing.impl.binder.*;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpBatchBinding;
import io.github.athingx.athing.thing.api.op.OpCall;
import io.github.athingx.athing.thing.api.op.OpData;
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
        final OpBatchBinding batch = thing.op().binding();
        final ThingDmCompContainer container = new ThingDmCompContainer(thing.path());

        // 批量绑定
        new PropertySetOpBinder(thing, container).bind(batch);
        new ServiceAsyncOpBinder(thing, container).bind(batch);
        new ServiceSyncOpBinder(thing, container).bind(batch);

        // 绑定事件上报呼叫
        final CompletableFuture<OpCall<OpData, OpReply<Void>>> eCallFuture
                = new EventCallOpBinder(thing, option)
                .bind(batch);

        // 绑定属性上报呼叫
        final CompletableFuture<OpCall<OpData, OpReply<Void>>> pCallFuture
                = new PropertyCallOpBinder(thing, option)
                .bind(batch);

        // 提交绑定
        return batch
                .commit()
                .thenCompose(bind -> tryCatchComplete(() -> new ThingDmImpl(
                        thing, container,
                        eCallFuture.get(),
                        pCallFuture.get()
                )));
    }

}
