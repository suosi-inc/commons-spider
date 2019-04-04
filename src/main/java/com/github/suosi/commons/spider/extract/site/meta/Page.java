package com.github.suosi.commons.spider.extract.site.meta;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class Page {
    private String charset;

    private String title;

    private String keywords;

    private String description;

    private String html;

    private Set<String> links;
}
