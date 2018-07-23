//package com.sleepybear.deskdanmu;

import org.json.JSONObject;

import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class danmuTransfer {

    //private static final String DANMU_SERVER="ptrees.win";
    private static final String DANMU_SERVER="http://127.0.0.1:3000";
    private static final int SEND_PORT=998;
    private static final int RECV_PORT=997;

    public String getDanmu(){
        return "";
    }

    public static Socket getSocket(){
        return null;
    }

}




