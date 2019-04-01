package com.github.suosi.commons.spider.extract.site;

import org.junit.Test;

public class SiteExtractTest {
    @Test
    public void test() {
        String[] domains = {
                "baidu.com",
                "zjol.com.cn",
                "jieju.cn",
                "iteye.com",
                "naver.com",
                "yuque.com",
        };
        for (String domain : domains) {
            Site site = SiteExtract.domain(domain);
            System.out.println(site);
        }


    }
}