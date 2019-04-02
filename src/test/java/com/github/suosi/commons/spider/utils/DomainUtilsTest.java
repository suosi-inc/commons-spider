package com.github.suosi.commons.spider.utils;

import org.junit.Test;


public class DomainUtilsTest {

    @Test
    public void topDomain() {
        System.out.println(DomainUtils.topDomain("www.baidu.com"));
        System.out.println(DomainUtils.topDomain("a.b.baidu.com"));
        System.out.println(DomainUtils.topDomain("abc.spring.io"));
        System.out.println(DomainUtils.topDomain("abc.spring.ai"));
    }

    @Test
    public void topDomainFromUrl() {
        System.out.println(DomainUtils.topDomainFromUrl("https://www.baidu.com/news"));
    }
}