package com.midoconline.app.Util;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * Created by Prashant on 11/8/15.
 */
public class StringUtils {

    /**
     * Check whether a string is not NULL, empty or "NULL", "null", "Null"
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        boolean flag = true;
        if (str != null) {
            str = str.trim();
            if (str.length() == 0 || str.equalsIgnoreCase("null")) {
                flag = false;
            }
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * Get column value for the passed cursor and column index.
     * @param cursor
     * @param columnIndex
     * @return
     */
    public static String getString(Cursor cursor, String columnIndex) {
        String str = cursor.getString(cursor.getColumnIndex(columnIndex));
        return isNotEmpty(str) ? str : "";
    }

    /**
     * 1. Get string value from cursor for the column index.<br/>
     * 2. Check whether the string is null or empty.<br/>
     * 3. Add prefix.<br/>
     * 4. Add suffix.<br/>
     *
     * @param cursor
     * @param columnIndex
     * @param prefix
     * @param suffix
     * @return
     */
    public static String getString(Cursor cursor, String columnIndex, String prefix, String suffix) {
        String str = cursor.getString(cursor.getColumnIndex(columnIndex));
        if (isNotEmpty(str) ) {
            if (!TextUtils.isEmpty(prefix)) {
                str = prefix + str;
            }
            if (! TextUtils.isEmpty(suffix)) {
                str = str + suffix;
            }
        } else {
            str = "";
        }
        return str;
    }

    /**
     *  Removes image tags from the html contents
     * @param htmlString
     * @return
     */
    public static String removeImageTag(String htmlString) {
        try {
            int index_st = 0, index_end = -1;
            String imgString = "";
            String match = "<img";

            while (index_st != -1) {
                index_st = htmlString.indexOf(match);

                if (index_st >= 0 && index_st < htmlString.length()) {
                    index_end = index_st
                            + htmlString.substring(index_st).indexOf(">") + 1;
                    imgString = htmlString.substring(index_st, index_end);
                    htmlString = htmlString.replace(imgString, "");
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return htmlString;
    }

}
