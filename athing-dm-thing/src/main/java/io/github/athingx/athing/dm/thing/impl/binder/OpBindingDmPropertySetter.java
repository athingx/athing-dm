package io.github.athingx.athing.dm.thing.impl.binder;

import com.google.gson.JsonObject;
import io.github.athingx.athing.common.gson.GsonFactory;
import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.common.FeatureKeys;
import io.github.athingx.athing.dm.common.meta.ThDmPropertyMeta;
import io.github.athingx.athing.dm.common.runtime.DmRuntime;
import io.github.athingx.athing.dm.common.util.FeatureCodec;
import io.github.athingx.athing.dm.thing.impl.ThingDmCompContainer;
import io.github.athingx.athing.thing.api.Thing;
import io.github.athingx.athing.thing.api.op.OpReply;
import io.github.athingx.athing.thing.api.op.OpRequest;
import io.github.athingx.athing.thing.api.op.ThingOpBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static io.github.athingx.athing.thing.api.op.function.OpMapper.mappingBytesToJson;
import static io.github.athingx.athing.thing.api.op.function.OpMapper.mappingJsonToOpRequest;
import static java.nio.charset.StandardCharsets.UTF_8;

public class OpBindingDmPropertySetter implements OpBinding<ThingOpBinder> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ThingDmCompContainer container;

    public OpBindingDmPropertySetter(ThingDmCompContainer container) {
        this.container = container;
    }

    @Override
    public CompletableFuture<ThingOpBinder> bind(Thing thing) {
        return thing.op().bind("/sys/%s/thing/service/property/set".formatted(thing.path().toURN()))
                .map(mappingBytesToJson(UTF_8))
                .map(mappingJsonToOpRequest(JsonObject.class))
                .consumer((topic, request) -> {
                    final var successIds = batchSetProperties(thing, request);
                    thing.op().post("/sys/%s/thing/service/property/set_reply".formatted(thing.path().toURN()), OpReply.succeed(
                            request.token(),
                            null,
                            FeatureCodec.codec.toString(new HashMap<>() {{
                                put(FeatureKeys.KEY_SET_PROPERTIES_SUCCESS_IDS, String.join(",", successIds));
                            }})
                    ));
                });
    }

    private Set<String> batchSetProperties(Thing thing, OpRequest<JsonObject> request) {

        final Set<String> successIds = new LinkedHashSet<>();
        final var token = request.token();
        final var paramsJsonObject = request.params();

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
            DmRuntime.enter();
            try {
                DmRuntime.getRuntime().setToken(token);
                meta.setPropertyValue(
                        stub.comp(),
                        GsonFactory.getGson().fromJson(entry.getValue(), meta.getPropertyType())
                );
                successIds.add(identity);
                logger.debug("{}/dm/property/set success; token={};identity={};", thing.path(), token, identity);
            } catch (Throwable cause) {
                logger.warn("{}/dm/property/set ignored; set occur error! token={};identity={};", thing.path(), token, identity, cause);
            } finally {
                DmRuntime.exit();
            }

        });

        return successIds;

    }

}
