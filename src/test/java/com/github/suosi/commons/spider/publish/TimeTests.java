package com.github.suosi.commons.spider.publish;

import com.github.suosi.commons.spider.extract.site.Parse;
import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import okhttp3.Response;
import org.junit.Test;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeTests {

    @Test
    public void testHasVeiry() {
        HashMap<String, String> urls = new HashMap<String,String>() {
            {
                put("http://www.xinhuanet.com/2019-03/25/c_1124279677.htm", "2019-03-25");
                put("https://www.18183.com/yxzjol/201903/1918691.html", "2019-03-22");
                put("http://finance.eastmoney.com/a/201904161097225717.html", "2019-04-16");
                put("https://www.autohome.com.cn/news/201903/100215882.html", "2019-03-08");
                put("http://stock.eastmoney.com/a/201904051089554756.html", "2019-04-05");
                put("http://news.hebei.com.cn/system/2019/04/09/019567589.shtml", "2019-04-09");
                put("https://www.gamersky.com/review/201904/1172364.shtml", "2019-04-13");
                put("https://www.gamersky.com/hardware/201810/1118388.shtml?tag=wap", "2018-11-22");
                put("http://news.bitauto.com/info/9360344.html", "2019-04-16");
                put("http://news.bitauto.com/xinchexiaoxi/20190209/0709281272.html", "2019-02-10");
                put("http://news.bitauto.com/hao/wenzhang/1194467", "2019-04-03");
                put("https://www.takefoto.cn/viewnews-1757723.html", "2019-04-15");
                put("http://auto.sina.com.cn/news/hy/2019-04-09/detail-ihvhiewr4293148.shtml", "2019-04-09");
                put("http://365jia.cn/news/2018-12-17/8A1B0D93EAA4F1CB.html", "2018-12-18");
                put("http://365jia.cn/news/2019-04-14/E2DD9521B5DFB37D.html", "2019-04-14");
                put("http://news.m.yiche.com/hao/wenzhang/30011872", "2019-04-15");
                put("http://zjnews.zjol.com.cn/zjnews/nbnews/201904/t20190411_9875072.shtml", "2019-04-10");
                put("https://36kr.com/p/5188134.html", "2019-03-24");
                put("https://36kr.com/coop/retail/post/5188446.html", "2019-03-25");
                put("https://sports.eastday.com/a/191220105653388000000.html", "2019-12-20");
                put("https://news.hexun.com/2019-04-16/196838758.html", "2019-04-16");
                put("http://news.hexun.com/2018-07-20/193522374.html", "2018-07-20");
            }
        };
        int total = 0;
        int success = 0;
        for (String url : urls.keySet()) {
            total += 1;
            String time = getHtml(url);
            System.out.println(url + ", " + urls.get(url)+ ", " + time.substring(0, 10));
            if (urls.get(url).equals(time.substring(0, 10))) {
                success += 1;
            }
        }
        System.out.println(String.format("total: %d, success: %d", total, success));
    }

    @Test
    public void test() {
        String url = "https://www.zbytb.com/s-zb-6631607.html";
        String time = getHtml(url);
        System.out.println(time);
    }

    @Test
    public void testReg() {
        String html = "发布时间为5月18日-19日，各地面试公告陆续发布";
        html = Pattern.compile("发布时间").matcher(html).replaceAll("发_ab_布_cd_时间");
        html = Pattern.compile("[\\u4e00-\\u9fa5]+时间").matcher(html).replaceAll("");
        html = Pattern.compile("发_ab_布_cd_时间").matcher(html).replaceAll("发布时间");
        System.out.println(html);
        System.exit(-11);

        String str = " <div class=\"time\">2019年04月16日 00:07</div>\n" +
                "            <div class=\"source data-source\" data-source=\"21世纪经济报道\">\n" +
                "            <span>来源：</span>\n" +
                "                <a href=\"http://media.eastmoney.com/MediaView.aspx?mediaName=21cn\" target=\"_blank\">21世纪经济报道</a>";

        // String pattern = "(?i)(((发布|创建|出版|发表|编辑|星期|来源|时间)(时间|于|日期)?.{0,50}20\\d{2}[-/年.](0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?)|(20\\d{2}[-/年.](0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?.{0,50}(发布|创建|出版|发表|编辑|星期|来源|时间)(时间|于|日期)))";
        // String pattern = "(?i)(((发布|创建|出版|发表|编辑|星期|来源|时间)(时间|于|日期).{0,50}(20)?(\\d{2}[-/年.])?(0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?)|((20)?(\\d{2}[-/年.])?(0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?.{0,50}(发布|创建|出版|发表|编辑|星期|来源|时间)(时间|于|日期)))";
        // String pattern = "(发布|创建|出版|发表|编辑|星期|来源|时间).*?20\\d{2}[-/年.](0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?";
        String pattern = "(20)?(\\d{2}[-/年.])?(0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?[\\s\\S]{0,150}(发布|创建|出版|发表|编辑|星期|来源|时间)(时间|于|日期)?";
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
