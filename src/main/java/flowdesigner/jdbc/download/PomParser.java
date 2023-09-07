package flowdesigner.jdbc.download;

import flowdesigner.util.parser.XmlParser;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PomParser {
    private static final String _packaging = "packaging";

    private static final String properties = "properties";
    private static final String parent = "parent";
    private static final String _dependencies = "dependencies";
    private static final String dependencyManagement = "dependencyManagement";
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

    public static List<Dependency> getDependencies(String url, String scope, @NotNull String filePath, Dependency documentDependency) {
        ArrayList<Dependency> dependencies = new ArrayList<>();

        File file = new File(filePath);
        if (!file.exists()) {
            return dependencies;
        }

        Document document = null;
        try {
            document = XmlParser.getDocument(filePath);
            return getDependencies(url, scope, document, documentDependency);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        return dependencies;

    }

    private static List<Dependency> getDependencies(String url, String scope, Document document, Dependency documentDependency) {
        // 用于保存解析后的Dependency对象
        List<Dependency> dependencyList = new ArrayList<>();
        if (document == null) {
            return dependencyList;
        }
        // 获取父类的属性和父类的依赖管理
        ArrayList<Dependency> dependenciesInManagement = new ArrayList<>();
        HashMap<String, String> properties = new HashMap<>();
        try {
            getPropertiesAndDependencyManagement(url, scope, document, properties, dependenciesInManagement);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 找到正确的dependences
        Node nodeDependencies = getNode(document, PomParser._dependencies);
        if (nodeDependencies == null) {
            return dependencyList;
        }

        NodeList dependencyNodes = nodeDependencies.getChildNodes();
        // 遍历所有Node
        for (int i = 1; i < dependencyNodes.getLength(); i += 2) {
            // 获取第i个dependency结点
            Node node = dependencyNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Dependency dependency = getDependency(node, properties, dependenciesInManagement, documentDependency.getVersion());
                if (dependency.getGroupId() != null) {
                    dependencyList.add(dependency);
                }
            }
        }

        return dependencyList;

    }

    private static Node getNode(Document document, String nodeTag) {
        Node retNod = null;
        Element documentElement = document.getDocumentElement();
        NodeList childNodes = documentElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            String nodeName = node.getNodeName();
            if (nodeName.equals(nodeTag)) {
                retNod = node;
                break;
            }
        }
        return retNod;
    }

    private static String getProjectVersion(Document document) {
        Node node = getNode(document, version);
        if (node == null) {
            return null;
        }
        return node.getTextContent();
    }

    @NotNull
    private static Dependency getDependency(Node node, HashMap<String, String> properties, ArrayList<Dependency> dependenciesInManagement, String project_version) {
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

            Node firstChild = childNode.getFirstChild();
            if (firstChild == null) {
                continue;
            }
            String content = firstChild.getTextContent();

            switch (nodeName) {
                case groupId -> dependency.setGroupId(content);
                case artifactId -> dependency.setArtifactId(content);
                case version -> {
//                    if (!content.isEmpty()) {
                        if (content.equalsIgnoreCase(projectVersion)) {
                            content = project_version;
                        } else if (content.startsWith("${")) {
                            content = properties.getOrDefault(content.replace("${", "").replace("}", ""), content);
                        }
//                    } else {
//                        if (dependenciesInManagement != null) {
//                            for (Dependency dependencyInParent : dependenciesInManagement) {
//                                if (dependencyInParent.getGroupId().equals(dependency.getGroupId()) &&
//                                        dependencyInParent.getArtifactId().equals(dependency.getArtifactId())) {
//                                    content = dependencyInParent.getVersion();
//                                }
//                            }
//                        } else if (project_version != null) {
//                            content = project_version;
//                        }
//                    }
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

        // 处理没有指定version的情况
        if (dependency.getVersion() == null) {
            String managementVersion = null;
            if (dependenciesInManagement != null && !dependenciesInManagement.isEmpty()) {
                for (Dependency dependencyInParent : dependenciesInManagement) {
                    if (dependencyInParent.getGroupId().equals(dependency.getGroupId()) &&
                            dependencyInParent.getArtifactId().equals(dependency.getArtifactId())) {
                        managementVersion = dependencyInParent.getVersion();
                    }
                }
            } else if (project_version != null) {
                managementVersion = project_version;
            }
            dependency.setVersion(managementVersion);
        }

        return dependency;
    }

    /**
     * 获取父类依赖管理中的版本
     * @param document
     * @return
     */
    private static ArrayList<Dependency> getManagementDependencies(Document document, HashMap<String, String> properties, String project_version) {
        ArrayList<Dependency> dependencies = new ArrayList<>();

        // 获取dependencies节点
        Node nodeManagement = getNode(document, dependencyManagement);
        if (nodeManagement == null) {
            return dependencies;
        }
        Node nextSibling = nodeManagement.getFirstChild();
        Node dependenciesInManagement = null;
        while (nextSibling != null) {
            if (nextSibling.getNodeName().equals(_dependencies)) {
                dependenciesInManagement = nextSibling;
                break;
            }
            nextSibling = nextSibling.getNextSibling();
        }
        if (dependenciesInManagement == null) {
            return dependencies;
        }

        // 遍历所有dependency
        NodeList dependencyNodeList = dependenciesInManagement.getChildNodes();
        for (int i = 1; i < dependencyNodeList.getLength(); i += 2) {
            // 获取第i个dependency结点
            Node dependencyNode = dependencyNodeList.item(i);
            if (dependencyNode.getNodeType() == Node.ELEMENT_NODE) {
                Dependency dependency = getDependency(dependencyNode, properties, null, project_version);
                dependencies.add(dependency);
            }
        }

        return dependencies;
    }

    private static ArrayList<Dependency.Exclusion> getExclusions(Node exclusionsNode) {
        ArrayList<Dependency.Exclusion> exclusions = new ArrayList<>();

        if (exclusionsNode == null) {
            return exclusions;
        }

        NodeList exclusionNodes = exclusionsNode.getChildNodes();

        // 遍历具有book标签的所有Node
        for (int i = 1; i < exclusionNodes.getLength(); i += 2) {
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

    private static void getPropertiesAndDependencyManagement(String url, String scope,
                                                             Document document, HashMap<String, String> propertiesMap, ArrayList<Dependency> dependenciesInManagement) throws IOException {
        String projectVersion = getProjectVersion(document);
        // 获取当前的属性
        getProperties(document, propertiesMap);
        // 获取当前依赖管理
        ArrayList<Dependency> managementDependencies = getManagementDependencies(document, propertiesMap, projectVersion);
        if (!managementDependencies.isEmpty()) {
            dependenciesInManagement.addAll(managementDependencies);
        }

        // 如果有父类parent，那么获取父类的属性
        Node nodeParent = getNode(document, parent);
        if (nodeParent == null) {
            return;
        }
        Dependency dependency = getDependency(nodeParent, propertiesMap, dependenciesInManagement, projectVersion);
        StringBuilder netPath = dependency.toNetPath(url, scope);
        netPath.append(dependency.toFileName()).append(".pom");
        try (InputStream ins = new URL(netPath.toString()).openStream()) {
            Document documentParent = XmlParser.getDocument(ins);
            getPropertiesAndDependencyManagement(url, scope, documentParent, propertiesMap, dependenciesInManagement);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取本文档和父文档中的所有依赖父文档不保存
     * @param document
     * @return key=value
     */
    private static void getProperties(Document document, HashMap<String, String> propertiesMap) {
        // 按文档顺序返回在文档中，具有book标签的所有Node
        Node nodeProperties = getNode(document, properties);
        if (nodeProperties == null) {
            return;
        }

        NodeList childNodes = nodeProperties.getChildNodes();
        // 这里由于偶数行是text类型无用节点，所以只取1,3,5,7节点
        for (int j = 1; j < childNodes.getLength(); j += 2) {
            Node childNode = childNodes.item(j);
            String nodeName = childNode.getNodeName();
            if (childNode.getFirstChild() != null) {
                String content = childNode.getFirstChild().getTextContent();
                propertiesMap.put(nodeName, content);
            }
        }
    }

}



