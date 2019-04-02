package com.github.suosi.commons.spider.utils;

import com.google.common.net.InternetDomainName;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;

/**
 * @author niuchaoqun
 */
public class DomainUtils {
    public static String topDomainFromUrl(String url) {
        URL parse = UrlUtils.parse(url);
        if (parse != null) {
            String domain = parse.getHost();
            return topDomain(domain);
        }

        return null;
    }

    public static String topDomain(String domain) {
        if (StringUtils.isNotBlank(domain) && InternetDomainName.isValid(domain)) {
            InternetDomainName topDomain = InternetDomainName.from(domain).topPrivateDomain();
            return topDomain.toString();
        }

        return null;
    }
}
