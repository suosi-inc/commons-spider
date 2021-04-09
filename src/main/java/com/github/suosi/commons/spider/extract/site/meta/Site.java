package com.github.suosi.commons.spider.extract.site.meta;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * @author niuchaoqun
 */
@Data
@Builder
public class Site {
    private String protocol;

    private String mainDomain;

    private String charset;

    private String title;

    private String cleanTitle;

    private String keywords;

    private String description;

    private String icp;

    private String html;

    private Set<String> subDomain;

    private Set<String> links;

    private Map<String, String> linkTitles;

    private int httpcode;
}
