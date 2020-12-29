package com.zackmurry.cardtown.util;

public class HtmlUtils {

    public static String removeScriptTags(String html) {
        if (html == null) {
            return null;
        }
        return html.replace("<script>", "&lt;script&gt;").replace("</script>", "&lt;/script&gt;");
    }

}
