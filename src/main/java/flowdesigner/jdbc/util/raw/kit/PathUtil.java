package flowdesigner.jdbc.util.raw.kit;

import java.util.Properties;

public final class PathUtil {

    public String getSystemHomeDir() {
        Properties props=System.getProperties(); //系统属性
        return props.getProperty("user.home") + "\\.flowDesigner";
    }
}
