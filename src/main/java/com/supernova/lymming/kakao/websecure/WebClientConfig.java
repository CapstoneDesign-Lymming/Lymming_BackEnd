
//package com.supernova.lymming.kakao.websecure;
//
//import io.netty.channel.ChannelOption;
//import io.netty.handler.timeout.ReadTimeoutHandler;
//import io.netty.handler.timeout.WriteTimeoutHandler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.client.reactive.ClientHttpConnector;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.netty.http.client.HttpClient;
//
//import java.time.Duration;
//
//@Configuration
//public class WebClientConfig {
//
//    @Bean
//    public WebClient webClient() {
//        // Reactor Netty HttpClient 설정
//        HttpClient httpClient = HttpClient.create()
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000) // 연결 타임아웃 1초
//                .responseTimeout(Duration.ofSeconds(1)) // 응답 타임아웃 1초
//                .doOnConnected(conn ->
//                        conn.addHandlerLast(new ReadTimeoutHandler(10)) // 읽기 타임아웃 10초
//                                .addHandlerLast(new WriteTimeoutHandler(10))); // 쓰기 타임아웃 10초
//
//        // 설정한 HttpClient를 WebClient에 적용
//        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
//        return WebClient.builder().clientConnector(connector).build();
//    }
//}
