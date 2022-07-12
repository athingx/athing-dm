package io.github.athingx.athing.dm.thing;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmEvent;
import io.github.athingx.athing.dm.thing.define.ThingDmDefine;
import io.github.athingx.athing.thing.api.op.OpReply;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface ThingDm {

    /**
     * 投递设备事件
     *
     * @param event 事件
     * @return 投递应答
     */
    CompletableFuture<OpReply<Void>> event(ThingDmEvent<?> event);

    /**
     * 投递设备属性
     *
     * @param identifiers 设备属性ID集合
     * @return 投递应答，应答内容为最终本次参与投递的设备属性ID集合
     */
    CompletableFuture<OpReply<Set<Identifier>>> properties(Identifier... identifiers);

    ThingDmDefine define(String compId, String name, String desc);

}
