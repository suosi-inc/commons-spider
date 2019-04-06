package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.Site;
import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.DomainUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
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
            "help",
            "search",
    };

    /**
     * 根据域名抽取站点关键信息
     *
     * @param domain 域名
     * @return
     */
    public static Site domain(String domain) {
        String[] protocols = {HTTPS_PROTOCOL, HTTP_PROTOCOL};

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
                    e.printStackTrace();
                }
            }
        }

        return Site.builder().build();
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
