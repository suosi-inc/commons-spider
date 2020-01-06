package com.github.suosi.commons.spider.utils.okhttp;

import com.github.suosi.commons.spider.utils.UrlUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;

/**
 * Todo 代理不起效果
 */
public class OkHttpInterceptor implements Interceptor {
    /**
     * 最大内容长度 3M
     */
    private static final Long MAX_LENGTH = 3072000L;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Response response =  chain.proceed(request);

        String lastUrl = response.request().url().toString();
        String contentType = response.headers().get("Content-Type");
        String contentLength = response.headers().get("Content-Length");

        // 过滤URL
        if (!UrlUtils.filterUrl(lastUrl)) {
            response.close();
        }

        // 排除非以下类型的
        if (contentType != null && !StringUtils.containsAny(StringUtils.lowerCase(contentType),
                "text/html",
                "text/xml",
                "text/mathml",
                "text/vnd.wap.wml",
                "application/atom+xml",
                "application/rss+xml",
                "application/json",
                "application/xhtml+xml",
                "application/xspf+xml",
                "application/javascript")) {
            response.close();
        }

        // 排除超过3M的
        if (contentLength != null && NumberUtils.toLong(contentLength) > MAX_LENGTH) {
            response.close();
        }

        return response;
    }
}