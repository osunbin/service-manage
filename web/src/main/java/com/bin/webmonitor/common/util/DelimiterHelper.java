package com.bin.webmonitor.common.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.List;

public class DelimiterHelper {

    public static final String COMMAS = ",";

    public static final String COLON = ";";

    public static final String VERTICAL = "|";

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final Joiner COMMAS_JOINER_LINE = Joiner.on(",\n");


    public static final Splitter COMMAS_SPLITTER = Splitter.on(COMMAS).omitEmptyStrings()
            .trimResults();

    public static final Splitter COMMAS_ALLOW_EMPTY = Splitter.on(COMMAS).trimResults();

    public static final Joiner COMMAS_JOINER = Joiner.on(COMMAS).skipNulls();

    public static final Joiner COMMAS_SLASH = Joiner.on("/");

    public static final Joiner COMMAS_BR = Joiner.on(",<br />");


    public static final Joiner COLON_JOINER = Joiner.on(COLON).skipNulls();

    public static final Splitter VERTICAL_SPLITTER = Splitter.on(VERTICAL).omitEmptyStrings()
            .trimResults();

    public static final Joiner VERTICAL_JOINER = Joiner.on(VERTICAL).skipNulls();

    public static final String EMPTY_STRING = "";

    private static final String NULL = "null";

    private static final String UNDEFINED = "undefined";

    /**
     * is null or empty.
     */
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        String checkString = str.trim().toLowerCase();
        return EMPTY_STRING.equals(checkString) || NULL.equals(checkString)
                || UNDEFINED.equals(checkString);
    }

    /**
     * not null or empty.
     */
    public static boolean notEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * default if str empty.
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        if (isEmpty(str)) {
            return defaultStr;
        }
        return str;
    }

    /**
     * default if str empty.
     */
    public static Integer defaultIntIfEmpty(String str, String defaultStr) {
        String o1Quantity = defaultIfEmpty(str, defaultStr);
        return Integer.parseInt(o1Quantity);
    }

    /**
     * default if integer empty.
     */
    public static Integer defaultIntIfEmpty(Integer target, Integer defaultInt) {
        if (null == target) {
            return defaultInt;
        }
        return target;
    }

    public static List<String> splitToList(CharSequence sequence) {
       return COMMAS_SPLITTER.splitToList(sequence);
    }

    public static final String join(Iterable<? extends Object> parts) {

        return COMMAS_JOINER.join(parts);
    }

    public static final String joinLine(Iterable<? extends Object> parts) {
        return COMMAS_JOINER_LINE.join(parts);
    }


    public static Iterable<String> split(final CharSequence sequence) {
      return   COMMAS_ALLOW_EMPTY.split(sequence);
    }

}
