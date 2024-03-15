/*
 * Copyright 2019-2029 FISOK(www.fisok.cn).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package flowdesigner.util.raw.kit;


import flowdesigner.util.raw.lang.RawException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.Checksum;


public abstract class FileKit {

    private static DecimalFormat decimalFormat=(DecimalFormat) NumberFormat.getInstance();
    static{
        decimalFormat.setMaximumFractionDigits(2);
    }

    /** 使用java自带的log工具 */
    private static final Logger logger = Logger.getLogger("CommandManager");

    /**
     * 把文件路径处理成为标准文件路径,都用"/"作为路径分割符
     *
     * @param path path
     * @return string
     */
    public static String standardFilePath(String path){
        if(path!=null){
            return path.replaceAll("\\\\", "/");
        }else{
            return path;
        }
    }

    /**
     * 如果文件不存在，则创建新文件
     *
     * @param file file
     * @throws IOException IOException
     * @return File
     */
    public static File touchFile(String file) throws IOException {
        return touchFile(new File(file));
    }
    /**
     * 如果文件不存在，则创建新文件
     *
     * @param file file
     * @throws IOException IOException
     * @return File
     */
    public static File touchFile(File file) throws IOException {
        return touchFile(file,false);
    }

    /**
     * 如果文件不存在，则创建新文件
     *
     * @param file file
     * @param cleanFile cleanFile
     * @throws IOException IOException
     * @return File
     */
    public static File touchFile(File file,boolean cleanFile) throws IOException {
        if(cleanFile&&file.exists()) FileKit.deleteFile(file);
        if(!file.exists()){
            File parentFile = file.getParentFile();
            if(!parentFile.exists()){
                parentFile.mkdirs();
            }
            file.createNewFile();
        }
        return file;
    }

    public static long getFileSize(File file){
        FileChannel channel = null;
        if(file.exists() && file.isFile()){
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                channel = inputStream.getChannel();
                return channel.size();
            } catch (FileNotFoundException e) {
                throw new RawException("读取文件大小出错，文件:[{0}]不存在",file.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException("读取文件出错");
            }finally{
                IOKit.close(inputStream);
                IOKit.close(channel);
            }
        }
        return 0L;
    }

    public static boolean deleteFile(File file){
        String fileFullPath;
        try {
            fileFullPath = file.getCanonicalPath();
        } catch (IOException e) {
            fileFullPath = e.getMessage();
        }

        boolean r = file.delete();
        logger.info(MessageFormat.format("文件删除,【{0}】，删除结果【{1}】",fileFullPath,r));
        return r;
    }

    public static InputStream openInputStream(File file) throws IOException{
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw e;
        }

        return inputStream;
    }

    public static OutputStream openOutputStream(File file,boolean autoCreate) throws IOException{
        OutputStream outputStream = null;

        if(autoCreate&&!file.exists()){
            File parent = file.getParentFile();
            if(!parent.exists())parent.mkdirs();
            file.createNewFile();
        }
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw e;
        }

        return outputStream;
    }

    public static OutputStream openOutputStream(File file) throws IOException{
        return openOutputStream(file,true);
    }

    /**
     * 文件复制
     *
     * @param src src
     * @param dsc dsc
     * @param autoCreateDscDir autoCreateDscDir
     * @throws IOException IOException
     */
    public static void copy(File src,File dsc,boolean autoCreateDscDir) throws IOException{
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel fcIn = null;
        FileChannel fcOut = null;

        try {
            logger.info(MessageFormat.format("文件复制,【{0}】-->【{1}】", src.getCanonicalPath(),dsc.getCanonicalPath()));
            if(autoCreateDscDir){
                File parentFile = dsc.getParentFile();
                if(!parentFile.exists()){
                    parentFile.mkdirs();
                }
            }
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dsc);
            fcIn = fis.getChannel();
            fcOut = fos.getChannel();

            fcIn.transferTo(0, fcIn.size(), fcOut);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }finally{
            IOKit.close(fis);
            IOKit.close(fcIn);
            IOKit.close(fos);
            IOKit.close(fcOut);
        }
    }

    public  static String convertSizeText(Long size) {
        if (size == null || size == 0) return "0K";
        String text = "";
        Double size1 = size.doubleValue();
        text = decimalFormat.format(size1) + "B";
        int mulNumber = 1024;
        if (size1 > mulNumber) {
            size1 = size1 / mulNumber;
            text = decimalFormat.format(size1) + "K";
        }
        if (size1 > mulNumber) {
            size1 = size1 / mulNumber;
            text = decimalFormat.format(size1) + "M";
        }
        if (size1 > mulNumber) {
            size1 = size1 / mulNumber;
            text = decimalFormat.format(size1) + "G";
        }
        return text;
    }

    /**
     * 获取文件的扩展名
     *
     * @param file file
     * @return String
     */
    public static String getSuffix(String file){
        String suffix = "";
        if(StringUtils.isBlank(file)) return suffix;
        String[] ss = file.split("\\.");
        if(ss.length>1){
            suffix = ss[ss.length-1];
        }
        return suffix;
    }

    public static String getFileName(String file,boolean includeExtension){
        return getFileName(new File(file),includeExtension);
    }
    public static String getFileName(File file,boolean includeExtension){
        String fileName = file.getName();
        if(!includeExtension&&fileName.lastIndexOf(".")>=0){
            return fileName.substring(0,fileName.lastIndexOf("."));
        }else{
            return fileName;
        }
    }

    public static File getFile(File directory, String... names) {
        return FileUtils.getFile(directory, names);
    }

    public static File getFile(String... names) {
        return FileUtils.getFile(names);
    }

    public static String getTempDirectoryPath() {
        return FileUtils.getTempDirectoryPath();
    }

    public static File getTempDirectory() {
        return FileUtils.getTempDirectory();
    }

    public static String getUserDirectoryPath() {
        return FileUtils.getUserDirectoryPath();
    }

    public static File getUserDirectory() {
        return FileUtils.getUserDirectory();
    }

    public static String byteCountToDisplaySize(BigInteger size) {
        return FileUtils.byteCountToDisplaySize(size);
    }

    public static String byteCountToDisplaySize(long size) {
        return FileUtils.byteCountToDisplaySize(size);
    }

    public static void touch(File file) throws IOException {
        FileUtils.touch(file);
    }

    public static File[] convertFileCollectionToFileArray(Collection<File> files) {
        return FileUtils.convertFileCollectionToFileArray(files);
    }

    public static Collection<File> listFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        return FileUtils.listFiles(directory, fileFilter, dirFilter);
    }

    public static Collection<File> listFilesAndDirs(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        return FileUtils.listFilesAndDirs(directory, fileFilter, dirFilter);
    }

    public static Iterator<File> iterateFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        return FileUtils.iterateFiles(directory, fileFilter, dirFilter);
    }

    public static Iterator<File> iterateFilesAndDirs(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        return FileUtils.iterateFilesAndDirs(directory, fileFilter, dirFilter);
    }

    public static Collection<File> listFiles(File directory, String[] extensions, boolean recursive) {
        return FileUtils.listFiles(directory, extensions, recursive);
    }

    public static Iterator<File> iterateFiles(File directory, String[] extensions, boolean recursive) {
        return FileUtils.iterateFiles(directory, extensions, recursive);
    }

    public static boolean contentEquals(File file1, File file2) throws IOException {
        return FileUtils.contentEquals(file1, file2);
    }

    public static boolean contentEqualsIgnoreEOL(File file1, File file2, String charsetName) throws IOException {
        return FileUtils.contentEqualsIgnoreEOL(file1, file2, charsetName);
    }

    public static File toFile(URL url) {
        return FileUtils.toFile(url);
    }

    public static File[] toFiles(URL[] urls) {
        return FileUtils.toFiles(urls);
    }

    public static URL[] toURLs(File[] files) throws IOException {
        return FileUtils.toURLs(files);
    }

    public static void copyFileToDirectory(File srcFile, File destDir) throws IOException {
        FileUtils.copyFileToDirectory(srcFile, destDir);
    }

    public static void copyFileToDirectory(File srcFile, File destDir, boolean preserveFileDate) throws IOException {
        FileUtils.copyFileToDirectory(srcFile, destDir, preserveFileDate);
    }

    public static void copyFile(File srcFile, File destFile) throws IOException {
        FileUtils.copyFile(srcFile, destFile);
    }

    public static void copyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        FileUtils.copyFile(srcFile, destFile, preserveFileDate);
    }

    public static long copyFile(File input, OutputStream output) throws IOException {
        return FileUtils.copyFile(input, output);
    }

    public static void copyDirectoryToDirectory(File srcDir, File destDir) throws IOException {
        FileUtils.copyDirectoryToDirectory(srcDir, destDir);
    }

    public static void copyDirectory(File srcDir, File destDir) throws IOException {
        FileUtils.copyDirectory(srcDir, destDir);
    }

    public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {
        FileUtils.copyDirectory(srcDir, destDir, preserveFileDate);
    }

    public static void copyDirectory(File srcDir, File destDir, FileFilter filter) throws IOException {
        FileUtils.copyDirectory(srcDir, destDir, filter);
    }

    public static void copyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate) throws IOException {
        FileUtils.copyDirectory(srcDir, destDir, filter, preserveFileDate);
    }

    public static void copyURLToFile(URL source, File destination) throws IOException {
        FileUtils.copyURLToFile(source, destination);
    }

    public static void copyURLToFile(URL source, File destination, int connectionTimeout, int readTimeout) throws IOException {
        FileUtils.copyURLToFile(source, destination, connectionTimeout, readTimeout);
    }

    public static void copyInputStreamToFile(InputStream source, File destination) throws IOException {
        FileUtils.copyInputStreamToFile(source, destination);
    }

    public static void copyToFile(InputStream source, File destination) throws IOException {
        FileUtils.copyToFile(source, destination);
    }

    public static void deleteDirectory(File directory) throws IOException {
        FileUtils.deleteDirectory(directory);
    }

    public static boolean deleteQuietly(File file) {
        return FileUtils.deleteQuietly(file);
    }

    public static boolean directoryContains(File directory, File child) throws IOException {
        return FileUtils.directoryContains(directory, child);
    }

    public static void cleanDirectory(File directory) throws IOException {
        FileUtils.cleanDirectory(directory);
    }

    public static boolean waitFor(File file, int seconds) {
        return FileUtils.waitFor(file, seconds);
    }

    public static String readFileToString(File file, Charset encoding) throws IOException {
        return FileUtils.readFileToString(file, encoding);
    }

    public static String readFileToString(File file, String encoding) throws IOException {
        return FileUtils.readFileToString(file, encoding);
    }

    @Deprecated
    public static String readFileToString(File file) throws IOException {
        return FileUtils.readFileToString(file);
    }

    public static byte[] readFileToByteArray(File file) throws IOException {
        return FileUtils.readFileToByteArray(file);
    }

    public static List<String> readLines(File file, Charset encoding) throws IOException {
        return FileUtils.readLines(file, encoding);
    }

    public static List<String> readLines(File file, String encoding) throws IOException {
        return FileUtils.readLines(file, encoding);
    }

    @Deprecated
    public static List<String> readLines(File file) throws IOException {
        return FileUtils.readLines(file);
    }

    public static LineIterator lineIterator(File file, String encoding) throws IOException {
        return FileUtils.lineIterator(file, encoding);
    }

    public static LineIterator lineIterator(File file) throws IOException {
        return FileUtils.lineIterator(file);
    }

    public static void writeStringToFile(File file, String data, Charset encoding) throws IOException {
        FileUtils.writeStringToFile(file, data, encoding);
    }

    public static void writeStringToFile(File file, String data, String encoding) throws IOException {
        FileUtils.writeStringToFile(file, data, encoding);
    }

    public static void writeStringToFile(File file, String data, Charset encoding, boolean append) throws IOException {
        FileUtils.writeStringToFile(file, data, encoding, append);
    }

    public static void writeStringToFile(File file, String data, String encoding, boolean append) throws IOException {
        FileUtils.writeStringToFile(file, data, encoding, append);
    }

    @Deprecated
    public static void writeStringToFile(File file, String data) throws IOException {
        FileUtils.writeStringToFile(file, data);
    }

    @Deprecated
    public static void writeStringToFile(File file, String data, boolean append) throws IOException {
        FileUtils.writeStringToFile(file, data, append);
    }

    @Deprecated
    public static void write(File file, CharSequence data) throws IOException {
        FileUtils.write(file, data);
    }

    @Deprecated
    public static void write(File file, CharSequence data, boolean append) throws IOException {
        FileUtils.write(file, data, append);
    }

    public static void write(File file, CharSequence data, Charset encoding) throws IOException {
        FileUtils.write(file, data, encoding);
    }

    public static void write(File file, CharSequence data, String encoding) throws IOException {
        FileUtils.write(file, data, encoding);
    }

    public static void write(File file, CharSequence data, Charset encoding, boolean append) throws IOException {
        FileUtils.write(file, data, encoding, append);
    }

    public static void write(File file, CharSequence data, String encoding, boolean append) throws IOException {
        FileUtils.write(file, data, encoding, append);
    }

    public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
        FileUtils.writeByteArrayToFile(file, data);
    }

    public static void writeByteArrayToFile(File file, byte[] data, boolean append) throws IOException {
        FileUtils.writeByteArrayToFile(file, data, append);
    }

    public static void writeByteArrayToFile(File file, byte[] data, int off, int len) throws IOException {
        FileUtils.writeByteArrayToFile(file, data, off, len);
    }

    public static void writeByteArrayToFile(File file, byte[] data, int off, int len, boolean append) throws IOException {
        FileUtils.writeByteArrayToFile(file, data, off, len, append);
    }

    public static void writeLines(File file, String encoding, Collection<?> lines) throws IOException {
        FileUtils.writeLines(file, encoding, lines);
    }

    public static void writeLines(File file, String encoding, Collection<?> lines, boolean append) throws IOException {
        FileUtils.writeLines(file, encoding, lines, append);
    }

    public static void writeLines(File file, Collection<?> lines) throws IOException {
        FileUtils.writeLines(file, lines);
    }

    public static void writeLines(File file, Collection<?> lines, boolean append) throws IOException {
        FileUtils.writeLines(file, lines, append);
    }

    public static void writeLines(File file, String encoding, Collection<?> lines, String lineEnding) throws IOException {
        FileUtils.writeLines(file, encoding, lines, lineEnding);
    }

    public static void writeLines(File file, String encoding, Collection<?> lines, String lineEnding, boolean append) throws IOException {
        FileUtils.writeLines(file, encoding, lines, lineEnding, append);
    }

    public static void writeLines(File file, Collection<?> lines, String lineEnding) throws IOException {
        FileUtils.writeLines(file, lines, lineEnding);
    }

    public static void writeLines(File file, Collection<?> lines, String lineEnding, boolean append) throws IOException {
        FileUtils.writeLines(file, lines, lineEnding, append);
    }

    public static void forceDelete(File file) throws IOException {
        FileUtils.forceDelete(file);
    }

    public static void forceDeleteOnExit(File file) throws IOException {
        FileUtils.forceDeleteOnExit(file);
    }

    public static void forceMkdir(File directory) throws IOException {
        FileUtils.forceMkdir(directory);
    }

    public static void forceMkdirParent(File file) throws IOException {
        FileUtils.forceMkdirParent(file);
    }

    public static long sizeOf(File file) {
        return FileUtils.sizeOf(file);
    }

    public static BigInteger sizeOfAsBigInteger(File file) {
        return FileUtils.sizeOfAsBigInteger(file);
    }

    public static long sizeOfDirectory(File directory) {
        return FileUtils.sizeOfDirectory(directory);
    }

    public static BigInteger sizeOfDirectoryAsBigInteger(File directory) {
        return FileUtils.sizeOfDirectoryAsBigInteger(directory);
    }

    public static boolean isFileNewer(File file, File reference) {
        return FileUtils.isFileNewer(file, reference);
    }

    public static boolean isFileNewer(File file, Date date) {
        return FileUtils.isFileNewer(file, date);
    }

    public static boolean isFileNewer(File file, long timeMillis) {
        return FileUtils.isFileNewer(file, timeMillis);
    }

    public static boolean isFileOlder(File file, File reference) {
        return FileUtils.isFileOlder(file, reference);
    }

    public static boolean isFileOlder(File file, Date date) {
        return FileUtils.isFileOlder(file, date);
    }

    public static boolean isFileOlder(File file, long timeMillis) {
        return FileUtils.isFileOlder(file, timeMillis);
    }

    public static long checksumCRC32(File file) throws IOException {
        return FileUtils.checksumCRC32(file);
    }

    public static Checksum checksum(File file, Checksum checksum) throws IOException {
        return FileUtils.checksum(file, checksum);
    }

    public static void moveDirectory(File srcDir, File destDir) throws IOException {
        FileUtils.moveDirectory(srcDir, destDir);
    }

    public static void moveDirectoryToDirectory(File src, File destDir, boolean createDestDir) throws IOException {
        FileUtils.moveDirectoryToDirectory(src, destDir, createDestDir);
    }

    public static void moveFile(File srcFile, File destFile) throws IOException {
        FileUtils.moveFile(srcFile, destFile);
    }

    public static void moveFileToDirectory(File srcFile, File destDir, boolean createDestDir) throws IOException {
        FileUtils.moveFileToDirectory(srcFile, destDir, createDestDir);
    }

    public static void moveToDirectory(File src, File destDir, boolean createDestDir) throws IOException {
        FileUtils.moveToDirectory(src, destDir, createDestDir);
    }

    public static boolean isSymlink(File file) throws IOException {
        return FileUtils.isSymlink(file);
    }

    /**
     * 文件复制
     * @param inputStream
     * @param dscFile
     * @throws IOException
     */
    public static void transferTo(InputStream inputStream,File dscFile,boolean autoCreateDscDir) throws IOException {
        if(autoCreateDscDir&&!dscFile.exists()){
            touchFile(dscFile,true);
        }
        FileOutputStream outputStream = new FileOutputStream(dscFile);
        IOKit.copy(inputStream,outputStream);
    }

    /**
     * 从jar包中读取指定文件的内容.
     * @param path jar包文件的路径
     * @param innerFile jar包内部文件名
     * @return 文件内容
     */
    public static String readFileFromJar(String path, String innerFile) {
        return readFileFromJar(new File(path), innerFile);
    }

    public static String readFileFromJar(File path, String innerFile) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (jarFile == null) return "";

        Enumeration<JarEntry> entrys = jarFile.entries();
        while (entrys.hasMoreElements()) {
            JarEntry jarEntry = entrys.nextElement();
            if (!jarEntry.getName().endsWith(innerFile)) continue;

            StringBuilder stringBuilder = new StringBuilder();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarEntry)));
                String line;
                while ((line = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return stringBuilder.toString();
        }
        return "";
    }
}
