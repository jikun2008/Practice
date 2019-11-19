package com.yisingle.stomp.practice.utils;

import android.text.TextUtils;

import com.yisingle.stomp.practice.message.StompCommand;
import com.yisingle.stomp.practice.message.StompHeader;
import com.yisingle.stomp.practice.message.StompMessage;

import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class StompMessageHelper {
    public static final String DEFAULT_ACK = "auto";


    public static StompMessage createConnectStompMessage(String version, long sendHeartbeatTime, long acceptHeartbeatTime, List<StompHeader> otherHeaders) {
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader(StompHeader.VERSION, version));
        headers.add(new StompHeader(StompHeader.HEART_BEAT,
                sendHeartbeatTime + "," + acceptHeartbeatTime));
        if (null != otherHeaders) {
            headers.addAll(otherHeaders);
        }

        return new StompMessage(StompCommand.CONNECT, headers, null);
    }

    public static StompMessage createSendStompMessage(String destination, StompHeader stompHeader, String data) {
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader(StompHeader.DESTINATION, destination));


        if (null != stompHeader) {
            headers.add(stompHeader);
        }
        return new StompMessage(
                StompCommand.SEND,
                headers,
                data);
    }

    /**
     * @param destinationPath
     * @param headerList
     * @return
     */
    public static StompMessage createSubscribeStompMessage(String destinationPath, @Nullable List<StompHeader> headerList) {
        //String topicId = destinationPath + "_time_" + System.currentTimeMillis() + "uuid_" + UUID.randomUUID().toString();
        String topicId = destinationPath;
        ConcurrentHashMap<String, String> topics = new ConcurrentHashMap<>();

//        // Only continue if we don't already have a subscription to the topic
//        if (topics.containsKey(destinationPath)) {
//            Log.d(TAG, "Attempted to subscribe to already-subscribed path!");
//            return Completable.complete();
//        }

        topics.put(destinationPath, topicId);
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader(StompHeader.ID, topicId));
        headers.add(new StompHeader(StompHeader.DESTINATION, destinationPath));

        if (!isHaveAck(headerList)) {
            headers.add(new StompHeader(StompHeader.ACK, DEFAULT_ACK));
        }
        if (headerList != null) headers.addAll(headerList);
        return new StompMessage(StompCommand.SUBSCRIBE,
                headers, null);
    }


    public static StompMessage createAckStompMessage(String id) {
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("id", id));
        return new StompMessage(
                StompCommand.ACK,
                headers,
                null);
    }


    private static boolean isHaveAck(List<StompHeader> headers) {
        if (null == headers) {
            headers = new ArrayList<>();
        }
        for (StompHeader header : headers) {
            if (StompHeader.ACK.equals(header.getKey())) {
                return true;
            }
        }
        return false;
    }
}
