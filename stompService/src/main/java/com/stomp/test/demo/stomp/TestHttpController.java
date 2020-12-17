package com.stomp.test.demo.stomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestHttpController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping(value = "/testBroadcast")
    public String testBroadcast() {
        simpMessagingTemplate.convertAndSend("/all/driverMessage/broadcast", "广播的数据");
        return "发送广播";
    }


    @GetMapping(value = "/testSendUser")
    public String testSendUser() {
        //默认隐藏了前缀app
        simpMessagingTemplate.convertAndSendToUser("testUser", "/driverMessage/order", "单独用户发送");
        return "发送广播";
    }
}
