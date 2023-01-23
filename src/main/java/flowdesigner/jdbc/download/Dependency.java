package flowdesigner.jdbc.download;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class Dependency {
    private String groupId ;
    private String artifactId;
    private String version;
    private String scope;
    private boolean optional = false;
    private List<Exclusion> exclusions = new ArrayList<>();

    /**
     * dependence中的exclusion项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static
    class Exclusion {
        private String groupId ;
        private String artifactId;
    }

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

    public void addExclusion( String groupId ,String artifactId) {
        exclusions.add(new Exclusion(groupId,artifactId));
    }
    public void addExclusion( Exclusion exclusion) {
        exclusions.add(exclusion);
    }
}
