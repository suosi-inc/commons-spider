package com.github.suosi.commons.spider.utils;

import com.github.suosi.commons.spider.utils.cookie.CookieJarImp;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
//        String url = "http://test.abuyun.com";
//        String url = "http://121.199.28.117/t.php";
        String url = "https://weixin.sogou.com/weixin?type=2&tsn=1&query=%E4%B8%80%E5%B8%A6%E4%B8%80%E8%B7%AF";


        // 开启代理模式
        Authenticator proxyAuthenticator = (route, r) -> {
            String credential = Credentials.basic("HG686Y9765V56R8D", "491953B253B48306");
            return r.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };

        // 获取COOKIE SUV
        String cookieSuv = "";
        List<Cookie> cookies;
        CookieJarImp cookie = new CookieJarImp();
        OkHttpClient indexClient = OkHttpUtils.builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("http-dyn.abuyun.com", 9020)))
                .proxyAuthenticator(proxyAuthenticator)
                .cookieJar(cookie)
                .build();
        Request request = OkHttpUtils.requestBuilder()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                .header("Cache-Control", "no-cache")
                .header("Pragma", "no-cache")
                .url("https://pb.sogou.com/pv.gif?uigs_productid=webapp&type=antispider&subtype=imgCost&domain=weixin&suv=&snuid=&cost=69103&t=" + System.currentTimeMillis())
                .build();
        try (Response indexResponse = indexClient.newCall(request).execute()) {
            if (indexResponse.isSuccessful() && indexResponse.body() != null) {
                cookies = cookie.getCookies();
                for (Cookie c : cookies) {
                    if ("SUV".equals(c.name())) {
                        cookieSuv = c.value();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(cookieSuv);

        // 采集
        request = OkHttpUtils.requestBuilder()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                .header("Referer", url)
                .header("Cookie", "SUV=" + cookieSuv)
                .header("Cache-Control", "no-cache")
                .header("Pragma", "no-cache")
                .url(url)
                .build();

        for (int i = 0; i < 100; i++) {

            OkHttpClient client = OkHttpUtils.builder()
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("http-dyn.abuyun.com", 9020)))
                    .proxyAuthenticator(proxyAuthenticator)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                System.out.println(response.code());
                if (response.isSuccessful() && response.body() != null) {
                    byte[] bytes = response.body().bytes();
                    String charset = CharsetUtils.guessCharset(bytes, response);
                    String html = new String(bytes, charset);

                    Matcher matcher = Pattern.compile("IP：(.*)<br>").matcher(html);
                    if (matcher.find()) {
                        System.out.println(matcher.group(1));
                    } else {
                        System.out.println("Success");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
