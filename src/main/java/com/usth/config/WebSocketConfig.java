package com.usth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        // Client sẽ subscribe vào các đường dẫn bắt đầu bằng /topic để nhận tin nhắn
        // Ví dụ: /topic/Hanoi, /topic/Paris
        config.enableSimpleBroker("/topic");

        // Client sẽ gửi tin nhắn đến server qua đường dẫn bắt đầu bằng /app
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/chat-websocket")
                // QUAN TRỌNG: Cho phép tất cả các nguồn truy cập để dễ demo
                // (Thay vì chỉ cho localhost:3000)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}