package com.yisingle.stomp.practice.message;


import android.text.TextUtils;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by naik on 05.05.16.
 */
public class StompMessage {


    private static final String TERMINATE_MESSAGE_SYMBOL = "\u0000";

    private static final Pattern PATTERN_HEADER = Pattern.compile("([^:\\s]+)\\s*:\\s*([^:\\s]+)");

    private final String mStompCommand;
    private final  Map<String,String> headersMap=new HashMap<>();
    private final String mPayload;

    public StompMessage(String stompCommand,List<StompHeader> headers,String payload) {
        mStompCommand = stompCommand;
        if(null!=headers){
            for(StompHeader h:headers){
                headersMap.put(h.getKey(),h.getValue());
            }
        }
        mPayload = payload;
    }

    public Map<String,String> getHeaderMap() {
        return headersMap;
    }

    public String getPayload() {
        return mPayload;
    }

    public String getStompCommand() {
        return mStompCommand;
    }


    public String findHeader(String key) {
        if (headersMap == null) return null;
        return headersMap.get(key);
    }


    public String compile() {
        return compile(false);
    }


    public String compile(boolean legacyWhitespace) {
        StringBuilder builder = new StringBuilder();
        builder.append(mStompCommand).append('\n');
        for (String key : headersMap.keySet()) {
          String  value = headersMap.get(key);
            builder.append(key).append(':').append(value).append('\n');
        }
        if (null != findHeader("StompHeader.CONTENT_LENGTH")) {
            addContentLengthHeader(builder, mPayload);
        }

        builder.append('\n');
        if (mPayload != null) {
            builder.append(mPayload);
            if (legacyWhitespace) builder.append("\n\n");
        }
        builder.append(TERMINATE_MESSAGE_SYMBOL);
        return builder.toString();
    }

    //这里要把payload转换成utf-8  utf-8   中文是3个字符 英文是1个字符
    private void addContentLengthHeader(StringBuilder builder, String payload) {
        if (!TextUtils.isEmpty(payload)) {
            byte[] bss = new byte[0];
            try {
                bss = payload.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            builder.append(StompHeader.CONTENT_LENGTH).append(':').append(bss.length).append('\n');
        }
    }

    public static StompMessage from(String data) {
        if ("\n".equals(data)) {
            return new StompMessage(StompCommand.HEART,null,  data);
        }
        if (data == null || data.trim().isEmpty()) {
            return new StompMessage(StompCommand.UNKNOWN,null,  data);
        }
        Scanner reader = new Scanner(new StringReader(data));
        reader.useDelimiter("\\n");
        String command = reader.next();
        List<StompHeader> headers = new ArrayList<>();

        while (reader.hasNext(PATTERN_HEADER)) {
            Matcher matcher = PATTERN_HEADER.matcher(reader.next());
            matcher.find();
            headers.add(new StompHeader(matcher.group(1), matcher.group(2)));
        }

        //reader.skip("\n\n");
        reader.useDelimiter(TERMINATE_MESSAGE_SYMBOL);
        String payload = reader.hasNext() ? reader.next() : null;

        return new StompMessage(command, headers, payload);
    }

    @Override
    public String toString() {
        return compile();
    }

    public static String getHeartBeatMessage() {
        return "\r\n";
    }


}
