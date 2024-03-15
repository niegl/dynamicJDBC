package flowdesigner.jdbc.download;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;

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
//        boolean urlExist = doesURLExist(url);
//        if (!urlExist) {
//            return null;
//        }

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

        downloadByNIO2(netPath, localPath, fileName);
//        try {
//            URLConnection connection = url.openConnection();
//            connection.setConnectTimeout(connectTimeout);
//            connection.setReadTimeout(connectTimeout);
//            inputStream = connection.getInputStream();
//            outputStream = new FileOutputStream(toFile);
//            byte[] buffer = new byte[1024];
//
//            int byteRead;
//            while ((byteRead = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer,0, byteRead);
//            }
//        } catch (SocketTimeoutException e) {
//            log.error(e.getMessage());
//            throw e;
//        }   catch (IOException e) {
//            log.error(e.getMessage());
//            throw e;
//        } finally {
//            if (inputStream != null) {
//                inputStream.close();
//            }
//            if (outputStream != null) {
//                outputStream.close();
//            }
//        }

        return toFile;
    }

    /**
     * 使用 common-io库下载文件，需要引入commons-io-2.6.jar
     * @param url
     * @param saveDir
     * @return
     */
    public static String downloadByCommonIO(String url, String saveDir) {

        try {
            String fileName = FilenameUtils.getName(url);
            if (fileName.isEmpty()) {
                return null;
            }

            String fileString = saveDir + "/" + fileName;
            File destination1 = new File(fileString);
            if (destination1.exists()) {
                System.out.println("file exists: " + fileString);
                return fileString;
            }
            FileUtils.copyURLToFile(new URL(url), destination1);
            return fileString;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 使用NIO下载文件，需要 jdk 1.4+
     * @param url
     * @param saveDir
     * @param fileName
     */
    public static void downloadByNIO(String url, String saveDir, String fileName) {
        ReadableByteChannel rbc = null;
        FileOutputStream fos = null;
        FileChannel foutc = null;
        try {
            rbc = Channels.newChannel(new URL(url).openStream());
            File file = new File(saveDir, fileName);
            file.getParentFile().mkdirs();
            fos = new FileOutputStream(file);
            foutc = fos.getChannel();
            foutc.transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (rbc != null) {
                try {
                    rbc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (foutc != null) {
                try {
                    foutc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 使用NIO下载文件，需要 jdk 1.7+
     * @param url
     * @param saveDir
     * @param fileName
     * @return
     */
    public static String downloadByNIO2(String url, String saveDir, String fileName) {
        File file = new File(saveDir, fileName);
        if (file.exists()) {
            return file.getAbsolutePath();
        }

        try (InputStream ins = new URL(url).openStream()) {
            Path target = Paths.get(saveDir, fileName);
            Files.createDirectories(target.getParent());
            Files.copy(ins, target, StandardCopyOption.REPLACE_EXISTING);
            return target.toAbsolutePath().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用传统io stream下载文件
     * @param url
     * @param saveDir
     * @param fileName
     */
    public static void downloadByIO(String url, String saveDir, String fileName) {
        BufferedOutputStream bos = null;
        InputStream is = null;
        try {
            byte[] buff = new byte[8192];
            is = new URL(url).openStream();
            File file = new File(saveDir, fileName);
            file.getParentFile().mkdirs();
            bos = new BufferedOutputStream(new FileOutputStream(file));
            int count = 0;
            while ((count = is.read(buff)) != -1) {
                bos.write(buff, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
