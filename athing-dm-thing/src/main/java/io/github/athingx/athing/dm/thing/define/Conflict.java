package io.github.athingx.athing.dm.thing.define;

import io.github.athingx.athing.dm.common.meta.ThDmCompMeta;

import java.util.HashMap;
import java.util.function.BiFunction;

/**
 * 冲突解决
 */
public interface Conflict extends BiFunction<ThDmCompMeta, ThDmCompMeta, ThDmCompMeta> {

    /**
     * 解决冲突
     *
     * @param exist  现存
     * @param create 新建
     * @return 结果
     */
    @Override
    ThDmCompMeta apply(ThDmCompMeta exist, ThDmCompMeta create);

    /**
     * 冲突解决：覆盖
     */
    Conflict COVERED = (exist, create) -> create;

    /**
     * 冲突解决：创建
     */
    Conflict CREATED = (exist, create) -> {
        throw new IllegalArgumentException("duplicate defining component: %s, conflict: [ %s, %s ]".formatted(
                exist.getId(),
                exist.getType().getName(),
                create.getType().getName()
        ));
    };

    /**
     * 冲突解决：更新
     */
    Conflict UPDATED = (exist, create) -> new ThDmCompMeta(
            exist.getId(),
            exist.getName(),
            exist.getDesc(),
            exist.getType(),
            new HashMap<>() {{
                putAll(exist.getIdentityThDmEventMetaMap());
                putAll(create.getIdentityThDmEventMetaMap());
            }},
            new HashMap<>() {{
                putAll(exist.getIdentityThDmPropertyMetaMap());
                putAll(create.getIdentityThDmPropertyMetaMap());
            }},
            new HashMap<>() {{
                putAll(exist.getIdentityThDmServiceMetaMap());
                putAll(create.getIdentityThDmServiceMetaMap());
            }}
    );

}
