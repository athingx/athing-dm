package io.github.athingx.athing.dm.platform;

import io.github.athingx.athing.dm.api.Identifier;
import io.github.athingx.athing.dm.api.ThingDmComp;
import io.github.athingx.athing.dm.platform.domain.SortOrder;
import io.github.athingx.athing.dm.platform.domain.ThingDmPropertySnapshot;
import io.github.athingx.athing.platform.api.ThingPlatformException;
import io.github.athingx.athing.platform.api.ThingTemplate;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public interface ThingDmTemplate extends ThingTemplate {

    /**
     * 获取设备组件
     *
     * @param type 组件类型
     * @param <T>  组件类型
     * @return 设备组件
     */
    <T extends ThingDmComp> T getThingDmComp(String compId, Class<T> type);

    /**
     * 批量设置设备属性
     *
     * @param propertyValueMap 属性值集合
     * @throws ThingPlatformException 操作失败
     */
    void batchSetProperties(Map<Identifier, Object> propertyValueMap) throws ThingPlatformException;

    /**
     * 批量获取属性快照
     *
     * @param identifiers 属性标识集合
     * @return 属性快照集合
     * @throws ThingPlatformException 操作失败
     */
    Map<Identifier, ThingDmPropertySnapshot> batchGetProperties(Set<Identifier> identifiers) throws ThingPlatformException;

    /**
     * 迭代查询属性快照
     *
     * @param identifier 属性标识
     * @param batch      批次数量
     *                   每次迭代器更新时从云端获取数据量，迭代器会一次拿一批数据到本地内存中进行迭代遍历
     * @param order      排序顺序
     * @return 属性快照迭代器
     * @throws ThingPlatformException 操作失败
     */
    Iterator<ThingDmPropertySnapshot> iteratorForPropertySnapshot(Identifier identifier, int batch, SortOrder order) throws ThingPlatformException;

}
