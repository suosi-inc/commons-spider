package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.Site;
import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.DomainUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import com.github.suosi.commons.spider.utils.UrlUtils;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 根据域名，尝试抽取站点信息
 *
 * @author niuchaoqun
 */
public class SiteExtract {
    private static final String WWW_PREFIX = "www.";

    private static final String HTTP_PROTOCOL = "http";

    private static final String HTTPS_PROTOCOL = "https";

    private static final String[] DOMAIN_FILTERS = {
            "user",
            "u",
            "member",
            "api",
            "download",
            "upload",
            "reg",
            "register",
            "login",
            "mail",
            "photo",
            "photos",
            "pic",
            "video",
            "film",
            "v",
            "tv",
            "pay",
            "hr",
            "help"
    };

    /**
     * 根据域名抽取站点关键信息
     *
     * @param domain 域名
     * @return
     */
    public static Site domain(String domain) {
        if (StringUtils.isNotBlank(domain)) {
            String mainDomain = domain;
            HashMap<String, String> urls = new HashMap<>();

            // 是否是顶级域名，如果是顶级域名会进行www前缀主域名探测
            boolean top = false;
            String topDomain = DomainUtils.topDomain(domain);
            if (topDomain != null && domain.equals(topDomain)) {
                top = true;
            }
            if (top && !StringUtils.startsWithIgnoreCase(domain, WWW_PREFIX)) {
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
                        String charset = StringUtils.upperCase(CharsetUtils.guessEncoding(htmlBytes, response));
                        if (StringUtils.startsWith(charset, "GB")) {
                            charset = "GBK";
                        }
                        String html = new String(htmlBytes, charset);

                        // 信息抽取
                        Document document = Jsoup.parse(html);
                        String title = Parse.parseTitle(document);
                        String cleanTitle = Parse.parseCleanTitle(title);
                        String keywords = Parse.parseKeywords(document);
                        String description = Parse.parseDescription(document);
                        String icp = Parse.parseIcp(html);
                        Set<String> links = parseLinks(document, domain, url);
                        Set<String> subDomains = parseSubDomain(document, domain, url);

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

        return Site.builder().build();
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
    private static Set<String> parseSubDomain(Document document, String domain, String url) {
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

    /**
     * 尝试过滤非有效域名，可能是登录，帮助，用户，下载等等
     *
     * @param domain
     * @return
     */
    public static boolean filterDomain(String domain) {

        String topDomain = DomainUtils.topDomain(domain);

        if (domain.equals(topDomain)) {
            return true;
        } else {
            String subString = StringUtils.removeEndIgnoreCase(domain, topDomain);
            String[] subs = StringUtils.split(subString, ".");
            if (subs.length > 0) {
                String lastSub = subs[subs.length - 1];
                for (String domainFilter : DOMAIN_FILTERS) {
                    if (StringUtils.equalsIgnoreCase(domainFilter, lastSub)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
