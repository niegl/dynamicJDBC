package flowdesigner.jdbc.download;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.file.LinkOption;

@Slf4j
public class NetDownload {
    private static final int connectTimeout = 10*1000;

    /**
     * 通用网络文件下载功能
     * @param netPath 网络地址，如https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.30/mysql-connector-java-8.0.30.jar
     * @param localPath 本地文件存储地址，如C:/test/
     * @throws IOException
     */
    public static String download(String netPath, String localPath) throws IOException {
        // 下载网络文件
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        // 检查是否为路径
        URL url = new URL(netPath);
        boolean urlExist = doesURLExist(url);
        if (!urlExist) {
            return null;
        }

        if (!FileUtils.isDirectory(new File(localPath), LinkOption.NOFOLLOW_LINKS)) {
            return null;
        }

        // 检查是否已经存在,存在直接返回
        String fileName = FilenameUtils.getName(url.getPath());
        if (fileName.isEmpty()) {
            return null;
        }
        String toFile = localPath + "/" + fileName;
        File file = new File(toFile);
        if (file.exists()) {
            System.out.println("file exists: " + toFile);
            return toFile;
        }

        try {
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(connectTimeout);
            inputStream = connection.getInputStream();
            outputStream = new FileOutputStream(toFile);
            byte[] buffer = new byte[1024];

            int byteRead;
            while ((byteRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer,0, byteRead);
            }
        } catch (SocketTimeoutException e) {
            log.error(e.getMessage());
            throw e;
        }   catch (IOException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }

        return toFile;
    }

    public static boolean doesURLExist(URL url)
    {
        try {
            // We want to check the current URL
            HttpURLConnection.setFollowRedirects(false);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // We don't need to get data
            httpURLConnection.setRequestMethod("HEAD");

            // Some websites don't like programmatic access so pretend to be a browser
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
            int responseCode = httpURLConnection.getResponseCode();

            // We only accept response code 200
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }

    }

}
