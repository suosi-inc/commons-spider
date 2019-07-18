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
        String url = "https://weibo.com/1751960002/HBz0ns9eM?type=comment";
        String time = getHtml(url);
        System.out.println(time);
    }

    @Test
    public void time() {
        String timeReg = "(20\\d{2})\\D.?([0-1]?\\d)\\D?([0-3]?\\d)((\\D{0,2})?(\\d{1,2}\\D\\d{1,2})(\\D\\d{1,2})?)?";
        String html = "2018年7月24日下午4:00-5:00";

        System.out.println(Parse.parsePublishTime(html));
        Pattern r = Pattern.compile(timeReg);
        Matcher matcher = r.matcher(html);
        if (matcher.find()) {
            System.out.println(matcher.group());
        }
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
