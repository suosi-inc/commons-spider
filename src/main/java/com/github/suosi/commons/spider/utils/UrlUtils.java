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

    /**
     * 静态纯数字格式，匹配URL带有日期的 /0521/123.html
     */
    private static Pattern CONTENT_STATIC_DATE_PATTERN = Pattern.compile(
            "/([\\d]*)/([\\d]*)\\.(html|shtml|htm|shtm|jhtml)$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 静态纯数字格式，过滤长度小于4的（小于4可能是列表页的分页，并且一个网站应该有>1000篇内容才正常）
     */
    private static Pattern CONTENT_STATIC_PATTERN = Pattern.compile(
            "^([\\d]*)\\.(html|shtml|htm|shtm|jhtml)$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 静态格式，一般是 hashid.html 结尾，如 /RLNwb07lRm4nOX.html
     */
    private static Pattern CONTENT_STATIC_WORD_PATTERN = Pattern.compile(
            "[\\w\\d\\-]*\\d{1,}[\\w\\d\\-]*\\.(html|shtml|htm|shtm)$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 动态格式后缀，配合有限字典使用
     */
    private static Pattern CONTENT_DYNAMIC_PATTERN = Pattern.compile(
            "[\\w\\d\\-]*\\.(php|jsp|asp|aspx|do)$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 动态格式字典，包含有限字典的结尾，同时配合query参数。如 show.php?id=xxx
     */
    private static Pattern CONTENT_DICT_PATTERN = Pattern.compile(
            "(detail|article|view|show)$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 参数列表，限定>3个，为了过滤可能的列表页
     */
    private static Pattern CONTENT_QUERY_PATTERN = Pattern.compile(
            "id=\\d{3,}",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 伪静态格式，如 /p/1234，限定长度
     */
    private static Pattern CONTENT_NONSTATIC_PATTERN = Pattern.compile(
            "^[\\d_\\-]{4,}$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 伪静态关键词，可能是：/article-12345
     */
    private static Pattern CONTENT_NONSTATIC_KEYWORD_PATTERN = Pattern.compile(
            "[\\w\\d\\-]*(detail|article)[\\w\\d\\-]*\\d{4,}$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 英文网站，可能是多个单词连接 hi-china-hello-world
     */
    private static Pattern CONTENT_NONSTATIC_ENGLISH_PATTERN = Pattern.compile(
            "[\\w\\d]*\\-[\\w\\d]*\\-[\\w\\d]*\\-[\\w\\d]*",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 伪静态纯 Hash 格式，限定长度如 /md5
     */
    private static Pattern CONTENT_HASH_PATTERN = Pattern.compile(
            "[\\w\\d\\-]*\\d+[\\w\\d]",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * URL关键词过滤
     */
    private static Pattern ARTICLE_KEYWORD_FILTER_PATTERN = Pattern.compile(
            "^(video(s?)|movie(s?)|photo(s?)|photoview(s?)|pic(s?)|member(s?)|channel(s?)|sublist(s?)|list(s?)|category(s?)|user(s?)|tag(s?)|topic(s?)|upload(s?)|footer(s?)|header(s?)|login(s?)|register(s?)|logout(s?)|u)$",
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
                && !StringUtils.startsWithIgnoreCase(url, "sms:")
                && !StringUtils.startsWithIgnoreCase(url, "#")
                && !StringUtils.containsAny(url, "{", "}", "[", "]", "@", "$", "<", ">");
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
                System.out.println(e.getLocalizedMessage());
            }
        }
        return null;
    }

    /**
     * 尝试验证为内容页URL
     *
     * @param url          URL
     * @param strictDomain 如果提供域名，则过滤域名后缀以外的链接
     * @return
     */
    public static boolean guessContentUrl(String url, String strictDomain) {
        URL parseUrl = parse(url);
        if (parseUrl != null) {
            String host = parseUrl.getHost();
            String fullPath = parseUrl.getPath();
            String query = parseUrl.getQuery();

            // 限定域名
            if (StringUtils.isNotBlank(strictDomain)) {
                // 兼容 www.gov.cn 这种情况
                if (!strictDomain.equals(host) && !StringUtils.endsWithIgnoreCase(host, "." + strictDomain)) {
                    return false;
                }
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
                if (CONTENT_STATIC_DATE_PATTERN.matcher(parseUrl.toString()).find()) {
                    return true;
                } else {
                    String id = matcher.group(1);
                    if (id.length() < 4) {
                        return false;
                    }
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
                if (path.length() >= 10 && !StringUtils.contains(path, ".")) {
                    return true;
                }
            // 英文网站URL
            } else if (CONTENT_NONSTATIC_ENGLISH_PATTERN.matcher(path).find()) {
                return true;
            // 空 path，如：/?s=thread&tid=546595&bid=1
            } else if (StringUtils.isBlank(path) && StringUtils.isNotBlank(query)) {
                if (CONTENT_QUERY_PATTERN.matcher(query).find()) {
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
                String host = parseUrl.getHost();
                String fullPath = parseUrl.getPath();
                String path = StringUtils.removeEnd(fullPath, "/");

                if (StringUtils.isBlank(path)) {
                    return false;
                }

                if (strictDomain != null) {
                    if (!strictDomain.equals(host) && !StringUtils.endsWithIgnoreCase(host, "." + strictDomain)) {
                        return false;
                    }
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
