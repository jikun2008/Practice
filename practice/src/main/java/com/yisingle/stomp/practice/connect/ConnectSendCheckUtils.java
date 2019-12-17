package com.yisingle.stomp.practice.connect;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.WebSocket;

/**
 * 连接消息检查器
 * 例如  可能出现已经发送 Connect指令了 但服务器未返回Connected消息，那么我们需要每隔1s检查一下
 * 如果3s内没有返回那么关闭连接
 */
public class ConnectSendCheckUtils {

    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    private long checkTime = 1000;

    private long lastCheckTime;

    private WebSocket webSocket;



    /**
     * 当发送Connect指令后 检查是否有Connected消息返回。
     */
    public void startCheck(WebSocket webSocket) {
        this.webSocket = webSocket;
        lastCheckTime = System.currentTimeMillis();
        synchronized (this){
        if(null==executor||executor.isTerminating()||executor.isShutdown()||executor.isTerminated()){
            executor = new ScheduledThreadPoolExecutor(1);
            executor.scheduleAtFixedRate(checkRunable, 0, checkTime, TimeUnit.MILLISECONDS);
        }
        }

    }

    Runnable checkRunable = new Runnable() {
        @Override
        public void run() {
            long now = System.currentTimeMillis();
            final long boundary = now - (3 * checkTime);
            if (boundary > lastCheckTime) {
                if (null != webSocket) {
                    webSocket.close(1000, "主动关闭:reason:未能收到Connected指令");
                }
            }
        }
    };

    /**
     * 当收到Connected消息后就停止检查
     */
    public void stopCheck() {
        synchronized (this) {
            if (null != executor) {
                executor.shutdownNow();
                executor = null;
            }
        }


    }
}
