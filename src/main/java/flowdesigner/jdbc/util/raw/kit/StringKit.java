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
package flowdesigner.jdbc.util.raw.kit;

import flowdesigner.jdbc.util.raw.RawConsts;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @desc : 字串操作
 */
public abstract class StringKit extends StringUtils {
    /**
     * 空串
     *
     * @param s  字串
     * @param s1 字串为空时，返回的字串
     * @return String
     */
    public static String nvl(Object s, String s1) {
        String ss = "";
        if (s instanceof String) {
            ss = (String) s;
        } else if (s == null) {
            return s1;
        } else {
            ss = String.valueOf(s);
        }

        if (StringUtils.isEmpty(ss)) return s1;
        return ss;
    }

    /**
     * 模拟实现Oracle的decode函数
     * decodeKeyValue("k1","k1","v1","s0") 返回 v1
     * decodeKeyValue("k9","k1","v1","s0") 返回 s0
     * decodeKeyValue("k2","k1","v1","k2","v2","s0") 返回 v2
     * @param expression
     * @param args
     * @return
     */
    public static String decodeKeyValue(String expression,String ... args){
        if(args == null || args.length == 0){
            throw new IllegalArgumentException("调用StringKit.decodeKeyValue方法，参数为空");
        }
        if(args.length % 2 != 1){
            throw new IllegalArgumentException("调用StringKit.decodeKeyValue方法，参数个数必需为奇数个");
        }
        for(int i=0;i<args.length;i+=2){
            if(args[i].equals(expression)){
                return args[i+1];
            }
        }
        return args[args.length-1];//找不到，返回最后一个参数
    }

    /**
     * 连接字串
     *
     * @param strList   strList
     * @param delimiter delimiter
     * @return string
     */
    public static String join(String[] strList, String delimiter) {
        return join(Arrays.asList(strList), delimiter);
    }

    public static String join(final CharSequence... elements) {
        return join8(elements, "");
    }

