package flowdesigner.jdbc.download;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class MavenDownload {

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
    public static String download(String url, String scope,
                                  String repository, String groupId, String artifactId, String version) throws IOException {
        if (!url.endsWith("/")) {
            url = url +"/";
        }
        if (!repository.endsWith("/")) {
            repository = repository +"/";
        }

        Dependency dependency = new Dependency(groupId, artifactId, version);
        String toPath = repository + dependency.toPath();
        File path = new File(toPath);
        if (!path.exists()) {
            boolean b = path.mkdirs();
        }

        String pom = downloadPom(url, scope, dependency, toPath);
        String jar = downloadJar(url, scope, dependency, toPath);

        return jar;
    }

    public static String downloadRecursive(String url, String scope,
                                  String repository, String groupId, String artifactId, String version) throws IOException {

        String jar = download(url, scope, repository, groupId, artifactId, version);
        if (jar == null) {
            return null;
        }
        File jarFile = new File(jar);
        if (!jarFile.exists()) {
            return null;
        }

        String absolutePath = jarFile.getAbsolutePath();
        String pomNext = absolutePath.replace(".jar",".pom");
        Collection<Dependency> dependencies = PomParser.getDependencies(pomNext);
        for (Dependency dependency : dependencies) {
            String groupId1 = dependency.getGroupId();
            String artifactId1 = dependency.getArtifactId();
            String version1 = dependency.getVersion();
            String scope1 = dependency.getScope();

            if (scope1 != null) {
                if (scope1.equals("test")) {
                    continue;
                }
            }

            // 如果版本为空，那么默认下载最新的版本
            if (version1 == null || version1.isEmpty()) {
                List<String> versions = getVersions(url, scope, groupId, artifactId);
                if (versions.isEmpty()) {
                    log.warn(groupId + ":" + artifactId + " version is empty, skip download");
                    continue;
                }
                version1 = versions.get(versions.size()-1);
            }

            downloadRecursive(url, scope, repository, groupId1, artifactId1, version1);
        }

        return jar;
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
        StringBuilder netPath = new StringBuilder(url);

        try {
            if (!url.endsWith("/")) {
                netPath.append("/");
            }
            netPath = getUrlScope(url, scope);
            netPath.append(groupId).append("/").append(artifactId);

            URL url1 = new URL(netPath.toString());
            Document document = Jsoup.parse(url1, 10000);
            Elements postItems = document.getElementsByTag("a");
            String REGEX ="[^(0-9).]";
            for (Element postItem : postItems) {
                // 包含数字和点的认为是版本号
                String text = postItem.text();
                String versionNumber = Pattern.compile(REGEX).matcher(text).replaceAll("").trim();
                if (!versionNumber.isEmpty() && !(versionNumber.startsWith(".") || versionNumber.endsWith("."))) {
                    // 版本存在8.7.7-snapshot或8.7.0dmr的情况,仅保留正式的版本号
                    if (text.startsWith(versionNumber + "/")) {
                        versions.add(versionNumber);
                    }
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return versions;
    }

    private static String downloadPom(String url, String scope, Dependency dependency,
                                      String repository) throws IOException {

        StringBuilder netPath = getUrlScope(url, scope);
        netPath.append(dependency.toPath());
        netPath.append(dependency.toPom());

//        File file = new File(netPath.toString());
//        if (file.exists()) {
//            return netPath.toString();
//        }

        return NetDownload.download(netPath.toString(), repository);
    }

    private static String downloadJar(String url, String scope, Dependency dependency,
                                      String repository ) throws IOException {

        StringBuilder netPath = getUrlScope(url, scope);
        netPath.append(dependency.toPath());
        netPath.append(dependency.toJar());

//        File file = new File(netPath.toString());
//        if (file.exists()) {
//            return netPath.toString();
//        }

        return NetDownload.download(netPath.toString(), repository);
    }

    private static StringBuilder getUrlScope(String url, String scope) {
        StringBuilder netPath = new StringBuilder(url);
        if (!scope.isEmpty()) {
            netPath.append(scope.replaceAll("\\.", "/"));
            netPath.append("/");
        }

        return netPath;
    }

}
