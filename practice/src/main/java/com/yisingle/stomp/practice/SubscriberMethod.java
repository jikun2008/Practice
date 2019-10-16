package com.yisingle.stomp.practice;

import java.lang.reflect.Method;

public class SubscriberMethod {

    private String destination;
    private Object subscriber;
    private Method method;
    private Class<?> parameterType;

    public SubscriberMethod(String destination, Object subscriber, Method method, Class<?> parameterType) {
        this.destination = destination;
        this.subscriber = subscriber;
        this.method = method;
        this.parameterType = parameterType;
    }

    public Object getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Object subscriber) {
        this.subscriber = subscriber;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
