package org.example.connetion_pool.controller;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.PoolMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.netty.resources.ConnectionPoolMetrics;

import java.util.Map;

@RestController
@RequestMapping("/auth/db")
@RequiredArgsConstructor
public class ConnectionPoolController {
    private final ConnectionPool connectionPool;

    @GetMapping("/pool")
    public Mono<Map<String, Object>> getPoolStatus() {
        PoolMetrics metrics = connectionPool.getMetrics()
                .orElseThrow(() -> new IllegalStateException("ConnectionPoolMetrics is not available"));

        return Mono.just(Map.of(
                "MaxAllocatedSize", metrics.getMaxAllocatedSize(),
                "idleSize", metrics.idleSize(),
                "pendingAcquireSize", metrics.pendingAcquireSize(),
                "acquiredSize", metrics.acquiredSize(),
                "allocatedSize", metrics.allocatedSize()
        ));
    }

}

