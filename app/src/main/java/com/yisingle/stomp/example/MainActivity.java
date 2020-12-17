package com.yisingle.stomp.example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yisingle.stomp.practice.annotation.callback.OnStompConnect;
import com.yisingle.stomp.practice.annotation.callback.OnStompDisConnect;
import com.yisingle.stomp.practice.annotation.callback.OnStompSubscribe;
import com.yisingle.stomp.practice.message.StompMessage;

/**
 * demo请启动stompService
 * 调用这个接口 http://localhost:8090/test/testBroadcast
 * demo 就可以 收到服务器发送的广播消息
 * <p>
 * <p>
 * 调用这个接口 http://localhost:8090/test/testSendUser
 * demo 就可以 收到服务器发送的个人消息消息
 */

public class MainActivity extends AppCompatActivity {
    private PracticeInstance practiceInstance;
    private TextView tvTextView;

    private StringBuilder stringBuilder = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTextView = findViewById(R.id.tvTextView);

        practiceInstance = PracticeInstance.getInstance();
        practiceInstance.register(this);

    }

    public void testConnect(View view) {

        practiceInstance.connect();

    }

    public void testDisConnect(View view) {

        practiceInstance.disConnect();

    }

    public void testSendGps(View view) {
        //向服务端发送消息
        practiceInstance.sendGps();
    }

    public void testSendRely(View view) {
        //向服务端发送消息
        practiceInstance.sendRelyOrder(9999l);
    }

    public void testSendImMsg(View view) {
        if (view.getId() == R.id.btSendIm1) {
            practiceInstance.sendimMsg("123456");
        } else if (view.getId() == R.id.btSendIm2) {
            practiceInstance.sendimMsg("tttsss222");
        }
    }


    @OnStompConnect
    public void onConnect() {
        stringBuilder = new StringBuilder();
        stringBuilder.append("连接成功\n");
        tvTextView.setText(stringBuilder.toString());
        PracticeInstance.getInstance().sendSubscribeMessage();
    }

    @OnStompDisConnect
    public void onDisConnect(Integer code, String info) {
        stringBuilder.append("连接失败:code=" + code + "__info=" + info + "\n");
        tvTextView.setText(stringBuilder.toString());
    }


    @OnStompSubscribe("/user/driverMessage/order")
    public void onReviceOrderMessage(StompMessage message) {
        //接受服务端的消息
        stringBuilder.append(message.compile() + "\n");
        tvTextView.setText(stringBuilder.toString());

    }

    @OnStompSubscribe("/all/driverMessage/broadcast")
    public void onReviceBroadcastMessage(StompMessage message) {
        //接受服务端的消息
        stringBuilder.append(message.compile() + "\n");
        tvTextView.setText(stringBuilder.toString());

    }

    @OnStompSubscribe("/all/driverMessage/im/123456")
    public void onReviceBroadcastIMiDMessage(StompMessage message) {
        //接受服务端的消息
        stringBuilder.append(message.compile() + "\n");
        tvTextView.setText(stringBuilder.toString());

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        practiceInstance.unregister(this);
        practiceInstance.disConnect();
    }


}
