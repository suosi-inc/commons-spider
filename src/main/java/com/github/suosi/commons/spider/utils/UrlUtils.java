package com.github.suosi.commons.spider.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author niuchaoqun
 */
public class UrlUtils {
    private static Pattern URL_VERIFY_PATTERN = Pattern.compile(
            "^(http|https):",
            Pattern.CASE_INSENSITIVE
    );

    private static Pattern CONTENT_STATIC_PATTERN = Pattern.compile(
            "^([\\d]*)\\.(html|shtml|htm|shtm)$",
            Pattern.CASE_INSENSITIVE
    );

    private static Pattern CONTENT_STATIC_WORD_PATTERN = Pattern.compile(
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
            "(detail|article|view|show|s)$",
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

    private static Pattern ARTICLE_KEYWORD_FILTER_PATTERN = Pattern.compile(
            "(video|movie|photo|pic|member|channel|list|category|user|tag|topic|upload)",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 验证 URL 链接
     *
     * @param url
     * @return
     */
    public static boolean verifyUrl(String url) {
        if (StringUtils.isNotBlank(url)) {
            return URL_VERIFY_PATTERN.matcher(url).find();
        }
        return false;
    }

    /**
     * 过滤垃圾链接
     *
     * @param url
     * @return
     */
    public static boolean filterUrl(String url) {
        return StringUtils.isNotBlank(url)
                && !StringUtils.startsWithIgnoreCase(url, "java")
                && !StringUtils.startsWithIgnoreCase(url, "tencent")
                && !StringUtils.startsWithIgnoreCase(url, "ftp:")
                && !StringUtils.startsWithIgnoreCase(url, "tel:")
                && !StringUtils.startsWithIgnoreCase(url, "mailto:")
                && !StringUtils.startsWithIgnoreCase(url, "#");
    }

    /**
     * URL 解析
     *
     * @param url
     * @return
     */
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
     * 尝试验证为内容页URL
     *
     * @param url          URL
     * @param strictDomain 如果提供域名，则过滤域名以外的链接
     * @return
     */
    public static boolean guessContentUrl(String url, String strictDomain) {
        URL parseUrl = parse(url);
        if (parseUrl != null) {
            String host = parseUrl.getHost();
            String fullPath = parseUrl.getPath();
            String query = parseUrl.getQuery();

            // 限定域名
            if (StringUtils.isNotBlank(strictDomain) && !StringUtils.equals(host, strictDomain)) {
                return false;
            }

            // 获取 last path
            String path = "";
            if (fullPath != null) {
                fullPath = StringUtils.removeStart(fullPath, "/");
                path = fullPath;
                String[] splitPaths = StringUtils.split(fullPath, "/");
                if (splitPaths.length > 0) {
                    path = splitPaths[splitPaths.length - 1];
                }
            }

            // 静态纯数字
            Matcher matcher = CONTENT_STATIC_PATTERN.matcher(path);
            if (matcher.find()) {
                String id = matcher.group(1);
                if (id.length() < 4) {
                    return false;
                }
                return true;
            // 静态关键词
            } else if (CONTENT_STATIC_WORD_PATTERN.matcher(path).find()) {
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
        }

        return false;
    }

    /**
     * 尝试验证为文章 URL，采用过滤法则探测链接路径
     *
     * @param url
     * @param strictDomain
     * @return
     */
    public static boolean guessArticleUrl(String url, String strictDomain) {
        if (guessContentUrl(url, strictDomain)) {
            URL parseUrl = parse(url);
            if (parseUrl != null) {
                String fullPath = parseUrl.getPath();
                String[] paths = StringUtils.split(fullPath, "/");

                for (String path : paths) {
                    if (ARTICLE_KEYWORD_FILTER_PATTERN.matcher(path).find()) {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }


    /**
     * 尝试验证列表页 URL
     *
     * @param url
     * @param strictDomain
     * @return
     */
    public static boolean guessListUrl(String url, String strictDomain) {
        if (!guessContentUrl(url, strictDomain)) {
            URL parseUrl = parse(url);
            if (parseUrl != null) {
                String fullPath = parseUrl.getPath();
                String path = StringUtils.removeEnd(fullPath, "/");
                if (StringUtils.isBlank(path)) {
                    return false;
                }
                return true;
            }
        }

        return false;
    }

    /**
     * 根据链接，统计文章 URL 数量
     *
     * @param links
     * @param strictDomain
     * @return
     */
    public static int countArticleUrls(Set<String> links, String strictDomain) {
        int count = 0;
        if (links != null && !links.isEmpty()) {
            for (String link : links) {
                if (guessArticleUrl(link, strictDomain)) {
                    count++;
                }
            }
        }

        return count;
    }
}
