package com.zackmurry.cardtown.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class HtmlUtils {

    private static final Whitelist WHITELIST = Whitelist.basic()
            .addTags("h1", "h2", "h3", "h4", "h5", "h6", "div")
            .addAttributes("h1", "style")
            .addAttributes("h2", "style")
            .addAttributes("h3", "style")
            .addAttributes("h4", "style")
            .addAttributes("h5", "style")
            .addAttributes("h6", "style")
            .addAttributes("p", "style")
            .addAttributes("span", "style");

    /**
     * takes a piece of html and removes potentially harmful tags
     * @param html html to sanitize
     * @return html with the bad parts cut out
     */
    public static String sanitizeHtml(String html) {
        String withSpaces = Jsoup.clean(html, WHITELIST);

        // removing the styling that Jsoup does
        // todo this might also affect formatting on cards so i defo want to check that out
        return withSpaces.replaceAll("\\n\\s*", "");
    }

}
