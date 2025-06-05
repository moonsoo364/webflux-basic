package org.example.config.netty;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.resources.LoopResources;

import java.time.Duration;

@Configuration
public class NettyConfig {

    @Bean
    public ReactiveWebServerFactory reactiveWebServerFactory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();

        //http-nio http keep-alive 연결 시간 설정, Connection 및 Keep-Alive 설정
        factory.addServerCustomizers(httpServer -> {
            return httpServer
                    .idleTimeout(Duration.ofSeconds(30))  // 연결 유지시간
                    .compress(true);                       // HTTP 압축
        });

        return factory;
    }
}
