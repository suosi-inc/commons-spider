package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.Page;
import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import com.github.suosi.commons.spider.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

/**
 * @author niuchaoqun
 */
public class PageExtract {

    /**
     * 根据URL，尝试抽取页面信息
     *
     * @param url
     * @return
     */
    public static Page url(String url) throws Exception {
        url = url.trim();
        if (UrlUtils.verifyUrl(url)) {
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
        if (UrlUtils.verifyUrl(url)) {
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

    public static Page response(Response response, String host, String url) throws IOException {
        // 编码
        byte[] htmlBytes = response.body().bytes();
        String charset = CharsetUtils.guessCharset(htmlBytes, response);
        String html = new String(htmlBytes, charset);

        Document document = Jsoup.parse(html);
        String title = Parse.parseTitle(document);
        String keywords = Parse.parseKeywords(document);
        String description = Parse.parseDescription(document);
        Set<String> links = Parse.parseLinks(document, host, url);

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
