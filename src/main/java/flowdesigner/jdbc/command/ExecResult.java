package flowdesigner.jdbc.command;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;


public class ExecResult<T> {
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILED = "FAILED";

    @Setter
    @Getter
    @JSONField(ordinal = 0, name = "status")
    private String status = SUCCESS;
    @Setter
    @Getter
    @JSONField(ordinal = 1, name = "body")
    private T body;
    @Setter
    @Getter
    @JSONField(ordinal = 2, name = "properties")
    private Map<String,Object> properties = new HashMap<>();

    public ExecResult() {
    }

    public ExecResult(String status, T body) {
        this.status = status;
        this.body = body;
    }

//    public T getBody() {
//        return body;
//    }
//
//    public void setBody(T body) {
//        this.body = body;
//    }
}
