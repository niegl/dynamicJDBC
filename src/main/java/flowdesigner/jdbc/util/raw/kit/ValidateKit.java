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

import flowdesigner.jdbc.util.raw.lang.RawException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;


public abstract class ValidateKit {

    private static final String DEFAULT_NOT_NAN_EX_MESSAGE = "The validated value is not a number";
    private static final String DEFAULT_FINITE_EX_MESSAGE = "The value is invalid: {0,number,#.#}";
    private static final String DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE = "The value {0} is not in the specified exclusive range of {1} to {2}";
    private static final String DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE = "The value {0} is not in the specified inclusive range of {1} to {2}";
    private static final String DEFAULT_MATCHES_PATTERN_EX = "The string {0} does not match the pattern {1}";
    private static final String DEFAULT_IS_NULL_EX_MESSAGE = "The validated object is null";
    private static final String DEFAULT_IS_TRUE_EX_MESSAGE = "The validated expression is false";
    private static final String DEFAULT_NO_NULL_ELEMENTS_ARRAY_EX_MESSAGE = "The validated array contains null element at index: {0,number}";
    private static final String DEFAULT_NO_NULL_ELEMENTS_COLLECTION_EX_MESSAGE = "The validated collection contains null element at index: {0,number}";
    private static final String DEFAULT_NOT_BLANK_EX_MESSAGE = "The validated character sequence is blank";
    private static final String DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE = "The validated array is empty";
    private static final String DEFAULT_NOT_EMPTY_CHAR_SEQUENCE_EX_MESSAGE = "The validated character sequence is empty";
    private static final String DEFAULT_NOT_EMPTY_COLLECTION_EX_MESSAGE = "The validated collection is empty";
    private static final String DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE = "The validated map is empty";
    private static final String DEFAULT_VALID_INDEX_ARRAY_EX_MESSAGE = "The validated array index is invalid: {0,number}";
    private static final String DEFAULT_VALID_INDEX_CHAR_SEQUENCE_EX_MESSAGE = "The validated character sequence index is invalid: {0,number}";
    private static final String DEFAULT_VALID_INDEX_COLLECTION_EX_MESSAGE = "The validated collection index is invalid: {0,number}";
    private static final String DEFAULT_VALID_STATE_EX_MESSAGE = "The validated state is false";
    private static final String DEFAULT_IS_ASSIGNABLE_EX_MESSAGE = "Cannot assign a {0} to a {1}";
    private static final String DEFAULT_IS_INSTANCE_OF_EX_MESSAGE = "Expected type: {0}, actual: {1}";

    public static void isTrue(final boolean expression, final String message, final long value) {
        if (!expression) {
            throw new IllegalArgumentException(format(message, value));
        }
    }

    public static void isTrue(final boolean expression, final String message, final double value) {
        if (!expression) {
            throw new IllegalArgumentException(format(message, value));
        }
    }


    public static void isTrue(final boolean expression, final String message, final Object... values) {
        if (!expression) {
            throw new IllegalArgumentException(format(message, values));
        }
    }


