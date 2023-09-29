package io.github.athingx.athing.dm.thing;

import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;
import io.github.athingx.athing.dm.thing.impl.ThingDmImpl;
import io.github.athingx.athing.dm.thing.impl.binding.OpBindingDmPropertyPoster;
import io.github.athingx.athing.dm.thing.impl.binding.OpBindingDmPropertySetter;
import io.github.athingx.athing.dm.thing.impl.binding.OpBindingDmServiceInvoker;
import io.github.athingx.athing.dm.thing.impl.binding.OpBindingForDmEventPoster;
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
        final var ePosterF = new OpBindingForDmEventPoster().bind(thing);
        final var pPosterF = new OpBindingDmPropertyPoster().bind(thing);
        final var pSetterF = new OpBindingDmPropertySetter(container).bind(thing);
        final var sInvokerF = new OpBindingDmServiceInvoker(container).bind(thing);

        return CompletableFuture.allOf(ePosterF, pPosterF, pSetterF, sInvokerF)
                .thenApply(unused -> new ThingDmImpl(
                        thing,
                        option,
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
