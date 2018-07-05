package com.sleepybear.deskdanmu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class danmuTransfer {

    //private static final String DANMU_SERVER="http://ptrees.ddns.net";
    private static final String DANMU_SERVER="192.168.100.42";
    private static final int SEND_PORT=999;
    private static final int RECV_PORT=997;

    public String getDanmu(){
        return "";
    }

    public Socket getOutput(){
        try {
            return new Socket(DANMU_SERVER, RECV_PORT);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /*get danmu with http
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
    */


    /*
    *get danmu with socket
     */
    public Socket getInput(){
        try {
            return new Socket(DANMU_SERVER, SEND_PORT);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
