package io.github.athingx.athing.dm.thing;

import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;
import io.github.athingx.athing.dm.thing.impl.ThingDmImpl;
import io.github.athingx.athing.dm.thing.impl.binding.ThingOpBindingForDmEventPoster;
import io.github.athingx.athing.dm.thing.impl.binding.ThingOpBindingDmPropertyPoster;
import io.github.athingx.athing.dm.thing.impl.binding.ThingOpBindingDmPropertySetter;
import io.github.athingx.athing.dm.thing.impl.binding.ThingOpBindingDmServiceInvoker;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.plugin.ThingPluginInstaller;

import java.util.concurrent.CompletableFuture;

public class ThingDmInstaller implements ThingPluginInstaller<ThingDm> {

    private ThingDmOption option = new ThingDmOption();

    public ThingDmInstaller option(ThingDmOption option) {
        this.option = option;
        return this;
    }

    @Override
    public Meta<ThingDm> meta() {
        return new Meta<>(ThingDm.THING_ID, ThingDm.class);
    }

    @Override
    public CompletableFuture<ThingDm> install(Thing thing) {

        final var container = new ThingDmCompContainer(thing.path());
        final var ePosterF = new ThingOpBindingForDmEventPoster(option).bind(thing);
        final var pPosterF = new ThingOpBindingDmPropertyPoster(option).bind(thing);
        final var pSetterF = new ThingOpBindingDmPropertySetter(container).bind(thing);
        final var sInvokerF = new ThingOpBindingDmServiceInvoker(container).bind(thing);

        return CompletableFuture.allOf(ePosterF, pPosterF, pSetterF, sInvokerF)
                .thenApply(unused -> new ThingDmImpl(
                        thing,
                        container,
                        ePosterF.join(),
                        pPosterF.join(),
                        () -> CompletableFuture.allOf(
                                ePosterF.join().unbind(),
                                pPosterF.join().unbind(),
                                pSetterF.join().unbind(),
                                sInvokerF.join().unbind()
                        )
                ));
    }
}
