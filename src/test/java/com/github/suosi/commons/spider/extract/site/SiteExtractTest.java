package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.utils.UrlUtils;
import org.junit.Test;

import java.util.Set;

public class SiteExtractTest {
    private static String[] domains = {
            "caixin.com",
    };

    @Test
    public void subDomain() {
        for (String domain : domains) {
            Site site = SiteExtract.domain(domain, false);
            Set<String> subDomains = site.getSubDomain();
            System.out.println(site.getMainDomain());
            for (String subDomain : subDomains) {
                //if (SiteExtract.filterDomain(subDomain)) {
                    Site subSite = SiteExtract.domain(subDomain, true);
                    System.out.println(subDomain + "->" + subSite.getTitle());
                    System.out.println(UrlUtils.countArticleUrls(subSite.getLinks(), subDomain));
                //}
            }
        }
    }



    @Test
    public void url() {
        for (String domain : domains) {
            Site site = SiteExtract.domain(domain, false);
            Set<String> links = site.getLinks();
            Set<String> subDomains = site.getSubDomain();
            for (String link : links) {
                System.out.println(domain + " -> " + link);
            }

            for (String subDomain : subDomains) {
                Site subSite = SiteExtract.domain(subDomain, true);
                Set<String> subSiteLinks = subSite.getLinks();
                for (String subSiteLink : subSiteLinks) {
                    if (UrlUtils.guessArticleUrl(subSiteLink, null)) {
                        System.out.println(subDomain + " -> " + subSiteLink);
                    }
                }
            }
        }
    }


}