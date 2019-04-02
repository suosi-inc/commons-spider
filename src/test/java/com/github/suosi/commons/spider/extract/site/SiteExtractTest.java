package com.github.suosi.commons.spider.extract.site;

import org.junit.Test;

import java.util.Set;

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
            Set<String> links = site.getLinks();
            for (String link : links) {
                System.out.println(link);
            }
        }
    }


}