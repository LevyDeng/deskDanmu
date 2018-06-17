package com.lightingcontour.toucher;

import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class danmuTransfer {

    //private static final String DANMUSERVER="http://danmu.ptrees.top";
    private static final String DANMUSERVER="http://192.168.1.6/danmureceiver/";

    public String getDanmu(){
        String s="";
        return s;
    }

    public void sendDanmu(final String danmu){
        new Thread(new Runnable() {
            @Override
            public void run() {
                send(danmu);
            }
        }).start();
    }

    private void send(String danmu){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("user","ptrees")
                .add("content", danmu)
                .build();
        Request req = new Request.Builder()
                .post(body)
                .url(DANMUSERVER)
                .build();

        try{
            client.newCall(req).execute();
        }catch (IOException e){
            e.printStackTrace();
        }


    }
}
