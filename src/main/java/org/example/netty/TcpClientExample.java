package org.example.netty;

import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;

public class TcpClientExample {
    public static void main(String[] args) {
        TcpClient.create()
                .host("localhost")
                .port(9000)
                .handle((in, out) -> {
                    // 먼저 서버에 메시지를 보냄
                    Mono<Void> send = out.sendString(Mono.just("Hello, Server!")).then();

                    // 서버로부터 응답 수신 처리
                    Mono<Void> receive = in.receive()
                            .asString()
                            .doOnNext(System.out::println)
                            .then();

                    // send 먼저 하고, 그다음 receive 실행
                    return send.then(receive);
                })
                .connectNow()
                .onDispose()
                .block();
    }
}


