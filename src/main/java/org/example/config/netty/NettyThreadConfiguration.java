package org.example.config.netty;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.resources.LoopResources;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

@Configuration
public class NettyThreadConfiguration {

    // 1. 서버 (HttpServer)의 Event Loop 쓰레드 설정
    // 이 빈은 Spring Boot가 내장 Netty 서버를 구성할 때 사용됩니다.
    @Bean
    public LoopResources httpServerLoopResources() {
        // "http-server-event-loop"라는 이름의 쓰레드 그룹을 생성하고,
        // 쓰레드 수를 4개로 설정합니다. (기본: CPU 코어 수)
        // 두 번째 인자(4)가 쓰레드 수입니다.
        // 세 번째 인자(true)는 데몬 쓰레드 여부입니다.
        return LoopResources.create("http-server-event-loop", 4, true);
    }

    // NettyReactiveWebServerFactory를 커스터마이징하여 위에 정의한 LoopResources를 사용하도록 합니다.
    @Bean
    public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> nettyCustomizer(LoopResources httpServerLoopResources) {
        return factory -> factory.addServerCustomizers(httpServer ->
                httpServer.runOn(httpServerLoopResources));
    }


    // 2. WebClient (HttpClient)의 Event Loop 쓰레드 설정
    // 만약 서버와 클라이언트가 다른 Event Loop 리소스를 사용해야 한다면,
    // 별도의 LoopResources 빈을 정의할 수 있습니다.
    // 보통은 서버와 클라이언트가 동일한 LoopResources를 공유하는 경우가 많습니다.
    @Bean
    public LoopResources httpClientLoopResources() {
        // "http-client-event-loop"라는 이름의 쓰레드 그룹을 생성하고,
        // 쓰레드 수를 2개로 설정합니다. (예시)
        return LoopResources.create("http-client-event-loop", 2, true);
    }

    @Bean
    public HttpClient httpClient(LoopResources httpClientLoopResources) {
        // WebClient에서 사용할 HttpClient에 커스텀 LoopResources를 적용
        return HttpClient.create().runOn(httpClientLoopResources);
    }

    // WebClient를 빌드할 때 위에서 정의한 HttpClient를 사용하도록 설정
    // @Bean
    // public WebClient webClient(HttpClient httpClient) {
    //     return WebClient.builder()
    //         .clientConnector(new ReactorClientHttpConnector(httpClient))
    //         .build();
    // }
}
