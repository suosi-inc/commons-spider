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

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 根据域名，尝试抽取站点信息
 *
 * @author niuchaoqun
 */
public class SiteExtract {
    private static final String WWW_PREFIX = "www.";

    private static final String HTTP_PROTOCOL = "http";

    private static final String HTTPS_PROTOCOL = "https";

    private static final Pattern REFRESH_PATTERN = Pattern.compile("<meta.*?url=(.*?)[\"\']+.*?>", Pattern.CASE_INSENSITIVE);

    private static final Pattern LOCATION_PATTERN = Pattern.compile("location\\.href\\s*=\\s*[\"\']+(.*?)[\"\']+", Pattern.CASE_INSENSITIVE);

    private static final Pattern LOCATION2_PATTERN = Pattern.compile("\\.location\\s*=\\s*[\"\']+(.*?)[\"\']+", Pattern.CASE_INSENSITIVE);

    private static final Pattern LOCATION3_PATTERN = Pattern.compile("jump\\([\"\']+(.*?)[\"\']+\\)", Pattern.CASE_INSENSITIVE);

    private static final String[] DOMAIN_FILTERS = {
            "user",
            "u",
            "member",
            "api",
            "download",
            "down",
            "upload",
            "reg",
            "register",
            "login",
            "mail",
            "tupian",
            "photo",
            "music",
            "fm",
            "photos",
            "pic",
            "picture",
            "pictures",
            "movie",
            "dianying",
            "video",
            "video",
            "film",
            "v",
            "tv",
            "pay",
            "hr",
            "help",
            "search",
            "zhibo",
            "live",
            "vip",
            "110",
            "map",
            "ad",
            "weather",
            "about",
            "aboutus",
            "passport",
            "qiye",
    };

