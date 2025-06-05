//package org.example.config.netty;
//
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Hooks;
//import reactor.core.scheduler.Scheduler;
//import reactor.core.scheduler.Schedulers;
//
//@Slf4j
//@Configuration
//public class ReactorDebugConfig {
//
//    @PostConstruct
//    public void setupSchedulerLogging() {
//        Scheduler customParallelScheduler = Schedulers.newParallel("my-custom-parallel", 8);
//
//        Flux.range(1, 10)
//                .map(i -> {
//                    log.info("Map operation on thread: " + Thread.currentThread().getName());
//                    return i * 2;
//                })
//                .publishOn(customParallelScheduler) // 이 지점부터 아래의 연산은 customParallelScheduler 쓰레드 풀에서 실행
//                .map(i -> {
//                    log.info("Further map operation on thread: " + Thread.currentThread().getName());
//                    return i + 1;
//                })
//                .blockLast(); // 예제를 위해 블로킹
//    }
//}
