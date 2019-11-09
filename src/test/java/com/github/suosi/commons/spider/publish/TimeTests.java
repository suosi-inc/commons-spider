package com.github.suosi.commons.spider.publish;

import com.github.suosi.commons.spider.extract.site.Parse;
import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import okhttp3.Response;
import org.junit.Test;

public class TimeTests {
    @Test
    public void test() {
        String url = "https://www.discuz.net/forum-plugin-1.html";
        String time = getHtml(url);
        System.out.println(time);
    }

    @Test
    public void time() {
        String html = "<div class=\"entry-info\">\n" +
                "\t\t\t\t<span>2018-11-14 12:23:38</span>\n" +
                "\t\t\t\t<span class=\"dot\">•</span>\n" +
                "\t\t\t  <span>来源：</span>\n" +
                "\t\t\t</div>\n";

        System.out.println(Parse.parsePublishTime(html));
    }

    private static String getHtml(String url) {
        try (Response response = OkHttpUtils.client().newCall(OkHttpUtils.request(url)).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                byte[] htmlBytes = response.body().bytes();
                String charset = CharsetUtils.guessCharset(htmlBytes, response);
                String html = new String(htmlBytes, charset);
                return Parse.parsePublishTime(html);
            }

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage() + ":" + url);
        }

        return "";
    }

}
