package flowdesigner.util.parser;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class XmlParser {

    public static Document getDocument(String fileName) throws ParserConfigurationException, SAXException, IOException {
        // 工厂类可以设置很多XML解析参数
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        // 将给定URI的内容解析为一个 XML文档,并返回Document对象
        Document document = builder.parse(fileName);
        return document;
    }

    public static Document getDocument(InputStream is) throws ParserConfigurationException, SAXException, IOException {
        // 工厂类可以设置很多XML解析参数
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        // 将给定URI的内容解析为一个 XML文档,并返回Document对象
        Document document = builder.parse(is);
        return document;
    }

}
