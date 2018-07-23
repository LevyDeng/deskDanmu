package com.sleepybear.deskdanmu;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Random;

import io.socket.client.IO;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainService extends Service {

    private static final String TAG = "MainService";

    ConstraintLayout toucherLayout;
    WindowManager.LayoutParams params;
    LinearLayout danmakuLayout;
    WindowManager.LayoutParams danmakuParams;
    WindowManager windowManager;
    LinearLayout inputLayout;
    WindowManager.LayoutParams inputParams;

    ImageButton imageButton1;
    EditText textInput;
    Button inputButton;
    Socket socket;

    //状态栏高度.
    int statusBarHeight = -1;

    boolean showDanmaku;
    DanmakuView danmakuView;
    DanmakuContext danmakuContext;
    BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    //不与Activity进行绑定.
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i(TAG,"MainService Created");
        createDanmaku();
        createInput();
        createToucher();
    }

    @SuppressLint("ClickableViewAccessibility")

    private void createDanmaku()
    {

        //赋值WindowManager&LayoutParam.
        danmakuParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        danmakuParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//| WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;;
        //设置效果为背景透明.
        danmakuParams.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        danmakuParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;//|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;

        //设置窗口初始停靠位置.
        danmakuParams.gravity = Gravity.START | Gravity.TOP;
        danmakuParams.x = 0;
        danmakuParams.y = 0;

        //设置悬浮窗口长宽数据.
        danmakuParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        danmakuParams.height = 750;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局.
        danmakuLayout = (LinearLayout) inflater.inflate(R.layout.danmakulayout,null);
        //添加toucherlayout
        windowManager.addView(danmakuLayout,danmakuParams);

        Log.i(TAG,"toucherlayout-->left:" + danmakuLayout.getLeft());
        Log.i(TAG,"toucherlayout-->right:" + danmakuLayout.getRight());
        Log.i(TAG,"toucherlayout-->top:" + danmakuLayout.getTop());
        Log.i(TAG,"toucherlayout-->bottom:" + danmakuLayout.getBottom());

        //主动计算出当前View的宽高信息.
        danmakuLayout.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);

        //用于检测状态栏高度.
        int resourceId = getResources().getIdentifier("status_bar_height","dimen","android");
        if (resourceId > 0)
        {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        Log.i(TAG,"状态栏高度为:" + statusBarHeight);

        danmakuView = (DanmakuView) danmakuLayout.findViewById(R.id.danmakuView);
        danmakuView.enableDanmakuDrawingCache(true);
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                showDanmaku = true;
                danmakuView.start();
                //generateSomeDanmaku();

                socket = getSocket();
                socket.connect();
                socket.emit("danmaku", "哈喽");
                Log.i(TAG,"Ws connection built.");

            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });

        danmakuContext = DanmakuContext.create();
        danmakuView.prepare(parser, danmakuContext);
    }

    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    private void createInput(){

        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(getApplication());

        inputParams = new WindowManager.LayoutParams();
        inputParams.gravity = Gravity.START | Gravity.TOP;
        inputParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        inputParams.format = PixelFormat.RGBA_8888;
        inputParams.x = 0;
        inputParams.y = 760;
        inputParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        inputParams.height = 150;
        inputLayout = (LinearLayout) inflater.inflate(R.layout.inputlayout,null);
        windowManager.addView(inputLayout,inputParams);

        inputButton = (Button) inputLayout.findViewById(R.id.inputButton);
        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = textInput.getText().toString();
                        textInput.setText("");
                        if (!TextUtils.isEmpty(msg)){
                            socket.emit("danmaku",msg);
                            Log.i(TAG,"Sent msg: "+msg);
                        }
                    }
                }).start();
            }

        });


    }

    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    private void createToucher()
    {

        //赋值WindowManager&LayoutParam.
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//| WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;;
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.START|Gravity.TOP;
        params.x = 800;
        params.y = 600;

        //设置悬浮窗口长宽数据.
        params.width = 150;
        params.height = 150;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局.
        toucherLayout = (ConstraintLayout) inflater.inflate(R.layout.toucherlayout,null);
        //添加toucherlayout
        windowManager.addView(toucherLayout,params);

        Log.i(TAG,"toucherlayout-->left:" + toucherLayout.getLeft());
        Log.i(TAG,"toucherlayout-->right:" + toucherLayout.getRight());
        Log.i(TAG,"toucherlayout-->top:" + toucherLayout.getTop());
        Log.i(TAG,"toucherlayout-->bottom:" + toucherLayout.getBottom());

        //浮动窗口按钮.
        imageButton1 = (ImageButton) toucherLayout.findViewById(R.id.imageButton1);
        textInput = (EditText) inputLayout.findViewById(R.id.textInput);

        imageButton1.setOnClickListener(new View.OnClickListener() {
            long[] hints = new long[2];
            @Override
            public void onClick(View v) {
                Log.i(TAG,"点击了");
                System.arraycopy(hints,1,hints,0,hints.length -1);
                hints[hints.length -1] = SystemClock.uptimeMillis();
                if (SystemClock.uptimeMillis() - hints[0] >= 500)
                {
                    Log.i(TAG,"要执行");
                    int visibility = inputLayout.getVisibility();
                    if (visibility != View.VISIBLE){
                        inputLayout.setVisibility(View.VISIBLE);
                        //inputParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                        //inputParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                        //windowManager.updateViewLayout(inputLayout,inputParams);
                    }
                    else{
                        inputLayout.setVisibility(View.GONE);
                        //inputParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                        //inputParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                        //windowManager.updateViewLayout(inputLayout,inputParams);
                    }
                    //Toast.makeText(MainService.this,"连续点击两次以退出",Toast.LENGTH_SHORT).show();
                }else
                {
                    Log.i(TAG,"即将关闭");
                    stopSelf();
                }
            }
        });

        imageButton1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                params.x = (int) event.getRawX() - 75;
                params.y = (int) event.getRawY() - 75 - statusBarHeight;
                danmakuParams.height=(int) event.getRawY() - 75 - statusBarHeight -160;
                inputParams.x=0;
                inputParams.y=params.y-150;
                //DisplayMetrics dm = getResources().getDisplayMetrics();
                //int width = dm.widthPixels;

                //使用layout方法移动
                //danmakuView.layout(10,10,width-20,params.y-150-10);
                //textInput.layout(10,params.y-140,width-150-20, params.y-10 );

                windowManager.updateViewLayout(danmakuLayout,danmakuParams);
                windowManager.updateViewLayout(toucherLayout,params);
                windowManager.updateViewLayout(inputLayout,inputParams);

                //使用layoutParams移动
                //LinearLayout.LayoutParams danmuParams = (LinearLayout.LayoutParams) danmakuView.getLayoutParams();
                //danmuParams.height=params.y;
                //danmuParams.setMargins(10,10,10,160);
                //danmakuView.setLayoutParams(danmuParams);

                //LinearLayout.LayoutParams inputParams = (LinearLayout.LayoutParams) textInput.getLayoutParams();
                //inputParams.setMargins(0,params.y-150,0,0);
                //textInput.setLayoutParams(inputParams);

                //inputParams.leftMargin=0;
                //inputParams.topMargin=params.y;

                return false;
            }
        });
    }

    /**
     * 向弹幕View中添加一条弹幕
     * @param content
     *          弹幕的具体内容
     * @param  withBorder
     *          弹幕是否有边框
     */
    public void addDanmaku(String content, boolean withBorder) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = sp2px(20);
        danmaku.textColor = Color.WHITE;
        danmaku.setTime(danmakuView.getCurrentTime());
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        danmakuView.addDanmaku(danmaku);
    }


    private Socket getSocket(){
        try {
            socket = IO.socket("http://192.168.100.213");

            socket.on("broadcasted danmaku", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    String msg = (String) args[0];
                    Log.i(TAG,msg);
                    addDanmaku(msg,false);
                }

            });
            return socket;
        }catch (URISyntaxException e){
            e.printStackTrace();
            return null;
        }
    }

    private Emitter.Listener onBroadcastedMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message = data.toString();
                /*
                String username;
                String message;
                try {
                    username = data.getString("username");
                    message = data.getString("message");
                } catch (JSONException e) {
                    return;
                }
                */

                    // add the message to view
                    addDanmaku(message,false);
                    Log.i(TAG,"Got msg: "+message);
                }
            });
        }
    };

    /**
     * 随机生成一些弹幕内容以供测试
     */
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(showDanmaku) {
                    int time = new Random().nextInt(300);
                    String content = "" + time + time;
                    addDanmaku(content, false);
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    @Override
    public void onDestroy()
    {
        if (imageButton1 != null)
        {
            windowManager.removeView(toucherLayout);
            windowManager.removeView(inputLayout);
            windowManager.removeView(danmakuLayout);
        }
        socket.disconnect();
        socket.off("broadcasted danmaku");
        super.onDestroy();
    }

}
