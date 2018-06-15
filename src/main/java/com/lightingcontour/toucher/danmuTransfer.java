package com.lightingcontour.toucher;


import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class danmuTransfer {

    private static final String DANMUSERVER="http://danmu.ptrees.top";

    public String getDanmu(){
        String s="";
        return s;
    }

    public void sendDanmu(String danmu){
        try {
            URL url = new URL(DANMUSERVER);
            URLConnection conn = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)conn;
            http.setConnectTimeout(5000);
            http.setReadTimeout(15000);
            http.setDoOutput(true);
            http.setDoInput(true);
            http.setRequestMethod("POST");
            Map<String,String> data = new HashMap<>();
            data.put("user","ptrees");
            data.put("content",danmu);
            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String,String> entry: data.entrySet()) {
                sj.add(URLEncoder.encode(entry.getKey(), "utf8") + "=" +
                        URLEncoder.encode(entry.getValue(), "utf8"));
            }
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            try(OutputStream os = http.getOutputStream()){
                os.write(out);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
