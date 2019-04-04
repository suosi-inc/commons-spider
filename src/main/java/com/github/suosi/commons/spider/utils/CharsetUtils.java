package com.github.suosi.commons.spider.utils;

import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author niuchaoqun
 */
public class CharsetUtils {
    private static final int CHUNK_SIZE = 2000;

    private static final Pattern META_PATTERN = Pattern.compile(
            "<meta\\s+([^>]*http-equiv=(\"|')?content-type(\"|')?[^>]*)>",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern CHARSET_PATTERN = Pattern.compile(
            "charset=\\s*([a-z][_\\-0-9a-z]*)", Pattern.CASE_INSENSITIVE);

    private static final Pattern CHARSET_PATTERN_HTML5 = Pattern.compile(
            "<meta\\s+charset\\s*=\\s*[\"']?([a-z][_\\-0-9a-z]*)[^>]*>",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern CHARSET_PATTERN_HEADER = Pattern.compile(
            "charset=\\s*([a-z][_\\-0-9a-z]*)",
            Pattern.CASE_INSENSITIVE);

    /**
     * 从Nutch借鉴的网页编码检测代码
     *
     * @param content
     * @return
     */
    private static String guessEncodingByNutch(byte[] content) {
        int length = Math.min(content.length, CHUNK_SIZE);

        String str;
        try {
            str = new String(content, "ascii");
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        Matcher metaMatcher = META_PATTERN.matcher(str);
        String encoding = null;
        if (metaMatcher.find()) {
            Matcher charsetMatcher = CHARSET_PATTERN.matcher(metaMatcher.group(1));
            if (charsetMatcher.find()) {
                encoding = charsetMatcher.group(1);
            }
        }
        if (encoding == null) {
            metaMatcher = CHARSET_PATTERN_HTML5.matcher(str);
            if (metaMatcher.find()) {
                encoding = metaMatcher.group(1);
            }
        }
        if (encoding == null) {
            if (length >= 3 && content[0] == (byte) 0xEF
                    && content[1] == (byte) 0xBB && content[2] == (byte) 0xBF) {
                encoding = "UTF-8";
            } else if (length >= 2) {
                if (content[0] == (byte) 0xFF && content[1] == (byte) 0xFE) {
                    encoding = "UTF-16LE";
                } else if (content[0] == (byte) 0xFE
                        && content[1] == (byte) 0xFF) {
                    encoding = "UTF-16BE";
                }
            }
        }

        return encoding;
    }

    /**
     * 根据字节数组，猜测可能的字符集，如果检测失败，返回utf-8
     *
     * @param bytes 待检测的字节数组
     * @return 可能的字符集，如果检测失败，返回utf-8
     */
    private static String guessEncodingByMozilla(byte[] bytes) {
        String defaultEncoding = "UTF-8";
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        if (encoding == null) {
            encoding = defaultEncoding;
        }
        return encoding;
    }

    /**
     * 根据 OkHttpClient Response 返回字符集，如果没有则返回 null
     *
     * @param response
     * @return
     */
    public static String guessResponseEncoding(Response response) {
        String encoding = null;
        String contentType = response.header("Content-Type");
        if (contentType != null) {
            Matcher matcher = CHARSET_PATTERN_HEADER.matcher(contentType);
            if (matcher.find()) {
                encoding = matcher.group(1);
            }
        }

        return encoding;
    }

    /**
     * 根据字节数组，猜测可能的字符集，如果检测失败，返回utf-8
     *
     * @param content 待检测的字节数组
     * @return 可能的字符集，如果检测失败，返回utf-8
     */
    public static String guessCharset(byte[] content) {
        String encoding;
        try {
            encoding = guessEncodingByNutch(content);
        } catch (Exception ex) {
            return formatEncoding(guessEncodingByMozilla(content));
        }

        if (encoding == null) {
            encoding = guessEncodingByMozilla(content);
        }

        return formatEncoding(encoding);
    }

    /**
     * 猜测可能的字符集，以 Response 为准
     *
     * @param content
     * @param response
     * @return
     */
    public static String guessCharset(byte[] content, Response response) {
        String encoding = guessResponseEncoding(response);
        if (encoding == null) {
            encoding = guessCharset(content);
        }

        return formatEncoding(encoding);
    }

    /**
     * 格式化
     *
     * @param encoding
     * @return
     */
    public static String formatEncoding(String encoding) {
        String charset = StringUtils.upperCase(encoding);
        if (StringUtils.startsWith(charset, "GB")) {
            charset = "GBK";
        }

        return charset;
    }
}
