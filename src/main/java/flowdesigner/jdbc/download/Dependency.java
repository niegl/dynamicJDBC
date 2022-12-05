package flowdesigner.jdbc.download;

import lombok.Data;

@Data
public class Dependency {
    private String groupId ;
    private String artifactId;
    private String version;
    private String scope;

    public Dependency() {
    }

    public Dependency(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public Dependency(String groupId, String artifactId, String version, String scope) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.scope = scope;
    }

    public String toPath() {
        return groupId.replaceAll("\\.","/") + "/" + artifactId + "/" + version + "/";
    }

    public String toPom() {
        return artifactId + "-" + version + ".pom";
    }

    public String toJar() {
        return artifactId + "-" + version + ".jar" ;
    }
}
