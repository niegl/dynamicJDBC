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
package flowdesigner.util.sql;

import org.apache.commons.lang3.tuple.Pair;


/**
 * @desc : 连接以及解析处理工具类
 */
public abstract class ConnParseKit {

    /**
     * 从注释文字中解析表的业务名（中文名）
     *
     * @param remarks
     * @return Left为字段名，Right为字段注释
     */
    public static Pair<String,String> parseNameAndComment(String remarks) {
        String[] delimters = new String[]{" ", ",", "，", ";", "\t", "\n"};

        //有括号的字段，不解析其中的注释了
        if (remarks.indexOf("(") > 0
                //&& !remarks.contains(")")
                && remarks.indexOf("(") < remarks.indexOf(")")
        ) {
            return Pair.of("", remarks);
        }


        Pair<String, String> pair = null;
        for (int i = 0; i < delimters.length; i++) {
            String delimter = delimters[i];
            int idxDelimter = remarks.indexOf(delimter);
            if (idxDelimter > 0) {
                String left = remarks.substring(0, idxDelimter);
                String right = remarks.substring(idxDelimter + 1);
                pair = Pair.of(left, right);
            }
            if (pair != null) {
                break;
            }
        }
        if (pair == null) {
            pair = Pair.of("", remarks);
        }
        return pair;
    }
}
