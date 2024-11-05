package com.supernova.lymming.chatting.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import org.springframework.messaging.simp.config.MessageBrokerRegistry;


import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /*클라이언트가 웹 소켓 서버에 연결하는데 사용할 웹 소켓 엔드포인트 등록
	  withSockJS를 통해 웹 소켓을 지원하지 않는 브라우저에 대해 웹 소켓을 대체한다.
	  +)메소드명에 STOMP가 들어가는 경우 통신 프로토콜인 STOMP구현에서 작동된다. */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chatting") // 프론트엔드 주소 허용
                .setAllowedOrigins("http://localhost:5173", "http://localhost:5174") // 명시적인 출처 설정
                .withSockJS();
        // 아래 줄은 필요하지 않으므로 삭제합니다.
        // registry.addEndpoint("/chatting").setAllowedOrigins("*").withSockJS();
        // registry.addEndpoint("/chat").setAllowedOrigins("*").withSockJS();
        System.out.println("STOMP endpoint registered: /chatting");
    }

    /*한 클라이언트에서 다른 클라이언트로 메시지를 라우팅하는데 사용될 메시지 브로커*/
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // /sub 로 시작하는 stomp 메세지는 브로커로 라우팅함
        registry.enableSimpleBroker("/sub");
        // pub 시작되는 메시지는 MessageMaping으로 라우팅된다.
        registry.setApplicationDestinationPrefixes("/pub");
    }

}
