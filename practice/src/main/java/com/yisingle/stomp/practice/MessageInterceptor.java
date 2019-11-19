package com.yisingle.stomp.practice;

import com.yisingle.stomp.practice.message.StompMessage;

public interface MessageInterceptor {
    void intercept(StompMessage stompMessage);
}
