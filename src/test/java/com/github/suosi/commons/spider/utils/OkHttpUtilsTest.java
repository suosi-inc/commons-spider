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
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void proxy() {
        // String url = "http://ip.suosi.net.cn/t.php";
        String url = "https://weixin.sogou.com/antispider/";
        String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";

        String tunnel = String.valueOf((int) (Math.random() * (Integer.MAX_VALUE - 1)));

        // 开启代理模式
        Authenticator abuyunAuthenticator = (route, r) -> {
            String credential = Credentials.basic("16ZTBOZP", "335517");
            return r.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };

        OkHttpClient client = OkHttpUtils.builder(null, 0)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("u3411.300.tp.16yun.cn", 6474)))
                .proxyAuthenticator(abuyunAuthenticator)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();

        try {
            for (int i = 0; i < 15; i++) {

                Response response = client.newCall(new Request.Builder()
                        .header("User-Agent", ua)
                        // .header("Connection", "close")
                        // .header("Proxy-Tunnel", tunnel)
                        .removeHeader("Cookie")
                        .url(url)
                        .build()).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String html = response.body().string();

                    Matcher matcher = Pattern.compile("IP：(.*)<br>").matcher(html);
                    // Matcher matcher = Pattern.compile("\\[REMOTE_ADDR\\] => (.*)").matcher(html);
                    if (matcher.find()) {
                        System.out.println(matcher.group(1));
                    }
                }

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
