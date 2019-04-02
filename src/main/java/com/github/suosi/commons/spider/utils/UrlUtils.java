package com.github.suosi.commons.spider.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author niuchaoqun
 */
public class UrlUtils {
    public static boolean verifyUrl(String url) {
        return StringUtils.isNotBlank(url)
                && !StringUtils.startsWithIgnoreCase(url, "javascript:")
                && !StringUtils.startsWithIgnoreCase(url, "mailto:")
                && !StringUtils.startsWithIgnoreCase(url, "#");
    }

    public static URL parse(String url) {
        if (verifyUrl(url)) {
            try {
                return new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();

            }
        }
        return null;
    }


    public static boolean guessListUrl(String url) {
        return true;
    }

    public static boolean guessArticleUrl(String url) {
        URL parseUrl = parse(url);
        String fullPath = parseUrl.getPath();

        String path = "";
        if (fullPath != null) {
            fullPath = StringUtils.removeStart(fullPath, "/");
            path = fullPath;
            String[] splitPaths = StringUtils.split(fullPath, "/");
            if (splitPaths.length > 0) {
                path = splitPaths[splitPaths.length - 1];
            }
        }

        if (StringUtils.endsWithIgnoreCase(path, ".html") || StringUtils.endsWithIgnoreCase(path, ".htm") || StringUtils.endsWithIgnoreCase(path, ".shtml")) {

        } else {

        }

        System.out.println(path);

        return true;
    }


}
