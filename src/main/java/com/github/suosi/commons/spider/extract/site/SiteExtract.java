package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.Site;
import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.DomainUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import com.github.suosi.commons.spider.utils.UrlUtils;
import com.github.suosi.commons.spider.utils.okhttp.OkHttpInterceptor;
import com.github.suosi.commons.spider.utils.okhttp.OkHttpProxy;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.Set;
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
        return getDomain(domain, 0L, null);
    }

    /**
     * 根据域名抽取站点关键信息
     *
     * @param domain 域名
     * @param timeoutSecond 超时时间
     * @return
     */
    public static Site domain(String domain, Long timeoutSecond) throws Exception {
        return getDomain(domain, timeoutSecond, null);
    }

    /**
     * 根据域名抽取站点关键信息
     *
     * @param domain 域名
     * @param timeoutSecond 超时时间
     * @return
     */
    public static Site domain(String domain, Long timeoutSecond, OkHttpProxy userProxy) throws Exception {
        return getDomain(domain, timeoutSecond, userProxy);
    }

    /**
     *  Domain中间处理方法
     * @param domain
     * @param timeoutSecond
     * @return
     * @throws Exception
     */
    public static Site getDomain(String domain, Long timeoutSecond, OkHttpProxy userProxy) throws Exception {
        String[] protocols = {HTTP_PROTOCOL, HTTPS_PROTOCOL};

        Site res = null;
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

            for (String protocol : protocols) {
                // 构造 URL
                String url = protocol + "://" + mainDomain;
                res = dealDomainResponse(url, domain, mainDomain, protocol, timeoutSecond, userProxy);
                if (null != res) {
                    break;
                }
            }

            if (null == res && !mainDomain.equals(domain)) {
                // 再探不带www的
                for (String protocol : protocols) {
                    // 构造 URL
                    String url = protocol + "://" + domain;
                    res = dealDomainResponse(url, domain, domain, protocol, timeoutSecond, userProxy);
                    if (null != res) {
                        break;
                    }
                }
            }
        }

        return res;
    }

    /**
     * 处理Domain请求
     * @param url
     * @param domain
     * @param mainDomain
     * @param protocol
     * @param timeoutSecond
     * @return
     */
    public static Site dealDomainResponse(String url, String domain, String mainDomain, String protocol, long timeoutSecond, OkHttpProxy userProxy) {
        long timeout = timeoutSecond > 0 ? timeoutSecond : 3;
        Response response = null;
        try {
            OkHttpClient client = OkHttpUtils.builder(null, timeout, userProxy)
                    // 增加下载拦截器
                    .addInterceptor(new OkHttpInterceptor())
                    .build();
            response = client.newCall(OkHttpUtils.request(url)).execute();
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
        } finally {
            if (null != response) {
                response.close();
            }
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
    public static Site dealResponseToSite(Response response, String url, String domain) throws Exception {
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
