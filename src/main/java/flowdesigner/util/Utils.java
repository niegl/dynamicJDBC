package flowdesigner.util;

import com.alibaba.druid.util.JdbcUtils;
import flowdesigner.util.raw.kit.FileKit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 工程级别的操作对象和方法.
 */
public class Utils {
    /**
     * 获取驱动类名。首先从文件中获取，如果获取不到，再从配置中获取
     * @param files jar文件列表
     * @param rawUrl 驱动模版URL
     * @return 驱动类名列表
     */
    public static ArrayList<String> searchDriverClassName(List<String> files, String rawUrl) {
        ArrayList<String> strings = new ArrayList<>();

        // 文件中获取驱动名称
        if (files != null) {
            for (String file : files) {
                Collection<File> jarFiles = FileUtils.listFiles(new File(file),
                        FileFilterUtils.and(new SuffixFileFilter("jar"), EmptyFileFilter.NOT_EMPTY),
                        DirectoryFileFilter.INSTANCE);
                jarFiles.forEach(jarFile -> {
                    String className = FileKit.readFileFromJar(jarFile, "java.sql.Driver");
                    if (!className.isEmpty()) {
                        strings.add(className);
                    }
                });
            }
        }
        // 从配置中获取驱动名称
        if (strings.isEmpty()) {
            try {
                JdbcUtils.getDriverClassName(rawUrl);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return strings;
    }

    /**
     * 文件内容行加载（保留行顺序）
     * @param path 文件路径
     * @param list 加载后的列表
     */
    public static void loadFromFile(String path, Collection<String> list) {
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
                if (!line.isEmpty()) {
                    list.add(line);
                }
            }
        } catch (Exception ignored) {
        } finally {
            JdbcUtils.close(is);
            JdbcUtils.close(reader);
        }

    }


    /**
     * 方法 2：使用 BufferedWriter 写文件
     * @param filepath 文件目录
     * @param content  待写入内容
     * @throws IOException
     */
    public static void bufferedWriterMethod(String filepath, String content) {

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath))) {
            bufferedWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
