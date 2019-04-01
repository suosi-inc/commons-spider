package com.github.suosi.commons.spider.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author niuchaoqun
 */
public class UrlUtils {
    public static boolean verifyUrl(String url) {
        return StringUtils.isNotBlank(url)
                && !StringUtils.startsWithIgnoreCase(url, "javascript:")
                && !StringUtils.startsWithIgnoreCase(url, "mailto:")
                && !StringUtils.startsWithIgnoreCase(url, "#");
    }
}
