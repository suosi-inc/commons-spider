package com.github.suosi.commons.spider.utils;

import org.junit.Test;


public class DomainUtilsTest {

    @Test
    public void topDomain() {
        System.out.println(DomainUtils.topDomain("hi.chinanews.com"));
        System.out.println(DomainUtils.topDomain("a.wh.cn"));
        System.out.println(DomainUtils.topDomain("siat.ac.cn"));
        System.out.println(DomainUtils.topDomain("abc.spring.io"));
        System.out.println(DomainUtils.topDomain("abc.spring.ai"));
        System.out.println(DomainUtils.topDomain("www.china-embassy.or.jp"));
        System.out.println(DomainUtils.topDomain("whszdj.wh.cn"));
        System.out.println(DomainUtils.topDomain("gk.wh.cn"));
        System.out.println(DomainUtils.topDomain("xwxc.mwr.cn"));
        System.out.println(DomainUtils.topDomain("legismac.safp.gov.mo"));
        System.out.println(DomainUtils.topDomain("dezhou.rcsd.cn"));




    }

    @Test
    public void topDomainFromUrl() {
        System.out.println(DomainUtils.topDomainFromUrl("https://www.baidu.com/news"));
    }
}