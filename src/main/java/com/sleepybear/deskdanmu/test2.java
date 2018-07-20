//package com.sleepybear.deskdanmu;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class test2 {

    public static void main(String[] args){
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();
        Request request = new Request.Builder().url("ws://127.0.0.1:3000").build();
        DanmuWebSocketListener listener = new DanmuWebSocketListener();
        WebSocket ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
        ws.send("Hello!");

    }

}

class DanmuWebSocketListener extends WebSocketListener{
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    @Override
    public void onOpen(WebSocket webSocket, Response response){
        webSocket.send("Hello!");
        System.out.println("Starting connection");
    }

    @Override
    public void onMessage(WebSocket webSocket, String msg){
        System.out.println("Got message: "+msg);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes){
        System.out.println("Got bytes: "+bytes);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason){
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        System.out.println("Closing.");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response){
        System.out.println("Error: "+t.getMessage());
    }
}
