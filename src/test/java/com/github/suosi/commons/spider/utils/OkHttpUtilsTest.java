package com.github.suosi.commons.spider.utils;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

import static com.github.suosi.commons.helper.Static.base64Encode;

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
                    String charset = CharsetUtils.guessCharset(bytes, response);
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

    @Test
    public void proxyUserPassword() {
        String url = "http://test.abuyun.com";
//        String url = "https://weixin.sogou.com/weixin?type=2&tsn=1&query=%E7%BD%97%E6%9D%B0";

        // 开启代理模式
        Authenticator proxyAuthenticator = (route, r) -> {
            String credential = Credentials.basic("HG686Y9765V56R8D", "491953B253B48306");
            return r.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };

        OkHttpClient client = OkHttpUtils.builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("http-dyn.abuyun.com", 9020)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();

        Request request = OkHttpUtils.requestBuilder(url)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.0 Safari/537.36")
                .header("Referer", "https://weixin.sogou.com/weixin?type=2&tsn=1&query=")
                .build();

        for (int i = 0; i < 3; i++) {
            try (Response response = client.newCall(OkHttpUtils.request(url)).execute()) {
                System.out.println(response.code());
                if (response.isSuccessful() && response.body() != null) {
                    byte[] bytes = response.body().bytes();
                    String charset = CharsetUtils.guessCharset(bytes, response);
                    String html = new String(bytes, charset);

                    System.out.println(html);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}