package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.Page;
import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import com.github.suosi.commons.spider.utils.UrlUtils;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
    public static Page url(String url) {
        if (UrlUtils.verifyUrl(url))  {
            URL parseUrl = UrlUtils.parse(url);
            if (parseUrl != null) {
                String host = parseUrl.getHost();

                try (Response response = OkHttpUtils.client().newCall(OkHttpUtils.request(url)).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
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
                } catch (IOException e) {
                    System.out.println(e.getLocalizedMessage() + url);
                }
            }
        }

        return null;
    }
}
