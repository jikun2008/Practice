package com.stomp.test.demo.stomp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/")
@Slf4j
public class StompReceiveController {


    //前缀默认隐藏了app
    //实际客户端向服务器发送的地址名称为 app/driver/relyOrder。
    @MessageMapping("/driver/relyOrder")
    private void receiveDriverOrderMsg(Principal principal, Long orderId) {
        log.error("orderId=" + orderId);
    }

    //前缀默认隐藏了app
    //实际客户端向服务器发送的地址名称为 app/driver/receiveGps。
    //并且Principal代表地址
    @MessageMapping("/driver/receiveGps")
    private void receiveDriverGpsMsg(Principal principal, String gps) {
        log.error("gps=" + gps);

    }
}