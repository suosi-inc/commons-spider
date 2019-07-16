package com.github.suosi.commons.spider.publish;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.suosi.commons.spider.extract.site.Parse;
import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import okhttp3.Response;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TimeTests {

    @Test
    public void run() throws IOException {
        String json = this.loadJson();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = new ObjectMapper().readTree(json);
        List<HashMap> list = new ArrayList<>();

        for (JsonNode objNode : data) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            String url = objNode.get("url").toString();
            map.put("id",objNode.get("id"));
            map.put("url",url);
            map.put("published",objNode.get("published"));
            map.put("time", this.getHtml(url.replaceAll("\"","")));
            list.add(map);
            System.out.println(map);
        }
        mapper.writeValue(new File("fix.json"), list);
//        System.out.println(list);

    }

    @Test
    public void  test()
    {
        String url = "http://www.mnw.cn/news/digi/2070601.html";
        String time = this.getHtml(url);
        System.out.println(time);
    }

    private String getHtml(String url) {
        try (Response response = OkHttpUtils.client().newCall(OkHttpUtils.request(url)).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                byte[] htmlBytes = response.body().bytes();
                String charset = CharsetUtils.guessCharset(htmlBytes, response);
                String html = new String(htmlBytes, charset);
                return Parse.parsePublishTime(html);
            }

        } catch (Exception e){
            System.out.println(e.getLocalizedMessage() + ":" + url);
        }

        return "";
    }


    private String loadJson() {
        String jsonStr = "";
        try {
            File jsonFile = new File("/Users/yangtao/Desktop/time.json");
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new java.io.InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();

            return jsonStr;

        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return jsonStr;
    }
}
