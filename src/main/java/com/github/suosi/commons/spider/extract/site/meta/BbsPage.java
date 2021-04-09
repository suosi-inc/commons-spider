package com.github.suosi.commons.spider.extract.site.meta;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class BbsPage {
    private String charset;

    private String title;

    private String keywords;

    private String description;

    private String html;

    private Set<String> links;

    private Map<String, String> linkTitles;

    private int httpcode;
    
    private String url;

    private Set<HashMap> lists;
}
