package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.BbsPage;
import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import com.github.suosi.commons.spider.utils.UrlUtils;
import com.github.suosi.commons.spider.utils.okhttp.OkHttpInterceptor;
import com.github.suosi.commons.spider.utils.okhttp.OkHttpProxy;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author niuchaoqun
 */
public class BbsPageExtract {

    private static final Pattern REFRESH_PATTERN = Pattern.compile("<meta.*?url=(.*?)[\"\']+.*?>", Pattern.CASE_INSENSITIVE);

    private static final Pattern LOCATION_PATTERN = Pattern.compile("location\\.href\\s*=\\s*[\"\']+(.*?)[\"\']+", Pattern.CASE_INSENSITIVE);

    private static final Pattern LOCATION2_PATTERN = Pattern.compile("\\.location\\s*=\\s*[(\"\']+(.*?)[\"\')]+", Pattern.CASE_INSENSITIVE);

    private static final Pattern LOCATION3_PATTERN = Pattern.compile("jump\\([\"\']+(.*?)[\"\']+\\)", Pattern.CASE_INSENSITIVE);

    /**
     * 根据URL，尝试抽取页面信息
     *
     * @param url
     * @return
     */
    public static BbsPage url(String url) throws Exception {
        BbsPage info;
        try {
            info = getUrl(url, 0,null);
        } catch (Exception e) {
            throw new Exception("page url except: " +e.getLocalizedMessage() + ":" + url);
        }
        return info;
    }

    /**
     * 根据URL，尝试抽取页面信息
     *
     * @param url
     * @return
     */
    public static BbsPage url(String url, boolean location) throws Exception {
        BbsPage info;
        try {
            info = getUrl(url, 0,null);
            if (location && info != null && info.getLinks().size() == 0) {
                String locationUrl = getHtmlLocationUrl(info.getHtml(), url);
                if (locationUrl != null && !locationUrl.equals(url)) {
                    info = getUrl(locationUrl, 0, null);
                }
            }
        } catch (Exception e) {
            throw new Exception("page url except: " +e.getLocalizedMessage() + ":" + url);
        }
        return info;
    }

    /**
     * 根据URL，尝试抽取页面信息，带有超时时间
     *
     * @param url
     * @param timeoutSecond
     * @return
     */
    public static BbsPage url(String url, long timeoutSecond) throws Exception {
        BbsPage info;
        try {
            info = getUrl(url, timeoutSecond, null);
        } catch (Exception e) {
            throw new Exception("page url except: " +e.getLocalizedMessage() + ":" + url);
        }
        return info;
    }

    /**
     * 根据URL，尝试抽取页面信息，带有超时时间
     *
     * @param url
     * @param timeoutSecond
     * @return
     */
    public static BbsPage url(String url, long timeoutSecond, boolean location) throws Exception {
        BbsPage info;
        try {
            info = getUrl(url, timeoutSecond, null);
            if (location && info != null && info.getLinks().size() == 0) {
                String locationUrl = getHtmlLocationUrl(info.getHtml(), url);
                if (locationUrl != null && !locationUrl.equals(url)) {
                    info = getUrl(locationUrl, timeoutSecond, null);
                }
            }
        } catch (Exception e) {
            throw new Exception("page url except: " +e.getLocalizedMessage() + ":" + url);
        }
        return info;
    }

    /**
     * 根据URL，尝试抽取页面信息，带有超时时间 和 跳转
     *
     * @param url
     * @param timeoutSecond
     * @param userProxy 代理
     * @return
     */
    public static BbsPage url(String url, long timeoutSecond, OkHttpProxy userProxy) throws Exception {
        BbsPage info;
        try {
            info = getUrl(url, timeoutSecond, userProxy);
        } catch (Exception e) {
            throw new Exception("page url except: " +e.getLocalizedMessage() + ":" + url);
        }
        return info;
    }

    /**
     * 根据URL，尝试抽取页面信息，带有超时时间 和 跳转
     *
     * @param url
     * @param timeoutSecond
     * @param userProxy 代理
     * @param location
     * @return
     */
    public static BbsPage url(String url, long timeoutSecond, OkHttpProxy userProxy, boolean location) throws Exception {
        BbsPage info;
        try {
            info = getUrl(url, timeoutSecond, userProxy);
            if (location && info != null && info.getLinks().size() == 0) {
                String locationUrl = getHtmlLocationUrl(info.getHtml(), url);
                if (locationUrl != null && !locationUrl.equals(url)) {
                    info = getUrl(locationUrl, timeoutSecond, userProxy);
                }
            }
        } catch (Exception e) {
            throw new Exception("page url except: " +e.getLocalizedMessage() + ":" + url);
        }
        return info;
    }

