package com.xcoder.utilities.common;

/**
 * Html util
 *
 * @author Chuck Lee
 */
public class HtmlUtensil {
    /**
     * Html Unescape 4 Json
     *
     * @param html like &quot;
     * @return String
     */
    public static String htmlUnescape4Json(String html) {
        return html.replaceAll("&quot;", "\\\\\"");
    }
}
