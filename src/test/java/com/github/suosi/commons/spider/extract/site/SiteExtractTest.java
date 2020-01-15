package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.Site;
import com.github.suosi.commons.spider.utils.UrlUtils;
import org.junit.Test;

import java.io.*;
import java.util.Set;

public class SiteExtractTest {
    private static String[] domains = {
            // "jzggzy.com",
            // "ansteelgroup.com",
            // "hscarbonfibre.com",
            // "zzggzy.com",
            // "hxb.com.cn",
            // "saas.ac.cn",
            // "chinafuning.gov.cn",
            // "newmarketingcn.com",
            // "wnggzy.com",
            // "boznews.cn",
            // "icbc.com.cn",
            // "lyggzyjy.cn",
            // "fsdt.com.cn",
            // "lssggzy.com",
            // "yaggzyjy.cn",
            // "sfc.hk",
            // "safp.gov.mo",
            // "gzslyxx.com",
            // "ccb.com",
            // "fwol.cn",
            // "cfpi.cn",
            // "hnagroup.com",
            // "szcdc.cn",
            // "nmhms.org.cn",
            // "xtxh.net",
            // "hkbea.com.cn",
            // "xhyc.net",
            // "cinda.com.cn",
            // "iwhr.com",
            // "cmatc.cn",
            // "zjwclw.cn",
            // "i2863.com",
            // "jyggjy.cn",
            // "rcxxpt.cn",
            // "szftu.org",
            // "qxnrtv.com",
            // "bzggzyjy.gov.cn",
            // "taggzyjy.com.cn",
            // "hgggzy.com"
            // "scio.gov.cn"
            "yisu.com"
    };

    @Test
    public void domain() {
        for (String domainUrl : domains) {
            try {
                System.out.println(domainUrl);
                Site domain = SiteExtract.domain(domainUrl, 3L);
                domain.setHtml("");
                System.out.println(domain.getLinks());
                System.out.println(domain.getSubDomain());
                System.out.println(domain.getHttpcode());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }


    }

    @Test
    public void txtSubDomain() {
        try {
            /* 读入TXT文件 */
            String filename = "tmp_new_en.txt";
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言

            /* 写入Txt文件 */
            File writename = new File("domain_url_type.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
            writename.createNewFile(); // 创建新文件
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));

            String line = "";
            line = br.readLine();
            while (line != null) {
                line = br.readLine(); // 一次读入一行数据
                String domainUrl = line.replace("\"", "");
                Site domain = SiteExtract.domain(domainUrl);
                if (domain == null) continue;
                Set<String> subSiteLinks = domain.getLinks();
                if (subSiteLinks != null) {
                    for (String subSiteLink : subSiteLinks) {
                        if (UrlUtils.guessArticleUrl(subSiteLink, null)) {
                            out.write(String.format("%s,%s,A\n", domainUrl, subSiteLink)); // \r\n即为换行
                            System.out.println(domainUrl + " -> A -> " + subSiteLink);
                        } else {
                            out.write(String.format("%s,%s,L\n", domainUrl, subSiteLink)); // \r\n即为换行
                            System.out.println(domainUrl + " -> L -> " + subSiteLink);
                        }
                    }
                }
            }
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取站点子域名信息
     */
    @Test
    public void subDomain() {
        for (String domain : domains) {
            try {
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
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

    }

    /**
     * 获取站点子域名，以及链接信息
     */
    @Test
    public void articleUrls() {
        for (String domain : domains) {
            try {
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
            } catch (Exception e) {
                System.out.println(e.getMessage());
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
