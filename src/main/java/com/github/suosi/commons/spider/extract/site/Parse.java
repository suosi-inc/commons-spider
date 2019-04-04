package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.utils.DomainUtils;
import com.github.suosi.commons.spider.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parse {
    private static final Pattern ICP_PATTERN = Pattern.compile(
            "(京|津|冀|晋|蒙|辽|吉|黑|沪|苏|浙|皖|闽|赣|鲁|豫|鄂|湘|粤|桂|琼|川|蜀|贵|黔|云|滇|渝|藏|陇|甘|陕|秦|青|宁|新)ICP(备|证)(.*)号",
            Pattern.CASE_INSENSITIVE
    );

    private static final String HTTP_PROTOCOL = "http";

    private static final String HTTPS_PROTOCOL = "https";

    /**
     * 获取标题
     *
     * @param document
     * @return
     */
    public static String parseTitle(Document document) {
        String title = StringUtils.trimToEmpty(document.title());
        if (title.length() > 255) {
            title = StringUtils.substring(title, 0, 255);
        }

        return title;
    }

    /**
     * 获取关键词
     *
     * @param document
     * @return
     */
    public static String parseKeywords(Document document) {
        String keywords = document.select("meta[name=keywords]").attr("content");
        return StringUtils.trimToEmpty(keywords);
    }

    /**
     * 获取摘要
     *
     * @param document
     * @return
     */
    public static String parseDescription(Document document) {
        String description = document.select("meta[name=description]").attr("content");
        return StringUtils.trimToEmpty(description);
    }

    /**
     * 尝试解析处干净一点的Title
     *
     * @param title
     * @return
     */
    public static String parseCleanTitle(String title) {
        String tempTitle = title;
        tempTitle = StringUtils.removeStart(tempTitle, "【");
        if (StringUtils.startsWithIgnoreCase(tempTitle, "首页")) {
            tempTitle = StringUtils.removeAll(tempTitle, "首页( |\\||-|_)*");
        }
        String[] split = tempTitle.split(" |\\||-|_|◎|,|，|—|（|：|－|·|\\(|：|｜|【|】");
        String cleanTitle = split.length > 0 ? split[0] : tempTitle;
        cleanTitle = StringUtils.removeEnd(cleanTitle, "首页");
        cleanTitle = StringUtils.removeEnd(cleanTitle, "】");

        return cleanTitle;
    }

    /**
     * 尝试解析 ICP 信息
     *
     * @param html
     * @return
     */
    public static String parseIcp(String html) {
        String icp = "";
        Matcher matcher = ICP_PATTERN.matcher(html);

        if (matcher.find()) {
            icp = matcher.group(0);
        }
        icp = Jsoup.clean(icp, Whitelist.none());
        icp = StringUtils.replaceAll(icp, " |", "");
        icp = StringUtils.substring(icp, 0, 255);
        icp = StringUtils.substring(icp, 0, StringUtils.indexOf(icp, "号") + 1);

        return icp;
    }

    /**
     * 获取当前页面所有链接集合
     *
     * @param document
     * @param domain  顶级域名
     * @param url    当前请求URL
     * @return
     */
    public static Set<String> parseLinks(Document document, String domain, String url) {
        Set<String> links = new HashSet<>();

        // 获取所有a链接
        Elements elements = document.select("a");
        if (elements.size() > 0) {
            for (Element element : elements) {
                String link = StringUtils.trimToEmpty(element.attr("href"));

                // 过滤垃圾链接
                if (!UrlUtils.filterUrl(link)) {
                    continue;
                }

                // 转换补全相对、绝对路径
                if (!StringUtils.startsWithIgnoreCase(link, HTTP_PROTOCOL)
                        && !StringUtils.startsWithIgnoreCase(link, HTTPS_PROTOCOL)) {
                    try {
                        URL absoluteUrl = new URL(url);
                        URL parseUrl = new URL(absoluteUrl, link);
                        link = parseUrl.toString();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        continue;
                    }
                }

                // 排除站外链接
                URL parse = UrlUtils.parse(link);
                if (parse == null) {
                    continue;
                }
                String host = parse.getHost();
                String topDomain = DomainUtils.topDomain(domain);
                if (topDomain != null) {
                    if (!StringUtils.endsWithIgnoreCase(host, topDomain)) {
                        continue;
                    }
                }

                // 最后验证一下这个 URL 的格式
                if (UrlUtils.verifyUrl(link)) {
                    links.add(link);
                }
            }
        }

        return links;
    }

    /**
     * 根据页面上的链接，获取子域名集合
     *
     * @param document
     * @param domain
     * @param url
     * @return
     */
    public static Set<String> parseSubDomain(Document document, String domain, String url) {
        Set<String> subDomains = new HashSet<>();
        Set<String> links = parseLinks(document, domain, url);
        if (!links.isEmpty()) {
            for (String link : links) {
                URL parseUrl = UrlUtils.parse(link);

                if (parseUrl != null) {
                    String host = parseUrl.getHost();
                    String topDomain = DomainUtils.topDomain(domain);

                    if (topDomain != null) {
                        if (!host.equals(topDomain) && StringUtils.endsWithIgnoreCase(host, topDomain)) {
                            subDomains.add(host);
                        }
                    }
                }
            }
        }

        return subDomains;
    }
}
