package com.github.suosi.commons.spider.utils.okhttp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OkHttpProxy {
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String key;
}
