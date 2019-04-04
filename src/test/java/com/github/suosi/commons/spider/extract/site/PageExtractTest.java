package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.Page;
import com.github.suosi.commons.spider.utils.UrlUtils;
import org.junit.Test;

import java.util.Set;

public class PageExtractTest {

    /**
     * 测试 一个 URL 中的链接抽取
     */
    @Test
    public void url() {
        String url = "http://news.baidu.com/";

        Page page = PageExtract.url(url);
        Set<String> links = page.getLinks();
        if (links != null) {
            for (String link : links) {
                if (UrlUtils.guessArticleUrl(link, null)) {
                    System.out.println("A -> " + link);
                } else if (UrlUtils.guessListUrl(link, null)) {
                    System.out.println("L -> " + link);
                } else {
                    System.out.println("N -> " + link);
                }
            }
        }
    }
}