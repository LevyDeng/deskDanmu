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

    //private static final String DANMUSERVER="http://danmu.ptrees.top";
    private static final String DANMU_SERVER="127.0.0.1";
    private static final int SEND_PORT=9998;
    private static final int RECV_PORT=9999;

    public String getDanmu(){
        String s="";
        return s;
    }

    public DataInputStream getReader(){
        try {
            Socket socket = new Socket(DANMU_SERVER, RECV_PORT);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            return in;
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
            Socket socket = new Socket(DANMU_SERVER, SEND_PORT);
            return socket;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
