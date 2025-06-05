package org.example.connetion_pool.controller;

import io.r2dbc.pool.ConnectionPool;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.netty.resources.ConnectionPoolMetrics;

import java.util.Map;

@RestController("/r2dbc")
@RequiredArgsConstructor
public class ConnectionPoolController {
    private final ConnectionPool connectionPool;

    @GetMapping("/pool")
    public Mono<Map<String,Object>> getPoolStatus(){
        ConnectionPoolMetrics metrics = (ConnectionPoolMetrics) connectionPool.getMetrics().get();

        return Mono.just(Map.of(
                "idleConnections", metrics.idleSize(),
                "acquiredConnections", metrics.acquiredSize(),
                "pendingAcquireSize", metrics.pendingAcquireSize(),
                "maxAllocatedSize", metrics.maxAllocatedSize()
                ));

    }
}
