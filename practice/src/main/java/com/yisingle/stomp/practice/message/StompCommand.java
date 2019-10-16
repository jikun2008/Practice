package com.yisingle.stomp.practice.message;

/**
 * Created by naik on 05.05.16.
 */
public class StompCommand {

    public static final String CONNECT = "CONNECT";
    public static final String CONNECTED = "CONNECTED";
    public static final String SEND = "SEND";
    public static final String MESSAGE = "MESSAGE";
    public static final String SUBSCRIBE = "SUBSCRIBE";
    public static final String UNSUBSCRIBE = "UNSUBSCRIBE";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String ACK = "ACK";

    public static final String UNKNOWN = "UNKNOWN";

    /**
     * stomp dont have HEART command just custom by myself
     */
    public static final String HEART = "HEART";
}
