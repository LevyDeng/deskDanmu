package com.sleepybear.deskdanmu;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class test{

    public static void test(String[] args){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("user","ptrees")
                .add("content","hello")
                .build();
        Request req = new Request.Builder()
                .post(body)
                .url("http://127.0.0.1/danmureceiver/")
                .build();
        try {
            Response res = client.newCall(req).execute();
            System.out.println(res.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}