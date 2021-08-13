package com.github.suosi.commons.spider.utils;

import java.io.*;

public class FileUtils {


    // 读取html
    public static String  readHtml(String filepath) {
        File file = new File(filepath);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    // 记录
    public static void recordFile(String filepath, String content){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filepath, true));
            out.write(content);
            out.flush();
            out.close();
        } catch (Exception e) {

        }

    }
}
