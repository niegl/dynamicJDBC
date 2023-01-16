package flowdesigner.jdbc.download;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class MavenDownload {

    /**
     * 用于取消长时间任务
     */
    public static void stopDownload() {
        MavenDownload.bStopDownload = true;
    }

    private static volatile boolean bStopDownload = false;

    /**
     * 从网络下载指定驱动jar文件
     * @param url 网络地址
     * @param scope 网络地址范围，完整URL为：url/scope/groupId/artifactId/version/***.jar
     * @param repository 本地存储库
     * @param groupId 组织id，一般为驱动名称，如mysql
     * @param artifactId 具体驱动，如 mysql-connector-java
     * @param version 驱动版本
     * @return 下载成功返回 下载后文件地址，失败返回 null
     * @throws IOException
     */
    private static String download(String url, String scope,
                                  String repository, String groupId, String artifactId, String version) throws IOException {

        if (url.isEmpty() || repository.isEmpty() || groupId.isEmpty() || artifactId.isEmpty() || version.isEmpty()) {
            return null;
        }

        Dependency dependency = new Dependency(groupId, artifactId, version);
        String toPath = dependency.toLocalPath(repository).toString();
        File path = new File(toPath);
        if (!path.exists()) {
            boolean b = path.mkdirs();
        }

        String pom = downloadPom(url, scope, dependency, toPath);
        String jar = downloadJar(url, scope, dependency, toPath);

        return jar;
    }

    public static List<String> downloadRecursive(String url, String scope, String repository,
                                           String groupId, String artifactId, String version) {
        ArrayList<String> locations = new ArrayList<>();
        try {
            bStopDownload = false;
            downloadRecursive(url, scope, repository, new Dependency(groupId, artifactId, version), locations);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return locations;
    }

    private static void downloadRecursive(String url, String scope, String repository,
                                          Dependency pom, List<String> locations) throws IOException {
        if (bStopDownload) {
            return;
        }

        String jar = download(url, scope, repository, pom.getGroupId(), pom.getArtifactId(), pom.getVersion());
        if (jar == null) {
            return;
        }
        File jarFile = new File(jar);
        if (!jarFile.exists()) {
            return;
        }

        locations.add(jar);

        String absolutePath = jarFile.getAbsolutePath();
        String pomNext = absolutePath.replace(".jar",".pom");
        Collection<Dependency> dependencies = PomParser.getDependencies(pomNext, pom);
        Collection<Dependency.Exclusion> exclusions = pom.getExclusions();
        for (Dependency dependency : dependencies) {
            String groupId1 = dependency.getGroupId();
            String artifactId1 = dependency.getArtifactId();
            String version1 = dependency.getVersion();
            String scope1 = dependency.getScope();

            boolean bSkip = false;
            for (Dependency.Exclusion exclusion: exclusions) {
                if (exclusion.getGroupId().equals(groupId1) && exclusion.getArtifactId().equals(artifactId1)) {
                    bSkip = true;
                    break;
                }
            }
            if (bSkip) {
                continue;
            }

            if (scope1 != null && (scope1.equals("test")||scope1.equals("provided")||scope1.equals("system"))) {
                continue;
            }
            if (dependency.isOptional()) {
                continue;
            }

            // 如果版本为空，那么默认下载最新的版本
            if (version1 == null || version1.isEmpty()) {
                List<String> versions = getVersions(url, scope, groupId1, artifactId1);
                if (versions.isEmpty()) {
                    log.warn(groupId1 + ":" + artifactId1 + " version is empty, skip download");
                    continue;
                }
                version1 = versions.get(versions.size()-1);
                if (!version1.isEmpty()) {
                    dependency.setVersion(version1);
                }
            }

            downloadRecursive(url, scope, repository, dependency, locations);
        }

    }

    /**
     * 获取驱动的版本号列表
     * @param url 界面设置的URL列表中的一个
     * @param scope 对应的URL设置的范围
     * @param groupId 驱动的依赖，见配置列表
     * @param artifactId 驱动的依赖，见配置列表
     * @return 版本列表
     */
    public static List<String> getVersions(String url, String scope,
                                                   String groupId, String artifactId) {
        ArrayList<String> versions = new ArrayList<>();

        Dependency dependency = new Dependency(groupId, artifactId);
        StringBuilder netPath = dependency.toNetPath(url, scope);

        Document document = null;
        try {
            URL url1 = new URL(netPath.toString());
            document = Jsoup.parse(url1, 10000);
        } catch (IOException e) {
            log.error(e.getMessage());
            return versions;
        }

        String REGEX ="[^(0-9).]";
        Elements versionElements = document.getElementsByTag("a");
        for (Element element : versionElements) {
            // 包含数字和点的认为是版本号
            String text = element.text();
            String versionNumber = Pattern.compile(REGEX).matcher(text).replaceAll("").trim();
            if (versionNumber.isEmpty() ||
                    versionNumber.startsWith(".") || versionNumber.endsWith(".")) {
                continue;
            }
            // 版本存在8.7.7-snapshot或8.7.0dmr的情况,仅保留正式的版本号
            if (text.startsWith(versionNumber + "/")) {
                versions.add(versionNumber);
            }
        }

        return versions;
    }

    private static String downloadPom(String url, String scope, @NotNull Dependency dependency,
                                      String repository) throws IOException {

        StringBuilder netPath = dependency.toNetPath(url, scope);
        netPath.append(dependency.toFileName()).append(".pom");

        return NetDownload.download(netPath.toString(), repository);
    }

    private static String downloadJar(String url, String scope, Dependency dependency,
                                      String repository ) throws IOException {

        StringBuilder netPath = dependency.toNetPath(url, scope);
        netPath.append(dependency.toFileName()).append(".jar");

        return NetDownload.download(netPath.toString(), repository);
    }

}
