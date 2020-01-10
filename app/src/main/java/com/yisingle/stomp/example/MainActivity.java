package com.yisingle.stomp.example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yisingle.stomp.practice.MessageInterceptor;
import com.yisingle.stomp.practice.Practice;
import com.yisingle.stomp.practice.VersionEnum;
import com.yisingle.stomp.practice.annotation.callback.OnStompConnect;
import com.yisingle.stomp.practice.annotation.callback.OnStompDisConnect;
import com.yisingle.stomp.practice.annotation.callback.OnStompSubscribe;
import com.yisingle.stomp.practice.message.StompHeader;
import com.yisingle.stomp.practice.message.StompMessage;
import com.yisingle.stomp.practice.utils.StompMessageHelper;

import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    private PracticeInstance practiceInstance;
    private TextView tvTextView;

    private StringBuilder stringBuilder = new StringBuilder();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTextView = findViewById(R.id.tvTextView);

        practiceInstance =PracticeInstance.getInstance();
        practiceInstance.register(this);

    }

    public void testConnect(View view) {

        practiceInstance.connect();

    }

    public void testDisConnect(View view) {

        practiceInstance.disConnect();

    }

    public void testSendMessage(View view) {
        //向服务端发送消息

        Gps gps=new Gps(30.658562,104.065735);
        practiceInstance.sendGps(gps);
    }


    @OnStompConnect
    public void onConnect() {
        stringBuilder = new StringBuilder();
        stringBuilder.append("连接成功\n");
        tvTextView.setText(stringBuilder.toString());
    }

    @OnStompDisConnect
    public void onDisConnect(Integer code, String info) {
        stringBuilder.append("连接失败:code=" + code + "__info=" + info + "\n");
        tvTextView.setText(stringBuilder.toString());
    }


    @OnStompSubscribe("/user/driverMessage/point")
    public void onReviceMessage(StompMessage message){

        //接受服务端的消息
        stringBuilder.append(message.compile() + "\n");
        tvTextView.setText(stringBuilder.toString());
    }
    @OnStompSubscribe("/user/driverMessage/order")
    public void onReviceOrderMessage(StompMessage message ){
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
