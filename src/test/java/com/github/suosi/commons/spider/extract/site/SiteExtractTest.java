package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.Site;
import com.github.suosi.commons.spider.utils.UrlUtils;
import org.junit.Test;

import java.util.Set;

public class SiteExtractTest {
    private static String[] domains = {
            "zungu.net",
    };

    @Test
    public void domain() {
        Site domain = SiteExtract.domain("dlt.gov.cn");
        domain.setHtml("");

        System.out.println(domain.getLinks());
        System.out.println(domain.getSubDomain());
    }

    /**
     * 获取站点子域名信息
     */
    @Test
    public void subDomain() {
        for (String domain : domains) {
            Site site = SiteExtract.domain(domain);
            Set<String> subDomains = site.getSubDomain();

            for (String subDomain : subDomains) {
                StringBuilder sb = new StringBuilder();
                sb.append(subDomain + " ");
                if (SiteExtract.filterDomain(subDomain)) {
                    Site subSite = SiteExtract.domain(subDomain);
                    sb.append(subSite.getTitle());
                    int total = UrlUtils.countArticleUrls(subSite.getLinks(), null);
                    if (total < 10) {
                        sb.append(" -> [total:" + total + "]");
                    }
                } else {
                    sb.append(" -> [filter]");
                }
                System.out.println(sb.toString());
            }
        }

    }

    /**
     * 获取站点子域名，以及链接信息
     */
    @Test
    public void articleUrls() {
        for (String domain : domains) {
            Site site = SiteExtract.domain(domain);
            Set<String> subDomains = site.getSubDomain();

            for (String subDomain : subDomains) {
                Site subSite = SiteExtract.domain(subDomain);
                Set<String> subSiteLinks = subSite.getLinks();
                if (subSiteLinks != null) {
                    for (String subSiteLink : subSiteLinks) {
                        if (UrlUtils.guessArticleUrl(subSiteLink, null)) {
                            System.out.println(subDomain + " -> A -> " + subSiteLink);
                        } else {
                            System.out.println(subDomain + " -> L -> " + subSiteLink);
                        }
                    }
                }
            }
        }
    }

    /**
     * 域名规则验证
     */
    @Test
    public void filterDomain() {
        boolean b = SiteExtract.filterDomain("api.abaidu.com");
        System.out.println(b);
    }


}
