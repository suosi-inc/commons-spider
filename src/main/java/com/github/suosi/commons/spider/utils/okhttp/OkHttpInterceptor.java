package com.github.suosi.commons.spider.utils.okhttp;

import com.github.suosi.commons.spider.utils.UrlUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;

/**
 * Todo 代理不起效果
 */
public class OkHttpInterceptor implements Interceptor {
    private static final String TAG = "OkHttpInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // long startTime = System.nanoTime();
        // System.out.println(TAG + String.format(" Sending request %s on %s%n%s",
        //         request.url(), chain.connection(), request.headers()));

        Response response =  chain.proceed(request);

        // long endTime = System.nanoTime();
        // System.out.println(TAG +  String.format(" Received response for %s in %.1fms%n%s",
        //         response.request().url(), (endTime - startTime) / 1e6d, response.headers()));

        String lastUrl = response.request().url().toString();
        String contentType = response.headers().get("Content-Type");
        String contentLength = response.headers().get("Content-Length");

        // 过滤URL
        if (!UrlUtils.filterUrl(lastUrl)) {
            response.close();
        }

        // 排除非以下类型的
        if (contentType != null && !StringUtils.containsAny(StringUtils.lowerCase(contentType),
                "text/html", "text/xml","application/atom+xml", "application/rss+xml",
                "text/mathml", "application/json", "application/xhtml+xml", "application/xspf+xml",
                "text/vnd.wap.wml", "application/javascript")) {
            response.close();
        }

        // 排除超过3M的
        if (contentLength != null && Long.parseLong(contentLength) > 3072000) {
            // 3M
            response.close();
        }

        return response;
    }
}