package flowdesigner.jdbc.download;

import flowdesigner.util.parser.XmlParser;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PomParser {
    private static final String properties = "properties";

    private static final String dependencies = "dependencies";
    private static final String dependency = "dependency";
    private static final String groupId = "groupId";
    private static final String artifactId = "artifactId";
    private static final String version = "version";
    private static final String optional = "optional";
    private static final String scope = "scope";
    private static final String exclusions = "exclusions";
    private static final String exclusion = "exclusion";
    private static final String projectVersion = "${project.version}";

    public static Dependency getArtifact(Document document) {

        // 按文档顺序返回在文档中，具有book标签的所有Node
        Element documentElement = document.getDocumentElement();
        NodeList nodes = documentElement.getChildNodes();

        Dependency dependency = new Dependency();
        // 将数组中的内容按照顺序写入对象
        for (int i = 0; i < nodes.getLength(); i++) {
            // 获取第i个book结点
            Node node = nodes.item(i);
            String nodeName = node.getNodeName();
            String nodeValue = node.getNodeValue();
            switch (nodeName) {
                case groupId -> dependency.setGroupId(nodeValue);
                case artifactId -> dependency.setArtifactId(nodeValue);
                case version -> dependency.setVersion(nodeValue);
            }
        }

        return dependency;

    }

    public static List<Dependency> getDependencies(@NotNull String document, Dependency pom) {
        ArrayList<Dependency> dependencies = new ArrayList<>();

        File file = new File(document);
        if (!file.exists()) {
            return dependencies;
        }

        Document document1 = null;
        try {
            document1 = XmlParser.getDocument(document);
            return getDependencies(document1, pom);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        return dependencies;

    }

    public static List<Dependency> getDependencies(Document document, Dependency pom) {
        // 用于保存解析后的Dependency对象
        List<Dependency> dependencyList = new ArrayList<>();
        if (document == null) {
            return dependencyList;
        }

        HashMap<String, String> properties = getProperties(document);

        // 找到正确的dependences
        Node nodeDependencies = null;
        Element documentElement = document.getDocumentElement();
        NodeList childNodes = documentElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            String nodeName = node.getNodeName();
            if (nodeName.equals(dependencies)) {
                nodeDependencies = node;
                break;
            }
        }

        if (nodeDependencies == null) {
            return dependencyList;
        }

        NodeList dependencyNodes = nodeDependencies.getChildNodes();

        // 遍历具有book标签的所有Node
        for (int i = 1; i < dependencyNodes.getLength(); i+=2) {
            // 获取第i个dependency结点
            Node node = dependencyNodes.item(i);

            // 获取dependency结点的子节点,包含了text类型的换行
            NodeList childNodes1 = node.getChildNodes();
            Dependency dependency = new Dependency();
            // 这里由于偶数行是text类型无用节点，所以只取1,3,5,7节点
            for (int j = 1; j < childNodes1.getLength(); j += 2) {
                Node childNode = childNodes1.item(j);
                String nodeName = childNode.getNodeName();
                if (nodeName.equals("#comment")) {
                    continue;
                }

                String content = childNode.getFirstChild().getTextContent();

                switch (nodeName) {
                    case groupId -> dependency.setGroupId(content);
                    case artifactId -> dependency.setArtifactId(content);
                    case version -> {
                        if (content.startsWith("${")) {
                            content = properties.getOrDefault(content, content);
                        }
                        if (content.equalsIgnoreCase(projectVersion)) {
                            content = pom.getVersion();
                        }
                        dependency.setVersion(content);
                    }
                    case scope -> dependency.setScope(content);
                    case optional -> dependency.setOptional(Boolean.parseBoolean(content));
                    case exclusions -> {
                        ArrayList<Dependency.Exclusion> exclusions = getExclusions(childNode);
                        if (!exclusions.isEmpty()) {
                            exclusions.forEach(dependency::addExclusion);
                        }
                    }
                }
            }

            if (dependency.getGroupId() != null) {
                dependencyList.add(dependency);
            }
        }

        return dependencyList;

    }

    private static ArrayList<Dependency.Exclusion> getExclusions(Node exclusionsNode) {
        ArrayList<Dependency.Exclusion>  exclusions= new ArrayList<>();

        if (exclusionsNode == null) {
            return exclusions;
        }

        NodeList exclusionNodes = exclusionsNode.getChildNodes();

        // 遍历具有book标签的所有Node
        for (int i = 1; i < exclusionNodes.getLength(); i+=2) {
            // 获取第i个dependency结点
            Node node = exclusionNodes.item(i);

            // 获取dependency结点的子节点,包含了text类型的换行
            NodeList childNodes1 = node.getChildNodes();
            Dependency.Exclusion exclusion = new Dependency.Exclusion();
            // 这里由于偶数行是text类型无用节点，所以只取1,3,5,7节点
            for (int j = 1; j < childNodes1.getLength(); j += 2) {
                Node childNode = childNodes1.item(j);
                String nodeName = childNode.getNodeName();
                if (nodeName.equals("#comment")) {
                    continue;
                }

                String content = childNode.getFirstChild().getTextContent();

                switch (nodeName) {
                    case groupId -> exclusion.setGroupId(content);
                    case artifactId -> exclusion.setArtifactId(content);
                }
            }

            // 将解析好的book加入返回列表
            if (exclusion.getGroupId() != null) {
                exclusions.add(exclusion);
            }
        }

        return exclusions;
    }

    public static HashMap<String, String> getProperties(Document document) {
        // 用于保存解析后的 key=value
        HashMap<String, String> propertiesMap = new HashMap<>();

        // 按文档顺序返回在文档中，具有book标签的所有Node
        NodeList propertiesNodes = document.getElementsByTagName(properties);
        if (propertiesNodes.getLength() == 0) {
            return propertiesMap;
        }

        Node node = propertiesNodes.item(0);
        NodeList childNodes = node.getChildNodes();
        // 这里由于偶数行是text类型无用节点，所以只取1,3,5,7节点
        for (int j = 1; j < childNodes.getLength(); j += 2) {
            Node childNode = childNodes.item(j);
            String nodeName = childNode.getNodeName();
            if (childNode.getFirstChild() != null) {
                String content = childNode.getFirstChild().getTextContent();
                propertiesMap.put(nodeName, content);
            }
        }

        return propertiesMap;

    }

}
