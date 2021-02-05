package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.BbsPage;
import com.github.suosi.commons.spider.extract.site.meta.Page;
import com.github.suosi.commons.spider.utils.UrlUtils;
import com.github.suosi.commons.spider.utils.okhttp.OkHttpProxy;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Set;

public class BbsPageExtractTest {

    /**
     * 测试 一个 URL 中的链接抽取
     */
    @Test
    public void urls() {
        String[] urls = {
                "http://bbs.ydss.cn/forum-honor4x-2.html",
                "http://bbs.qidong.gov.cn/forum-29-1.html",
                "https://www.discuz.net/forum-flea-1.html",
                "http://bbs.hefei.cc/forum-1534-1.html",
                "https://club.autohome.com.cn/bbs/brand-48-c-354-1.html",
                "http://www.020lg.com/bbs/showforum-117.aspx",
                "http://bbs.0668hz.com/index.php?c=thread&fid=12"
        };
        for (String url: urls) {
            try {
                BbsPage info = BbsPageExtract.url(url);
                System.out.println(url + " => " + info.getLists());
            } catch (Exception e) {
            }
        }
    }

    /**
     * 测试 一个 URL 中的链接抽取
     */
    @Test
    public void url() throws Exception {
       String url = "http://bbs.0668hz.com/index.php?c=thread&fid=12";
       BbsPage info = BbsPageExtract.url(url);
       System.out.println(info.getLists());
    }



    @Test
    public void contents() {
        String[] urls = {
                "https://www.discuz.net/forum.php?mod=viewthread&tid=3852333&extra=page%3D1%26filter%3Dsortid%26sortid%3D154",
                "http://bbs.ydss.cn/thread-601900-1-2.html",
                "https://club.autohome.com.cn/bbs/thread/697dbaf791ca1bc0/90956277-1.html",
                "http://www.020lg.com/bbs/showtopic-10672.aspx",
                "http://bbs.0668hz.com/read.php?tid=335318&fid=12",
        };
        for (String url: urls) {
            try {
                String content  = BbsPageExtract.parseBbsContent(url);
                System.out.println(url + " => " + content.length());
            } catch (Exception e) {
            }
        }
    }


    @Test
    public void content() {
        String url = "http://bbs.0668hz.com/read.php?tid=335318&fid=12";
        String content  = BbsPageExtract.parseBbsContent(url);
        System.out.println(content);
    }
}
