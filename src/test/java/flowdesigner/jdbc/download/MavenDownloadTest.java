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

    @org.junit.jupiter.api.Test
    void testVersions() throws IOException {

        Collection<String> versions1 = MavenDownload.getVersions("https://repo1.maven.org/maven2/", "",
                "mysql", "mysql-connector-java");
        System.out.println(versions1);
    }

    @Test
    void downloadRecursive() throws IOException, ParserConfigurationException, SAXException {
        Collection<String> strings;
        strings = MavenDownload.downloadRecursive("https://repo1.maven.org/maven2/", "", "C:\\Users\\nieguangling\\Desktop\\1/Mysql/8.0.29/",
                "mysql", "mysql-connector-java", "8.0.29");
//        System.out.println(strings);

//        strings = MavenDownload.downloadRecursive("https://repo1.maven.org/maven2/", "", "C:/test/",
//                "com.ibm.db2","jcc",  "11.5.7.0");
//        System.out.println(strings);

//        strings = MavenDownload.downloadRecursive("https://repo1.maven.org/maven2/", "", "C:/test/",
//                "org.mariadb.jdbc","mariadb-java-client",  "3.0.7");
//        System.out.println(strings);

//        strings = MavenDownload.downloadRecursive("https://repo1.maven.org/maven2/", "", "C:/test/",
//                "com.oracle.database.jdbc","ojdbc8",  "12.2.0.1");
//        System.out.println(strings);

//        strings = MavenDownload.downloadRecursive("https://repo1.maven.org/maven2/", "", "C:/test/",
//                "org.postgresql","postgresql",  "42.5.2");
        System.out.println(strings);
    }

    @Test
    void testDownloadRecursive() {
        Collection<String> strings = MavenDownload.downloadRecursive("https://github.com/timveil/hive-jdbc-uber-jar/releases/download/v1.9-2.6.5/hive-jdbc-uber-2.6.5.0-292.jar", "", "C:/test/",
                "org.apache.hive", "hive-jdbc", "1.2.1");
        System.out.println(strings);
    }
}