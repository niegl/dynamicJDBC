package flowdesigner.jdbc.download;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Collection;

class MavenDownloadTest {

    @org.junit.jupiter.api.Test
    void urlDownload() throws IOException {

        NetDownload.download("https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.30/mysql-connector-java-8.0.30.jar",
                "C:/test/");
    }

    @Test
    void downLoad() throws IOException {
        MavenDownload.download("https://repo1.maven.org/maven2/", "", "C:/test/",
                "mysql", "mysql-connector-java","8.0.30");
    }

    @org.junit.jupiter.api.Test
    void testVersions() throws IOException {

        Collection<String> versions1 = MavenDownload.getVersions("https://repo1.maven.org/maven2/", "",
                "mysql", "mysql-connector-java");
        System.out.println(versions1);
    }

    @Test
    void downloadRecursive() throws IOException, ParserConfigurationException, SAXException {
        MavenDownload.downloadRecursive("https://repo1.maven.org/maven2/", "", "C:/test/",
                "mysql", "mysql-connector-java","8.0.30");
    }

}