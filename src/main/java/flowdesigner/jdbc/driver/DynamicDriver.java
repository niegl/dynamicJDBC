package flowdesigner.jdbc.driver;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import javax.sql.DataSource;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Properties;

/**
 * jdk17 需要设置--add-opens java.base/jdk.internal.loader=ALL-UNNAMED
 * DynamicDriver代表一个具体的驱动。
 * 前面的driver和配置类耦合性太强，不容易剥离。目前需要简化driver功能，将其设计为一个随着文件变化的驱动，也就是接收文件接口用来生成驱动。
 * 但是由于驱动需要动态参数，这个功能由参数设计接口setProperties来实现。
 * 另外，通用URL的提供也由setUrl来提供。
 * 通过以上方式将driver功能简单化。如果后续提供不同版本的参数和URL等管理功能，有其他类实现。
 */

public class DynamicDriver {
    //定义连接池对象
    private DataSource m_ds;
    /**
     * jar包驱动路径,用;分隔多个路径
     */
    @Setter
    private String m_driverDir;
    /**
     * jdbc连接时使用的动态属性配置,至少应该包括 DriverClassName，一般包括用户名和密码
     */
    @Setter
    private Properties m_propertyInfo;

    public DynamicDriver() {
    }

    public DynamicDriver(String driverDir) {
        Objects.requireNonNull(driverDir);
        m_driverDir = driverDir;
    }

    public void createDataSource() {
        try {
            // 文件后缀为.java且不为空，读子文件夹
            Collection<File> files = new ArrayList<>();
            for (String dir :
                    m_driverDir.split(";")) {
                Collection<File> listFiles = FileUtils.listFiles(new File(dir),
                        FileFilterUtils.and(new SuffixFileFilter("jar"), EmptyFileFilter.NOT_EMPTY),
                        DirectoryFileFilter.INSTANCE);
                if (!listFiles.isEmpty()) {
                    files.addAll(listFiles);
                }
            }

            if (loadJar(files)) {
                //通过prop创建连接池对象
                m_ds = DruidDataSourceFactory.createDataSource(m_propertyInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接
     */
    public Connection getConnection() throws SQLException {
        return m_ds.getConnection();
    }

    /**
     * 释放资源
     */
    public static void close(Statement statement, Connection connection) {
        close(null, statement, connection);
    }

    /**
     * 释放资源
     */
    public static void close(ResultSet resultSet, Statement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取连接池对象
     */
    public DataSource getDataSource() {
        return m_ds;
    }

    /**
     * 动态加载Jar
     *
     * @param jarPath jar包文件路径列表
     */
    private static boolean loadJar(Collection<File> jarPath) {

        //文件存在
        if (jarPath.isEmpty()) {
            System.out.println("jar file is empty.");
            return false;
        }
        //从URLClassLoader类加载器中获取类的addURL方法
        Method method = null;
        boolean accessible = false;
        try {
            ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            if (systemClassLoader instanceof URLClassLoader) {
                URLClassLoader classLoader = (URLClassLoader)systemClassLoader;

                method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                // 获取方法的访问权限
                accessible = method.isAccessible();
                //修改访问权限为可写
                if (!accessible) {
                    method.setAccessible(true);
                }
                //获取jar文件的url路径
                for (File jarFile :
                        jarPath) {
                    URL url = jarFile.toURI().toURL();
                    //jar路径加入到系统url路径里
                    method.invoke(classLoader, url);
                }
            } else {
                Class<?> superclass = systemClassLoader.getClass().getSuperclass();
                Field field = superclass.getDeclaredField("ucp");
                field.setAccessible(true);
                Object ucp = field.get(systemClassLoader);
                if (ucp != null) {
                    method = ucp.getClass().getDeclaredMethod("addURL", URL.class);
                    // 获取方法的访问权限
                    accessible = method.isAccessible();
                    //修改访问权限为可写
                    if (!accessible) {
                        method.setAccessible(true);
                    }
                    //获取jar文件的url路径
                    for (File jarFile :
                            jarPath) {
                        URL url = jarFile.toURI().toURL();
                        //jar路径加入到系统url路径里
                        method.invoke(ucp, url);
                    }
                }
            }
        } catch (NoSuchMethodException | SecurityException | MalformedURLException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e1) {
            e1.printStackTrace();
            return false;
        } finally {
            method.setAccessible(accessible);
        }

        return true;
    }

}
