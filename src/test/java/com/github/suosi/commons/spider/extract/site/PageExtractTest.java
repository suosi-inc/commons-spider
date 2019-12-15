package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.Page;
import com.github.suosi.commons.spider.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageExtractTest {

    /**
     * 测试 一个 URL 中的链接抽取
     */
    @Test
    public void url() {
        String url = "http://www.mmsonline.com.cn/mmsonline/downloadlist.jsp";

        ArrayList<String> articles = new ArrayList<>();
        ArrayList<String> lists = new ArrayList<>();
        ArrayList<String> nones = new ArrayList<>();

        try {
            Page page = PageExtract.url(url, 5);
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
        } catch (Exception e) {
            System.out.println("error: " +  e.getMessage());
        }
    }
}