    /**
     * JAVA8的字串连接
     *
     * @param array    array
     * @param separator separator
     * @return string
     */
    public static String join8(final CharSequence[] array, final String separator) {
        StringJoiner joiner = new StringJoiner(separator);
        for (CharSequence object : array) {
            joiner.add(object);
        }
        return joiner.toString();
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static String ltrim(String str) {
        return str == null ? null : str.replaceAll("^\\\\s+", "");
    }

    public static String rtrim(String str) {
        return str == null ? null : str.replaceAll("\\\\s+$", "");
    }

    public static String clearSpace(String str) {
        return str == null ? null : str.replaceAll("\\\\s+", "");
    }

    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    public static String[] toStringArray(Collection<String> collection) {
        if (collection == null) {
            return null;
        }
        return collection.toArray(new String[collection.size()]);
    }

    // ==========================================================================
    // Perl风格的chomp和chop函数。
    // ==========================================================================

    /**
     * 删除字符串末尾的换行符。如果字符串不以换行结尾，则什么也不做。
     * <p>
     * 换行符有三种情形：&quot;<code>\n</code>&quot;、&quot;<code>\r</code>&quot;、&quot;
     * <code>\r\n</code>&quot;。
     * <p/>
     * <pre>
     * chomp(null)          = null
     * chomp("")            = ""
     * chomp("abc \r")      = "abc "
     * chomp("abc\n")       = "abc"
     * chomp("abc\r\n")     = "abc"
     * chomp("abc\r\n\r\n") = "abc\r\n"
     * chomp("abc\n\r")     = "abc\n"
     * chomp("abc\n\rabc")  = "abc\n\rabc"
     * chomp("\r")          = ""
     * chomp("\n")          = ""
     * chomp("\r\n")        = ""
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要处理的字符串
     * @return 不以换行结尾的字符串，如果原始字串为<code>null</code>，则返回<code>null</code>
     */
    public static String chomp(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }

        if (str.length() == 1) {
            char ch = str.charAt(0);

            if (ch == '\r' || ch == '\n') {
                return RawConsts.EMPTY_STRING;
            } else {
                return str;
            }
        }

        int lastIdx = str.length() - 1;
        char last = str.charAt(lastIdx);

        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                lastIdx--;
            }
        } else if (last == '\r') {
        } else {
            lastIdx++;
        }

        return str.substring(0, lastIdx);
    }

    /**
     * 删除最后一个字符。
     * <p>
     * 如果字符串以<code>\r\n</code>结尾，则同时删除它们。
     * <p/>
     * <pre>
     * chop(null)          = null
     * chop("")            = ""
     * chop("abc \r")      = "abc "
     * chop("abc\n")       = "abc"
     * chop("abc\r\n")     = "abc"
     * chop("abc")         = "ab"
     * chop("abc\nabc")    = "abc\nab"
     * chop("a")           = ""
     * chop("\r")          = ""
     * chop("\n")          = ""
     * chop("\r\n")        = ""
     * </pre>
     * <p/>
     * </p>
     *
     * @param str 要处理的字符串
     * @return 删除最后一个字符的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String chop(String str) {
        if (str == null) {
            return null;
        }

        int strLen = str.length();

        if (strLen < 2) {
            return RawConsts.EMPTY_STRING;
        }

        int lastIdx = strLen - 1;
        String ret = str.substring(0, lastIdx);
        char last = str.charAt(lastIdx);

        if (last == '\n') {
            if (ret.charAt(lastIdx - 1) == '\r') {
                return ret.substring(0, lastIdx - 1);
            }
        }

        return ret;
    }

    public static String format(String tpl, Object... args) {
        return MessageFormat.format(tpl, args);
    }

    /**
     * 把驼峰转为下划线，并且全大写,例如:PersonName转为PERSON_NAME
     *
     * @param property property
     * @return String
     */
    public static String camelToUnderline(String property) {
        return camelTo(property,"_");
    }

    /**
     * 把驼峰转为下划线，并且全大写,例如:PersonName转为PERSON_NAME
     *
     * @param property property
     * @return String
     */
    public static String camelTo(String property,String delimiter) {
//        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, str);

//        String[] strs = splitByCharacterTypeCamelCase(str);
//        List<String> strArray = Arrays
//                .stream(strs)
//                .map(s -> s.toUpperCase())
//                .collect(Collectors.toList());
//        return join(strArray,"_");

        if (StringKit.isBlank(property)) return property;

        char[] chars = property.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (char c : chars) {
            if (CharUtils.isAsciiAlphaUpper(c)) {
                sb.append(delimiter + StringUtils.lowerCase(CharUtils.toString(c)));
            } else {
                sb.append(c);
            }
        }
        String ret = sb.toString();

        //处理可能出现的多个下划线
        ret = ret.replaceAll(delimiter+"+", delimiter);
        ret = removeStart(ret, delimiter);
        ret = removeEnd(ret, delimiter);
        ret = ret.toUpperCase();

        return ret;

    }

    /**
     * 把下划线转为驼峰,例如:PERSON_NAME转为PersonName
     *
     * @param str str
     * @return String
     */
    public static String underlineToCamel(String str) {
        return toCamel("_",str);
    }

    /**
     * 把指定分割线转为驼峰,例如:PERSON-NAME转为PersonName
     * @param delimiter 分割符
     * @param str
     * @return
     */
    public static String toCamel(String delimiter,String str) {
//        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
//        List<String> strArray = Arrays
//                .stream(strs)
//                .map(s -> lowerCase(s))
//                .map(s -> capitalize(s))
//                .collect(Collectors.toList());
        if (StringKit.isBlank(str)) return str;

        String[] strs = split(str, delimiter);
        for (int i = 0; i < strs.length; i++) {
            strs[i] = lowerCase(strs[i]);
            if (i > 0) {
                strs[i] = capitalize(strs[i]);
            }
        }
        return join(strs);
    }


    /**
     * 使用变量填充字串模板 ${name}你好，{name:李雷}
     *
     * @param tpl  tpl
     * @param vars vars
     * @return String
     */
    public static String fillTpl(String tpl, Map<String, String> vars) {
        Set<Map.Entry<String, String>> sets = vars.entrySet();
        for (Map.Entry<String, String> entry : sets) {
            String regex = "\\$\\{" + entry.getKey() + "\\}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(tpl);
            tpl = matcher.replaceAll(entry.getValue());
        }
        return tpl;
    }

    public static String uuid() {
        return uuid("");
    }

    public static String uuid(String delimiter) {
        String uuid = UUID.randomUUID().toString();	//获取UUID并转化为String对象
        uuid = uuid.replace("-", delimiter);				//因为UUID本身为32位只是生成时多了“-”，所以将它们去点就可
        return uuid;
    }

    public static String maxLenLimit(String str,int len){
        if(StringKit.isBlank(str))return str;
        if(str.length()>len)return str.substring(0,len);
        else return str;
    }

    /**
     * 中文转unicode字符
     * @param cnStr
     * @return
     */
    public static String chineseToUnicode(final String cnStr) {

        try {
            StringBuffer out = new StringBuffer("");
            //直接获取字符串的unicode二进制
            byte[] bytes = cnStr.getBytes("unicode");
            //然后将其byte转换成对应的16进制表示即可
            for (int i = 0; i < bytes.length - 1; i += 2) {
                out.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for (int j = str.length(); j < 2; j++) {
                    out.append("0");
                }
                String str1 = Integer.toHexString(bytes[i] & 0xff);
                out.append(str1);
                out.append(str);
            }
            return out.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * unicode转中文字符
     * @param unicodeStr
     * @return
     */
    public static String unicodeToChinese(final String unicodeStr) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(unicodeStr);
        String cnStr = unicodeStr;
        char ch;
        while (matcher.find()) {
            //group
            String group = matcher.group(2);
            //ch:'李四'
            ch = (char) Integer.parseInt(group, 16);
            //group1
            String group1 = matcher.group(1);
            cnStr = cnStr.replace(group1, ch + "");
        }
        return cnStr.replace("\\", "").trim();
    }

    private static String HEX_STR =  "0123456789ABCDEF";
    /**
     *
     * @param bytes
     * @return 将二进制转换为十六进制字符输出
     */
    public static String bytesToHex(byte[] bytes){

        String result = "";
        String hex = "";
        for(int i=0;i<bytes.length;i++){
            //字节高4位
            hex = String.valueOf(HEX_STR.charAt((bytes[i]&0xF0)>>4));
            //字节低4位
            hex += String.valueOf(HEX_STR.charAt(bytes[i]&0x0F));
            result +=hex+" ";
        }
        return result;
    }
    /**
     *
     * @param hexString
     * @return 将十六进制转换为字节数组
     */
    public static byte[] hexToBytes(String hexString){
        //hexString的长度对2取整，作为bytes的长度
        int len = hexString.length()/2;
        byte[] bytes = new byte[len];
        byte high = 0;//字节高四位
        byte low = 0;//字节低四位

        for(int i=0;i<len;i++){
            //右移四位得到高位
            high = (byte)((HEX_STR.indexOf(hexString.charAt(2*i)))<<4);
            low = (byte) HEX_STR.indexOf(hexString.charAt(2*i+1));
            bytes[i] = (byte) (high|low);//高地位做或运算
        }
        return bytes;
    }

    /**
     * 判断是否为数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        try {
            new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }
        return true;
    }

}
