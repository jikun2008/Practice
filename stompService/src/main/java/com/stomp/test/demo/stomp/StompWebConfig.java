package com.stomp.test.demo.stomp;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.web.socket.config.annotation.*;

import java.security.Principal;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebConfig extends WebSocketMessageBrokerConfigurationSupport implements WebSocketMessageBrokerConfigurer {

    //配置端口名称
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/driver")  //端点名称
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //服务端向客户端发送消息地址  客户端需要向服务注册该地址才能接受到这个消息
        //如果setUserDestinationPrefix("/user")
        //注意messagingTemplat.convertAndSendToUser("username","/queue/message","单个用户发送");
        //中的 "/queue/message"前面的这个/queue 一定要添加到enableSimpleBroker 否则convertAndSendToUser
        //会没有作用
        registry.enableSimpleBroker("/all", "/driverMessage")
                //第一参数 代表每3ms向客户端发送心跳,第二参数希望每ml收到心跳。
                .setHeartbeatValue(new long[]{3000L, 6000L})
                .setTaskScheduler(new DefaultManagedTaskScheduler());


        //发送到服务端目的地前缀 /app 开头的数据会被@MessageMapping拦截 进入方法体
        //客户端向服务器 发送的消息的开头地址
        registry.setApplicationDestinationPrefixes("/app");
        //服务端向客户端 向一个客户端发送消息
        registry.setUserDestinationPrefix("/user");
    }


    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {

    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                StompCommand command = accessor.getCommand();
                if (null != command) {

                    if (StompCommand.CONNECT.equals(command)) {
                        accessor.setUser(new Principal() {
                            @Override
                            public String getName() {
                                return "testUser";
                            }
                        });
                    }
                }
                return message;
            }
        });
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {

    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        return false;
    }


}
