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
package flowdesigner.jdbc.util.raw.holder;

import java.util.Date;

/**
 * @desc : 日期保持器，把当前系统时间绑定到线程上
 */
public abstract class DateHolder {
    private static final ThreadLocal<Date> dateHolder = new ThreadLocal<Date>();
    public static Date getDate(){
        Date retDate = dateHolder.get();
        if(retDate == null) retDate = new Date();
        return retDate;
    }
    public static void setDate(Date date){
        dateHolder.set(date);
    }

    public static void clear() {
        dateHolder.remove();
    }
}
