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


import flowdesigner.util.raw.io.ByteInputStream;
import flowdesigner.util.raw.io.ByteOutputStream;
import flowdesigner.util.raw.lang.RawException;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * @desc :
 */
public abstract class IOKit extends IOUtils {
    public static void close(Closeable stream) {
        try {
            if (stream != null) stream.close();
        } catch (IOException e) {
            new RawException("close {0} error", stream.getClass().getName());
        }
    }

    public static void close(AutoCloseable stream) {
        try {
            if (stream != null) stream.close();
        } catch (Exception e) {
            new RawException("close {0} error", stream.getClass().getName());
        }
    }

    /**
     * 把流转为字符数组缓冲区
     *
     * @param inputStream inputStream
     * @return ByteOutputStream
     * @throws IOException IOException
     */
    public static ByteOutputStream convertToByteArrayBuffer(InputStream inputStream) throws IOException {
        ByteOutputStream byteBuffer = new ByteOutputStream();

        int bufferSize = 1024 * 4;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer;
    }

    /**
     * 将输入流中的数据写入字节数组
     *
     * @param inputStream inputStream
     * @param isClose     isClose
     * @return byte
     * @throws IOException IOException
     */
    @SuppressWarnings("deprecation")
    public static byte[] convertToBytes(InputStream inputStream, boolean isClose) throws IOException {
        ByteOutputStream byteBuffer = convertToByteArrayBuffer(inputStream);
        if (isClose) IOKit.close(inputStream);
//        return byteBuffer.toByteArray();
        return byteBuffer.toBytes();
    }

    /**
     * 把流转为二进制
     *
     * @param inputStream inputStream
     * @return byte
     * @throws IOException IOException
     */
    public static byte[] convertToBytes(InputStream inputStream) throws IOException {
        return convertToBytes(inputStream, false);
    }

    /**
     * 转成二进制流
     *
     * @param inputStream inputStream
     * @return ByteInputStream
     * @throws IOException IOException
     */
    public static ByteInputStream convertToByteArrayInputStream(InputStream inputStream) throws IOException {
        ByteOutputStream byteBuffer = convertToByteArrayBuffer(inputStream);
        return byteBuffer.createInputStream();
    }

    /**
     * 把二进制转为输入流
     *
     * @param bytes bytes
     * @return InputStream
     */
    public static InputStream convertToInputStream(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        return bis;
    }

    /**
     * 文件流复制
     *
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    public static void transferTo(InputStream inputStream, OutputStream outputStream) throws IOException {
        IOKit.copy(inputStream, outputStream);
    }


    /**
     * 根据流推测字符集
     * @param inputStream
     * @return
     */
    public static String getCharset(InputStream inputStream) {
        //默认GBK
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try (BufferedInputStream bis = new BufferedInputStream(inputStream)) {
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            // 文件编码为 ANSI
            if (read == -1) {
                return charset;
            }
            // 文件编码为 Unicode
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                return "UTF-16LE";
            }
            // 文件编码为 Unicode big endian
            if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                return "UTF-16BE";
            }
            // 文件编码为 UTF-8
            if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                return "UTF-8";
            }
            bis.reset();

            int loc = 0;
            while ((read = bis.read()) != -1) {
                loc++;
                if (read >= 0xF0) {
                    break;
                }
                // 单独出现BF以下的，也算是GBK
                if (0x80 <= read && read <= 0xBF) {
                    break;
                }
                if (0xC0 <= read && read <= 0xDF) {
                    read = bis.read();
                    // 双字节 (0xC0 - 0xDF)
                    if (0x80 <= read && read <= 0xBF) {
                        // (0x80
                        // - 0xBF),也可能在GB编码内
                        continue;
                    }
                    break;
                }
                // 也有可能出错，但是几率较小
                if (0xE0 <= read && read <= 0xEF) {
                    read = bis.read();
                    if (0x80 <= read && read <= 0xBF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            charset = "UTF-8";
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            throw new RawException("计算inputStream的字符集出错",e);
        }
        return charset;
    }

}
