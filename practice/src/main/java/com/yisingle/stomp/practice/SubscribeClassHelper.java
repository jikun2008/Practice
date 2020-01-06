package com.yisingle.stomp.practice;

import android.os.Handler;
import android.os.Looper;


import com.yisingle.stomp.practice.annotation.callback.OnStompConnect;
import com.yisingle.stomp.practice.annotation.callback.OnStompDisConnect;
import com.yisingle.stomp.practice.annotation.callback.OnStompSubscribe;
import com.yisingle.stomp.practice.message.StompMessage;
import com.yisingle.stomp.practice.utils.StompMessageHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class SubscribeClassHelper {
    private Handler handler;

    /*
     * In newer class files, compilers may add methods. Those are called bridge or synthetic methods.
     * EventBus must ignore both. There modifiers are not public but defined in the Java class file format:
     * http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.6-200-A.1
     */
    private static final int BRIDGE = 0x40;
    private static final int SYNTHETIC = 0x1000;

    private static final int MODIFIERS_IGNORE = Modifier.ABSTRACT | Modifier.STATIC | BRIDGE | SYNTHETIC;
    private Map<Class<?>, List<SubscriberMethod>> METHOD_CACHE = new ConcurrentHashMap<>();

    private Map<String, CopyOnWriteArrayList<SubscriberMethod>> destinationMethodMap = new ConcurrentHashMap<>();

    private Map<String, CopyOnWriteArrayList<SubscriberMethod>> connectMethodMap = new ConcurrentHashMap<>();


    protected SubscribeClassHelper() {

        handler = new Handler(Looper.getMainLooper());
        connectMethodMap.put("OnConnect", new CopyOnWriteArrayList<SubscriberMethod>());
        connectMethodMap.put("OnClose", new CopyOnWriteArrayList<SubscriberMethod>());

    }

    protected void register(Object subscriber) {
        Class<?> subscriberClass = subscriber.getClass();//获取当前对象的class
        List<SubscriberMethod> methodList = findSubscriberMethodList(subscriber, subscriberClass);


        for (SubscriberMethod subscriberMethod : methodList) {
            String destination = subscriberMethod.getDestination();
            CopyOnWriteArrayList<SubscriberMethod> subscriptions = destinationMethodMap.get(destination);
            if (subscriptions == null) {
                subscriptions = new CopyOnWriteArrayList<>();
                destinationMethodMap.put(destination, subscriptions);
            }
            subscriptions.add(subscriberMethod);
        }


    }

    protected synchronized void unregister(Object subscriber) {
        METHOD_CACHE.remove(subscriber.getClass());
        for (Map.Entry<String, CopyOnWriteArrayList<SubscriberMethod>> entry : destinationMethodMap.entrySet()) {
            List<SubscriberMethod> methList = entry.getValue();
            for (SubscriberMethod method : methList) {
                if (method.getSubscriber() == subscriber) {
                    methList.remove(method);
                }
            }
        }
    }

    protected void clear() {
        METHOD_CACHE.clear();
        destinationMethodMap.clear();

    }

    private List<SubscriberMethod> findSubscriberMethodList(Object subscriber, Class<?> subscriberClass) {
        List<SubscriberMethod> methodList = METHOD_CACHE.get(subscriberClass);
        if (null != methodList && methodList.size() > 0) {
            return methodList;
        } else {
            return findUsingReflectionInSingleClass(subscriber, subscriberClass);
        }

    }

    private List<SubscriberMethod> findUsingReflectionInSingleClass(Object subscriber, Class<?> subscriberClass) {
        List<SubscriberMethod> methodList = new ArrayList<>();
        Method[] methods;
        try {
            methods = subscriberClass.getDeclaredMethods();//反射获取所有方法（public,private,protected,默认类型 ）
        } catch (Throwable th) {
            methods = subscriberClass.getMethods();
        }
        for (Method method : methods) {
            int modifiers = method.getModifiers();//方法修饰符
            if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & MODIFIERS_IGNORE) == 0) {
                //方法修饰类型必须是public=共有的 && 同时（不能抽象的，不能静态的，不能桥接的，不能合成的）
                // （ MODIFIERS_IGNORE == Modifier.ABSTRACT | Modifier.STATIC | BRIDGE | SYNTHETIC）
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1) {
                    OnStompSubscribe subscribeAnnotation = method.getAnnotation(OnStompSubscribe.class);
                    if (subscribeAnnotation != null) {
                        String destination = subscribeAnnotation.value();
                        //参数类型
                        Class<?> parameterType = parameterTypes[0];
                        if (!parameterType.equals(StompMessage.class)) {
                            throw new Error("method " + method.getName() + " have wrong parameterType:" + parameterType.getName() + " parameterType must be StompMessage");
                        }

                        methodList.add(new SubscriberMethod(destination, subscriber, method, parameterType));
                        METHOD_CACHE.put(subscriberClass, methodList);
                    }
                }


                OnStompConnect connectAnnotation = method.getAnnotation(OnStompConnect.class);
                if (null != connectAnnotation) {
                    if (parameterTypes.length > 0) {
                        throw new Error("method " + method.getName() + " have wrong parameterType size" +
                                ":" + " @OnStompConnect method  do not have any parameters  please use " + method.getName() + "() instead of it");
                    }
                    connectMethodMap.get("OnConnect").add(new SubscriberMethod("", subscriber, method, null));
                }

                OnStompDisConnect disConnectAnnotation = method.getAnnotation(OnStompDisConnect.class);
                if (null != disConnectAnnotation) {
                    if (parameterTypes.length != 2) {
                        throw new Error("method " + method.getName() + " have wrong parameterType size" +
                                ":" + " @StompDisConnect method  must have two parameters  please use " + method.getName() + "(Integer code,String info) instead of it");
                    } else {

                        if (!(parameterTypes[0].equals(Integer.class)||  parameterTypes[0].equals(int.class))) {
                            throw new Error("method " + method.getName() + " have wrong parameterType:" + parameterTypes[0].getName() +
                                    "  first parameterType must be Integer " +
                                    " please use " + method.getName() + "(Integer code,String info) instead of it");
                        }
                        if (!parameterTypes[1].equals(String.class)) {
                            throw new Error("method " + method.getName() + " have wrong parameterType:" + parameterTypes[1].getName() +
                                    "  first parameterType must be String " +
                                    " please use " + method.getName() + "(Integer code,String info) instead of it");
                        }
                    }
                    connectMethodMap.get("OnClose").add(new SubscriberMethod("", subscriber, method, null));
                }


            }

        }
        return methodList;


    }

    public List<StompMessage> getsubscribeStompMessage() {
        List<StompMessage> stompMessageList = new ArrayList<>();
        for (Map.Entry<String, CopyOnWriteArrayList<SubscriberMethod>> entry : destinationMethodMap.entrySet()) {
            stompMessageList.add(StompMessageHelper.createSubscribeStompMessage(entry.getKey(), null));
        }
        return stompMessageList;
    }

    public void invokeConnectAll() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                CopyOnWriteArrayList<SubscriberMethod> writeArrayList = connectMethodMap.get("OnConnect");
                Object[] emptyObject = new Object[0];
                if (null != writeArrayList) {
                    for (SubscriberMethod method : writeArrayList) {
                        try {
                            method.getMethod().invoke(method.getSubscriber(), emptyObject);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void invokeCloseAll(final Integer code, final String info) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                CopyOnWriteArrayList<SubscriberMethod> writeArrayList = connectMethodMap.get("OnClose");
                if (null != writeArrayList) {
                    for (SubscriberMethod method : writeArrayList) {
                        try {
                            method.getMethod().invoke(method.getSubscriber(), code, info);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     * 收到消息应该调用这个
     *
     * @param destination  注册的destination 地址
     * @param stompMessage StompMessage
     */
    public void invokeSubscriberAll(final String destination, final StompMessage stompMessage) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                CopyOnWriteArrayList<SubscriberMethod> writeArrayList = destinationMethodMap.get(destination);
                if (null != writeArrayList) {
                    for (SubscriberMethod method : writeArrayList) {
                        try {
                            method.getMethod().invoke(method.getSubscriber(), stompMessage);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
        });
    }
}
