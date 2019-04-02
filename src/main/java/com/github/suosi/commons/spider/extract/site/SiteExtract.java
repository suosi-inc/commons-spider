package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import com.github.suosi.commons.spider.utils.UrlUtils;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author niuchaoqun
 */
public class SiteExtract {
    private static final Pattern ICP_PATTERN = Pattern.compile("(京|津|冀|晋|蒙|辽|吉|黑|沪|苏|浙|皖|闽|赣|鲁|豫|鄂|湘|粤|桂|琼|川|蜀|贵|黔|云|滇|渝|藏|陇|甘|陕|秦|青|宁|新)ICP(备|证)(.*)号", Pattern.CASE_INSENSITIVE);

    private static final String WWW_PREFIX = "www.";

    private static final String HTTP_PROTOCOL = "http";

    private static final String HTTPS_PROTOCOL = "https";

    /**
     * 根据顶级域名，获取站点主要信息
     *
     * @param domain
     * @return
     */
    public static Site domain(String domain) {
        if (StringUtils.isNotBlank(domain)) {

            String mainDomain = domain;
            HashMap<String, String> urls = new HashMap<>();

            if (!StringUtils.startsWithIgnoreCase(domain, WWW_PREFIX)) {
                mainDomain = WWW_PREFIX + domain;
                urls.put(HTTP_PROTOCOL, HTTP_PROTOCOL + "://" + mainDomain);
                urls.put(HTTPS_PROTOCOL, HTTPS_PROTOCOL + "://" + mainDomain);
            } else {
                urls.put(HTTP_PROTOCOL, HTTP_PROTOCOL + "://" + mainDomain);
                urls.put(HTTPS_PROTOCOL, HTTPS_PROTOCOL + "://" + mainDomain);
            }

            for (Map.Entry<String, String> urlEntry : urls.entrySet()) {
                String protocol = urlEntry.getKey();
                String url = urlEntry.getValue();

                try (Response response = OkHttpUtils.client().newCall(OkHttpUtils.request(url)).execute()) {
                    if (response.isSuccessful() && response.body() != null) {

                        // 编码
                        byte[] htmlBytes = response.body().bytes();
                        String charset = StringUtils.upperCase(CharsetUtils.guessEncoding(htmlBytes));
                        if (StringUtils.startsWith(charset, "GB")) {
                            charset = "GBK";
                        }
                        String html = new String(htmlBytes, charset);

                        Document document = Jsoup.parse(html);
                        String title = parseTitle(document);
                        String cleanTitle = parseCleanTitle(title);
                        String keywords = parseKeywords(document);
                        String description = parseDescription(document);
                        String icp = parseIcp(html);
                        Set<String> links = parseLinks(document, domain, url);
                        Set<String> subDomains = parseSubDomain(document, mainDomain, domain, url);

                        return Site.builder().protocol(protocol)
                                .mainDomain(mainDomain)
                                .charset(charset)
                                .html(html)
                                .title(title)
                                .cleanTitle(cleanTitle)
                                .keywords(keywords)
                                .description(description)
                                .icp(icp)
                                .subDomain(subDomains)
                                .links(links)
                                .build();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        return null;
    }

    /**
     * 解析 Title
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
     * 尝试解析干净的Title
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
     * 尝试解析 ICP
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
     * 获取当前页面所有链接
     *
     * @param document
     * @param url
     * @return
     */
    public static Set<String> parseLinks(Document document, String domain, String url) {
        Set<String> links = new HashSet<>();
        Elements elements = document.select("a");
        if (elements.size() > 0) {
            for (Element element : elements) {
                String link = StringUtils.trimToEmpty(element.attr("href"));

                // 验证URL有效性
                if (!UrlUtils.verifyUrl(link)) {
                    continue;
                }

                // 转换相对路径
                if (!StringUtils.startsWithIgnoreCase(link, HTTP_PROTOCOL)
                        && !StringUtils.startsWithIgnoreCase(link, HTTPS_PROTOCOL)) {
                    try {
                        URL absoluteUrl = new URL(url);
                        URL parseUrl = new URL(absoluteUrl, link);
                        link = parseUrl.toString();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }

                // 排除非本站域名的链接
                URL parse = UrlUtils.parse(link);
                if (parse == null) {
                    continue;
                }
                String host = parse.getHost();
                if (!StringUtils.endsWithIgnoreCase(host, domain)) {
                    continue;
                }

                links.add(link);
            }
        }
        return links;
    }

    /**
     * 获取当前页面链接
     *
     * @param html
     * @param url
     * @return
     */
    public static Set<String> parseLinksFromHtml(String html, String domain, String url) {
        Document document = Jsoup.parse(html);
        return parseLinks(document, domain, url);
    }

    /**
     * 获取子域名
     *
     * @param document
     * @param mainDomain
     * @param domain
     * @param url
     * @return
     */
    private static Set<String> parseSubDomain(Document document, String mainDomain, String domain, String url) {
        Set<String> subDomains = new HashSet<>();
        Set<String> links = parseLinks(document, domain, url);
        if (!links.isEmpty()) {
            for (String link : links) {
                try {
                    URL parseUrl = new URL(link);
                    String host = parseUrl.getHost();
                    if (!host.equals(domain) && !host.equals(mainDomain) && StringUtils.endsWithIgnoreCase(host, domain)) {
                        subDomains.add(host);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        return subDomains;
    }

    /**
     * 获取域名关键词
     *
     * @param document
     * @return
     */
    public static String parseKeywords(Document document) {
        String keywords = document.select("meta[name=keywords]").attr("content");
        return StringUtils.trimToEmpty(keywords);
    }

    /**
     * 获取域名关键词
     *
     * @param html
     * @return
     */
    public static String parseKeywordsFromHtml(String html) {
        Document document = Jsoup.parse(html);
        return parseKeywords(document);
    }

    /**
     * 获取域名摘要
     *
     * @param document
     * @return
     */
    public static String parseDescription(Document document) {
        String description = document.select("meta[name=description]").attr("content");
        return StringUtils.trimToEmpty(description);
    }
}
