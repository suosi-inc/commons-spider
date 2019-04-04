package com.github.suosi.commons.spider.extract.site;

import com.github.suosi.commons.spider.extract.site.meta.Site;

public class PageExtract {
    public static Site url(String url) {
//        if (StringUtils.isNotBlank(url) && UrlUtils.verifyUrl(url)) {
//            try (Response response = OkHttpUtils.client().newCall(OkHttpUtils.request(url)).execute()) {
//                if (response.isSuccessful() && response.body() != null) {
//
//                    // 编码
//                    byte[] htmlBytes = response.body().bytes();
//                    String charset = StringUtils.upperCase(CharsetUtils.guessEncoding(htmlBytes, response));
//                    if (StringUtils.startsWith(charset, "GB")) {
//                        charset = "GBK";
//                    }
//                    String html = new String(htmlBytes, charset);
//
//                    Document document = Jsoup.parse(html);
//                    String title = Parse.parseTitle(document);
//                    String keywords = Parse.parseKeywords(document);
//                    String description = Parse.parseDescription(document);
//                    String icp = Parse.parseIcp(html);
//
//
//                    return Page.builder()
//                            .charset(charset)
//                            .html(html)
//                            .title(title)
//                            .keywords(keywords)
//                            .description(description)
//                            .links(links)
//                            .build();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        return null;
    }
}
