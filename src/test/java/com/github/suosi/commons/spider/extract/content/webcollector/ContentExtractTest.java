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
        String url = "http://www.bzqzf.gov.cn/interactionLive/detailJson/?detail_id=111&class_id=990?r=1542342734";
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
}