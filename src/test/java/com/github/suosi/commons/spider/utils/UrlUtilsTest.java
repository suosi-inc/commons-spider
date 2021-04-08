package com.github.suosi.commons.spider.utils;

import com.github.suosi.commons.spider.extract.site.Parse;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtilsTest {

    /**
     * 文章 URL 列表测试
     */
    @Test
    public void guessArticleList() {
        String[] urls = urls();
        int count = 0;

        ArrayList<String> articles = new ArrayList<>();
        ArrayList<String> lists = new ArrayList<>();
        ArrayList<String> nones = new ArrayList<>();

        for (String url : urls) {
            if (UrlUtils.guessArticleUrl(url, null)) {
                articles.add(url);
                count++;
            } else if (UrlUtils.guessListUrl(url, null)) {
                lists.add(url);
            } else {
                nones.add(url);
            }
        }

        for (String article : articles) {
            System.out.println("A -> " + article);
        }
        for (String list : lists) {
            System.out.println("L -> " + list);
        }
        for (String none : nones) {
            System.out.println("N -> " + none);
        }

        System.out.println(urls.length);
        System.out.println(count);

    }

    /**
     * 文章 URL 列表测试
     */
    @Test
    public void guessTxtArticleList() {
        int count = 0;
        int total_count = 0;

        String filename = "es_wangmei_urls_201909.txt";
        try {
            /* 读入TXT文件 */
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            line = br.readLine();
            while (line != null) {
                line = br.readLine(); // 一次读入一行数据
                String url = line.replace("\"", "");
                if (UrlUtils.guessArticleUrl(url, null)) {
                    // System.out.println("A -> " + url);
                    count++;
                } else if (UrlUtils.guessListUrl(url, null)) {
                    System.out.println("L -> " + url);
                } else {
                    System.out.println("N -> " + url);
                }
                total_count += 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(total_count);
        System.out.println(count);
        System.out.println(String.format("%.4f%s" ,
                Double.parseDouble(String.valueOf((float)count*100 / (float)total_count)), "%"));


    }

    /**
     * 文章 URL 测试
     */
    @Test
    public void testPattern() {
        String url = "http://www.siyst.org.cn/portal/article/index/id/12/cid/1.html";

        Pattern LIST_KEYWORD_FILTER_PATTERN = Pattern.compile(
                "/\\w+/\\d+/\\w+/\\d+",
                Pattern.CASE_INSENSITIVE);

        Matcher aaa = LIST_KEYWORD_FILTER_PATTERN.matcher(url);
        if (aaa.find()) {
            System.out.println(aaa.group());
        }

        System.exit(-1);
    }

    /**
     * 文章 URL 测试
     */
    @Test
    public void guessArticleUrl() {
        String url = "https://www.zbytb.com/s-zb-6631607.html";

        if (UrlUtils.guessArticleUrl(url, "zbytb.com")) {
            System.out.println("A -> " + url);
        } else if (UrlUtils.guessListUrl(url, "zbytb.com")) {
            System.out.println("L -> " + url);
        } else {
            System.out.println("N -> " + url);
        }
    }

    @Test
    public void absoluteUrl() {
        //绝对路径
        String absolutePath = "http://www.aaa.com/a";
        String relativePath = "../abc/";

        URL absoluteUrl = null;
        try {
            absoluteUrl = new URL(absolutePath);

            URL parseUrl = new URL(absoluteUrl, relativePath);
            System.out.println(parseUrl.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseUrl() {
        // String url = "http://jyj.suqian.gov.cn/../sjyj/mbjyjygl/list_wz.shtml";
//         String url = "http://www.jptour.cn/../../../../s_毛里求斯.html";
         String url = "http://www.jptour.cn/abc/../../../../s_毛里求斯.html";
        // String url = "http://zggdjy.jyb.cn/./qkgk/";
//        String url = "https://www.baidu.com";

        try {
            URL absoluteUrl = new URL(url);
            System.out.println(absoluteUrl);
            System.out.println(absoluteUrl.getHost());
            System.out.println(absoluteUrl.getProtocol());
            String file = absoluteUrl.getFile();
            file = StringUtils.removeStart(file, "/");
            file = Parse.removeStartComplete(file, "./");
            file = Parse.removeStartComplete(file, "../");
            URL parseUrl =  new URL(absoluteUrl.getProtocol() + "://" + absoluteUrl.getHost() + "/");

            URL cleanUrl = new URL(parseUrl, file);
            String link = cleanUrl.toString();

            System.out.println(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 列表 URL 测试
     */
    @Test
    public void guessListUrl() {
        String[] urls = {
                "http://beijing.edushi.com/info",
                "http://beijing.edushi.com/info/",
                "http://beijing.edushi.com",
                "http://beijing.edushi.com/",
                "http://beijing.edushi.com/list_1234.html",
                "http://www.edushi.com/list_1234.html",
        };
        for (String url : urls) {
            if (UrlUtils.guessListUrl(url, "edushi.com")) {
                System.out.println(url);
            }
        }
    }


    private String[] urls() {
        String[] urls = {
                "http://china.findlaw.cn/city",
                "http://www.sh-deem.com/trade-show-wiki",
                "http://www.whicec.com/list/36.html",
                "http://www.tunbosh.com/kh.asp",
                "http://www.gxdapeng2.com/content/?637.html",
                "http://jingjiguanli.zzdl.com/Article/255.html",
                "http://www.mtnczj.cn",
                "http://www.apexsportsclub.com/ydxm",
                "http://job.lz520.net/index.php?m=&c=news&a=index",
                "http://www.jiaolian.store/face/star.php?star1=1808171538290A2C37",
                "http://tools.cocoachina.com/list/1033",
                "http://www.shcat.com.cn/news/18",
                "http://www.dx-cnc.cn/news/2",
                "http://www.hahqmy.com/product.asp?Classid=1",
                "http://www.ggbing.com/ipzl",
                "http://www.jstailong.com.cn/shownews.asp?id=86&BigClass=serves",
                "http://www.hand99.com/course/course/signup/cid/666.html",
                "http://www.ruanwen21.com/pinpai/15.html",
                "http://pep.exfree.com/Bookshelf/welcome.asp?Ptype=27",
                "http://www.hechuangup.com/?cate=17",
                "http://aboutus.le.com/link/index.shtml",
                "http://sz18565736246.tpy888.cn/contact/",
                "http://jiamei.linqu001.com",
                "http://www.health-way.cn/",
                "http://dflz.sdivc.edu.cn/llxx.htm",
                "http://www.spdl.com/product/",
                "https://wsmj.net/affiche.php?ad_id=92&uri=",
                "http://www.texnet.com.cn/mail/",
                "http://www.twfry.com/list_32.html",
                "http://commend.nmgnews.com.cn/nmgrb/xinbao/index.html",
                "http://news.dongyingnews.cn/gj/index.html",
                "http://www.hbzgzq.com/html/sashuiche/",
                "http://2018.wqhouse.com/index.php?caid=2&ccid18=197&addno=1",
                "http://www.dyolw.com/jk/yiyao/Index.html",
                "https://www.acwing.com/user/myspace/index/1055/",
                "http://www.wwwsd.cn/finance/",
                "http://www.jsjnw.org/",
                "http://www.gnpjbeer.cn/index.php?m=content&c=index&a=show&catid=18&id=82",
                "http://dzwjw.dezhou.gov.cn/n1306088/c52189621/content.html",
                "http://www.bribripr.com/news/1.html",
                "http://www.freshqiao.com/contact.html",
                "http://www.kidstarxm.com/_p18",
                "http://www.cnbaowen.net/member/",
                "http://www.jkinvest.cn/desktop/business.html",
                "http://www.boshigu.com/Index.aspx",
                "http://www.wlj99.com/alone/alone.php?id=59",
                "http://www.cdstm.cn/show.php?action=about",
                "http://www.redcocf.com/News/Default.aspx",
                "http://plsxxcl.chn0769.com/news/",
                "http://www.shjgnkyy.com/yyzj/",
                "http://mail.swupl.edu.cn/",
                "http://www.tjhwyb.cn/index.php/category/index/id/97.html",
                "http://www.kq188.com/Recr/SearchJobs.aspx",
                "http://www.sungeon.com/post.php?id=19",
                "http://cnyhwl.wz56w.com/ads.aspx",
                "http://www.ntyllh.com.cn/?gcgl/tzgg/259.html",
                "http://www.lgkyj.cn/cp/kyjpjhc/fs",
                "http://www.crrc.com/contact.html",
                "http://www.cqlinding.cn/html/product/bxhl/sbbxhl/110.html",
                "http://www.sapims.com:8080/sapimsSJ/index.jsp",
                "http://pcl.ynnubs.com",
                "http://www.wmwj.cn/href/Product.asp?/_ClassCode/101101/_ClassName/接插件2.html",
                "http://app.hgpu.cn",
                "http://www.duoyidoor.com/product.asp?id=32.html",
                "http://www.nongchan.com/index.php/index/cate/cid/3.html",
                "https://a564701126.qipei8.com/contact.html",
                "https://sbike.cn/d/",
                "http://www.njwhad.cn/list3.asp",
                "https://sad.yngsxy.net/8/aboutinfo/477/",
                "https://jwjc.jju.edu.cn/jdjb.htm",
                "http://ljt.liangjiang.gov.cn/service?classifyId=10043",
                "http://www.sdhuasi.com/phpinfo.php",
                "http://yunshiwen.cnelc.com/",
                "http://home.babytree.com/u/u72037115158/j/14103293",
                "http://www.dongfanglijing.com/about.asp",
                "http://www.apweituo.com/chanpin/50.html",
                "http://bbs.sg169.com/forum-39-1.html",
                "http://scnews.newssc.org/system/topic/7590/index.shtml",
                "http://www.1024sj.com/com/list-b703-330000.html",
                "http://www.hljms.gov.cn/tzms/",
                "http://bbs.chiznews.com/search.php?mod=forum&srchtxt=%B9%CA%D2%E2%B2%BB%CF%EB%C4%EE&formhash=e890d67b&searchsubmit=true&source=hotsearch",
                "http://bkjw.chd.edu.cn/eams/home.action",
                "https://gkzyjx.lmjx.net/weizhushigaokongzuoyepingtai/dingli/amwp1158100/param/",
                "http://www.dzrzs.cn/knowledge",
                "http://life.china.com.cn/node_1004384.html",
                "http://www.hncj.edu.cn/",
                "http://ganbu.zsdj.gov.cn/gbtj.asp",
                "https://www.yuebangjd.com/case/dp.html",
                "http://www.ntrc.com/job?jobsort1=200210",
                "http://zzk.fh21.com.cn",
                "http://www.zgybsf.net/login/password.html",
                "https://newhouse.fang.com/house/s/fengtai/",
                "http://sswgh.hgu.edu.cn/tzgg.htm",
                "https://www.chinafloor.cn/news/list-1515.html",
                "https://xinan.mysteel.com/",
                "http://ll.qjrd.gov.cn",
                "http://www.9y9y.com/guona/",
                "http://www.zjyj.org.cn/tzgg.asp",
                "http://www.mwcar.cn/2019/08/",
                "https://naicai.mysteel.com/index.html"
        };

        return urls;
    }
}
