package io.github.athingx.athing.dm.thing.define;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;
import io.github.athingx.athing.dm.common.meta.ThDmEventMeta;
import io.github.athingx.athing.dm.common.meta.ThDmPropertyMeta;
import io.github.athingx.athing.dm.common.meta.ThDmServiceMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public interface Conflict extends BiFunction<ThDmCompMeta, ThDmCompMeta, ThDmCompMeta> {

    @Override
    ThDmCompMeta apply(ThDmCompMeta exist, ThDmCompMeta create);

    Conflict COVERED = (exist, create) -> exist;

    Conflict CREATED = (exist, create) -> {
        throw new IllegalArgumentException("duplicate defining component: %s, conflict: [ %s ]".formatted(
                exist.getId(),
                exist.getType().getName()
        ));
    };

    Conflict UPDATED = (exist, create) -> {

        // 合并事件
        final Map<Identifier, ThDmEventMeta> identityThDmEventMetaMap = new HashMap<>() {{
            putAll(exist.getIdentityThDmEventMetaMap());
            putAll(create.getIdentityThDmEventMetaMap());
        }};

        // 合并属性
        final Map<Identifier, ThDmPropertyMeta> identityThDmPropertyMetaMap = new HashMap<>() {{
            putAll(exist.getIdentityThDmPropertyMetaMap());
            putAll(create.getIdentityThDmPropertyMetaMap());
        }};

        // 合并服务
        final Map<Identifier, ThDmServiceMeta> identityThDmServiceMetaMap = new HashMap<>() {{
            putAll(exist.getIdentityThDmServiceMetaMap());
            putAll(create.getIdentityThDmServiceMetaMap());
        }};

        // 清空
        create.getIdentityThDmEventMetaMap().clear();
        create.getIdentityThDmPropertyMetaMap().clear();
        create.getIdentityThDmServiceMetaMap().clear();

        // 替换
        create.getIdentityThDmEventMetaMap().putAll(identityThDmEventMetaMap);
        create.getIdentityThDmPropertyMetaMap().putAll(identityThDmPropertyMetaMap);
        create.getIdentityThDmServiceMetaMap().putAll(identityThDmServiceMetaMap);

        return create;
    };

}
