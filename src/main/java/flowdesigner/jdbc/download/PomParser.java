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
            if (nodeName.equals("groupId")) {
                dependency.setGroupId(nodeValue);
            } else if (nodeName.equals("artifactId")) {
                dependency.setGroupId(nodeValue);
            } else if (nodeName.equals("version")) {
                dependency.setGroupId(nodeValue);
            }
        }

        return dependency;

    }

    public static List<Dependency> getDependencies(@NotNull String document) {
        ArrayList<Dependency> dependencies = new ArrayList<>();

        File file = new File(document);
        if (!file.exists()) {
            return dependencies;
        }

        Document document1 = null;
        try {
            document1 = XmlParser.getDocument(document);
            return getDependencies(document1);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        return dependencies;

    }

    public static List<Dependency> getDependencies(Document document) {
        // 用于保存解析后的Dependency对象
        List<Dependency> dependencies = new ArrayList<>();
        if (document == null) {
            return dependencies;
        }

        HashMap<String, String> properties = getProperties(document);

        // 按文档顺序返回在文档中，具有book标签的所有Node
        NodeList dependencyNodes = document.getElementsByTagName("dependency");
        // 遍历具有book标签的所有Node
        for (int i = 0; i < dependencyNodes.getLength(); i++) {
            // 获取第i个dependency结点
            Node node = dependencyNodes.item(i);

            // 获取dependency结点的子节点,包含了text类型的换行
            NodeList childNodes = node.getChildNodes();
            Dependency dependency = new Dependency();
            // 这里由于偶数行是text类型无用节点，所以只取1,3,5,7节点
            for (int j = 1; j < childNodes.getLength(); j += 2) {
                Node childNode = childNodes.item(j);
                String nodeName = childNode.getNodeName();
                if (nodeName.equals("#comment")) {
                    continue;
                }

                String content = childNode.getFirstChild().getTextContent();

                switch (nodeName) {
                    case "groupId" -> dependency.setGroupId(content);
                    case "artifactId" -> dependency.setArtifactId(content);
                    case "version" -> {
                        if (content.startsWith("$")) {
                            content = properties.getOrDefault(content, "");
                        }
                        dependency.setVersion(content);
                    }
                    case "test" -> dependency.setScope(content);
                }
            }

            // 将解析好的book加入返回列表
            dependencies.add(dependency);
        }

        return dependencies;

    }

    public static HashMap<String, String> getProperties(Document document) {
        // 用于保存解析后的 key=value
        HashMap<String, String> properties = new HashMap<>();

        // 按文档顺序返回在文档中，具有book标签的所有Node
        NodeList propertiesNodes = document.getElementsByTagName("properties");
        if (propertiesNodes.getLength() == 0) {
            return properties;
        }

        Node node = propertiesNodes.item(0);
        NodeList childNodes = node.getChildNodes();
        // 这里由于偶数行是text类型无用节点，所以只取1,3,5,7节点
        for (int j = 1; j < childNodes.getLength(); j += 2) {
            Node childNode = childNodes.item(j);
            String nodeName = childNode.getNodeName();
            if (childNode.getFirstChild() != null) {
                String content = childNode.getFirstChild().getTextContent();
                properties.put(nodeName, content);
            }
        }

        return properties;

    }

}
