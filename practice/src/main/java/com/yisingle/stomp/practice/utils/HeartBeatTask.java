package com.yisingle.stomp.practice.utils;

import android.util.Log;


import com.yisingle.stomp.practice.Code;
import com.yisingle.stomp.practice.Practice;
import com.yisingle.stomp.practice.message.StompMessage;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.WebSocket;

public class HeartBeatTask {

    private String TAG = HeartBeatTask.class.getSimpleName();

    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private ScheduledThreadPoolExecutor executorServiceCheck = new ScheduledThreadPoolExecutor(1);
    private long sendHeatTime;
    private long acceptHeatTime;

    private long lastCheckServerTime;
    private WebSocket webSocket;

    private OnCheckServerListener failServerListener;

    public void beginHeartBeat(WebSocket webSocket, long sendHeatTime, long acceptHeatTime, OnCheckServerListener failServerListener) {
        this.webSocket = webSocket;
        this.sendHeatTime = sendHeatTime;
        this.acceptHeatTime = acceptHeatTime;
        this.failServerListener = failServerListener;
        stopHeartBeat();
        lastCheckServerTime = System.currentTimeMillis();
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(runnable, 0, this.sendHeatTime, TimeUnit.MILLISECONDS);
        executorServiceCheck = new ScheduledThreadPoolExecutor(1);
        executorServiceCheck.scheduleAtFixedRate(checkRunable, 0, this.acceptHeatTime, TimeUnit.MILLISECONDS);
    }


    public void stopHeartBeat() {
        if (null != executor) {
            executor.shutdownNow();
            executor = null;
        }
        if (null != executorServiceCheck) {
            executorServiceCheck.shutdownNow();
            executorServiceCheck = null;
        }

    }


    private void sendHeartMessage() {
        if (null != webSocket) {
            if(Practice.isDebug){
                Log.e(TAG, "[Send]:Heart");
            }
            webSocket.send(StompMessage.getHeartBeatMessage());
        } else {
            Log.e(TAG, "sendHeartMessage Failed beacause websocket is null");
        }
    }


    public void reshServerHeatTime() {
        lastCheckServerTime = System.currentTimeMillis();
    }

    /**
     * 这里需要在收到服务器的心跳消息来设置
     */
    public void checkServerHeatTime() {

        if (acceptHeatTime > 0) {
            long now = System.currentTimeMillis();
            //use a forgiving boundary as some heart beats can be delayed or lost.
            final long boundary = now - (3 * acceptHeatTime);

            if (lastCheckServerTime < boundary) {
                Log.e(TAG, "It's a sad day ;( Server didn't send heart-beat on time. Last received at '" + lastCheckServerTime + "' and now is '" + now + "'");
                if (null != webSocket) {
                    webSocket.close(Code.errorCode, "未收到服务器心跳数据");
                }
                if (null != failServerListener) {
                    failServerListener.onServerHeartBeatFailed();
                }
            } else {
                Log.e(TAG, "We were checking and server sent heart-beat on time. So well-behaved :)");
                lastCheckServerTime = System.currentTimeMillis();
            }
        }


    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                sendHeartMessage();
            } catch (Exception e) {
                Log.e(TAG, "sendHeartMessage Failed beacause websocket have" + e.toString());
            }

        }
    };

    Runnable checkRunable = new Runnable() {
        @Override
        public void run() {
            checkServerHeatTime();
        }
    };

    public interface OnCheckServerListener {

        void onServerHeartBeatFailed();

    }
}
