package flowdesigner.jdbc.driver;

import lombok.Setter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * DynamicDriver代表一个具体的驱动。
 * 前面的driver和配置类耦合性太强，不容易剥离。目前需要简化driver功能，将其设计为一个随着文件变化的驱动，也就是接收文件接口用来生成驱动。
 * 但是由于驱动需要动态参数，这个功能由参数设计接口setProperties来实现。
 * 另外，通用URL的提供也由setUrl来提供。
 * 通过以上方式将driver功能简单化。如果后续提供不同版本的参数和URL等管理功能，有其他类实现。
 */

public class DynamicDriver {
    /** jar包驱动路径 */
    private List<String> m_driverDir;
    /** URL*/
    @Setter
    private String m_url;
    /** jdbc连接时使用的动态属性配置,至少应该包括 DriverClassName，一般包括用户名和密码*/
    @Setter
    private Properties m_propertyInfo;
    /** 连接建立后返回的connection，用于以后的增删改查等操作.*/
    private Connection m_connection;

    private DynamicDriver() {}
    public DynamicDriver(List<String> driverDir) {
        Objects.requireNonNull(driverDir);
        m_driverDir = driverDir;
    }

    /**
     * 主要用于连接测试时进行多次连接需要关闭掉之前的连接
     * @return 新建连接
     */
    public Connection reConnect() {
        try {
            if (m_connection != null && !m_connection.isClosed()) {
                m_connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getConnection();
    }


    /**
     *  功能：获取指定数据库连接。通过getPropertyInfo来获取当前驱动连接需要传递的参数，然后将当前参数组装后传递给connect接口进行实际连接操作。
     * @return
     */
    public Connection getConnection() {

        try {
            if (m_connection != null && !m_connection.isClosed()) {
                return  m_connection;
            }
            Driver driver = loadDriver();
            if (driver != null) {
                if (driver.acceptsURL(m_url)) {
                    DriverPropertyInfo[] propertyInfo = driver.getPropertyInfo(m_url, null);
                    Properties info = getProperties(propertyInfo);
                    return driver.connect(m_url, info);
                }
            }
        } catch (Exception e) {
            System.out.println("connection exception: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public void closeConnection(Statement stmt) {
        closeConnection(stmt,null);
    }
    /**
     * 关闭数据库连接
     * @param
     * @return
     */
    public void closeConnection(Statement stmt, Connection connection) {
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 动态获取数据库驱动
     * @return
     */
    private Driver loadDriver() throws NullPointerException, ClassNotFoundException, NoSuchMethodException, MalformedURLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Driver driver = null;

        ArrayList<URL> arrayList = new ArrayList<>();
        for (String path : m_driverDir) {
            File file = new File(path);
            if (!file.exists()) continue;

            if (file.isDirectory()) {
                File[] subFiles = file.listFiles();
                for (File subFile:
                        subFiles) {
                    if (subFile.getPath().contains(".jar")) {
                        arrayList.add(subFile.toURI().toURL());
                    }
                }
            } else if(file.isFile()) {
                if (file.getPath().contains(".jar")) {
                    arrayList.add(file.toURI().toURL());
                }
            }
        }

        int size = arrayList.size();
        if (size > 0) {
            URLClassLoader urlClassLoader = new URLClassLoader((URL[])arrayList.toArray(new URL[size]));
            Class<?> aClass = urlClassLoader.loadClass(getDriverClassName());
            driver = (Driver) aClass.getDeclaredConstructor().newInstance();
        }

        return driver;
    }

    /**
     * 获取数据库连接时必须的参数设置。
     * @param driverPropertyInfos 必须的参数列表。由 prop.required 标识.
     * @return 必须的参数
     */
    private Properties getProperties(DriverPropertyInfo[] driverPropertyInfos) {

        Properties properties = new Properties();
        for (var prop :
                driverPropertyInfos) {
            if (prop.required) {
                properties.setProperty(prop.name,m_propertyInfo.getProperty(prop.name));
            }
        }
        return properties;
    }

    private String getDriverClassName() {
        return m_propertyInfo.getProperty("DriverClassName");
    }
}
