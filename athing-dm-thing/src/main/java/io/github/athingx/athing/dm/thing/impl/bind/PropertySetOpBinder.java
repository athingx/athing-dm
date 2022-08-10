package io.github.athingx.athing.dm.thing.impl.bind;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.athingx.athing.common.gson.GsonFactory;
import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.common.FeatureKeys;
import io.github.athingx.athing.dm.common.meta.ThDmPropertyMeta;
import io.github.athingx.athing.dm.common.util.FeatureCodec;
import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpBind;
import io.github.athingx.athing.thing.api.op.OpGroupBinder;
import io.github.athingx.athing.thing.api.op.OpGroupBinding;
import io.github.athingx.athing.thing.api.op.OpReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.function.CompletableFutureFn.whenCompleted;
import static io.github.athingx.athing.thing.api.function.ThingFn.mappingByteToJson;
import static java.nio.charset.StandardCharsets.UTF_8;

public class PropertySetOpBinder implements OpGroupBinder<OpBind> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Thing thing;
    private final ThingDmCompContainer container;

    public PropertySetOpBinder(Thing thing, ThingDmCompContainer container) {
        this.thing = thing;
        this.container = container;
    }

    @Override
    public CompletableFuture<OpBind> bindFor(OpGroupBinding group) {
        return group
                .binding("/sys/%s/thing/service/property/set".formatted(thing.path().toURN()))
                .map(mappingByteToJson(UTF_8))
                .bind((topic, json) -> {

                    final JsonObject requestJsonObject = JsonParser.parseString(json).getAsJsonObject();
                    final String token = requestJsonObject.get("id").getAsString();
                    final Set<String> successIds = batchSetProperties(token, requestJsonObject.getAsJsonObject("params"));
                    final String message = FeatureCodec.codec.toString(new HashMap<>() {{
                        put(FeatureKeys.KEY_SET_PROPERTIES_SUCCESS_IDS, String.join(",", successIds));
                    }});

                    thing.op().data(topic + "_reply", OpReply.success(token, message))
                            .whenComplete(whenCompleted(
                                    v -> logger.debug("{}/dm/property/set success; token={};identities={};", thing.path(), token, successIds),
                                    ex -> logger.warn("{}/dm/property/set failure; token={};identities={};", thing.path(), token, successIds, ex)
                            ));

                });
    }

    private Set<String> batchSetProperties(String token, JsonObject paramsJsonObject) {

        final Set<String> successIds = new LinkedHashSet<>();
        if (null == paramsJsonObject) {
            return successIds;
        }

        // 批量设置属性
        paramsJsonObject.entrySet().forEach(entry -> {

            final String identity = entry.getKey();
            if (!Identifier.test(identity)) {
                logger.warn("{}/dm/property/set ignored; illegal identity! token={};identity={};", thing.path(), token, identity);
                return;
            }

            final Identifier identifier = Identifier.parseIdentity(identity);

            // 过滤掉未提供的组件
            final ThingDmCompContainer.Stub stub = container.get(identifier.getComponentId());
            if (null == stub) {
                logger.warn("{}/dm/property/set ignored; comp not provided! token={};identity={};", thing.path(), token, identity);
                return;
            }

            // 过滤掉未提供的属性
            final ThDmPropertyMeta meta = stub.meta().getIdentityThDmPropertyMetaMap().get(identifier);
            if (null == meta) {
                logger.warn("{}/dm/property/set ignored; property not provided! token={};identity={};", thing.path(), token, identity);
                return;
            }

            // 过滤掉只读属性
            if (meta.isReadonly()) {
                logger.warn("{}/dm/property/set ignored; property is readonly! token={};identity={};", thing.path(), token, identity);
                return;
            }

            // 属性赋值
            try {
                meta.setPropertyValue(
                        stub.comp(),
                        GsonFactory.getGson().fromJson(entry.getValue(), meta.getPropertyType())
                );
                successIds.add(identity);
                logger.debug("{}/dm/property/set success; token={};identity={};", thing.path(), token, identity);
            } catch (Throwable cause) {
                logger.warn("{}/dm/property/set ignored; set occur error! token={};identity={};", thing.path(), token, identity, cause);
            }

        });

        return successIds;

    }

}
