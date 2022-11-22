package flowdesigner.util;

import commonUtility.file.FileKit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
}
