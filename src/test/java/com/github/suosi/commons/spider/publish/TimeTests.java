package com.github.suosi.commons.spider.publish;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.suosi.commons.helper.Static;
import com.github.suosi.commons.spider.extract.site.Parse;
import com.github.suosi.commons.spider.utils.CharsetUtils;
import com.github.suosi.commons.spider.utils.OkHttpUtils;
import okhttp3.Response;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            map.put("time", getHtml(url.replaceAll("\"","")));
            list.add(map);
            System.out.println(map);
        }
        mapper.writeValue(new File("/Users/yangtao/Desktop/fix3.json"), list);
//        System.out.println(list);

    }

    @Test
    public void  test()
    {
        System.out.println(Static.strtotime("2015年04月6日 16:03:3"));
//        String year = new SimpleDateFormat("yyyy").format(new Date());
//        System.out.println(year);
//        String url = "https://weibo.com/1751960002/HBz0ns9eM?type=comment";
//        String time = getHtml(url);
//        System.out.println(time);
    }

    @Test
    public void time()
    {
        String timeReg = "(20\\d{2})\\D.?([0-1]?\\d)\\D?([0-3]?\\d)((\\D{0,2})?(\\d{1,2}\\D\\d{1,2})(\\D\\d{1,2})?)?";
        String html = "title=\\\"2019-07-01 11:41\\\"";

        System.out.println( Parse.parsePublishTime(html));
        Pattern r = Pattern.compile(timeReg);
        Matcher matcher = r.matcher(html);
        if (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    static String getHtml(String url) {
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
