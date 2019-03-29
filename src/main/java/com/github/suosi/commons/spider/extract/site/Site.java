package com.github.suosi.commons.spider.extract.site;

import lombok.Builder;
import lombok.Data;

/**
 * @author niuchaoqun
 */
@Data
@Builder
public class Site {
    private String protocol;

    private String mainDomain;

    private String subDomain;

    private String charset;

    private String originTitle;

    private String title;

    private String icp;

    private String html;
}
