package flowdesigner.jdbc.download;

import lombok.Data;

@Data
public class Dependency {
    private String groupId ;
    private String artifactId;
    private String version;
    private String scope;
    private boolean optional = false;

    public Dependency() {
    }

    public Dependency(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
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

    public StringBuilder toNetPath(String url, String scopePath) {
        StringBuilder netPath = new StringBuilder(url);

        if (!url.endsWith("/")) {
            netPath.append("/");
        }
        if (!scopePath.isEmpty()) {
            netPath.append(scopePath.replaceAll("\\.", "/"));
            netPath.append("/");
        }

        netPath.append(groupId.replaceAll("\\.","/"))
                .append("/")
                .append(artifactId)
                .append("/");

        if (version != null && !version.isEmpty()) {
            netPath.append(version)
                    .append("/");
        }

        return netPath;
    }

    public StringBuilder toLocalPath(String repository) {
        StringBuilder stringBuilder = new StringBuilder(repository);

        if (!repository.endsWith("/")) {
            stringBuilder.append("/");
        }

        return stringBuilder.append(groupId)
                .append("/");
    }

    public String toFileName() {
        return artifactId + "-" + version;
    }

}
