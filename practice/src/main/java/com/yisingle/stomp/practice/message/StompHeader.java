package com.yisingle.stomp.practice.message;

/**
 * Created by naik on 05.05.16.
 */
public final class StompHeader {

    public static final String VERSION = "accept-version";
    public static final String HEART_BEAT = "heart-beat";
    public static final String DESTINATION = "destination";
    public static final String CONTENT_LENGTH="content-length";
    public static final String CONTENT_TYPE = "content-type";
    public static final String MESSAGE_ID = "message-id";
    public static final String ID = "id";
    public static final String ACK = "ack";

    private  String mKey;
    private  String mValue;

    public StompHeader(String key, String value) {
        mKey = key;
        mValue = value;
    }

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String mValue) {
        this.mValue = mValue;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    @Override
    public String toString() {
        return "StompHeader{" + mKey + '=' + mValue + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StompHeader that = (StompHeader) o;

        return mKey != null ? mKey.equals(that.mKey) : that.mKey == null;
    }

    @Override
    public int hashCode() {
        return mKey != null ? mKey.hashCode() : 0;
    }
}
