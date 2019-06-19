package com.github.suosi.commons.spider.utils;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.concurrent.TimeUnit;

/**
 * @author niuchaoqun
 */
public class OkHttpUtils {
    private static final ConnectionPool DEFAULT_CONNECTION_POOL = new ConnectionPool(300, 10, TimeUnit.SECONDS);

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";

    private static final long TIMEOUT = 5;

    public static OkHttpClient.Builder builder() {
       return builder(DEFAULT_CONNECTION_POOL);
    }

    public static OkHttpClient client() {
        return builder(DEFAULT_CONNECTION_POOL).build();
    }

    public static OkHttpClient client(ConnectionPool connectionPool) {
        return builder(connectionPool).build();
    }

    public static Request.Builder requestBuilder() {
        return new Request.Builder()
                .header("User-Agent", USER_AGENT);
    }

    public static Request request(String url) {
        return requestBuilder().url(url).build();
    }

    public static OkHttpClient.Builder builder(ConnectionPool connectionPool) {
        HttpsUtils.SSLParams sslSocketFactory = HttpsUtils.getSslSocketFactory(null, null, null);

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.connectionPool(connectionPool);
        builder.sslSocketFactory(sslSocketFactory.sSLSocketFactory, sslSocketFactory.trustManager);
        builder.hostnameVerifier((hostname, session) -> true);
        builder.followSslRedirects(true);
        builder.followRedirects(true);
        builder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(TIMEOUT, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);

        return builder;
    }
}
