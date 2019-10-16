package com.yisingle.stomp.practice;

import android.text.TextUtils;
import android.util.Log;


import com.yisingle.stomp.practice.connect.ConnectSendCheckUtils;
import com.yisingle.stomp.practice.message.StompCommand;
import com.yisingle.stomp.practice.message.StompHeader;
import com.yisingle.stomp.practice.message.StompMessage;
import com.yisingle.stomp.practice.utils.HeartBeatTask;
import com.yisingle.stomp.practice.utils.StompMessageHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class Practice {
    public static String TAG = Practice.class.getSimpleName();
    private OkHttpClient okHttpClient;
    private OkHttpClient.Builder okHttpBuilder;
    private WebSocket webSocket;
    public SubscribeClassHelper subscribeClassHelper;
    private StompMessage connectMessage;
    private ConnectSendCheckUtils connectSendCheckUtils;
    private HeartBeatTask heartBeatTask;

    private Practice(Builder builder, StompMessage connectMessage) {
        okHttpBuilder = builder.okHttpBuilder;
        okHttpClient = okHttpBuilder.build();
        this.connectMessage = connectMessage;
        connectSendCheckUtils = new ConnectSendCheckUtils();
        subscribeClassHelper = new SubscribeClassHelper();
        heartBeatTask = new HeartBeatTask();
    }


    public void startConnect(Request request) {
        okHttpClient.newWebSocket(request, webSocketListener);
    }

    public void disConnect() {
        if (null != webSocket) {
            webSocket.close(Code.errorCode, "主动关闭");
        }
    }

    public void register(Object subscriber) {
        if (null != subscriber) {
            subscribeClassHelper.register(subscriber);
        }
    }

    public void unregister(Object subscriber) {
        if (null != subscriber) {
            subscribeClassHelper.unregister(subscriber);
        }
    }

    public boolean sendStompMessage(StompMessage stompMessage) {
        return sendMessage(stompMessage.compile());
    }

    private boolean sendMessage(String message) {
        Log.e(TAG, message);
        if (null != webSocket) {
            return webSocket.send(message);
        } else {
            return false;
        }

    }

    private void parseMessage(String message, WebSocket webSocket) {
        //收到消息 那么刷新服务器的心跳时间
        heartBeatTask.reshServerHeatTime();


        //开始解析消息
        StompMessage stompMessage = StompMessage.from(message);

        if (!"\n".equals(message)) {
            Log.e(TAG, TAG + "-parseMessage:" + message);
        }

        if (StompCommand.CONNECTED.equals(stompMessage.getStompCommand())) {
            connectSendCheckUtils.stopCheck();
            String[] heartbeats = stompMessage.findHeader(StompHeader.HEART_BEAT).split(",");
            heartBeatTask.beginHeartBeat(webSocket, Long.parseLong(heartbeats[1]), Long.parseLong(heartbeats[0]), null);

            subscribeClassHelper.invokeConnectAll();
            //这里循环注册指令
//            for (StompMessage subscribeMessage : subscribeClassHelper.getsubscribeStompMessage()) {
//                boolean isSubscribeSuccess = sendMessage(subscribeMessage.compile());
//                if (isSubscribeSuccess) {
//                    // TODO: 2019/10/9 需要校验 是否注册成功
//                }
//            }


        } else if (StompCommand.HEART.equals(stompMessage.getStompCommand())) {
            //收到服务器心跳

        } else if (StompCommand.MESSAGE.equals(stompMessage.getStompCommand())) {

            String destination = "";
            for (StompHeader header : stompMessage.getStompHeaders()) {
                if (header.getKey().equals("destination")) {
                    destination = header.getValue();
                    break;
                }
            }
            if (!TextUtils.isEmpty(destination)) {
                subscribeClassHelper.invokeSubscriberAll(destination, stompMessage);
            }
        }
    }

    private WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
            heartBeatTask.stopHeartBeat();
            Log.e(TAG, TAG + "-onClosed-code=" + code + "reason=" + reason);
            subscribeClassHelper.invokeCloseAll(code, reason);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);
            heartBeatTask.stopHeartBeat();
            Log.i(TAG, TAG + "-onClosing-code=" + code + "reason=" + reason);
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            heartBeatTask.stopHeartBeat();
            Log.e(TAG, TAG + "-onFailure" + t.toString());
            subscribeClassHelper.invokeCloseAll(Code.errorCode, t.toString());

        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
            parseMessage(text, webSocket);
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            super.onMessage(webSocket, bytes);
            parseMessage(bytes.utf8(), webSocket);
        }

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            Practice.this.webSocket = webSocket;

            //发送连接消息
            if (sendMessage(connectMessage.compile())) {
                //开始检查是否收到Connected消息
                connectSendCheckUtils.startCheck(webSocket);
            } else {
                webSocket.close(Code.errorCode, "未能成功发送stomp连接消息");
            }
        }
    };


    public static final class Builder {
        private long sendHeartbeatTime = 3000;
        private long acceptHeartbeatTime = 3000;
        private String version = VersionEnum.VERSION_1_1.getVersion();
        private List<StompHeader> headerList;
        private OkHttpClient.Builder okHttpBuilder;
        private List<Class<?>> classList = new ArrayList<>();
        private Object subscribe;


        public Builder() {
            okHttpBuilder = new OkHttpClient.Builder()
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .connectTimeout(3, TimeUnit.SECONDS);
        }

        public Builder okHttpBuilder(OkHttpClient.Builder val) {
            okHttpBuilder = val;
            return this;
        }


        public Builder sendHeartbeatTime(long val) {
            sendHeartbeatTime = val;
            return this;
        }

        public Builder acceptHeartbeatTime(long val) {
            acceptHeartbeatTime = val;
            return this;
        }

        /**
         * 这里建议使用这个方法 可以避免错误
         *
         * @param val VersionEnum
         * @return Builder
         */
        public Builder version(VersionEnum val) {
            version = val.getVersion();
            return this;
        }

        /**
         * 扩展版本号。
         *
         * @param val String
         * @return
         */
        public Builder version(String val) {
            version = val;
            return this;
        }


        public Practice build() {
            StompMessage connectMessage = StompMessageHelper.createConnectStompMessage(version, sendHeartbeatTime, acceptHeartbeatTime, headerList);
            return new Practice(this, connectMessage);
        }
    }
}