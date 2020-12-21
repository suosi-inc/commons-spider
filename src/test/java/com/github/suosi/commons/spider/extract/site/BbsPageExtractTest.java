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
    public void url() throws Exception {
       // String url = "http://bbs.ydss.cn/forum-honor4x-2.html";
       // String url = "http://bbs.qidong.gov.cn/forum-29-1.html";
       // String url = "https://www.discuz.net/forum-flea-1.html";
       // String url = "http://bbs.hefei.cc/forum-1534-1.html";
       String url = "https://club.autohome.com.cn/bbs/brand-48-c-354-1.html";
       BbsPage info = BbsPageExtract.url(url);
       System.out.println(info.getLists());
    }

    @Test
    public void content() {
        String url = "https://www.discuz.net/forum.php?mod=viewthread&tid=3852333&extra=page%3D1%26filter%3Dsortid%26sortid%3D154";
        String content  = BbsPageExtract.parseBbsContent(url);
        System.out.println(content);
    }
}