    /**
     * 根据域名抽取站点关键信息
     *
     * @param domain 域名
     * @return
     */
    public static Site domain(String domain) throws Exception {
        String[] protocols = {HTTP_PROTOCOL, HTTPS_PROTOCOL,HTTP_PROTOCOL, HTTPS_PROTOCOL};

        if (StringUtils.isNotBlank(domain)) {
            String mainDomain = domain;

            // 是否是顶级域名，如果是顶级域名会进行 www 前缀主域名探测
            boolean top = false;
            String topDomain = DomainUtils.topDomain(domain);
            if (domain.equals(topDomain)) {
                top = true;
            }

            if (top && !StringUtils.startsWithIgnoreCase(domain, WWW_PREFIX)) {
                mainDomain = WWW_PREFIX + domain;
            }

            int tryCnt = 0;
            for (String protocol : protocols) {
                // 构造 URL
                String url;
                if (tryCnt > 1) {
                    mainDomain = domain;
                }
                url = protocol + "://" + mainDomain;
                tryCnt += 1;
                try (Response response = OkHttpUtils.client().newCall(OkHttpUtils.request(url)).execute()) {
                    if (response.isSuccessful() && response.body() != null) {

                        // 编码
                        byte[] htmlBytes = response.body().bytes();
                        String charset = CharsetUtils.guessCharset(htmlBytes, response);
                        String html = new String(htmlBytes, charset);

                        // 信息抽取
                        Document document = Jsoup.parse(html);
                        String title = Parse.parseTitle(document);
                        String cleanTitle = Parse.parseCleanTitle(title);
                        String keywords = Parse.parseKeywords(document);
                        String description = Parse.parseDescription(document);
                        String icp = Parse.parseIcp(html);
                        Set<String> links = Parse.parseLinks(document, domain, url);
                        Set<String> subDomains = Parse.parseSubDomain(document, domain, url);

                        return Site.builder()
                                .protocol(protocol)
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
                    System.out.println(e.getLocalizedMessage() + ":" + url);
                    // throw new Exception("site domain except:  " + e.getLocalizedMessage() + ":" + url);
                }
            }
        }

        return null;
    }

    /**
     * 根据域名抽取站点关键信息
     *
     * @param domain 域名
     * @return
     */
    public static Site domain(String domain, Long timeoutSecond, boolean location) throws Exception {
        String[] protocols = {HTTP_PROTOCOL, HTTPS_PROTOCOL, HTTP_PROTOCOL, HTTPS_PROTOCOL};

        if (StringUtils.isNotBlank(domain)) {
            String mainDomain = domain;

            // 是否是顶级域名，如果是顶级域名会进行 www 前缀主域名探测
            boolean top = false;
            String topDomain = DomainUtils.topDomain(domain);
            if (domain.equals(topDomain)) {
                top = true;
            }

            if (top && !StringUtils.startsWithIgnoreCase(domain, WWW_PREFIX)) {
                mainDomain = WWW_PREFIX + domain;
            }

            int tryCnt = 0;
            for (String protocol : protocols) {
                // 构造 URL
                String url;
                if (tryCnt > 1) {
                    mainDomain = domain;
                }
                url = protocol + "://" + mainDomain;
                tryCnt += 1;
                Site info =  getSiteByDomain(url, domain, mainDomain, protocol, timeoutSecond);
                if (info != null) {
                    Set<String> links = info.getLinks();
                    if (links.isEmpty() && location) {
                        String refreshUrl = "";
                        String html = info.getHtml();

                        Matcher matcher1 = LOCATION_PATTERN.matcher(html);
                        Matcher matcher2 = LOCATION2_PATTERN.matcher(html);
                        Matcher matcher3 = REFRESH_PATTERN.matcher(html);
                        Matcher matcher4 = LOCATION3_PATTERN.matcher(html);
                        if (matcher1.find()) {
                            refreshUrl = matcher1.group(1);
                        } else if (matcher2.find()) {
                            refreshUrl = matcher2.group(1);
                        } else if (matcher3.find()) {
                            refreshUrl = matcher3.group(1);
                        } else if (matcher4.find()) {
                            refreshUrl = matcher4.group(1);
                        }

                        if (refreshUrl.length() > 0) {
                            try {
                                URL absoluteUrl = new URL(url);

                                // path 为空的情况，这种一般是错误，直接移除
                                if (StringUtils.isBlank(absoluteUrl.getPath())) {
                                    refreshUrl = Parse.removeStartComplete(refreshUrl, "./");
                                    refreshUrl = Parse.removeStartComplete(refreshUrl, "../");
                                }

                                URL parseUrl = new URL(absoluteUrl, refreshUrl);
                                refreshUrl = parseUrl.toString();

                                Site info2 = getSiteByDomain(refreshUrl, domain, mainDomain, protocol, timeoutSecond);
                                if (info2.getLinks().size() > 0) {
                                    return info2;
                                }
                            } catch (IOException e) {
                                System.out.println("again error:" + e.getLocalizedMessage() + ":" + refreshUrl);
                            }
                        }
                    } else {
                        return info;
                    }
                }
            }
        }

        return null;
    }

    public static Site getSiteByDomain(String url, String domain, String mainDomain, String protocol, long timeoutSecond) {
        long timeout = timeoutSecond > 0 ? timeoutSecond : 3;
        try (Response response = OkHttpUtils.client(timeout).newCall(OkHttpUtils.request(url)).execute()) {

            if (response.isSuccessful() && response.body() != null) {

                // 编码
                byte[] htmlBytes = response.body().bytes();
                String charset = CharsetUtils.guessCharset(htmlBytes, response);
                String html = new String(htmlBytes, charset);

                // 信息抽取
                Document document = Jsoup.parse(html);
                String title = Parse.parseTitle(document);
                String cleanTitle = Parse.parseCleanTitle(title);
                String keywords = Parse.parseKeywords(document);
                String description = Parse.parseDescription(document);
                String icp = Parse.parseIcp(html);
                Set<String> links = Parse.parseLinks(document, domain, url);
                Set<String> subDomains = Parse.parseSubDomain(document, domain, url);

                return Site.builder()
                        .protocol(protocol)
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
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage() + ":" + url);
            // throw new Exception("site domain except: " + e.getLocalizedMessage() + ":" + url);
        }
        return null;
    }


    /**
     * 根据用户传递的response 解析成site
     *
     * @param response
     * @param url
     * @param domain
     * @return
     */
    public static Site responseToSite(Response response, String url, String domain) throws Exception {
        // 编码
        byte[] htmlBytes = response.body().bytes();
        String charset = CharsetUtils.guessCharset(htmlBytes, response);
        String html = new String(htmlBytes, charset);

        URL parseUrl = UrlUtils.parse(url);

        String protocol = parseUrl.getProtocol();
        String mainDomain = parseUrl.getHost();

        // 信息抽取
        Document document = Jsoup.parse(html);
        String title = Parse.parseTitle(document);
        String cleanTitle = Parse.parseCleanTitle(title);
        String keywords = Parse.parseKeywords(document);
        String description = Parse.parseDescription(document);
        String icp = Parse.parseIcp(html);
        Set<String> links = Parse.parseLinks(document, domain, url);
        Set<String> subDomains = Parse.parseSubDomain(document, domain, url);

        return Site.builder()
                .protocol(protocol)
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