    public static void isTrue(final boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException(DEFAULT_IS_TRUE_EX_MESSAGE);
        }
    }

    public static void isFalse(final boolean expression, final String message, final Object... values) {
        if (expression) {
            throw new IllegalArgumentException(format(message, values));
        }
    }

    public static void isFalse(final boolean expression) {
        if (expression) {
            throw new IllegalArgumentException(DEFAULT_IS_TRUE_EX_MESSAGE);
        }
    }


    public static <T> T notNull(final T object) {
        return notNull(object, DEFAULT_IS_NULL_EX_MESSAGE);
    }


    public static <T> T notNull(final T object, final String message, final Object... values) {
        if (object == null) {
            throw new NullPointerException(format(message, values));
        }
        return object;
    }


    public static <T> T[] notEmpty(final T[] array, final String message, final Object... values) {
        if (array == null) {
            throw new NullPointerException(format(message, values));
        }
        if (array.length == 0) {
            throw new IllegalArgumentException(format(message, values));
        }
        return array;
    }


    public static <T> T[] notEmpty(final T[] array) {
        return notEmpty(array, DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE);
    }


    public static <T extends Collection<?>> T notEmpty(final T collection, final String message, final Object... values) {
        if (collection == null) {
            throw new NullPointerException(format(message, values));
        }
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(format(message, values));
        }
        return collection;
    }


    public static <T extends Collection<?>> T notEmpty(final T collection) {
        return notEmpty(collection, DEFAULT_NOT_EMPTY_COLLECTION_EX_MESSAGE);
    }


    public static <T extends Map<?, ?>> T notEmpty(final T map, final String message, final Object... values) {
        if (map == null) {
            throw new NullPointerException(format(message, values));
        }
        if (map.isEmpty()) {
            throw new IllegalArgumentException(format(message, values));
        }
        return map;
    }


    public static <T extends Map<?, ?>> T notEmpty(final T map) {
        return notEmpty(map, DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE);
    }


    public static <T extends CharSequence> T notEmpty(final T chars, final String message, final Object... values) {
        if (chars == null) {
            throw new NullPointerException(format(message, values));
        }
        if (chars.length() == 0) {
            throw new IllegalArgumentException(format(message, values));
        }
        return chars;
    }


    public static <T extends CharSequence> T notEmpty(final T chars) {
        return notEmpty(chars, DEFAULT_NOT_EMPTY_CHAR_SEQUENCE_EX_MESSAGE);
    }


    public static <T extends CharSequence> T notBlank(final T chars, final String message, final Object... values) {
        if (chars == null) {
            throw new NullPointerException(format(message, values));
        }
        if (StringUtils.isBlank(chars)) {
            throw new IllegalArgumentException(format(message, values));
        }
        return chars;
    }


    public static <T extends CharSequence> T notBlank(final T chars) {
        return notBlank(chars, DEFAULT_NOT_BLANK_EX_MESSAGE);
    }


    public static <T> T[] noNullElements(final T[] array, final String message, final Object... values) {
        Validate.notNull(array);
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                final Object[] values2 = ArrayUtils.add(values, Integer.valueOf(i));
                throw new IllegalArgumentException(format(message, values2));
            }
        }
        return array;
    }


    public static <T> T[] noNullElements(final T[] array) {
        return noNullElements(array, DEFAULT_NO_NULL_ELEMENTS_ARRAY_EX_MESSAGE);
    }


    public static <T extends Iterable<?>> T noNullElements(final T iterable, final String message, final Object... values) {
        Validate.notNull(iterable);
        int i = 0;
        for (final Iterator<?> it = iterable.iterator(); it.hasNext(); i++) {
            if (it.next() == null) {
                final Object[] values2 = ArrayUtils.addAll(values, i);
                throw new IllegalArgumentException(format(message, values2));
            }
        }
        return iterable;
    }


    public static <T extends Iterable<?>> T noNullElements(final T iterable) {
        return noNullElements(iterable, DEFAULT_NO_NULL_ELEMENTS_COLLECTION_EX_MESSAGE);
    }


    public static <T> T[] validIndex(final T[] array, final int index, final String message, final Object... values) {
        Validate.notNull(array);
        if (index < 0 || index >= array.length) {
            throw new IndexOutOfBoundsException(format(message, values));
        }
        return array;
    }


    public static <T> T[] validIndex(final T[] array, final int index) {
        return validIndex(array, index, DEFAULT_VALID_INDEX_ARRAY_EX_MESSAGE, index);
    }


    public static <T extends Collection<?>> T validIndex(final T collection, final int index, final String message, final Object... values) {
        Validate.notNull(collection);
        if (index < 0 || index >= collection.size()) {
            throw new IndexOutOfBoundsException(format(message, values));
        }
        return collection;
    }


    public static <T extends Collection<?>> T validIndex(final T collection, final int index) {
        return validIndex(collection, index, DEFAULT_VALID_INDEX_COLLECTION_EX_MESSAGE, index);
    }


    public static <T extends CharSequence> T validIndex(final T chars, final int index, final String message, final Object... values) {
        Validate.notNull(chars);
        if (index < 0 || index >= chars.length()) {
            throw new IndexOutOfBoundsException(format(message, values));
        }
        return chars;
    }


    public static <T extends CharSequence> T validIndex(final T chars, final int index) {
        return validIndex(chars, index, DEFAULT_VALID_INDEX_CHAR_SEQUENCE_EX_MESSAGE, index);
    }


    public static void validState(final boolean expression) {
        if (!expression) {
            throw new IllegalStateException(DEFAULT_VALID_STATE_EX_MESSAGE);
        }
    }


    public static void validState(final boolean expression, final String message, final Object... values) {
        if (!expression) {
            throw new IllegalStateException(format(message, values));
        }
    }


    public static void matchesPattern(final CharSequence input, final String pattern) {
        if (!Pattern.matches(pattern, input)) {
            throw new IllegalArgumentException(format(DEFAULT_MATCHES_PATTERN_EX, input, pattern));
        }
    }


    public static void matchesPattern(final CharSequence input, final String pattern, final String message, final Object... values) {
        if (!Pattern.matches(pattern, input)) {
            throw new IllegalArgumentException(format(message, values));
        }
    }


    public static void notNaN(final double value) {
        notNaN(value, DEFAULT_NOT_NAN_EX_MESSAGE);
    }


    public static void notNaN(final double value, final String message, final Object... values) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException(format(message, values));
        }
    }


    public static void finite(final double value) {
        finite(value, DEFAULT_FINITE_EX_MESSAGE, value);
    }


    public static void finite(final double value, final String message, final Object... values) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(format(message, values));
        }
    }


    public static <T> void inclusiveBetween(final T start, final T end, final Comparable<T> value) {
        if (value.compareTo(start) < 0 || value.compareTo(end) > 0) {
            throw new IllegalArgumentException(format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }


    public static <T> void inclusiveBetween(final T start, final T end, final Comparable<T> value, final String message, final Object... values) {
        if (value.compareTo(start) < 0 || value.compareTo(end) > 0) {
            throw new IllegalArgumentException(format(message, values));
        }
    }


    @SuppressWarnings("boxing")
    public static void inclusiveBetween(final long start, final long end, final long value) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }


    public static void inclusiveBetween(final long start, final long end, final long value, final String message) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(format(message));
        }
    }


    @SuppressWarnings("boxing")
    public static void inclusiveBetween(final double start, final double end, final double value) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }


    public static void inclusiveBetween(final double start, final double end, final double value, final String message) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(format(message));
        }
    }


    public static <T> void exclusiveBetween(final T start, final T end, final Comparable<T> value) {
        if (value.compareTo(start) <= 0 || value.compareTo(end) >= 0) {
            throw new IllegalArgumentException(format(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }


    public static <T> void exclusiveBetween(final T start, final T end, final Comparable<T> value, final String message, final Object... values) {
        if (value.compareTo(start) <= 0 || value.compareTo(end) >= 0) {
            throw new IllegalArgumentException(format(message, values));
        }
    }


    @SuppressWarnings("boxing")
    public static void exclusiveBetween(final long start, final long end, final long value) {
        if (value <= start || value >= end) {
            throw new IllegalArgumentException(format(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }


    public static void exclusiveBetween(final long start, final long end, final long value, final String message) {
        if (value <= start || value >= end) {
            throw new IllegalArgumentException(format(message));
        }
    }


    @SuppressWarnings("boxing")
    public static void exclusiveBetween(final double start, final double end, final double value) {
        if (value <= start || value >= end) {
            throw new IllegalArgumentException(format(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }


    public static void exclusiveBetween(final double start, final double end, final double value, final String message) {
        if (value <= start || value >= end) {
            throw new IllegalArgumentException(format(message));
        }
    }

    public static void isInstanceOf(final Class<?> type, final Object obj) {
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(format(DEFAULT_IS_INSTANCE_OF_EX_MESSAGE, type.getName(),
                    obj == null ? "null" : obj.getClass().getName()));
        }
    }


    public static void isInstanceOf(final Class<?> type, final Object obj, final String message, final Object... values) {
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(format(message, values));
        }
    }


    public static void isAssignableFrom(final Class<?> superType, final Class<?> type) {
        if (!superType.isAssignableFrom(type)) {
            throw new IllegalArgumentException(format(DEFAULT_IS_ASSIGNABLE_EX_MESSAGE, type == null ? "null" : type.getName(),
                    superType.getName()));
        }
    }


    public static void isAssignableFrom(final Class<?> superType, final Class<?> type, final String message, final Object... values) {
        if (!superType.isAssignableFrom(type)) {
            throw new IllegalArgumentException(format(message, values));
        }
    }

    public static void isNotExist(Object object,final String message, final Object... values){
        if(object==null){
            throw new RawException(message,values);
        }
    }

    private static String format(String pattern,Object... args){
        return MessageFormat.format(pattern,args);
    }
    private static String format(String pattern){
        return pattern;
    }

}
