package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.helper.Static;
import com.github.suosi.commons.spider.extract.content.webcollector.contentextractor.ContentExtractor;
import com.github.suosi.commons.spider.extract.site.meta.Page;
import com.github.suosi.commons.spider.utils.DomainUtils;
import com.github.suosi.commons.spider.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author niuchaoqun
 */
public class Parse {
    private static final Pattern ICP_PATTERN = Pattern.compile(
            "(京|津|冀|晋|蒙|辽|吉|黑|沪|苏|浙|皖|闽|赣|鲁|豫|鄂|湘|粤|桂|琼|川|蜀|贵|黔|云|滇|渝|藏|陇|甘|陕|秦|青|宁|新)ICP(备|证)(.*)号",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^[a-z0-9A-Z][a-z0-9A-Z\\.\\-]*$");

    private static final String HTTP_PROTOCOL = "http";

    private static final String HTTPS_PROTOCOL = "https";

    /**
     * 获取标题
     *
     * @param document
     * @return
     */
    public static String parseTitle(Document document) {
        String title = StringUtils.trimToEmpty(document.title());
        if (title.length() > 255) {
            title = StringUtils.substring(title, 0, 255);
        }

        return title;
    }

    /**
     * 获取关键词
     *
     * @param document
     * @return
     */
    public static String parseKeywords(Document document) {
        String keywords = document.select("meta[name=keywords]").attr("content");
        return StringUtils.trimToEmpty(keywords);
    }

    /**
     * 获取摘要
     *
     * @param document
     * @return
     */
    public static String parseDescription(Document document) {
        String description = document.select("meta[name=description]").attr("content");
        return StringUtils.trimToEmpty(description);
    }

    /**
     * 尝试解析干净一点的Title
     *
     * @param title
     * @return
     */
    public static String parseCleanTitle(String title) {
        String tempTitle = title;
        tempTitle = StringUtils.removeStart(tempTitle, "【");
        if (StringUtils.startsWithIgnoreCase(tempTitle, "首页")) {
            tempTitle = StringUtils.removeAll(tempTitle, "首页( |\\||-|_)*");
        }
        String[] split = tempTitle.split(" |\\||-|_|◎|,|，|—|（|：|－|·|\\(|：|｜|【|】");
        String cleanTitle = split.length > 0 ? split[0] : tempTitle;
        cleanTitle = StringUtils.removeEnd(cleanTitle, "首页");
        cleanTitle = StringUtils.removeEnd(cleanTitle, "】");

        return cleanTitle;
    }

    /**
     * 获取文章标题
     *
     * @param html
     * @return
     */
    public static String parseArticleTitle(String html) {
        try {
            return ContentExtractor.getNewsByHtml(html).getTitle();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取文章发布时间
     *
     * @param html
     * @return
     */
    public static String parsePublishTime(String html, String url) {
        html = Pattern.compile("\\s+").matcher(html).replaceAll(" ");
        html = Pattern.compile("[\\u4e00-\\u9fa5]+来源").matcher(html).replaceAll(" ");
        html = Pattern.compile("<!--.*?-->").matcher(html).replaceAll("");
        html = Pattern.compile("/\\*.*?\\*/").matcher(html).replaceAll("");
        html = Pattern.compile("<style.*?>.*?</style>").matcher(html).replaceAll("");
        html = Pattern.compile("<script.*?>.*?</script>").matcher(html).replaceAll("");

        String res = "";
        String chinese = "(发布|创建|出版|发表|编辑|星期|来源|时间)";
        String english = "(publish|create)";
        String classSource = "(class=\"source\"|class=\"publishTime\")";
        String timeReg = "(20\\d{2}[-/年.])?(0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?";
        String ymd = "20\\d{2}[-/年.](0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?";
        String md = "(0[1-9]|1[0-2]|[1-9])[-/月.](0[1-9]|[1-2][0-9]|3[0-1]|[1-9])[日T]?\\s{0,2}(([0-1][0-9]|2[0-3]|[1-9])[:点时]([0-5][0-9]|[0-9])([:分]([0-5][0-9]|[0-9]))?)?";
        String urlTimeReg = "20\\d{2}[-/年_]*(0[1-9]|1[0-2]|[1-9])[-/月_]*(0[1-9]|[1-2][0-9]|3[0-1]|[1-9])";
        //第一优先级
        String[] first = {
                "(pubdate|pubtime|dateupdate)",
                english + "(.{0,10})(time|at|date)",
                chinese + "(时间|于|日期)",
                english + "(.{0,10})(time|at|date)",
                chinese + "(时间|于|日期)",
                classSource
        };

        //第二优先级
        String[] second = {
                chinese,
//                "(时间|time|日期|date|at\\W)",
        };

        Set<String> resList = new HashSet<>();
        Pattern patternTime = Pattern.compile(timeReg);

        // step 1
        //第一优先级先必须有年份匹配
        String match1 = matches(first, html, ymd);
        if (!"".equals(match1)) {
            Matcher matcherTime = patternTime.matcher(match1);
            if (matcherTime.find()) {
                res = matcherTime.group();
                resList.add(res);
            }
        }

        //第一优先级宽到可以没有年份
        String match2 = matches(first, html, timeReg);
        if (!"".equals(match2)) {
            Matcher matcherTime = patternTime.matcher(match2);
            if (matcherTime.find()) {
                res = matcherTime.group();
                resList.add(res);
            }
        }

        // 额外增加URL提取时间
        Matcher rm = Pattern.compile(urlTimeReg).matcher(url);
        if (rm.find()) {
            res = rm.group();
            res = Pattern.compile("[/_-]").matcher(res).replaceAll("");
            resList.add(res);
        }

        // step 2
        if (resList.size() == 0) {

            //第二优先级必须要有年份
            String match3 = matches(second, html, ymd);
            if (!"".equals(match3)) {
                Matcher matcherTime = patternTime.matcher(match3);
                if (matcherTime.find()) {
                    res = matcherTime.group();
                    resList.add(res);
                }
            }

            // step 3
            if (resList.size() == 0) {
                // 直接匹配日期
                Matcher m = Pattern.compile(timeReg).matcher(html);
                List<String> time = new ArrayList<>();
                while (m.find()) {
                    String item = m.group(0);
                    time.add(item);
                }
                if (time.size() == 1) {
                    res = time.get(0);
                    resList.add(res);
                } else {
                    List<String> newTime = new ArrayList<>();
                    for (String item : time) {
                        if (Pattern.compile(ymd).matcher(item).matches()) {
                            //优先找年月日齐全的
                            newTime.add(item);
                        }
                    }
                    if (!newTime.isEmpty()) {
                        time = newTime;
                    }
                    Iterator<String> timeIterator = time.iterator();
                    while (timeIterator.hasNext()) {
                        String item = timeIterator.next();
                        long ts = Static.strtotime(item);
                        //取时间精度高的
                        if (ts * 1000 > System.currentTimeMillis()) {
                            timeIterator.remove();
                            continue;
                        }
                        if (ts != 0 && ts % 100 != 0) {
                            res = item;
                            resList.add(res);
                            break;
                        }
                    }
                    if (res.equals("") && !time.isEmpty()) {
                        res = time.get(0);
                        resList.add(res);
                    }
                }

            }
        }

        long lastTimeStamp = 0;
        for (String res2 : resList) {
            res2 = filter(res2, ymd, md);
            long timeStamp = Static.strtotime(res2);
            if (timeStamp > 0 && timeStamp * 1000 <= System.currentTimeMillis()) {
                if (timeStamp >= lastTimeStamp) {
                    lastTimeStamp = timeStamp;
                }
            }
        }

        if (lastTimeStamp > 0) {
            return Static.date("yyyy-MM-dd HH:mm:ss", lastTimeStamp);
        }
        return null;
    }

    /**
     * 尝试解析 ICP 信息
     *
     * @param html
     * @return
     */
    public static String parseIcp(String html) {
        String icp = "";
        Matcher matcher = ICP_PATTERN.matcher(html);

        if (matcher.find()) {
            icp = matcher.group(0);
        }
        icp = Jsoup.clean(icp, Whitelist.none());
        icp = StringUtils.replaceAll(icp, " |", "");
        icp = StringUtils.substring(icp, 0, 255);
        icp = StringUtils.substring(icp, 0, StringUtils.indexOf(icp, "号") + 1);

        return icp;
    }

    /**
     * 获取当前页面所有链接集合
     *
     * @param document
     * @param domain   顶级域名
     * @param url      当前请求URL
     * @return
     */
    public static Set<String> parseLinks(Document document, String domain, String url) {
        Set<String> links = new HashSet<>();

        // 获取所有a链接
        Elements elements = document.select("a,area");
        if (elements.size() > 0) {
            for (Element element : elements) {
                String tmpLink = StringUtils.trimToEmpty(element.attr("href"));
                Set<String> tmpLinks = new HashSet<>();
                tmpLinks.add(tmpLink);

                String tmpLink2 = StringUtils.trimToEmpty(element.attr("url"));
                if (tmpLink2.length() > 0) {
                    tmpLinks.add(tmpLink2);
                }

                for (String link : tmpLinks) {
                    link = link.replace("\r\n", "");
                    link = link.replace("\n", "");
                    link = link.trim();

                    // 过滤垃圾链接
                    if (!UrlUtils.filterUrl(link)) {
                        continue;
                    }

                    // 转换补全相对、绝对路径
                    if (!StringUtils.startsWithIgnoreCase(link, HTTP_PROTOCOL)
                            && !StringUtils.startsWithIgnoreCase(link, HTTPS_PROTOCOL)) {
                        try {
                            URL absoluteUrl = new URL(url);

                            // path 为空的情况，这种一般是错误，直接移除
                            if (StringUtils.isBlank(absoluteUrl.getPath())) {
                                link = removeStartComplete(link, "./");
                                link = removeStartComplete(link, "../");
                            }

                            URL parseUrl = new URL(absoluteUrl, link);
                            link = parseUrl.toString();
                        } catch (IOException e) {
                            System.out.println(e.getLocalizedMessage() + ":" + url + ":" + link);
                            continue;
                        }
                    }

                    // 验证链接
                    if (!UrlUtils.verifyUrl(link)) {
                        continue;
                    }

                    // 排除站外链接
                    URL parse = UrlUtils.parse(link);
                    if (parse == null) {
                        continue;
                    }
                    String host = parse.getHost();
                    String topDomain = DomainUtils.topDomain(domain);
                    if (topDomain != null) {
                        if (!topDomain.equals(host) && !StringUtils.endsWithIgnoreCase(host, "." + topDomain)) {
                            continue;
                        }
                    }

                    // 最后验证一下这个 URL 的格式
                    if (UrlUtils.verifyUrl(link)) {
                        links.add(link);
                    }
                }
            }
        }

        return links;
    }

    /**
     * 根据页面上的链接，获取子域名集合
     *
     * @param document
     * @param domain
     * @param url
     * @return
     */
    public static Set<String> parseSubDomain(Document document, String domain, String url) {
        Set<String> subDomains = new HashSet<>();
        Set<String> links = parseLinks(document, domain, url);
        if (!links.isEmpty()) {
            for (String link : links) {
                URL parseUrl = UrlUtils.parse(link);

                if (parseUrl != null) {
                    String host = parseUrl.getHost();
                    String topDomain = DomainUtils.topDomain(domain);


                    if (topDomain != null) {
                        if (!topDomain.equals(host) && StringUtils.endsWithIgnoreCase(host, "." + topDomain)) {
                            host = StringUtils.lowerCase(host);
                            if (DOMAIN_PATTERN.matcher(host).find()) {
                                subDomains.add(host);
                            }
                        }
                    }
                }
            }
        }

        return subDomains;
    }

    /**
     * 循环删除起始字符串
     *
     * @param str
     * @param remove
     * @return
     */
    public static String removeStartComplete(final String str, final String remove) {
        String originString = str;

        do {
            if (originString.startsWith(remove)) {
                originString = StringUtils.removeStart(originString, remove);
            } else {
                break;
            }

        } while (true);

        return originString;
    }

    /**
     * 匹配合适的时间
     *
     * @param rules
     * @param str
     * @param timeReg
     * @return
     */
    private static String matches(String[] rules, String str, String timeReg) {
        for (String pattern : rules) {
            pattern = "(?i)((" + pattern + ".{0,50}" + timeReg + ")|(" + timeReg + ".{0,50}" + pattern + "))";
            Pattern r = Pattern.compile(pattern);
            Matcher matcher = r.matcher(str);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return "";
    }

    /**
     * 过滤时间字符串
     *
     * @param str
     * @param ymd
     * @param md
     * @return
     */
    private static String filter(String str, String ymd, String md) {
        str = Pattern.compile("[年月./]").matcher(str).replaceAll("-");
        str = Pattern.compile("[日秒]").matcher(str).replaceAll(" ");
        str = Pattern.compile("([点时分])").matcher(str).replaceAll(":");
        str = Pattern.compile("(T\\s?|\\s+)").matcher(str).replaceAll(" ");
        // 纯日期的 补-
        if (str.length() == 8 && Pattern.compile("\\d{8}").matcher(str).find()) {
            str = StringUtils.substring(str, 0, 4) + "-" + StringUtils.substring(str, 4, 6)
                    + "-" + StringUtils.substring(str, 6, 8);
        }
        //年份不齐的，补齐年份
        if (!Pattern.compile(ymd).matcher(str).find() && Pattern.compile(md).matcher(str).find()) {
            str = new SimpleDateFormat("yyyy").format(new Date()) + "-" + str;
        }
        return str.trim();
    }
}
