package com.yisingle.stomp.example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yisingle.stomp.practice.Practice;
import com.yisingle.stomp.practice.VersionEnum;
import com.yisingle.stomp.practice.annotation.callback.OnStompConnect;
import com.yisingle.stomp.practice.annotation.callback.OnStompDisConnect;
import com.yisingle.stomp.practice.annotation.callback.OnStompSubscribe;
import com.yisingle.stomp.practice.message.StompMessage;
import com.yisingle.stomp.practice.utils.StompMessageHelper;

import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    private Practice practice;
    private TextView tvTextView;

    private StringBuilder stringBuilder = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTextView = findViewById(R.id.tvTextView);

        practice = new Practice.Builder().version(VersionEnum.VERSION_1_2)
                .build();
        practice.register(this);

    }

    public void testConnect(View view) {
        Request request = new Request.Builder().url("ws://10.28.6.69:8091/noSocketJs").build();
        practice.startConnect(request);

    }

    public void testDisConnect(View view) {
        practice.disConnect();

    }

    public void testSendMessage(View view) {
        practice.sendStompMessage(StompMessageHelper.createSendStompMessage("/app/hello1",null,"sssssss"));
    }

    public void testSendMessage1(View view) {
        practice.sendStompMessage(StompMessageHelper.createSendStompMessage("/app/hello1",null,"a测"));
    }

    @OnStompConnect
    public void onConnect() {
        stringBuilder = new StringBuilder();
        stringBuilder.append("连接成功\n");
        tvTextView.setText(stringBuilder.toString());
        practice.sendStompMessage(StompMessageHelper.createSubscribeStompMessage("/topic/hello", null));
        practice.sendStompMessage(StompMessageHelper.createSubscribeStompMessage("/queue/hello", null));
    }

    @OnStompDisConnect
    public void onDisConnect(Integer code, String info) {
        stringBuilder.append("连接失败:code=" + code + "__info=" + info + "\n");
        tvTextView.setText(stringBuilder.toString());
    }


    @OnStompSubscribe("/topic/hello")
    public void message(StompMessage stompMessage) {
        stringBuilder.append(stompMessage.compile() + "\n");
        tvTextView.setText(stringBuilder.toString());


    }

    @OnStompSubscribe("/queue/hello")
    public void messagequeue(StompMessage stompMessage) {
        stringBuilder.append(stompMessage.compile() + "\n");
        tvTextView.setText(stringBuilder.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        practice.unregister(this);
        practice.disConnect();
    }




}
