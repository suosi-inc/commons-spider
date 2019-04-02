package com.github.suosi.commons.spider.utils;

import org.junit.Test;

public class UrlUtilsTest {

    @Test
    public void guessArticleUrl() {
        String[] urls = {
                "http://www.sasac.gov.cn",
                "http://www.sasac.gov.cn/",
                "http://www.sasac.gov.cn/aaa",
                "http://www.sasac.gov.cn/中文",
                "http://www.sasac.gov.cn/%e4%b8%ad%e6%96%87",
                "http://www.sasac.gov.cn/aaa/",
                "http://www.sasac.gov.cn/aaa/bbb",
                "http://www.sasac.gov.cn/aaa.html",
                "http://www.sasac.gov.cn/n2588025/n2588124/c8957994/content.html?language=cn#j2se"
        };
        for (String url : urls) {
            System.out.println(url + " -> " +(UrlUtils.guessArticleUrl(url) ? "1" : "0"));
        }


    }
}