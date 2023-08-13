package io.github.athingx.athing.dm.thing.impl.binder;

import io.github.athingx.athing.dm.api.ThingDmEvent;
import io.github.athingx.athing.dm.thing.builder.ThingDmOption;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpMapData;
import io.github.athingx.athing.thing.api.op.OpReply;
import io.github.athingx.athing.thing.api.op.ThingOpBind;
import io.github.athingx.athing.thing.api.op.ThingOpCaller;
import io.github.athingx.athing.thing.api.util.MapData;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.op.function.OpFunction.identity;
import static io.github.athingx.athing.thing.api.op.function.OpMapper.mappingBytesToJson;
import static io.github.athingx.athing.thing.api.op.function.OpMapper.mappingJsonToOpReply;
import static java.nio.charset.StandardCharsets.UTF_8;

public class OpBindingDmEventPoster implements OpBinding<ThingOpCaller<ThingDmEvent<?>, OpReply<Void>>> {

    private final ThingDmOption option;

    public OpBindingDmEventPoster(ThingDmOption option) {
        this.option = option;
    }

    @Override
    public CompletableFuture<ThingOpCaller<ThingDmEvent<?>, OpReply<Void>>> bind(Thing thing) {

        final var opOption = new ThingOpBind.Option()
                .setTimeoutMs(option.getEventTimeoutMs());

        return thing.op().bind("/sys/%s/thing/event/+/post_reply".formatted(thing.path().toURN()))
                .map(mappingBytesToJson(UTF_8))
                .map(mappingJsonToOpReply(Void.class))
                .caller(opOption, identity())

                // 封装请求入参
                .thenApply(caller -> caller.<ThingDmEvent<?>>compose((topic, event) -> {
                    final var token = thing.op().genToken();
                    final var identity = event.getIdentifier().getIdentity();
                    return new OpMapData(token, new MapData()
                            .putProperty("id", token)
                            .putProperty("version", "1.0")
                            .putProperty("method", "thing.event.%s.post".formatted(identity))
                            .putProperty("params", new MapData()
                                    .putProperty("time", new Date(event.getOccurTimestampMs()))
                                    .putProperty("value", event.getData())
                            )
                    );
                }))

                // 固定请求主题
                .thenApply(caller -> caller.route(event -> "/sys/%s/thing/event/%s/post".formatted(
                        thing.path().toURN(),
                        event.getIdentifier().getIdentity()
                )));
    }

}
