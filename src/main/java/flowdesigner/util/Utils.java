package flowdesigner.util;

import com.alibaba.druid.util.JdbcUtils;
import flowdesigner.util.raw.kit.FileKit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 工程级别的操作对象和方法.
 */
public class Utils {
    public static ArrayList<String> searchDriverClassName(List<String> files) {
        ArrayList<String> strings = new ArrayList<>();
        if (files == null) {
            return strings;
        }

        for (String file : files) {
            Collection<File> jarFiles = FileUtils.listFiles(new File(file),
                    FileFilterUtils.and(new SuffixFileFilter("jar"), EmptyFileFilter.NOT_EMPTY),
                    DirectoryFileFilter.INSTANCE);
            jarFiles.forEach(jarFile -> {
                String className = FileKit.readFileFromJar( jarFile,"java.sql.Driver");
                if (!className.isEmpty()) {
                    strings.add(className);
                }
            });
        }

        return strings;
    }

    /**
     * 文件内容行加载（保留行顺序）
     * @param path 文件路径
     * @param list 加载后的列表
     */
    public static void loadFromFile(String path, List<String> list) {
        InputStream is = null;
        BufferedReader reader = null;

        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            if (is == null) {
                return;
            }

            reader = new BufferedReader(new InputStreamReader(is));

            while(true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                line = line.trim();
                if (line.length() != 0) {
                    list.add(line);
                }
            }
        } catch (Exception ignored) {
        } finally {
            JdbcUtils.close(is);
            JdbcUtils.close(reader);
        }

    }
}
