package fr.tsadeo.app.dsntotree.util;

public class StringUtils {

    public static String concat(Object... items) {

        if (items == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Object item : items) {
            sb.append(item == null ? "" : item.toString());
        }
        return sb.toString();

    }

    public static String concat(String... items) {
        if (items == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (String item : items) {
            sb.append(item == null ? "" : item).append(" ");
        }
        return sb.toString();
    }

}
