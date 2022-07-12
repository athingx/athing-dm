package io.github.athingx.athing.dm.common.meta;

/**
 * 元数据
 */
class ThDmMeta {

    private final String id;
    private final String name;
    private final String desc;

    /**
     * 元数据
     *
     * @param id   ID
     * @param name 名称
     * @param desc 描述
     */
    ThDmMeta(String id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    /**
     * 获取ID
     *
     * @return ID
     */
    public String getId() {
        return id;
    }

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取描述
     *
     * @return 描述
     */
    public String getDesc() {
        return desc;
    }


}
