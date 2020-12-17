package com.yisingle.stomp.practice;

import java.lang.reflect.Method;

public class SubscriberMethod {

    private String group;
    private String destination;
    private Object subscriber;
    private Method method;
    private Class<?> parameterType;
    private Class<?> parameterType1;


    public SubscriberMethod(String group, String destination, Object subscriber, Method method, Class<?> parameterType, Class<?> parameterType1) {
        this.group=group;
        this.destination = destination;
        this.subscriber = subscriber;
        this.method = method;
        this.parameterType = parameterType;
        this.parameterType1=parameterType1;

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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Class<?> getParameterType1() {
        return parameterType1;
    }

    public void setParameterType1(Class<?> parameterType1) {
        this.parameterType1 = parameterType1;
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
