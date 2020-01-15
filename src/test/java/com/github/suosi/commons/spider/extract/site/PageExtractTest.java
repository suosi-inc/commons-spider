package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.Page;
import com.github.suosi.commons.spider.utils.UrlUtils;
import com.github.suosi.commons.spider.utils.okhttp.OkHttpProxy;
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
        // String url = "https://bidnews.cn/caigou/zhaobiao-9718177.html";
        // String url = "https://www.zbytb.com/gongcheng/";
        // String url = "https://jump.d.cn/ngoff/09hu70/10172_775078_97261.html";
        // String url = "http://bbs.e763.com/attachment.php?aid=912131&k=985b3de39fbcebc7b4646d1612b68e1c&t=1577762661&nothumb=yes";
        // String url = "http://www.wanweiedu.com/index.php?m=content&c=down&a_k=495ep6Dl6vV-Ayu-HkLEyjMpm0HLhCmY5dV7dMkFHSUF184raSJsc2gTCPgOe9FbqBOE2E2_ZqW8135FXh5cnI8tmvvRJJLvUbObtm9HfmEQYreMhlJU5E5u2C1viKGG1MvBBLUMrXF-Op5X5hjEDA5ySaGkzYRwVzcTLuExx-hiU4t7R1GkP80eHjqyhbDTVBa1ZA";
        // String url = "https://www.zbytb.com/s-zb-6634205.html";
        // String url = "http://mirrors.163.com/centos/7.7.1908/isos/x86_64/CentOS-7-x86_64-DVD-1908.iso";
        // String url = "https://m.weibo.cn/statuses/show?id=IgM3CxqiC";
        // String url = "http://bbs.jxnews.com.cn/forum.php?mod=forumdisplay&fid=298&orderby=dateline&filter=author&orderby=dateline&page=2";
        // String url = "http://ip.suosi.net.cn/t.php";
        String url = "http://www.cntv.cn/";

        ArrayList<String> articles = new ArrayList<>();
        ArrayList<String> lists = new ArrayList<>();
        ArrayList<String> nones = new ArrayList<>();

        try {

            // 设置代理
            String proxyIp = "u3411.300.tp.16yun.cn";
            Integer proxyPort = 6474;
            String username = "16ZTBOZP";
            String password = "335517";
            OkHttpProxy userProxy = OkHttpProxy.builder()
                    .host(proxyIp).port(proxyPort)
                    .username(username).password(password)
                    .build();

            Page page = PageExtract.url(url, 3, true);
            // Page page = PageExtract.url(url, 50,  userProxy);
            if (page != null) {
                System.out.println(page.getHttpcode());
                System.out.println(page.getHtml());
                Set<String> links = page.getLinks();
                System.out.println(links);

                if (links != null && links.size() > 0) {
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

    /**
     * 测试 一个 URL 中的链接抽取
     */
    @Test
    public void urlContent() {
        try {
            String url = "https://www.zbytb.com/s-zb-6631607.html";
            Page info = PageExtract.url(url, 3);
            System.out.println(info.getHttpcode());
            System.out.println(info.getHtml());
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