    /**
     * 根据URL，尝试抽取页面信息
     *
     * @param url
     * @param timeoutSecond
     * @param userProxy 代理
     * @return
     */
    public static BbsPage getUrl(String url, long timeoutSecond, OkHttpProxy userProxy) throws Exception {
        url = url.trim();
        if (UrlUtils.verifyUrl(url) && UrlUtils.filterUrl(url)) {
            URL parseUrl = UrlUtils.parse(url);
            if (parseUrl != null) {
                String host = parseUrl.getHost();
                long timeout = timeoutSecond > 0 ? timeoutSecond : 3;
                Response response = null;
                try {
                    OkHttpClient client = OkHttpUtils.builder(null, timeout, userProxy)
                            // 增加下载拦截器
                            .addInterceptor(new OkHttpInterceptor())
                            .build();
                    response = client.newCall(OkHttpUtils.request(url)).execute();
                    // System.out.println("response=" + response);
                    if (response.isSuccessful() && response.body() != null) {
                        // 编码
                        byte[] htmlBytes = response.body().bytes();
                        String charset = CharsetUtils.guessCharset(htmlBytes, response);
                        String html = "";
                        try {
                            html = new String(htmlBytes, charset);
                        } catch (Exception e) {
                            if (!StringUtils.containsAny(StringUtils.lowerCase(charset), "utf-8", "utf8")) {
                                html = new String(htmlBytes, StandardCharsets.UTF_8);
                            } else {
                                throw new Exception(e.getMessage());
                            }
                        }
                        Document document = Jsoup.parse(html);
                        String title = Parse.parseTitle(document);
                        String keywords = Parse.parseKeywords(document);
                        String description = Parse.parseDescription(document);
                        url = getResponseUrl(response, url);
                        Set<String> links = Parse.parseLinks(document, host, url);
                        Set<HashMap> lists = Parse.parseBbsLists(html, host, url);

                        return BbsPage.builder()
                                .charset(charset)
                                .html(html)
                                .title(title)
                                .keywords(keywords)
                                .description(description)
                                .links(links)
                                .httpcode(response.code())
                                .url(url)
                                .lists(lists)
                                .build();
                    } else {
                        return BbsPage.builder()
                                .html("")
                                .url(getResponseUrl(response, url))
                                .httpcode(response.code())
                                .build();
                    }
                } catch (IOException e) {
                    throw new Exception("page url except: " +e.getLocalizedMessage() + ":" + url);
                } finally {
                    if (null != response) {
                        response.close();
                    }
                }
            }
        }

        return null;
    }

    public static BbsPage html(String html, String url) {
        Document document = Jsoup.parse(html);
        String title = Parse.parseTitle(document);
        String keywords = Parse.parseKeywords(document);
        String description = Parse.parseDescription(document);
        URL parseUrl = UrlUtils.parse(url);
        Set<String> links = null;
        if (parseUrl != null) {
            String host = parseUrl.getHost();
            links = Parse.parseLinks(document, host, url);
        }

        return BbsPage.builder()
                .charset("UTF-8")
                .html(html)
                .title(title)
                .keywords(keywords)
                .description(description)
                .links(links)
                .httpcode(200)
                .url(url)
                .build();
    }

    /**
     * 获取301等自动跳转后的新URL
     * @param response Response
     * @param url 请求URL
     * @return
     */
    public static String getResponseUrl(Response response, String url) {
        String lastUrl = url;
        try {
            assert response.networkResponse() != null;
            lastUrl = response.networkResponse().request().url().toString();
        } catch (Exception e) {}
        return lastUrl;
    }

    /**
     * 提取Html中跳转的URL
     * @param html 源代码
     * @param url 请求URL
     * @return
     */
    public static String getHtmlLocationUrl(String html, String url) {
        String refreshUrl = "";
        // 去除一些可能导致异常的注释
        html = html.replaceAll("[\\s]+//[^\\n]+", "");
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
                return refreshUrl;
            } catch (IOException e) {
                System.out.println("again error:" + e.getLocalizedMessage() + ":" + refreshUrl);
            }
        }
        return null;
    }

    /**
     * 单独请求论坛内容页
     * @param url
     * @return
     */
    public static String parseBbsContent(String url) {
        try {
            String bbsHtml = Parse.reqBbsContent(url);
            return Parse.parseBbsContent(bbsHtml);
        } catch (Exception e) {
            return "";
        }
    }

}
