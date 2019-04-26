package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.Page;
import com.github.suosi.commons.spider.utils.UrlUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Set;

public class PageExtractTest {

    /**
     * 测试 一个 URL 中的链接抽取
     */
    @Test
    public void url() {
        String url = "https://www.pinalcentral.com/";

        ArrayList<String> articles = new ArrayList<>();
        ArrayList<String> lists = new ArrayList<>();
        ArrayList<String> nones = new ArrayList<>();


        Page page = PageExtract.url(url);
        if (page != null) {
            Set<String> links = page.getLinks();
            if (links != null) {
                for (String link : links) {
                    if (UrlUtils.guessArticleUrl(link, null)) {
                        articles.add(link);
                    } else if (UrlUtils.guessListUrl(link, null)) {
                        lists.add(link);
                    } else {
                        nones.add(link);
                    }
                }
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
    }
}
