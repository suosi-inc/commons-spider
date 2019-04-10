package com.github.suosi.commons.spider.extract.content.webcollector;

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
            System.out.println(response.code());
            if (response.isSuccessful() && response.body() != null) {
                byte[] bytes = response.body().bytes();
                String charset = CharsetUtils.guessCharset(bytes, response);
                String html = new String(bytes, charset);

                Article newsByHtml = ContentExtract.getNewsByHtml(html);
                System.out.println(newsByHtml);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}