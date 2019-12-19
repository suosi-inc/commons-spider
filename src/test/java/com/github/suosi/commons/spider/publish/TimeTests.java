package com.github.suosi.commons.spider.publish;

import com.github.suosi.commons.spider.extract.site.Parse;
import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import okhttp3.Response;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeTests {
    @Test
    public void test() {
        String url = "http://news.lcxw.cn/zt/liaocheng/dqwrdc/wtzg/2019-04-14/818538.html";
        String time = getHtml(url);
        System.out.println(time);
    }

    @Test
    public void testReg() {
        String str = " <div class=\"else\"> <h2> <span>来源 2019-04-04 08:23</span> </h2> <p class=\"btns\">|";

        String pattern = "(?i)(((发布|创建|出版|发表|编辑|星期|来源|时间)(时间|于|日期)?.{0,50}20\\d{2}[-/年.](0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?)|(20\\d{2}[-/年.](0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?.{0,50}(发布|创建|出版|发表|编辑|星期|来源|时间)(时间|于|日期)))";
        // String pattern = "(?i)(((发布|创建|出版|发表|编辑|星期|来源|时间)(时间|于|日期).{0,50}(20)?(\\d{2}[-/年.])?(0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?)|((20)?(\\d{2}[-/年.])?(0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?.{0,50}(发布|创建|出版|发表|编辑|星期|来源|时间)(时间|于|日期)))";
        // String pattern = "(发布|创建|出版|发表|编辑|星期|来源|时间).*?20\\d{2}[-/年.](0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(str);
        if (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    @Test
    public void time() {
        String html = "<div class=\"entry-info\">\n" +
                "\t\t\t\t<span>2018-11-14 12:23:38</span>\n" +
                "\t\t\t\t<span class=\"dot\">•</span>\n" +
                "\t\t\t  <span>来源：</span>\n" +
                "\t\t\t</div>\n";

        System.out.println(Parse.parsePublishTime(html, ""));
    }

    private static String getHtml(String url) {
        try (Response response = OkHttpUtils.client().newCall(OkHttpUtils.request(url)).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                byte[] htmlBytes = response.body().bytes();
                String charset = CharsetUtils.guessCharset(htmlBytes, response);
                String html = new String(htmlBytes, charset);
                return Parse.parsePublishTime(html, url);
            }

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage() + ":" + url);
        }

        return "";
    }


}
