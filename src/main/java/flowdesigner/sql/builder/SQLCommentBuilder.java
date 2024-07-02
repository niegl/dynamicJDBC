package flowdesigner.sql.builder;

import com.alibaba.druid.sql.ast.statement.SQLCommentStatement;

public interface SQLCommentBuilder {
    SQLCommentBuilder setComment(String comment);

    default void setType(SQLCommentStatement.Type type) {

    }

    default void setType(String onType) {

    }

    void setOn(String on);
}
