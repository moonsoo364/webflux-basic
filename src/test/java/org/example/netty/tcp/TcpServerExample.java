package org.example.netty.tcp;

import lombok.extern.slf4j.Slf4j;
import reactor.netty.tcp.TcpServer;

@Slf4j
public class TcpServerExample {
    public static void main(String[] args) {
        log.info("run");
        TcpServer.create()
                .host("localhost")
                .port(9000)
                .handle((in, out) -> {
                    return out.sendString(
                            in.receive()
                                    .asString()
                                    .map(data -> "Echo: " + data)
                    );
                })
                .bindNow()
                .onDispose()
                .block();
    }
}
