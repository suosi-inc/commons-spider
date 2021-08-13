package com.github.suosi.commons.spider.utils;

import com.google.common.net.InternetDomainName;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;


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
        System.out.println(DomainUtils.topDomain("www.gov.cn"));
        System.out.println(DomainUtils.topDomain("scopsr.gov.cn"));




    }

    @Test
    public void topDomainFromUrl() {
        System.out.println(DomainUtils.topDomainFromUrl("https://www.baidu.com/news"));
    }



    @Test
    public void topDomain2() {

        String[] subDomains = {
                "spartanswire.usatoday.com",
                "badgerswire.usatoday.com",
                "wolverineswire.usatoday.com",
                "volswire.usatoday.com",
                "buckeyeswire.usatoday.com",
                "ugawire.usatoday.com",
                "guce.yahoo.com",
                "pageviewer.yomiuri.co.jp",
                "partner.buy.yahoo.com",
                "tw.edit.yahoo.com",
                "tw.security.yahoo.com",
                "tw.knowledge.yahoo.com",
                "travel.m.pchome.com.tw",
                "blogs.reuters.com",
                "reuters.com",
                "tw.money.yahoo.com",
                "tw.mobile.yahoo.com",
                "asia.adspecs.yahoo.com",
                "learngerman.dw.com",
                "conference.udn.com",
                "mediadirectory.economist.com",
                "eventsregistration.economist.com",
                "eventscustom.economist.com",
                "technologyforchange.economist.com",
                "sustainabilityregistration.economist.com",
                "learn-french.lemonde.fr",
                "jungeleute.sueddeutsche.de",
                "jetzt.sueddeutsche.de",
                "coupons.cnn.com",
                "www.cnn.com",
                "www.khmer.voanews.com",
                "www.burmese.voanews.com",
                "www.tigrigna.voanews.com",
                "nkpos.nikkei.co.jp",
                "nvs.nikkei.co.jp",
                "simonglazin.dailymail.co.uk",
                "adweb.nikkei.co.jp",
                "broganblog.dailymail.co.uk",
                "pclub.nikkei.co.jp",
                "araward.nikkei.co.jp",
                "blend.nikkei.co.jp",
                "esf.nikkei.co.jp",
                "hoshiaward.nikkei.co.jp",
                "marketing.nikkei.com",
                "www.now.com",
                "jp.wsj.com",
                "subscribenow.economist.com",
                "sportsawards.usatoday.com",
                "cooking.nytimes.com"
        };
        for (String subDomain : subDomains) {
            String domain = DomainUtils.topDomain(subDomain);
            System.out.println(domain + "," + subDomain);
        }
    }


    @Test
    public void topDomain22() {

        File file = new File("tmpUrls.txt");
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                System.out.println(tempStr.toString());

                String url = tempStr.toString();
                String domain = "";
                String topDomain = "";
                URL parse = UrlUtils.parse(url);
                if (parse != null) {
                    domain = parse.getHost();

                    if (StringUtils.isNotBlank(domain) && InternetDomainName.isValid(domain)) {
                        InternetDomainName topDomain2 = InternetDomainName.from(domain).topPrivateDomain();
                        topDomain =  topDomain2.toString();
                    }
                }
                String content = topDomain + "##" + domain + "##" + url;
                FileUtils.recordFile("tmpUrls2.txt", content + "\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }


        // for (String subDomain : subDomains) {
        //         String domain = DomainUtils.topDomain(subDomain);
        //         System.out.println(domain + "," + subDomain);
        //     }
        // }
    }

}