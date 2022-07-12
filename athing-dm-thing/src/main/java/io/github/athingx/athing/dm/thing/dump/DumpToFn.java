package io.github.athingx.athing.dm.thing.dump;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 导出处理函数
 */
@FunctionalInterface
public interface DumpToFn {

    /**
     * 处理设备模型
     *
     * @param map 设备模型内容
     *            <p>KEY: 组件ID</p>
     *            <p>VAL: 组件定义内容，JSON格式</p>
     * @throws Exception 处理失败
     */
    void accept(Map<String, String> map) throws Exception;

    /**
     * 导出到：{@code Map<String,String>}
     */
    class ToMap implements DumpToFn {

        private final DumpToFn fn;

        public ToMap(DumpToFn fn) {
            this.fn = fn;
        }

        @Override
        public void accept(Map<String, String> map) throws Exception {
            fn.accept(map);
        }

    }

    /**
     * 导出到Zip文件，该文件符合阿里云物模型文件导入规范
     */
    class ToZip extends ToMap {

        public ToZip(File file) {
            this(() -> file);
        }

        public ToZip(Supplier<File> supplier) {
            super(map -> {
                try (final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(supplier.get()))) {
                    for (final Map.Entry<String, String> entry : map.entrySet()) {
                        zos.putNextEntry(new ZipEntry("/" + entry.getKey() + ".json"));
                        zos.write(entry.getValue().getBytes(UTF_8));
                        zos.closeEntry();
                    }
                    zos.finish();
                }
            });
        }

    }

}
