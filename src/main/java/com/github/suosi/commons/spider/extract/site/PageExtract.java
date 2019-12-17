package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.Page;
import com.github.suosi.commons.spider.extract.site.meta.Site;
import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import com.github.suosi.commons.spider.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author niuchaoqun
 */
public class PageExtract {

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
    public static Page url(String url) throws Exception {
        url = url.trim();
        if (UrlUtils.verifyUrl(url) && UrlUtils.filterUrl(url)) {
            URL parseUrl = UrlUtils.parse(url);

            if (parseUrl != null) {
                String host = parseUrl.getHost();

                try (Response response = OkHttpUtils.client().newCall(OkHttpUtils.request(url)).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        return response(response, host, url);
                    }
                } catch (IOException e) {
                    // System.out.println(e.getLocalizedMessage() + ":" + url);
                    throw new Exception("page url except: " + e.getLocalizedMessage() + ":" + url);
                }
            }
        }

        return null;
    }

    /**
     * 根据URL，尝试抽取页面信息，带有超时时间
     *
     * @param url
     * @param timeoutSecond
     * @return
     */
    public static Page url(String url, long timeoutSecond) throws Exception {
        if (UrlUtils.verifyUrl(url) && UrlUtils.filterUrl(url)) {
            URL parseUrl = UrlUtils.parse(url);
            if (parseUrl != null) {
                String host = parseUrl.getHost();

                long timeout = timeoutSecond > 0 ? timeoutSecond : 3;
                try (Response response = OkHttpUtils.client(timeout).newCall(OkHttpUtils.request(url)).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        return response(response, host, url);
                    }
                } catch (IOException e) {
                    // System.out.println(e.getLocalizedMessage() + ":" + url);
                    throw new Exception("page url except: " +e.getLocalizedMessage() + ":" + url);
                }
            }
        }

        return null;
    }

    /**
     * 根据URL，尝试抽取页面信息，带有超时时间
     *
     * @param url
     * @param timeoutSecond
     * @return
     */
    public static Page url(String url, long timeoutSecond, int deep) throws Exception {
        if (UrlUtils.verifyUrl(url) && UrlUtils.filterUrl(url)) {
            URL parseUrl = UrlUtils.parse(url);
            if (parseUrl != null) {
                String host = parseUrl.getHost();

                long timeout = timeoutSecond > 0 ? timeoutSecond : 3;
                try (Response response = OkHttpUtils.client(timeout).newCall(OkHttpUtils.request(url)).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        return response(response, host, url, deep);
                    }
                } catch (IOException e) {
                    // System.out.println(e.getLocalizedMessage() + ":" + url);
                    throw new Exception("page url except: " +e.getLocalizedMessage() + ":" + url);
                }
            }
        }

        return null;
    }

    public static Page response(Response response, String host, String url) throws IOException {
        // 编码
        byte[] htmlBytes = response.body().bytes();
        String charset = CharsetUtils.guessCharset(htmlBytes, response);
        String html = new String(htmlBytes, charset);

        Document document = Jsoup.parse(html);
        String title = Parse.parseTitle(document);
        String keywords = Parse.parseKeywords(document);
        String description = Parse.parseDescription(document);
        String lastUrl = "";
        try {
            lastUrl = response.networkResponse().request().url().toString();
        } catch (Exception e) {}
        Set<String> links = Parse.parseLinks(document, host, lastUrl);

        return Page.builder()
                .charset(charset)
                .html(html)
                .title(title)
                .keywords(keywords)
                .description(description)
                .links(links)
                .build();
    }

    public static Page response(Response response, String host, String url, int deep) throws IOException {
        // 编码
        byte[] htmlBytes = response.body().bytes();
        String charset = CharsetUtils.guessCharset(htmlBytes, response);
        String html = new String(htmlBytes, charset);

        Document document = Jsoup.parse(html);
        String title = Parse.parseTitle(document);
        String keywords = Parse.parseKeywords(document);
        String description = Parse.parseDescription(document);
        String lastUrl = "";
        try {
            lastUrl = response.networkResponse().request().url().toString();
        } catch (Exception e) {}
        Set<String> links = Parse.parseLinks(document, host, lastUrl);

        if (links.size() == 0 && deep == 0) {
            String refreshUrl = "";
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

                    try (Response response2 = OkHttpUtils.client().newCall(OkHttpUtils.request(refreshUrl)).execute()) {
                        if (response2.isSuccessful() && response2.body() != null) {
                            return response(response2, host, refreshUrl, deep + 1);
                        }
                    } catch (IOException e) {
                        System.out.println("refreshurl:" + e.getLocalizedMessage() + ":" + url);
                    }
                } catch (IOException e) {
                    System.out.println("again error:" + e.getLocalizedMessage() + ":" + refreshUrl);
                }
            }
        }
        return Page.builder()
                .charset(charset)
                .html(html)
                .title(title)
                .keywords(keywords)
                .description(description)
                .links(links)
                .build();
    }


    public static Page html(String html, String url) {
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

        return Page.builder()
                .charset("UTF-8")
                .html(html)
                .title(title)
                .keywords(keywords)
                .description(description)
                .links(links)
                .build();
    }
}
