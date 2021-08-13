package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.Page;
import com.github.suosi.commons.spider.utils.DomainUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import com.github.suosi.commons.spider.utils.UrlUtils;
import com.github.suosi.commons.spider.utils.okhttp.OkHttpProxy;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

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
        // String url = "http://dzb.whb.cn/";
        // String url = "http://www.nytimes.com/";
        // String url = "https://time.com";
        // String url = "https://tw.appledaily.com/realtime/new/";
        // String url = "http://www.mainichi.jp/";
        // String url = "http://www.jejunews.com/";
        // String url = "https://tw.appledaily.com/realtime/new/";
        // String url = "https://www.cnal.com/news-%E9%9B%B2%E5%8D%97%E5%AE%8F%E6%B3%B0/";
        // String url = "https://www.bezformata.com/";
        String url = "http://www.88884400.com/html/41144773.html";

        ArrayList<Map.Entry> articles = new ArrayList<>();
        ArrayList<Map.Entry> lists = new ArrayList<>();
        ArrayList<String> nones = new ArrayList<>();

        try {

            // 设置代理
            // String proxyIp = "u3411.300.tp.16yun.cn";
            // Integer proxyPort = 6474;
            // String username = "16ZTBOZP";
            // String password = "335517";
            // OkHttpProxy userProxy = OkHttpProxy.builder()
            //         .host(proxyIp).port(proxyPort)
            //         .username(username).password(password)
            //         .build();


            OkHttpProxy userProxy = OkHttpProxy.builder()
                    .host("127.0.0.1").port(10809)
                    .username("").password("")
                    .build();

            // Page page = PageExtract.url(url, 10);
            Page page = PageExtract.url(url, 30,  userProxy);
            if (page != null) {
                System.out.println(page.getHttpcode());
                // System.out.println(page.getHtml());
                Set<String> links = page.getLinks();
                Map<String,String> linkTitles = page.getLinkTitles();
                // System.out.println(linkTitles);

                if (linkTitles != null && linkTitles.size() > 0) {
                    for (Map.Entry<String,String> link : linkTitles.entrySet()) {
                        if (UrlUtils.guessArticleUrlByTitle(link.getKey(), link.getValue())) {
                            articles.add(link);
                        }else {
                            lists.add(link);
                        }
                    }
                }
            }

            for (Map.Entry article : articles) {
                System.out.println("A -> " + article.getKey() + " -> " + article.getValue());
            }

            for (Map.Entry list : lists) {
                System.out.println("L -> " +  list.getKey() + " -> " + list.getValue());
            }

        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    @Test
    public void titleUrl() throws IOException {
        String url = "https://www.csis.org/";
        ArrayList<String> articles = new ArrayList<>();
        ArrayList<String> lists = new ArrayList<>();
        ArrayList<String> nones = new ArrayList<>();

        OkHttpProxy userProxy = OkHttpProxy.builder()
                .host("127.0.0.1").port(10809)
                .username("").password("")
                .build();
        OkHttpClient client = OkHttpUtils.builder(null, 3000, null).build();

        Response response = client.newCall(OkHttpUtils.request(url)).execute();
        if (response.isSuccessful() && response.body() != null) {
            String html = response.body().string();
            Document document = Jsoup.parse(html);  

            Map<String, String> links = Parse.parseLinkTitles(document, "csis.org", url);

            if (links.size() > 0) {
                for (Map.Entry<String, String> link : links.entrySet()) {
                    String href = link.getKey();
                    String text = link.getValue();
                    StringTokenizer token = new StringTokenizer(text, " &:,");

                    if (token.countTokens() > 3) {
                        System.out.println("A ->" + href + "，" + text + "," + token.countTokens());
                    } else {
                        System.out.println("L ->" + href + "，" + text+ "," + token.countTokens());
                    }

                    // 历史
                    // if (UrlUtils.guessArticleUrl(href, null)) {
                    //     articles.add(href);
                    // } else if (UrlUtils.guessListUrl(href, null)) {
                    //     lists.add(href);
                    // } else {
                    //     nones.add(href);
                    // }

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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void parseTitle()
    {
        try {
            String url = "https://www.163.com/news/article/GG5HFTIG000189FH.html?clickfrom=w_yw";
            Page info = PageExtract.url(url, 3);
            System.out.println(info.getHttpcode());
            System.out.println(info.getHtml());

            String title = Parse.parseArticleTitle(info.getHtml());
            System.out.println("aaa" + title + "bbb");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


    @Test
    public void parseContent()
    {
        try {
            String url = "https://www.reuters.com/business/retail-consumer/amazon-sales-forecast-misses-estimates-shares-fall-2021-07-29/";
            Page info = PageExtract.url(url, 3);
            System.out.println(info.getHttpcode());
            System.out.println(info.getHtml());

            String content = Parse.parseArticleContent(info.getHtml());
            System.out.println("aaa" + content + "bbb");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    public void test123()
    {
        String[] domains = {
                "www.ap.org",
                "www.upi.com",
                "www.nytimes.com",
                "www.wsj.com",
                "www.washingtonpost.com",
                "www.csmonitor.com",
                "foreignpolicy.com",
                "www.voanews.com",
                "www.cnn.com",
                "abc.go.com",
                "www.nbc.com",
                "www.cbs.com",
                "www.interfax.ru",
                "tass.ru",
                "rg.ru",
                "www.kazpravda.kz",
                "www.consultant.ru",
                "english.kyodonews.net",
                "www.yomiuri.co.jp",
                "theconversation.com",
                "www.asahi.com",
                "www.sankei.com",
                "www.cmu.edu",
                "www.nhk.or.jp",
                "www.tbs.co.jp",
                "www.yonhapnews.co.kr",
                "www.donga.com",
                "www.kbs.co.kr",
                "www.northkoreatech.org",
                "www.cctv.com",
                "www.vietnamplus.vn",
                "www.philstar.com",
                "www.shangbao.com.ph",
                "www.reuters.com",
                "www.bbc.co.uk",
                "www.theguardian.com",
                "www.ft.com",
                "www.thetimes.co.uk",
                "www.telegraph.co.uk",
                "www.afp.com",
                "www.lemonde.fr",
                "www.lefigaro.fr",
                "www.francetelevisions.fr",
                "www.rfi.fr",
                "www.dpa.com",
                "www.spiegel.de",
                "www.sueddeutsche.de",
                "www.deutschlandradio.de",
                "www.ansa.it",
                "www.parstimes.com",
                "www.republicain-lorrain.fr",
                "www.cna.com.tw",
                "vip.udn.com",
                "tw.appledaily.com",
                "www.ltn.com.tw",
                "www.chinatimes.com",
                "www.zaobao.com.sg",
                "www.tvbs.com.tw",
                "www.setn.com",
                "www.ftv.com.tw",
                "www.bcc.com.tw",
                "www.mingpao.com",
                "www.hkcd.com",
                "www.sengpou.com",
                "www.macaodaily.com",
                "www.cmoney.tw",
                "1059044.wiwe.com.tw",
                "5550555.com",
                "blog.udn.com",
                "cdns.com.tw",
                "eprice.ufc.com.tw",
                "matsu-news.towin.com.tw",
                "oldwww.ydn.com.tw",
                "twlugang.com",
                "www.aborigine.tw",
                "www.cdnews.com.tw",
                "www.cfnews.com.tw",
                "www.duping.net",
                "www.eyny.com",
                "www.fingermedia.tw",
                "www.gbo.com.tw",
                "www.gcaa.org.tw",
                "www.gq.com.tw",
                "www.greatnews.com.tw",
                "www.investor.com.tw",
                "www.kmt.org.tw",
                "www.ksnews.com.tw",
                "www.moneydj.com",
                "www.my-formosa.com",
                "www.mypeople.tw",
                "www.pacificnews.com.tw",
                "www.ptt666.com",
                "www.taipeiforum.org.tw",
                "www.taiwan.cn",
                "www.taiwanhot.net",
                "www.tc-news.com.tw",
                "www.ustv.idun.com.tw",
                "aamacau.com",
                "anntw.com",
                "apk.tw",
                "braintrust.tw",
                "buzzorange.com",
                "cheers-tw.tumblr.com",
                "ck101.com",
                "ctee.com.tw",
                "data.gov.tw",
                "disp.cc",
                "e-info.org.tw",
                "esc.nccu.edu.tw",
                "focus.586.com.tw",
                "idemocracy.asia",
                "kairos.news",
                "kunmedia.org",
                "life.tw",
                "mna.gpwb.gov.tw",
                "money.udn.com",
                "mypaper.pchome.com.tw",
                "news.ebc.net.tw",
                "news.pchome.com.tw",
                "newtalk.tw",
                "oursogo.com",
                "pnn.pts.org.tw",
                "stars.udn.com",
                "taiwan-reports.com",
                "theinitium.com",
                "today.line.me",
                "tsjh301.blogspot.com",
                "tw.answers.yahoo.com",
                "tw.appledaily.com",
                "tw.nextmgz.com",
                "tw.yahoo.com",
                "udn.com",
                "video.udn.com",
                "www.accupass.com",
                "www.ait.org.tw",
                "www.babyhome.com.tw",
                "www.bcc.com.tw",
                "www.bnext.com.tw",
                "www.businesstoday.com.tw",
                "www.businessweekly.com.tw",
                "www.cardu.com.tw",
                "www.cec.gov.tw",
                "www.chinatimes.com",
                "www.cib.gov.tw",
                "www.cna.com.tw",
                "www.cnyes.com",
                "www.coolloud.org.tw",
                "www.ctasc.org.tw",
                "www.cts.com.tw",
                "www.cw.com.tw",
                "www.cyberctm.com",
                "www.digitimes.com.tw",
                "www.dpp.org.tw",
                "www.epochtimes.com",
                "www.epochtimes.com",
                "www.eracom.com.tw",
                "www.ftc.gov.tw",
                "www.ftv.com.tw",
                "www.gamme.com.tw",
                "www.gvm.com.tw",
                "www.haixia-info.com",
                "www.hamastar.com.tw",
                "www.hinet.net",
                "www.housefun.com.tw",
                "www.idn.com.tw",
                "www.inside.com.tw",
                "www.ithome.com.tw",
                "www.ltn.com.tw",
                "www.managertoday.com.tw",
                "www.mdnkids.com",
                "www.merit-times.com",
                "www.mobile01.com",
                "www.msn.com",
                "www.mygonews.com",
                "www.new7.com.tw",
                "www.newsmarket.com.tw",
                "www.newstaiwandigi.com",
                "www.newstaiwandigi.com",
                "www.nownews.com",
                "www.nownews.com",
                "www.nsb.gov.tw",
                "www.ntpc.gov.tw",
                "www.pcdvd.com.tw",
                "www.peoplenews.tw",
                "www.pinterest.com",
                "www.pixnet.net",
                "www.quamnet.com",
                "www.rti.org.tw",
                "www.saydigi.com",
                "www.setn.com",
                "www.sina.com.tw",
                "www.sina.com.tw",
                "www.stat.gov.tw",
                "www.storm.mg",
                "www.taichung.gov.tw",
                "www.tainan.gov.tw",
                "www.taiwantimes.com.tw",
                "www.thenewslens.com",
                "www.tier.org.tw",
                "www.ttv.com.tw",
                "www.tvbs.com.tw",
                "www.twpowernews.com",
                "www.twreporter.org",
                "www.tycg.gov.tw",
                "www.u-car.com.tw",
                "www.upmedia.mg",
                "www.voh.com.tw",
                "www.wealth.com.tw",
                "www.yam.com",
                "www.zaobao.com.sg",
                "xuite.net",
                "www.coco-in.net",
                "cablenews.i-cable.com",
                "ftu.org.hk",
                "hk.crntt.com",
                "www.aastocks.com",
                "www.aimpact.com",
                "www.chengpou.com.mo",
                "www.hkcna.hk",
                "www.hkej.com",
                "www.hsbc.com.hk",
                "www.infocast.com.hk",
                "www.metroradio.com.hk",
                "www.mingpao.com",
                "www.my-magazine.me",
                "www.passiontimes.hk",
                "www.schroders.com.hk",
                "www.scmp.com",
                "www.takungpao.com",
                "cn.apdnews.com",
                "cnfol.com",
                "forum.hkgolden.com",
                "hd.stheadline.com",
                "hk.appledaily.com",
                "hk.appledaily.com",
                "hk.finance.yahoo.com",
                "hk.nextmgz.com",
                "hk.on.cc",
                "hk.ulifestyle.com.hk",
                "hk.yahoo.com",
                "hkcd.com",
                "hkgpao.com",
                "memehk.com",
                "money18.on.cc",
                "news.mingpao.com",
                "orientaldaily.on.cc",
                "popbee.com",
                "sc.sfc.hk",
                "sina.com.hk",
                "skypost.ulifestyle.com.hk",
                "std.stheadline.com",
                "unwire.hk",
                "www.881903.com",
                "www.am730.com.hk",
                "www.amnesty.org.hk",
                "www.bastillepost.com",
                "www.beautyexchange.com.hk",
                "www.cosmopolitan.com.hk",
                "www.dbpower.com.hk",
                "www.dcfever.com",
                "www.dotdotnews.com",
                "www.easecurities.com.hk",
                "www.elle.com.hk",
                "www.eprice.com.hk",
                "www.fanpiece.com",
                "www.finet.hk",
                "www.gameapps.hk",
                "www.hk01.com",
                "www.hkcnews.com",
                "www.hkepc.com",
                "www.hket.com",
                "www.hkex.com.hk",
                "www.hkma.gov.hk",
                "www.hkma.gov.hk",
                "www.hksi.org",
                "www.ia.org.hk",
                "www.infinitewater.com",
                "www.inmediahk.net",
                "www.jessicahk.com",
                "www.kgieworld.com",
                "www.metrodaily.hk",
                "www.mirrormedia.mg",
                "www.mirrormedia.mg",
                "www.mpfinance.com",
                "www.msn.com",
                "www.news.gov.hk",
                "www.nmplus.hk",
                "www.orangenews.hk",
                "www.orientalsunday.hk",
                "www.orientalsunday.hk",
                "www.post852.com",
                "www.pru.hk",
                "www.rthk.hk",
                "www.shkdirect.com",
                "www.singpao.com.hk",
                "www.soft4fun.net",
                "www.speakout.hk",
                "www.stocks.com.hk",
                "www.thestandnews.com",
                "www.three.com.hk",
                "www.tvmost.com.hk",
                "www.undergrad.hk",
                "www.weekendhk.com",
                "www.wenweipo.com",
                "www.nyotron.com",
                "www.westpac.com.au",
                "www.sengpou.com",
                "www.macaodaily.com",
                "www.am853.com",
                "www.macaubbs.com",
                "www.macautimes.cc",
                "www.shimindaily.net",
                "www.waou.com.mo",
                "www.idreamx.com",
                "www.gcs.gov.mo",
                "www.exmoo.com",
                "www.cyberctm.com",
                "abc.go.com",
                "cn.wsj.com",
                "time.com",
                "www.aeroinfo.com.cn",
                "www.ap.org",
                "www.baltimoresun.com",
                "www.cbs.com",
                "www.chicagotribune.com",
                "www.chinastarmedia.com",
                "www.huffingtonpost.com",
                "www.latimes.com",
                "www.msnbc.msn.com",
                "www.newsweek.com",
                "www.nydailynews.com",
                "www.nypost.com",
                "www.nytimes.com",
                "www.usatoday.com",
                "www.uschinapress.com",
                "www.voanews.com",
                "www.washingtonpost.com",
                "www.wsj.com",
                "boxun.com",
                "cn.nytimes.com",
                "economictimes.indiatimes.com",
                "edition.cnn.com",
                "fas.org",
                "foreignpolicy.com",
                "fortune.com",
                "home.kpmg",
                "huronatwestern.ca",
                "news.yahoo.com",
                "uyghuramerican.org",
                "www.aboluowang.com",
                "www.afginc.com",
                "www.aig.com",
                "www.airfreight.com",
                "www.alixpartners.com",
                "www.allstate.com",
                "www.analysisgroup.com",
                "www.assurant.com",
                "www.aviva.com",
                "www.bain.com",
                "www.bcg.com",
                "www.bloomberg.com",
                "www.bnpparibas.ru",
                "www.boozallen.com",
                "www.bostonherald.com",
                "www.cnbc.com",
                "www.credit-suisse.com",
                "www.csmonitor.com",
                "www.dwnews.com",
                "www.euronews.com",
                "www.everestgrp.com",
                "www.forbes.com",
                "www.foxnews.com",
                "www.ft.com",
                "www.gartner.com",
                "www.genworth.com",
                "www.ibm.com",
                "www.icf.com",
                "www.infosys.com",
                "www.ing.com",
                "www.lek.com",
                "www.logisticsonline.com",
                "www.lowes.com.au",
                "www.markel.com",
                "www.marsh.com",
                "www.mckinsey.com",
                "www.metlife.com",
                "www.msnbc.com",
                "www.newyorklife.com",
                "www.oliverwyman.com",
                "www.paconsulting.com",
                "www.perficient.com",
                "www.rfa.org",
                "www.rgare.com",
                "www.santander.com",
                "www.smbc.co.jp",
                "www.supplychainbrain.com",
                "www.tibet.org.tw",
                "www.tradewindsnews.com",
                "www.upi.com",
                "www.voachinese.com",
                "www.westmonroe.com",
                "www.worldjournal.com",
                "www.gvalley.com",
                "www.dvidshub.net",
                "www.airforce-technology.com",
                "news.usni.org",
                "www.defenseone.com",
                "www.naval-technology.com",
                "www.defensenews.com",
                "www.ausa.org",
                "seapowermagazine.org",
                "www.army.mil",
                "www.mercer.com",
                "www.canadapost-postescanada.ca",
                "www.thehartford.com",
                "www.jpmorganchase.com",
                "www.bankofamerica.com",
                "www.wellsfargo.com",
                "www.mizuhogroup.com",
                "groupebpce.com",
                "www.td.com",
                "www.scotiabank.com",
                "www.commbank.com.au",
                "www.rabobank.com",
                "www.bbva.com",
                "asiatimes.com",
                "www.engadget.com",
                "www.amnesty.org",
                "www.brattle.com",
                "robertherjavec.com",
                "www.knowbe4.com",
                "www.cyberark.com",
                "www.rtx.com",
                "www.checkpoint.com",
                "talosintelligence.com",
                "www.zscaler.com",
                "www.foxbusiness.com",
                "cacm.acm.org",
                "www.broadcom.com",
                "www.baesystems.com",
                "www.secureworks.com",
                "www.optiv.com",
                "www.fortinet.com",
                "www.forcepoint.com",
                "www.blackberry.com",
                "www.barracuda.com",
                "www.gigamon.com",
                "www.oracle.com",
                "cybersecurity.att.com",
                "www.corero.com",
                "www.saic.com",
                "www.qualys.com",
                "www.pindrop.com",
                "www.imprivata.com",
                "www.f-secure.com",
                "www.threatstack.com",
                "www.juniper.net",
                "www.telos.com",
                "www.skyboxsecurity.com",
                "cofense.com",
                "sera-brynn.com",
                "www.fideliscare.org",
                "www.cyren.com",
                "karambasecurity.com",
                "www.cimcor.com",
                "www.f5.com",
                "www.coresecurity.com",
                "www.caci.com",
                "digital.ai",
                "www.i-sprint.com",
                "www.newnettechnologies.com",
                "attivonetworks.com",
                "www.onespan.com",
                "www8.hp.com",
                "www.root9b.com",
                "cn.ahnlab.com",
                "haystax.com",
                "www.gosecure.net"
        };
        for (String domain : domains) {
            String topDomain = DomainUtils.topDomain(domain);
            System.out.println(topDomain);
        }

    }
}
