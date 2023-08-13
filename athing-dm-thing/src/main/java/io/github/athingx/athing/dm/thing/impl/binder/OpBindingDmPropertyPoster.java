package io.github.athingx.athing.dm.thing.impl.binder;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.thing.builder.ThingDmOption;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpMapData;
import io.github.athingx.athing.thing.api.op.OpReply;
import io.github.athingx.athing.thing.api.op.ThingOpBind;
import io.github.athingx.athing.thing.api.op.ThingOpCaller;
import io.github.athingx.athing.thing.api.util.MapData;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.op.function.OpFunction.identity;
import static io.github.athingx.athing.thing.api.op.function.OpMapper.mappingBytesToJson;
import static io.github.athingx.athing.thing.api.op.function.OpMapper.mappingJsonToOpReply;
import static java.nio.charset.StandardCharsets.UTF_8;

public class OpBindingDmPropertyPoster implements OpBinding<ThingOpCaller<Map<Identifier, Object>, OpReply<Void>>> {

    private final ThingDmOption option;

    public OpBindingDmPropertyPoster(ThingDmOption option) {
        this.option = option;
    }

    @Override
    public CompletableFuture<ThingOpCaller<Map<Identifier, Object>, OpReply<Void>>> bind(Thing thing) {

        final var opOption = new ThingOpBind.Option()
                .setTimeoutMs(option.getEventTimeoutMs());

        return thing.op().bind("/sys/%s/thing/event/property/post_reply".formatted(thing.path().toURN()))
                .map(mappingBytesToJson(UTF_8))
                .map(mappingJsonToOpReply(Void.class))
                .caller(opOption, identity())

                // 封装请求入参
                .thenApply(caller -> caller.<Map<Identifier, Object>>compose((topic, propertyValueMap) -> {
                    final var token = thing.op().genToken();
                    return new OpMapData(token, new MapData()
                            .putProperty("id", token)
                            .putProperty("version", "1.0")
                            .putProperty("method", "thing.event.property.post")
                            .putProperty("params", property ->
                                    propertyValueMap.forEach((propertyId, propertyValue) ->
                                            property.putProperty(propertyId.getIdentity(), item -> {
                                                item.putProperty("value", propertyValue);
                                                item.putProperty("time", new Date());
                                            }))
                            )
                    );
                }))

                // 固定请求主题
                .thenApply(caller -> caller.route(propertyValueMap -> "/sys/%s/thing/event/property/post".formatted(
                        thing.path().toURN()
                )));
    }

}
