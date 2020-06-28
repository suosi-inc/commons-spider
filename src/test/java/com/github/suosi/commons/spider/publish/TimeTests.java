package com.github.suosi.commons.spider.publish;

import com.github.suosi.commons.spider.extract.site.Parse;
import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
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
                put("http://bbs.suyoo.cn/forum.php?mod=viewthread&tid=1137228&extra=page%3D3%26filter%3Dlastpost%26orderby%3Dlastpost%26orderby%3Dlastpost", "2017-11-09");
                put("http://www.pz.gov.cn/pzweb/xinxiang/XJDetail.aspx?HistoryGuid=0a3665bb-7fb7-4a05-a95a-27b200f8dd81&boxGuid=97c9918c-8575-4a8c-bf29-9a4158d4e82c", "2020-04-05");
                put("https://bbs.gdmm.com/thread-2663109-1-2.html", "2013-02-05");
                put("http://www.tmbbs.com/forum.php?mod=viewthread&tid=118120&extra=page%3D10%26filter%3Dtypeid%26typeid%3D40", "2011-10-09");
                put("http://www.tmbbs.com/forum.php?mod=viewthread&tid=75601&extra=page%3D43%26filter%3Dreply%26orderby%3Dviews", "2011-07-12 ");
                put("http://www.tmbbs.com/forum.php?mod=viewthread&tid=118120&extra=page%3D9%26filter%3Dtypeid%26typeid%3D40%26orderby%3Ddateline", "2011-10-09");
                put("http://finance.china.com.cn/industry/ny/20120214/532476.shtml", "2012-02-14");

            }
        };
        int total = 0;
        int success = 0;
        for (String url : urls.keySet()) {
            total += 1;
            String time = getHtml(url);
            System.out.println(url + ", " + urls.get(url)+ ", " + time);
            if (time.length() > 0 && urls.get(url).equals(time.substring(0, 10))) {
                success += 1;
            }
        }
        System.out.println(String.format("total: %d, success: %d", total, success));
    }

    @Test
    public void test() {
        String url = "http://www.afinance.cn/gp/gsxw/201207/464349.html";
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
        String html = "\n" +
                "<!-- end plus area -->\n" +
                "<!-- main area -->\n" +
                "<div class=\"main final fn-clear\">\n" +
                "\t<div class=\"wrap fn-clear\">\n" +
                "    \t<div class=\"breadcrumb fn-left\">\n" +
                "        \t您现在的位置：&nbsp;<a class='LinkPath' href='http://rumen.southmoney.com'>股票入门</a>&nbsp;>>&nbsp;<a class='LinkPath' href='/jbmfx/'>股票基本面分析</a>&nbsp;>>&nbsp;<a class='LinkPath' href='/jbmfx/hgfx/'>宏观分析</a>&nbsp;>>&nbsp;正文\n" +
                "        </div>        \n" +
                "    \t<div class=\"col1 fn-left\">\n" +
                "\t\t\t<div class=\"article\">\n" +
                "            \t<h1 class=\"artTitle\">8月3日周一股市开盘走势 </h1>\n" +
                "                <p class=\"artDate\">2015-8-3 10:51:58 股票入门 http://rumen.southmoney.com</p>\n" +
                "                <div class=\"gg-3\"><script src='/page/inc/tuijian.js' language='javascript'></script></div>\n" +
                "                <div class=\"articleCon\"> <div style=\"FLOAT: right;\">\n" +
                "<script language=\"javascript\" src=\"/page/inc/l001.js\"></script> \n" +
                "</div><P>　　8月3日周一<STRONG>股市<a class=\"channel_keylink\" href=\"http://rumen.southmoney.com/gsjj/kaipan/\">开盘</a>走势</STRONG>。下面南方财富网的小编为您整理介绍8月3日周一股市开盘走势。</P>\n" +
                "<P>　　今天是8月第一个股市交易日，也是本周股市首个交易日，不知道今天的全球股市具体走势会是如何？</P>\n" +
                "<P>　　A股市场</P>\n" +
                "<P>　　周五A股市场大幅下跌。上证指数收报3，663.73点，跌幅1.13%；深证成指收报12，374.25点，跌幅0.17%。沪深两市全日共<a class=\"channel_keylink\" href=\"http://rumen.southmoney.com/gprm/chengjiao/\">成交</a>7，871.52亿元，较前一交易日缩小25.35%。中小板指报8，336.12点，涨幅0.36%，<a class=\"channel_keylink\" href=\"http://rumen.southmoney.com/chuangyeban/\">创业板</a>指报2，539.84点，跌幅0.83%。盘面上，29个中信一级行业指数跌多涨少，跌幅居前的板块为国防军工、农林牧渔、计算机和餐饮旅游等。</P>\n" +
                "<P>　　香港市场</P>\n" +
                "<P>　　港股周五反复上涨；截至收盘，恒生指数报24，636点，涨0.56%；国企指数跑输大市，报11，131点，跌0.05%。大市成交萎缩至777亿港元。中资板块普遍下跌，电讯板块支？ 撑大市，涨1.5%。燃气股普遍造好，华润燃气 （01193.HK） 及中国燃气 （00384.HK） 分别涨9.0和6.3%。中资保险股逆市受压，平均下跌1.0%。个股方面，东吴水泥 （00695.HK） 受投资者追捧，股价涨12.1%。</P>\n" +
                "<P>　　海外市场</P>\n" +
                "<P>　　周五纽约原油期货下跌。NYMEX原油期货收盘报47.12美元，下跌1.40美元，跌幅2.89%。伦敦ICE欧洲期货<a class=\"channel_keylink\" href=\"http://rumen.southmoney.com/gprm/jys/\">交易所</a>北海布伦特原油期货报52.21美元，下跌1.10美元，跌幅2.06%。</P>\n" +
                "<P>　　美国股市下跌，道琼斯工业指数下跌56.12点，收盘报17，689.86点，跌幅0.32%；标准普尔500指数下跌4.79点，报2103.84点，跌幅0.23%；纳斯达克指数下跌0.5 点，报5，128.28点，跌幅0.01%。亚太主要股指涨多跌少，欧洲主要股指涨多跌少。</P>\n" +
                "<P>　　更多8月3日周一股市开盘走势的相关信息请关注南方财富网。</P>\n" +
                "<script language=\"javascript\" src=\"/page/inc/l002.js\"></script>  \n" +
                "                </div>\n" +
                "                <div class=\"articleFoot\">\n" +
                "                \t<div class=\"tip\">\n" +
                "                    <p><span class=\"fn-left\"><script language=\"javascript\" src=\"/page/inc/l003.js\"></script></span>\n" +
                "                    \t<span class=\"fn-right\">";

        String pattern = "(?i)(((发布|创建|出版|发表|编辑)(时间|于|日期)[\\s\\S]{0,350}20\\d{2}[-/年.](0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?)|(20\\d{2}[-/年.](0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?[\\s\\S]{0,350}(发布|创建|出版|发表|编辑)(时间|于|日期)))";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(html);
        if (matcher.find()) {
            System.out.println(matcher.group());
        } else {
            System.out.println("error");
        }

    }

    private static String getHtml(String url) {
        try (Response response = OkHttpUtils.client().newCall(OkHttpUtils.request(url)).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                byte[] htmlBytes = response.body().bytes();
                String charset = CharsetUtils.guessCharset(htmlBytes, response);
                String html = new String(htmlBytes, charset);
                // System.out.println(html);
                return Parse.parsePublishTime(html, url);
            }

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage() + ":" + url);
        }

        return "";
    }


    @Test
    public void testUrlList() {
        String[] urls = urls();
        for (String url : urls) {
            String time = getHtml(url);
            System.out.println(url + ", " + time);
        }
    }

    private String[] urls() {
        String[] urls = {
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=LJfXpK7cMn5HGlMLpzQRlTBvLvLzKGm319XMPnSmhYpsxN2SSSsX!-1416659116!1577761788496?id=201171",
                "http://xsc.dlvtc.edu.cn/web/guest/home;jsessionid=Ts9QpK7bS99BTLn2TQ0TmTnjcFvhsgP7FYVVBb5Kyj1s54Qkx3wX!-1416659116!1577761787101",
                "http://sy.fangxiaoer.com/shopsell;jsessionid=5DA8BD32443A2CBBD8394E48323523FA",
                "http://xsc.dlvtc.edu.cn/web/guest/11;jsessionid=Ts9QpK7bS99BTLn2TQ0TmTnjcFvhsgP7FYVVBb5Kyj1s54Qkx3wX!-1416659116!1577761787101",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=LJfXpK7cMn5HGlMLpzQRlTBvLvLzKGm319XMPnSmhYpsxN2SSSsX!-1416659116!1577761788496?id=869268",
                "http://sy.fangxiaoer.com/shops/b1;jsessionid=2F5A3F262FE5D5FC0636F700B678D116",
                "http://taopinquan.cn/index.php?r=every/miu&id=3222596&u=1097464&is_pc=1",
                "http://sy.fangxiaoer.com/helpSearch;jsessionid=77F7D3C9BC92AF710B7EE96944C4EDDA?ids=1",
                "https://www.xyaz.cn/gc/apps-3574946-1.html",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=Ts9QpK7bS99BTLn2TQ0TmTnjcFvhsgP7FYVVBb5Kyj1s54Qkx3wX!-1416659116!1577761787101?id=77441",
                "http://taopinquan.cn/index.php?r=every/miu&id=3223223&u=1097464&is_pc=1",
                "http://zhaopin.tjau.edu.cn/newsInfo;jsessionid=83BC71C2F4DB7693A1E16482F1102DA1?id=1148111677220384768",
                "http://sy.fangxiaoer.com/helpSearch;jsessionid=2F5A3F262FE5D5FC0636F700B678D116?ids=1",
                "https://liudediy.com/Dgyk/detail/id/R0l3dndIQWIxSHVqUw%253D%253D.html",
                "http://www.mnvym.com/vx680c/22508-89810-29716.html",
                "http://www.fyyp.net/chushou/show/28921/",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=LJfXpK7cMn5HGlMLpzQRlTBvLvLzKGm319XMPnSmhYpsxN2SSSsX!-1416659116!1577761788496?id=871544",
                "http://sy.fangxiaoer.com/helpSearch;jsessionid=7A6A566DF8B0A225BCEC6C4C4D4C2C07?ids=4",
                "http://taopinquan.cn/index.php?r=every/miu&id=3223214&u=1097464&is_pc=1",
                "http://sy.fangxiaoer.com/shops/b1;jsessionid=8DB48DB717728FF5B62AF3E0B8A4EB0E",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=LJfXpK7cMn5HGlMLpzQRlTBvLvLzKGm319XMPnSmhYpsxN2SSSsX!-1416659116!1577761788496?id=869195",
                "http://taopinquan.cn/index.php?r=every/miu&id=3222586&u=1097464&is_pc=1",
                "http://xsc.dlvtc.edu.cn/web/guest/45;jsessionid=LJfXpK7cMn5HGlMLpzQRlTBvLvLzKGm319XMPnSmhYpsxN2SSSsX!-1416659116!1577761788496",
                "http://zhaopin.tjau.edu.cn/newsInfo;jsessionid=83BC71C2F4DB7693A1E16482F1102DA1?id=1154267426027925504",
                "http://sy.fangxiaoer.com/shops/b2;jsessionid=2F3FD03B840FA6D3CA3E61C16AC07DBF",
                "http://xsc.dlvtc.edu.cn/web/guest/46;jsessionid=Ts9QpK7bS99BTLn2TQ0TmTnjcFvhsgP7FYVVBb5Kyj1s54Qkx3wX!-1416659116!1577761787101",
                "http://www.fyyp.net/caigo/show/14169/",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=pGmTpK7Y8HZflvkp7QVwYrpzvpP14NkhrGdBHLqkZgH1kL1G8FNc!-1416659116!1577761784973?id=77440",
                "http://www.htsjj1688.com/supply/4199209244.html",
                "http://www.fyyp.net/chushou/show/28938/",
                "https://www.ks95.com/hollywood-hot-dish-206/",
                "http://www.htsjj1688.com/supply/4199214468.html",
                "http://xsc.dlvtc.edu.cn/web/guest/43;jsessionid=Ts9QpK7bS99BTLn2TQ0TmTnjcFvhsgP7FYVVBb5Kyj1s54Qkx3wX!-1416659116!1577761787101",
                "http://sy.fangxiaoer.com/secondPublish/;jsessionid=74DBD00E168D02ECFB66B29365CCFAE5",
                "http://taopinquan.cn/index.php?r=every/miu&id=3223460&u=1097464&is_pc=1",
                "http://qiche.wenshang.tv/shikebiao23699/",
                "http://xsc.dlvtc.edu.cn/web/guest/11;jsessionid=LJfXpK7cMn5HGlMLpzQRlTBvLvLzKGm319XMPnSmhYpsxN2SSSsX!-1416659116!1577761788496",
                "http://taopinquan.cn/index.php?r=every/miu&id=3223229&u=1097464&is_pc=1",
                "http://www.jufantianxia.kim/index.php?r=every/miu&id=3222586&u=683759&is_pc=1",
                "http://xsc.dlvtc.edu.cn/web/guest/2;jsessionid=LJfXpK7cMn5HGlMLpzQRlTBvLvLzKGm319XMPnSmhYpsxN2SSSsX!-1416659116!1577761788496",
                "http://sy.fangxiaoer.com/shops/b1;jsessionid=87DD133693EA65E704ECDE05ECF56F6B",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=LJfXpK7cMn5HGlMLpzQRlTBvLvLzKGm319XMPnSmhYpsxN2SSSsX!-1416659116!1577761788496?id=872261",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=LJfXpK7cMn5HGlMLpzQRlTBvLvLzKGm319XMPnSmhYpsxN2SSSsX!-1416659116!1577761788496?id=77445",
                "http://my.quanxi.cc/home/teacher.aspx?uid=493072",
                "http://www.fyyp.net/chushou/show/28946/",
                "http://www.webup247.com/6/05xxvaizf.html",
                "https://dealer.autohome.com.cn/11086/order_2115_31580.html?eid=1|4|522|2978|203375|300951&Siteid=97&enfrom=1npc10000155#pvareaid=2113135",
                "http://sy.fangxiaoer.com/shopsell;jsessionid=617EF99E07F9865D21CF3E142DFEF029",
                "http://www.jufantianxia.kim/index.php?r=every/miu&id=3223223&u=683759&is_pc=1",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=Ts9QpK7bS99BTLn2TQ0TmTnjcFvhsgP7FYVVBb5Kyj1s54Qkx3wX!-1416659116!1577761787101?id=872261",
                "http://www.fyyp.net/chushou/show/28939/",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=LJfXpK7cMn5HGlMLpzQRlTBvLvLzKGm319XMPnSmhYpsxN2SSSsX!-1416659116!1577761788496?id=869199",
                "http://www.fyyp.net/chushou/show/28923/",
                "http://sy.fangxiaoer.com/helpSearch;jsessionid=E8F5DA88A38754DE98C8EC160F8713C0?ids=4",
                "http://xsc.dlvtc.edu.cn/web/guest/home;jsessionid=pGmTpK7Y8HZflvkp7QVwYrpzvpP14NkhrGdBHLqkZgH1kL1G8FNc!-1416659116!1577761784973",
                "http://sy.fangxiaoer.com/scriptoriums/;jsessionid=87DD133693EA65E704ECDE05ECF56F6B",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=LJfXpK7cMn5HGlMLpzQRlTBvLvLzKGm319XMPnSmhYpsxN2SSSsX!-1416659116!1577761788496?id=77440",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=Ts9QpK7bS99BTLn2TQ0TmTnjcFvhsgP7FYVVBb5Kyj1s54Qkx3wX!-1416659116!1577761787101?id=869232",
                "http://sy.fangxiaoer.com/scriptoriums/;jsessionid=77F7D3C9BC92AF710B7EE96944C4EDDA",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=Ts9QpK7bS99BTLn2TQ0TmTnjcFvhsgP7FYVVBb5Kyj1s54Qkx3wX!-1416659116!1577761787101?id=201171",
                "http://xsc.dlvtc.edu.cn/web/guest/6;jsessionid=Ts9QpK7bS99BTLn2TQ0TmTnjcFvhsgP7FYVVBb5Kyj1s54Qkx3wX!-1416659116!1577761787101",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=pGmTpK7Y8HZflvkp7QVwYrpzvpP14NkhrGdBHLqkZgH1kL1G8FNc!-1416659116!1577761784973?id=869203",
                "http://sy.fangxiaoer.com/helpSearch;jsessionid=D1298A263DC6A56516052FA6155C7082?ids=3",
                "http://www.fyyp.net/chushou/show/28945/",
                "http://www.fyyp.net/chushou/show/28922/",
                "http://sy.fangxiaoer.com/shopsell;jsessionid=E8F5DA88A38754DE98C8EC160F8713C0",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=LJfXpK7cMn5HGlMLpzQRlTBvLvLzKGm319XMPnSmhYpsxN2SSSsX!-1416659116!1577761788496?id=77430",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=LJfXpK7cMn5HGlMLpzQRlTBvLvLzKGm319XMPnSmhYpsxN2SSSsX!-1416659116!1577761788496?id=871536",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=pGmTpK7Y8HZflvkp7QVwYrpzvpP14NkhrGdBHLqkZgH1kL1G8FNc!-1416659116!1577761784973?id=872251",
                "http://qiche.wenshang.tv/shikebiao19031/",
                "http://www.fyyp.net/chushou/show/28947/",
                "http://xj.dlvtc.edu.cn/web/guest/more-list;jsessionid=Ts9QpK7bS99BTLn2TQ0TmTnjcFvhsgP7FYVVBb5Kyj1s54Qkx3wX!-1416659116!1577761787101?id=921787",
                "http://www.jufantianxia.kim/index.php?r=every/miu&id=3223219&u=683759&is_pc=1",
                "http://www.fyyp.net/chushou/show/28935/",
                "http://sy.fangxiaoer.com/scriptoriums/;jsessionid=7A6A566DF8B0A225BCEC6C4C4D4C2C07",
                "http://www.fyyp.net/chushou/show/28948/",
                "http://xsc.dlvtc.edu.cn/web/guest/home;jsessionid=sZgJpK7XmSpgTTnhpsVQyL0fjC4j0TdPsYPLQ2yJ0TnvGfTPLCrY!-1416659116!1577761783705",
                "http://projectinfo.bhi.com.cn/Projects/BiddingDetail.aspx?id=27187582&d=11111250201931",
                "http://projectinfo.bhi.com.cn/Projects/BiddingDetail.aspx?id=27199146&d=11111257201931",
                "http://www.jufantianxia.kim/index.php?r=every/miu&id=3224281&u=683759&is_pc=1",
                "http://sy.fangxiaoer.com/freeServiceIndex;jsessionid=646184BF0A18E270D55DC83E95061D86",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=Ts9QpK7bS99BTLn2TQ0TmTnjcFvhsgP7FYVVBb5Kyj1s54Qkx3wX!-1416659116!1577761787101?id=77430",
                "http://projectinfo.bhi.com.cn/Projects/BiddingDetail.aspx?id=27221288&d=11111250201931",
                "http://sy.fangxiaoer.com/scriptoriums/;jsessionid=57E9DA6CC9B75F80ADB63A4971855E87",
                "http://projectinfo.bhi.com.cn/Projects/BiddingDetail.aspx?id=27187371&d=11111250201931",
                "http://www.xw59c.cn/q1jjtj/92476-42505-12396.html",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=Ts9QpK7bS99BTLn2TQ0TmTnjcFvhsgP7FYVVBb5Kyj1s54Qkx3wX!-1416659116!1577761787101?id=869264",
                "http://www.webup247.com/20195/qq_01_kyej.html",
                "http://projectinfo.bhi.com.cn/Projects/Project_Details.aspx?id=27184601&d=11111250201931",
                "http://www.webup247.com/5/670xx18ekv.html",
                "http://www.fyyp.net/caigo/show/14167/",
                "http://sy.fangxiaoer.com/officeRent;jsessionid=23468AAB95D28FB3DCFC93D9994B8C22",
                "http://www.fyyp.net/chushou/show/28937/",
                "http://lygtjbj.com/world/55279.html",
                "http://www.fyyp.net/chushou/show/28949/",
                "http://xsc.dlvtc.edu.cn/web/guest/more-list;jsessionid=pGmTpK7Y8HZflvkp7QVwYrpzvpP14NkhrGdBHLqkZgH1kL1G8FNc!-1416659116!1577761784973?id=869195",
                "https://shop.fang.com/shou/3_441294731.html",
                "http://sy.fangxiaoer.com/rentwhole;jsessionid=23468AAB95D28FB3DCFC93D9994B8C22",
                "http://xsc.dlvtc.edu.cn/web/guest/more-detail;jsessionid=sZgJpK7XmSpgTTnhpsVQyL0fjC4j0TdPsYPLQ2yJ0TnvGfTPLCrY!-1416659116!1577761783705?articleId=2F195673-1756-7739-4EF7-D6F6C5852822",
                "http://sy.fangxiaoer.com/helpSearch;jsessionid=D1298A263DC6A56516052FA6155C7082?ids=1"
        };

        return urls;
    }
}
