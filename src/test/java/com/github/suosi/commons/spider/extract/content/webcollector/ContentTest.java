package com.github.suosi.commons.spider.extract.content.webcollector;

import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.HttpsUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ContentTest {
    @Test
    public void test() {
        HttpsUtils.SSLParams sslSocketFactory = HttpsUtils.getSslSocketFactory(null, null, null);

        // 请求连接池
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .sslSocketFactory(sslSocketFactory.sSLSocketFactory, sslSocketFactory.trustManager)
                .hostnameVerifier((hostname, session) -> true)
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .readTimeout(10000, TimeUnit.MILLISECONDS)
                .writeTimeout(10000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .build();

        String[] urls = {
                "http://shanghai.xinmin.cn/xmsq/2019/03/26/31506198.html"
        };
        for (String url : urls)
            try (Response response = okHttpClient.newCall(new Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                    .build()
            ).execute()) {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        byte[] bytes = body.bytes();
                        String charset = CharsetUtils.guessEncoding(bytes);
                        String html = new String(bytes, charset);

                        Article article = Content.getNewsByHtml(html);
                        System.out.println(article.getTitle());
                        System.out.println(article.getTime());
                        System.out.println(article.getContent());
                    }
                }


            } catch (Exception e) {
                System.out.println(url + " " + e.getLocalizedMessage());
            }
    }

}