package download;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UrlDownload {

    @org.junit.jupiter.api.Test
    void urlDownload() throws MalformedURLException {
        // 下载网络文件
        int bytesum = 0;
        int byteread = 0;

        URL url = new URL("https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.30/mysql-connector-java-8.0.30.jar");
        try {
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            FileOutputStream outputStream = new FileOutputStream("C:/test/mysql-connector-java-8.0.30.jar");
            byte[] buffer = new byte[1024];
            int length;
            while ((byteread = inputStream.read(buffer)) != -1) {
                bytesum += byteread;
                outputStream.write(buffer,0, byteread);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
