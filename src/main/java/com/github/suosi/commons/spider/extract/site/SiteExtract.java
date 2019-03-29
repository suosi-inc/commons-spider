package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author niuchaoqun
 */
public class SiteExtract {
    private static final Pattern ICP_PATTERN = Pattern.compile("(京|津|冀|晋|蒙|辽|吉|黑|沪|苏|浙|皖|闽|赣|鲁|豫|鄂|湘|粤|桂|琼|川|蜀|贵|黔|云|滇|渝|藏|陇|甘|陕|秦|青|宁|新)ICP(备|证)(.*)号", Pattern.CASE_INSENSITIVE);

    private static final String WWW_PREFIX = "www.";

    private static final String HTTP_PROTOCOL = "http";

    private static final String HTTPS_PROTOCOL = "https";

    public static Site domain(String domain) {
        if (StringUtils.isNotBlank(domain)) {

            String mainDomain = domain;
            HashMap<String, String> urls = new HashMap<>();

            if (!StringUtils.startsWithIgnoreCase(domain, WWW_PREFIX)) {
                mainDomain = WWW_PREFIX + domain;
                urls.put(HTTP_PROTOCOL, HTTP_PROTOCOL + "://" + mainDomain);
                urls.put(HTTPS_PROTOCOL, HTTPS_PROTOCOL + "://" + mainDomain);
            } else {
                urls.put(HTTP_PROTOCOL, HTTP_PROTOCOL + "://" + mainDomain);
                urls.put(HTTPS_PROTOCOL, HTTPS_PROTOCOL + "://" + mainDomain);
            }

            for (Map.Entry<String, String> urlEntry : urls.entrySet()) {
                String protocol = urlEntry.getKey();
                String url = urlEntry.getValue();

                try (Response response = OkHttpUtils.client().newCall(OkHttpUtils.request(url)).execute()) {
                    if (response.isSuccessful() && response.body() != null) {

                        // 编码
                        byte[] htmlBytes = response.body().bytes();
                        String charset = StringUtils.upperCase(CharsetUtils.guessEncoding(htmlBytes));
                        if (StringUtils.startsWith(charset, "GB")) {
                            charset = "GBK";
                        }
                        String html = new String(htmlBytes, charset);

                        Document document = Jsoup.parse(html);
                        String originTitle = parseOriginTitle(document);
                        String title = parseTitle(originTitle);
                        String icp = parseIcp(html);

                        return Site.builder().protocol(protocol)
                                .mainDomain(mainDomain)
                                .charset(charset)
                                //.html(html)
                                .title(title)
                                .originTitle(originTitle)
                                .icp(icp)
                                .build();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        return null;
    }

    private static String parseOriginTitle(Document document) {

        String originTitle = StringUtils.trimToEmpty(document.title());
        if (originTitle.length() > 255) {
            originTitle = StringUtils.substring(originTitle, 0, 255);
        }

        return originTitle;
    }

    private static String parseTitle(String originTitle) {
        String tempTitle = originTitle;
        tempTitle = StringUtils.removeStart(tempTitle, "【");
        if (StringUtils.startsWithIgnoreCase(tempTitle, "首页")) {
            tempTitle = StringUtils.removeAll(tempTitle, "首页( |\\||-|_)*");
        }
        String[] split = tempTitle.split(" |\\||-|_|◎|,|，|—|（|：|－|·|\\(|：|｜|【|】");
        String title = split.length > 0 ? split[0] : tempTitle;
        title = StringUtils.removeEnd(title, "首页");
        title = StringUtils.removeEnd(title, "】");

        return title;
    }

    private static String parseIcp(String html) {
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
}
