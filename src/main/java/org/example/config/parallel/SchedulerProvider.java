//package org.example.config.parallel;
//
//import org.springframework.stereotype.Component;
//import reactor.core.scheduler.Scheduler;
//import reactor.core.scheduler.Schedulers;
//
//@Component
////미리 쓰레드 풀을 생성해서 특정 작업에서 해당 쓰레드를 명시적으로 호출
//public class SchedulerProvider {
//    public static final Scheduler CUSTOM_PARALLEL = Schedulers.newParallel("custom-parallel", 4);
//}
