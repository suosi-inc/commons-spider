package com.github.suosi.commons.spider.extract.content.webcollector;

import com.github.suosi.commons.spider.extract.content.webcollector.contentextractor.ContentExtractor;
import com.github.suosi.commons.spider.extract.content.webcollector.contentextractor.News;
import com.github.suosi.commons.spider.extract.site.Parse;
import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import okhttp3.Response;
import org.junit.Test;

import java.io.IOException;


public class ContentExtractTest {
    @Test
    public void test() {
        String url = "http://www.mofcom.gov.cn/article/b/fwzl/201811/20181102812004.shtml";
        try (Response response = OkHttpUtils.client().newCall(OkHttpUtils.request(url)).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                byte[] bytes = response.body().bytes();
                String charset = CharsetUtils.guessCharset(bytes, response);
                String html = new String(bytes, charset);
                News newsByHtml = ContentExtractor.getNewsByHtml(html);
                System.out.println(newsByHtml.getTitle());
                System.out.println( Parse.parseArticleTitle(html));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}