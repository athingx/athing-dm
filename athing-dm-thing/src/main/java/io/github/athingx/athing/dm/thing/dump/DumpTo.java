package io.github.athingx.athing.dm.thing.dump;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 设备模型导出到目标处理函数
 */
@FunctionalInterface
public interface DumpTo<T> {

    /**
     * 导出设备模型
     *
     * @param map 设备模型内容
     * @return 导出结果
     * @throws Exception 导出失败
     */
    T dump(Map<String, String> map) throws Exception;

    /**
     * 导出到zip压缩文件
     *
     * @param target 目标文件
     * @return 导出结果
     */
    static DumpTo<File> toZip(File target) {
        return map -> {
            try (final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(target))) {
                for (final Map.Entry<String, String> entry : map.entrySet()) {
                    zos.putNextEntry(new ZipEntry("/" + entry.getKey() + ".json"));
                    zos.write(entry.getValue().getBytes(UTF_8));
                    zos.closeEntry();
                }
                zos.finish();
            }
            return target;
        };
    }

    /**
     * 导出到Map集合
     *
     * @return 导出结果
     */
    static DumpTo<Map<String, String>> toMap() {
        return map -> map;
    }

}
