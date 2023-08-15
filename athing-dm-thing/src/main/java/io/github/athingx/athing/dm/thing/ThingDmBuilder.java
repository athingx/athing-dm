package io.github.athingx.athing.dm.thing;

import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;
import io.github.athingx.athing.dm.thing.impl.ThingDmImpl;
import io.github.athingx.athing.dm.thing.impl.binding.OpBindingDmEventPoster;
import io.github.athingx.athing.dm.thing.impl.binding.OpBindingDmPropertyPoster;
import io.github.athingx.athing.dm.thing.impl.binding.OpBindingDmPropertySetter;
import io.github.athingx.athing.dm.thing.impl.binding.OpBindingDmServiceInvoker;
import io.github.athingx.athing.thing.api.Thing;

import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<ThingDm> build(Thing thing) {
        final var container = new ThingDmCompContainer(thing.path());
        final var ePosterF = new OpBindingDmEventPoster(option).bind(thing);
        final var pPosterF = new OpBindingDmPropertyPoster(option).bind(thing);
        final var pSetterF = new OpBindingDmPropertySetter(container).bind(thing);
        final var sInvokerF = new OpBindingDmServiceInvoker(container).bind(thing);
        return CompletableFuture.allOf(ePosterF, pPosterF, pSetterF, sInvokerF)
                .thenApply(unused -> new ThingDmImpl(
                        thing,
                        container,
                        ePosterF.join(),
                        pPosterF.join()
                ));
    }

}
