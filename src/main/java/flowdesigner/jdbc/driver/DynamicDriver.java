package flowdesigner.jdbc.driver;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.GetConnectionTimeoutException;
import com.alibaba.druid.util.JdbcUtils;
import flowdesigner.jdbc.JdbcConstantKey;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * jdk17 需要设置--add-opens java.base/jdk.internal.loader=ALL-UNNAMED <p>
 * DynamicDriver代表一个具体的驱动。
 * 前面的driver和配置类耦合性太强，不容易剥离。目前需要简化driver功能，将其设计为一个随着文件变化的驱动，也就是接收文件接口用来生成驱动。
 * 但是由于驱动需要动态参数，这个功能由参数设计接口setProperties来实现。
 * 另外，通用URL的提供也由setUrl来提供。
 * 通过以上方式将driver功能简单化。如果后续提供不同版本的参数和URL等管理功能，有其他类实现。
 */

@Slf4j
public class DynamicDriver {
    /**
     * 定义连接池对象
     */
    private DruidDataSource _ds = null;
    /**
     * jar包驱动路径,用;分隔多个路径
     */
    private List<String> _driverDir = new ArrayList<>();
    /**
     * jdbc连接时使用的动态属性配置,至少应该包括 DriverClassName，一般包括用户名和密码
     */
    @Setter
    private Properties _propertyInfo;
    @Getter
    @Setter
    private String _errMessage = "success";
    private static final HashMap<Connection,String> _urls = new HashMap<>();

    public DynamicDriver() {
    }
    public DynamicDriver(@NotNull String driverDir) {
        this(Arrays.asList(driverDir.split(",")));
    }
    public DynamicDriver(@NotNull List<String> driverDir) {
        set_driverDir(driverDir);
    }

    public void set_driverDir(String driverDir) {
        set_driverDir(Arrays.asList(driverDir.split(",")));
    }
    public void set_driverDir(List<String> driverDir) {
        this._driverDir = driverDir;
    }


    public void createDataSource() {
        try {
            // 文件后缀为.jar且不为空，读子文件夹
            Collection<File> files = new ArrayList<>();
            for (String dir : _driverDir) {
                Collection<File> listFiles = FileUtils.listFiles(new File(dir),
                        FileFilterUtils.and(new SuffixFileFilter("jar"), EmptyFileFilter.NOT_EMPTY),
                        DirectoryFileFilter.INSTANCE);
                if (!listFiles.isEmpty()) {
                    files.addAll(listFiles);
                }
            }

            if (files.isEmpty()) {
                return;
            }
            if (loadJar(files)) {
                //通过prop创建连接池对象
                _ds = (DruidDataSource) DruidDataSourceFactory.createDataSource(_propertyInfo);
            }

        } catch (Exception e) {
            set_errMessage(e.getCause().toString());
        }
    }

    /**
     * 获取连接
     */
    public Connection getConnection() throws SQLException {
        String errString = "java.net.ConnectException: Connection timed out: connect";

        if (_ds == null) {
            createDataSource();
        }
        if (_ds != null) {
            try {
                Connection connection = _ds.getConnection();
                // 保存到map
                String o = (String)_propertyInfo.get(JdbcConstantKey.CONSTANT_url);
                if (o != null) {
                    _urls.put(connection,  o);
                }
                return connection;
            } catch (SQLException e) {
                Throwable cause = e.getCause();
                if (cause != null) {
                    errString = cause.getMessage();
                }
                set_errMessage(errString);
            }
        }
        return null;
    }

    /**
     * 释放资源
     */
    public void close() {
        if (!_ds.isClosed()) {
            _ds.close();
        }
        _ds = null;
    }

    /**
     * 获取连接池对象
     */
    public DruidDataSource getDataSource() {
        return _ds;
    }

    /**
     * 断开连接
     */
    public static void close(Connection connection) {
        if (connection != null) {
            _urls.remove(connection);
        }
        JdbcUtils.close(connection);
    }

    /**
     * 动态加载Jar.支持JDK8和JDK11+
     *
     * @param jarPath jar包文件路径列表
     */
    private static boolean loadJar(@NotNull Collection<File> jarPath) {

        //文件存在
        if (jarPath.isEmpty()) {
            log.info("jar file is empty.");
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
        } catch (NoSuchMethodException | SecurityException | MalformedURLException | InvocationTargetException | IllegalAccessException | NoSuchFieldException |
                 InaccessibleObjectException e1) {
            log.error(e1.getMessage());
            return false;
        } finally {
            if (method != null) {
                method.setAccessible(accessible);
            }
        }

        return true;
    }

    /**
     * 获取与Connection对应的Url
     * @param connection
     * @return Url
     */
    public static String getUrl(Connection connection) {
        if (null == connection) {
            return null;
        }
        return _urls.getOrDefault(connection, null);
    }

}
