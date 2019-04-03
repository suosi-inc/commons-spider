package com.github.suosi.commons.spider.utils;

import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;

public class OkHttpUtilsTest {

    @Test
    public void test() {
        String[] urls = {
                "http://news.163.com",
                "http://www.sohu.com",
                "https://www.baidu.com",
                "https://www.a5.net",
                "https://www.zjjnews.cn",
                "http://www.zmdnews.cn",
                "https://www.yhfz.gov.cn"
        };

        for (String url : urls) {
            try (Response response = OkHttpUtils.client().newCall(OkHttpUtils.request(url)).execute()) {
                System.out.println(response.code());
                if (response.isSuccessful() && response.body() != null) {
                    byte[] bytes = response.body().bytes();
                    String charset = CharsetUtils.guessEncoding(bytes, response);
                    String html = new String(bytes, charset);

                    Document document = Jsoup.parse(html);
                    String title = StringUtils.trimToEmpty(document.title());
                    System.out.println(url + " " + StringUtils.upperCase(charset) + " " + title);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}