package com.github.suosi.commons.spider.utils;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.concurrent.TimeUnit;

/**
 * @author niuchaoqun
 */
public class OkHttpUtils {
    /**
     * DEFAULT_USER_AGENT
     */
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";

    /**
     * DEFAULT_TIMEOUT
     */
    private static final long DEFAULT_TIMEOUT = 3;

    /**
     * DEFAULT_CONNECTION_POOL
     */
    private static final ConnectionPool DEFAULT_CONNECTION_POOL = new ConnectionPool(5, 10, TimeUnit.SECONDS);

    /**
     * OkHttpClient
     */
    private static final OkHttpClient DEFAULT_CLIENT = new OkHttpClient();

    /**
     * connectionPool
     *
     * @return ConnectionPool
     */
    public static ConnectionPool connectionPool() {
        return DEFAULT_CONNECTION_POOL;
    }

    /**
     * OkHttpClient 默认
     *
     * @return OkHttpClient
     */
    public static OkHttpClient client() {
        return builder(DEFAULT_CONNECTION_POOL, 0).build();
    }

    /**
     * OkHttpClient 自定义连接池
     *
     * @param connectionPool
     * @return OkHttpClient
     */
    public static OkHttpClient client(ConnectionPool connectionPool) {
        return builder(connectionPool, 0).build();
    }

    /**
     * OkHttpClient 自定义超时时间，默认连接池
     *
     * @param timeoutSecond
     * @return OkHttpClient
     */
    public static OkHttpClient client(long timeoutSecond) {
        return builder(DEFAULT_CONNECTION_POOL, timeoutSecond).build();
    }


    /**
     * OkHttpClient 自定义连接池，自定义超时时间
     *
     * @param connectionPool
     * @param timeoutSecond
     * @return
     */
    public static OkHttpClient client(ConnectionPool connectionPool, long timeoutSecond) {
        return builder(connectionPool, timeoutSecond).build();
    }

    /**
     * 默认请求体
     *
     * @param url
     * @return
     */
    public static Request request(String url) {
        return requestBuilder().url(url).build();
    }

    /**
     * 默认请求体 Builder
     *
     * @return
     */
    public static Request.Builder requestBuilder() {
        return new Request.Builder()
                .header("User-Agent", DEFAULT_USER_AGENT);
    }

    /**
     * OkHttpClient 默认 Builder
     *
     * @return
     */
    public static OkHttpClient.Builder builder() {
        return builder(null, 0);
    }

    /**
     * OkHttpClient 自定义连接池，超时时间 Builder
     *
     * @param connectionPool
     * @param timeoutSecond
     * @return
     */
    public static OkHttpClient.Builder builder(ConnectionPool connectionPool, long timeoutSecond) {

        connectionPool = connectionPool != null ? connectionPool : DEFAULT_CONNECTION_POOL;
        long timeout = timeoutSecond > 0 ? timeoutSecond : DEFAULT_TIMEOUT;

        HttpsUtils.SSLParams sslSocketFactory = HttpsUtils.getSslSocketFactory(null, null, null);

        OkHttpClient.Builder builder = DEFAULT_CLIENT.newBuilder();
        builder.connectionPool(connectionPool);
        builder.sslSocketFactory(sslSocketFactory.sSLSocketFactory, sslSocketFactory.trustManager);
        builder.hostnameVerifier((hostname, session) -> true);
        builder.followSslRedirects(true);
        builder.followRedirects(true);
        builder.connectTimeout(timeout, TimeUnit.SECONDS);
        builder.readTimeout(timeout, TimeUnit.SECONDS);
        builder.writeTimeout(timeout, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);

        return builder;
    }
}
