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

        String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";


        // 开启代理模式
        Authenticator abuyunAuthenticator = (route, r) -> {
            String credential = Credentials.basic("HG686Y9765V56R8D", "491953B253B48306");
            return r.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };

        Authenticator proxyAuthenticator = (route, r) -> {
            String credential = Credentials.basic("squid", "SuosiSquid147!$&");
            return r.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };

        // 获取COOKIE SUV
        List<Cookie> cookies;
        Request request;
//
//
//        String cookieSuid = "";
//        OkHttpClient indexClient = new OkHttpClient()
//                .newBuilder()
//                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("squid01.suosi.net.cn", 31283)))
//                .proxyAuthenticator(proxyAuthenticator)
//                .cookieJar(cookie)
//                .build();
//        request = OkHttpUtils.requestBuilder()
//                .header("User-Agent", ua)
//                .url("https://weixin.sogou.com")
//                .build();
//        try (Response indexResponse = indexClient.newCall(request).execute()) {
//            if (indexResponse.isSuccessful() && indexResponse.body() != null) {
//                cookies = cookie.getCookies();
//                for (Cookie c : cookies) {
//                    if ("SUID".equals(c.name())) {
//                        cookieSuid = c.value();
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(cookieSuid);
//
//        String cookieSuv = "";
//        OkHttpClient pvClient = new OkHttpClient()
//                .newBuilder()
//                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("squid01.suosi.net.cn", 31283)))
//                .proxyAuthenticator(proxyAuthenticator)
//                .cookieJar(cookie)
//                .build();
//        request = OkHttpUtils.requestBuilder()
//                .header("User-Agent", ua)
//                .url("https://pb.sogou.com/pv.gif?uigs_productid=webapp&type=antispider&subtype=imgCost&domain=weixin&suv=&snuid=&cost=69103&t=" + System.currentTimeMillis())
//                .build();
//        try (Response indexResponse = pvClient.newCall(request).execute()) {
//            if (indexResponse.isSuccessful() && indexResponse.body() != null) {
//                cookies = cookie.getCookies();
//                for (Cookie c : cookies) {
//                    if ("SUV".equals(c.name())) {
//                        cookieSuv = c.value();
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(cookieSuv);


        // 采集


        for (int i = 0; i < 20; i++) {
            request = new Request.Builder()
                    .header("User-Agent", ua)
                    .header("Referer", "https://weixin.sogou.com")
                    .header("Cookie", "SUV=003E755D7179A9175CC57E286B9E8291;SNUID=D44D18948F8A091B0F4870488F3F1D67")
                    .url("https://weixin.sogou.com/weixin?type=2&s_from=input&query=%E4%B8%80%E5%B8%A6%E4%B8%80%E8%B7%AF")
                    .build();

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("http-dyn.abuyun.com", 9020)))
                    .proxyAuthenticator(abuyunAuthenticator)
                    .followRedirects(false)
                    .followSslRedirects(false)
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
                        Document doc = Jsoup.parse(html);
                        System.out.println(doc.title());
                    }
                } else if (response.code() == 302) {
                    System.out.println(302);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test302() {
        String url = "https://weixin.sogou.com/weixin?type=2&s_from=input&query=%E5%BC%A0%E9%A6%A8%E8%89%BA&ie=utf8&_sug_=n&_sug_type_=";
        String snuid = "";

        // 开启代理模式
        Authenticator abuyunAuthenticator = (route, r) -> {
            String credential = Credentials.basic("HG686Y9765V56R8D", "491953B253B48306");
            return r.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };

        Request request = new Request.Builder()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                .header("Referer", url)
                .header("Cookie", "SUV=0056C7C4DB91F3545CC57EB6C3764879")
                .url(url)
                .build();

        CookieJarImp cookieJarImp = new CookieJarImp();

        OkHttpClient sclient = new OkHttpClient().newBuilder()
                //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("http-dyn.abuyun.com", 9020)))
                //.proxyAuthenticator(abuyunAuthenticator)
                .followRedirects(false)
                .followSslRedirects(false)
                .cookieJar(cookieJarImp)
                .build();

        try (Response response = sclient.newCall(request).execute()) {
            System.out.println(response.code());
            if (response.isSuccessful() && response.body() != null) {

            } else if (response.code() == 302) {
                List<Cookie> cookies = cookieJarImp.getCookies();
                System.out.println(cookies);

                for (Cookie c : cookies) {
                    if ("SNUID".equals(c.name())) {
                        snuid = c.value();
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


//        for (int i = 0; i < 20; i++) {
//            CookieJarImp cookieJarImp2 = new CookieJarImp();
//
//            request = new Request.Builder()
//                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
//                    .header("Referer", "https://weixin.sogou.com")
//                    .header("Cookie", "SUV=0056C7C4DB91F3545CC57EB6C3764879;SNUID=")
//                    .url("https://weixin.sogou.com/weixin?type=2&s_from=input&query=%E4%B8%80%E5%B8%A6%E4%B8%80%E8%B7%AF")
//                    .build();
//
//            OkHttpClient client = new OkHttpClient().newBuilder()
//                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("http-dyn.abuyun.com", 9020)))
//                    .proxyAuthenticator(abuyunAuthenticator)
//                    .cookieJar(cookieJarImp2)
//                    .followRedirects(false)
//                    .followSslRedirects(false)
//                    .build();
//
//            try (Response response = client.newCall(request).execute()) {
//                System.out.println(response.code());
//                if (response.isSuccessful() && response.body() != null) {
//                    byte[] bytes = response.body().bytes();
//                    String charset = CharsetUtils.guessCharset(bytes, response);
//                    String html = new String(bytes, charset);
//
//                    Matcher matcher = Pattern.compile("IP：(.*)<br>").matcher(html);
//                    if (matcher.find()) {
//                        System.out.println(matcher.group(1));
//                    } else {
//                        Document doc = Jsoup.parse(html);
//                        System.out.println(doc.title());
//                    }
//                } else if (response.code() == 302) {
//                    List<Cookie> cookies = cookieJarImp.getCookies();
//                    System.out.println(cookies);
//                    for (Cookie c : cookies) {
//                        if ("SNUID".equals(c.name())) {
//                            snuid = c.value();
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }


    }
}
