package com.github.suosi.commons.spider.utils;

import java.util.Random;

/**
 * @author niuchaoqun
 */
public class UserAgentUtils {

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static final String MOZILLA = "Mozilla/5.0 ";

    private static final String[][] OS = {
            {
                    "Windows NT 6.1",
                    "Windows NT 6.1; Win64; x64",
                    "Windows NT 6.2",
                    "Windows NT 6.2; Win64; x64",
                    "Windows NT 6.3",
                    "Windows NT 6.3; Win64; x64",
                    "Windows NT 6.4",
                    "Windows NT 6.4; Win64; x64",
                    "Windows NT 10",
                    "Windows NT 10; Win64; x64"
            },
            {
                    "Macintosh; Intel Mac OS X 10_12_1",
                    "Macintosh; Intel Mac OS X 10_12_2",
                    "Macintosh; Intel Mac OS X 10_12_3",
                    "Macintosh; Intel Mac OS X 10_12_4",
                    "Macintosh; Intel Mac OS X 10_12_5",
                    "Macintosh; Intel Mac OS X 10_13_2",
                    "Macintosh; Intel Mac OS X 10_13_3",
                    "Macintosh; Intel Mac OS X 10_13_4",
                    "Macintosh; Intel Mac OS X 10_13_5",
            }
    };

    private static final String[] CHROME_VERSION = {
            "68.0.3440.0",
            "69.0.3497.0",
            "70.0.3538.0",
            "71.0.3578.0",
            "72.0.3626.0",
            "73.0.3683.0",
    };

    private static final String[] FIREFOX_VERSION = {
            "51.0",
            "52.0",
            "53.0",
            "54.0",
            "55.0",
            "56.0",
            "57.0",
            "58.0",
            "59.0",
            "60.0",
            "61.0",
            "62.0",
    };

    private static final String[] IE_VERSION = {
            "8.0",
            "9.0",
            "10.0",
    };

    public static String test() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.81 Safari/537.36";
    }

    public static String all() {
        int r = RANDOM.nextInt(3);
        switch (r) {
            case 0 :
                return chrome();
            case 1 :
                return firefox();
            case 2 :
                return ie();
            default:
                return chrome();
        }
    }

    public static String firefox() {
        String version = FIREFOX_VERSION[RANDOM.nextInt(FIREFOX_VERSION.length)];

        StringBuffer sb = new StringBuffer();
        sb.append(MOZILLA);
        sb.append("(");
        sb.append(randomStringArray(OS[RANDOM.nextInt(OS.length)]));
        sb.append("; rv:");
        sb.append(version);
        sb.append(")");
        sb.append(" Gecko/20121011 ");
        sb.append("Firefox/");
        sb.append(version);

        return sb.toString();
    }

    public static String chrome() {
        StringBuffer sb = new StringBuffer();
        sb.append(MOZILLA);
        sb.append("(");
        sb.append(randomStringArray((OS[RANDOM.nextInt(OS.length)])));
        sb.append(")");
        sb.append(" AppleWebKit/537.36 (KHTML, like Gecko) Chrome/");
        sb.append(CHROME_VERSION[RANDOM.nextInt(CHROME_VERSION.length)]);
        sb.append(" Safari/537.36");

        return sb.toString();
    }

    public static String ie() {
        String version = IE_VERSION[RANDOM.nextInt(IE_VERSION.length)];
        String trident = version.equals("10.0") ? "; Trident/6.0" : "; Trident/5.0";

        StringBuffer sb = new StringBuffer();
        sb.append(MOZILLA);
        sb.append("(");
        sb.append("compatible; MSIE ");
        sb.append(version);
        sb.append("; ");
        sb.append(randomStringArray((OS[0])));
        sb.append(trident);
        sb.append(")");

        return sb.toString();
    }

    private static String randomStringArray(String[] array) {
        int length = array.length;
        return array[RANDOM.nextInt(length)];
    }
}
