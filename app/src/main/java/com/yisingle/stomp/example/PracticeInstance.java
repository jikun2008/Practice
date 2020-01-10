package com.yisingle.stomp.example;

import com.yisingle.stomp.practice.Practice;
import com.yisingle.stomp.practice.VersionEnum;
import com.yisingle.stomp.practice.message.StompHeader;
import com.yisingle.stomp.practice.utils.StompMessageHelper;

import okhttp3.Request;

public class PracticeInstance {

    private Practice practice = new Practice.Builder().version(VersionEnum.VERSION_1_2)
            .build();

    /**
     * 对保存实例的变量添加volatile的修饰
     */

    private volatile static PracticeInstance instance = null;

    private PracticeInstance() {

    }

    public static PracticeInstance getInstance() {

//先检查实例是否存在，如果不存在才进入下面的同步块

        if (instance == null) {

//同步块，线程安全的创建实例

            synchronized (PracticeInstance.class) {

//再次检查实例是否存在，如果不存在才真的创建实例

                if (instance == null) {

                    instance = new PracticeInstance();

                }

            }

        }

        return instance;

    }
    public void register(Object object){
        practice.register(object);
    }
    public void unregister(Object object){
        practice.unregister(object);
    }

    public void connect(){
        Request request = new Request.Builder().url("ws://192.168.43.78:8072/driver").build();

        StompHeader nameHeader = new StompHeader("token","123");
        practice.startConnect(request,nameHeader);

    }

    public void disConnect(){
        practice.disConnect();
    }

    public void sendSubscribeMessage(){
        //发起注册端点连接
        practice.sendStompMessage(
                StompMessageHelper.createSubscribeStompMessage(
                        "/all/driverMessage/broadcast",
                        null
                )
        );
        //发起注册端点连接
        practice.sendStompMessage(
                StompMessageHelper.createSubscribeStompMessage(
                        "/user/driverMessage/point",
                        null
                )
        );

        //发起注册端点连接
        practice.sendStompMessage(
                StompMessageHelper.createSubscribeStompMessage(
                        "/user/driverMessage/order",
                        null
                )
        );
    }

    //长连接收到了订单并回复消息给服务器告诉服务器我收到了订单
    // 这个时候服务会把订单状态改成
    //已经分发给司机等待司机响应 STATUS_SEND_DRIVER(1)
    public void sendRelyOrder(Long id){
        practice.sendStompMessage(StompMessageHelper.createSendStompMessage("/app/driver/relyOrder", null,id.toString() ));
    }

    public void sendGps(Gps gps){
        practice.sendStompMessage(StompMessageHelper.createSendStompMessage("/app/driver/receiveGps", null,  "123"));
    }

}
