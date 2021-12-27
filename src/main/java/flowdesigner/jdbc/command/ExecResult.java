package flowdesigner.jdbc.command;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @desc : 执行命令的返回结果对象
 */
//@JsonPropertyOrder({
//        "status",
//        "body",
//        "properties",
//})
public class ExecResult<T> implements Serializable {
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILED = "FAILED";

    private String status = SUCCESS;
    private T body;
    private Map<String,Object> properties = new HashMap<String,Object>();

    public ExecResult() {
    }

    public ExecResult(String status, T body) {
        this.status = status;
        this.body = body;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
