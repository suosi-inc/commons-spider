package com.github.suosi.commons.spider.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * @author niuchaoqun
 */
public class UrlUtils {
    private static Pattern CONTENT_STATIC_PATTERN = Pattern.compile(
            "[\\w\\d\\-]*\\d{1,}[\\w\\d\\-]*\\.(html|shtml|htm|shtm)$",
            Pattern.CASE_INSENSITIVE
    );

    private static Pattern CONTENT_DYNAMIC_PATTERN = Pattern.compile(
            "[\\w\\d\\-]*\\.(php|jsp|asp|aspx|do|html|shtml|htm)$",
            Pattern.CASE_INSENSITIVE
    );

    private static Pattern CONTENT_NONSTATIC_PATTERN = Pattern.compile(
            "^[\\d_\\-]{4,}$",
            Pattern.CASE_INSENSITIVE
    );

    private static Pattern CONTENT_DICT_PATTERN = Pattern.compile(
            "(detail|article|view|show)$",
            Pattern.CASE_INSENSITIVE
    );

    private static Pattern CONTENT_NONSTATIC_KEYWORD_PATTERN = Pattern.compile(
            "[\\w\\d\\-]*(detail|article)[\\w\\d\\-]*\\d{4,}$",
            Pattern.CASE_INSENSITIVE
    );

    private static Pattern CONTENT_HASH_PATTERN = Pattern.compile(
            "[\\w\\d-]*\\d+[\\w\\d\\-]",
            Pattern.CASE_INSENSITIVE
    );

    private static Pattern CONTENT_QUERY_PATTERN = Pattern.compile(
            "id=\\d{4,}",
            Pattern.CASE_INSENSITIVE
    );

    private static Pattern ARTICLE_KEYWORD_PATTERN = Pattern.compile(
            "(video|photo|pic|member|channel|list|category|user|tag)",
            Pattern.CASE_INSENSITIVE
    );

    public static boolean verifyUrl(String url) {
        return StringUtils.isNotBlank(url)
                && !StringUtils.startsWithIgnoreCase(url, "javascript:")
                && !StringUtils.startsWithIgnoreCase(url, "ftp:")
                && !StringUtils.startsWithIgnoreCase(url, "tel:")
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

    /**
     * 判断是否为内容页URL
     *
     * @param url          URL
     * @param strictDomain 域名过滤
     * @return
     */
    public static boolean guessContentUrl(String url, String strictDomain) {
        URL parseUrl = parse(url);
        String host = parseUrl.getHost();
        String fullPath = parseUrl.getPath();
        String query = parseUrl.getQuery();

        // 限定域名
        if (StringUtils.isNotBlank(strictDomain) && !StringUtils.equals(host, strictDomain)) {
            return false;
        }

        String path = "";
        if (fullPath != null) {
            fullPath = StringUtils.removeStart(fullPath, "/");
            path = fullPath;
            String[] splitPaths = StringUtils.split(fullPath, "/");
            if (splitPaths.length > 0) {
                path = splitPaths[splitPaths.length - 1];
            }
        }


        // 静态
        if (CONTENT_STATIC_PATTERN.matcher(path).find()) {
            if (StringUtils.startsWithIgnoreCase(path, "forum-")
                    || StringUtils.startsWithIgnoreCase(path, "list")) {
                return false;
            }
            return true;
            // 动态
        } else if (CONTENT_DYNAMIC_PATTERN.matcher(path).find() || CONTENT_DICT_PATTERN.matcher(path).find()) {
            if (StringUtils.isNotBlank(query) && CONTENT_QUERY_PATTERN.matcher(query).find()) {
                return true;
            }
            // 伪静态数字
        } else if (CONTENT_NONSTATIC_PATTERN.matcher(path).find()) {
            return true;
            // 伪静态关键词
        } else if (CONTENT_NONSTATIC_KEYWORD_PATTERN.matcher(path).find()) {
            return true;
            // HASH
        } else if (CONTENT_HASH_PATTERN.matcher(path).find()) {
            if (path.length() >= 10) {
                return true;
            }
        }

        return false;
    }

    public static boolean guessArticleUrl(String url, String strictDomain) {
        if (guessContentUrl(url, strictDomain)) {
            URL parseUrl = parse(url);
            String fullPath = parseUrl.getPath();
            String[] paths = StringUtils.split(fullPath, "/");
            for (String path : paths) {
                if (ARTICLE_KEYWORD_PATTERN.matcher(path).find()) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}
