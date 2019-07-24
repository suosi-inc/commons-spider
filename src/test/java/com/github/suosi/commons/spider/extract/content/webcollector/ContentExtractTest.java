package com.github.suosi.commons.spider.extract.content.webcollector;

import com.github.suosi.commons.spider.extract.content.webcollector.contentextractor.ContentExtractor;
import com.github.suosi.commons.spider.extract.content.webcollector.contentextractor.News;
import com.github.suosi.commons.spider.extract.site.PageExtract;
import com.github.suosi.commons.spider.extract.site.Parse;
import com.github.suosi.commons.spider.extract.site.meta.Page;
import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import okhttp3.Response;
import org.junit.Test;

import java.io.IOException;


public class ContentExtractTest {
    @Test
    public void test() {
        String url = "http://www.jinchangnews.cn/content/2019-07/24/46_30893.html";
        try (Response response = OkHttpUtils.client().newCall(OkHttpUtils.request(url)).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                byte[] bytes = response.body().bytes();
                String charset = CharsetUtils.guessCharset(bytes, response);
                String html = new String(bytes, charset);
                News newsByHtml = ContentExtractor.getNewsByHtml(html);
//                System.out.println(newsByHtml);
                System.out.println(Parse.parsePublishTime(html));
                System.out.println(newsByHtml.getTitle());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        String url = "http://www.jinchangnews.cn/content/2019-07/24/46_30893.html";
        try (Response response = OkHttpUtils.client().newCall(OkHttpUtils.request(url)).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                byte[] bytes = response.body().bytes();
                String charset = CharsetUtils.guessCharset(bytes, response);
                String html = new String(bytes, charset);

                Page html1 = PageExtract.html(html, url);

                System.out.println(html1.getLinks());
                System.out.println(html1.getTitle());

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test3() {
        String url = "http://www.jinchangnews.cn/content/2019-07/24/46_30893.html";
        Page html1 = PageExtract.url(url);
        assert html1 != null;
        System.out.println(html1.getLinks());
        System.out.println(html1.getTitle());
    }
}