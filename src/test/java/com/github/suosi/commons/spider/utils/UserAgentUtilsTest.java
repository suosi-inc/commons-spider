package com.github.suosi.commons.spider.utils;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.junit.Test;

public class UserAgentUtilsTest {

    @Test
    public void test() {
        System.out.println(UserAgentUtils.test());
    }

    @Test
    public void all() {
        for (int i = 0; i < 10; i++) {
            System.out.println(UserAgentUtils.all());
        }
    }

    @Test
    public void firefox() {
        for (int i = 0; i < 10; i++) {
            System.out.println(UserAgentUtils.firefox());
        }
    }

    @Test
    public void chrome() {
        for (int i = 0; i < 10; i++) {
            System.out.println(UserAgentUtils.chrome());
        }
    }

    @Test
    public void ie() {
        for (int i = 0; i < 10; i++) {
            System.out.println(UserAgentUtils.ie());
        }
    }
}