package org.example.config.r2dbc;

import io.r2dbc.pool.ConnectionPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class R2dbcPoolWarmup {

    final private ConnectionPool connectionPool;

    @EventListener(ApplicationReadyEvent.class)
    public void warmupPool(){

        connectionPool.warmup()
                .doOnSuccess(pools-> log.info("R2DBC connetion pool warmed up! initial-pool-size: {}", pools))
                .doOnError(error -> log.error("Failed to warmup connection pool: {}", error.getMessage()))
                .subscribe();
    }

}
