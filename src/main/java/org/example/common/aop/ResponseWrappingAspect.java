package org.example.common.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.common.dto.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


//@Slf4j
//@Aspect
//@Component
//public class ResponseWrappingAspect {
//
//    @Around("execution(public reactor.core.publisher.Mono *(..)) && @within(org.springframework.web.bind.annotation.RestController)")
//    public Object wrapMono(ProceedingJoinPoint pjp) throws Throwable {
//        Object proceed = pjp.proceed();
//
//        if (proceed instanceof Mono<?> mono) {
//            return mono.map(data -> {
//                if (data instanceof ResponseEntity<?> entity) {
//                    return handleResponseEntity(entity);
//                }
//
//                return CommonResponse.builder()
//                        .resultCode("SUCCESS")
//                        .msg("요청이 성공적으로 처리되었습니다.")
//                        .body(data)
//                        .build();
//            }).onErrorResume(ex -> {
//                log.error("Exception during controller execution", ex);
//                return Mono.just(CommonResponse.builder()
//                        .resultCode("ERROR")
//                        .msg(ex.getMessage())
//                        .body(null)
//                        .build());
//            });
//        }
//
//        return proceed;
//    }
//
//    @Around("execution(public reactor.core.publisher.Flux *(..)) && @within(org.springframework.web.bind.annotation.RestController)")
//    public Object wrapFluxResponse(ProceedingJoinPoint pjp) throws Throwable {
//        Object proceed = pjp.proceed();
//
//        if (proceed instanceof Flux<?> flux) {
//            return flux
//                    .map(data -> (Object) data) // 그냥 패스해도 됨
//                    .onErrorResume(ex -> {
//                        log.error("Exception in controller method: ", ex);
//                        return Flux.error(ex); // 그대로 Flux 에러로 넘김
//                    });
//        }
//
//        return proceed;
//    }
//
//
//    private ResponseEntity<CommonResponse<Object>> handleResponseEntity(ResponseEntity<?> entity){
//            Object realBody = entity.getBody();
//            CommonResponse<Object> common = CommonResponse.builder()
//                    .resultCode("SUCCESS")
//                    .msg("요청이 성공적으로 처리되었습니다.")
//                    .body(realBody)
//                    .build();
//            return ResponseEntity.status(entity.getStatusCode())
//                    .headers(entity.getHeaders())
//                    .body(common);
//
//    }
//}
