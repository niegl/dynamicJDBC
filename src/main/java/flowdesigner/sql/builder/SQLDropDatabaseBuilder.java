package flowdesigner.sql.builder;

public interface SQLDropDatabaseBuilder extends SQLBuilder{

    void dropDatabaseOrSchema(String database);
    void setIfExists(boolean setIfExists);
    void setRestrict(boolean restrict);
    void setCascade(boolean cascade);

    public void setPhysical(boolean physical);
    public boolean setServer(String server);
}
