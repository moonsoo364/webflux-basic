package org.example.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.AuthRequest;
import org.example.auth.dto.AuthResponse;
import org.example.auth.dto.CheckUserDto;
import org.example.auth.dto.MemberDto;
import org.example.auth.model.Member;
import org.example.auth.service.AuthService;
import org.example.auth.service.MemberService;
import org.example.common.consts.ResultCode;
import org.example.common.dto.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@Valid @RequestBody AuthRequest authRequest){
        return authService.authenticate(authRequest)
                .map(ResponseEntity::ok)
                .onErrorReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<ApiResponseDto>> register(
            @Valid @RequestBody MemberDto dto
            ){
        return memberService.registerMember(dto)
                .map(member -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(
                                new ApiResponseDto(
                                        ResultCode.SUCCESS,"User registered successfully!")
                        ))
                .onErrorResume(IllegalArgumentException.class, e-> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(
                                new ApiResponseDto(ResultCode.BAD_REQUEST,"check your member request")
                        ))
                );
    }
//    @PostMapping("/register")
//    public Mono<ApiResponseDto> register(@Valid @RequestBody MemberDto dto) {
//        return memberService.registerMember(dto)
//                .map(member -> new ApiResponseDto(ResultCode.SUCCESS, "User registered successfully!"))
//                .onErrorResume(IllegalArgumentException.class, e ->
//                        Mono.just(new ApiResponseDto(ResultCode.BAD_REQUEST, "check your member request")));
//    }
    @GetMapping("/check/user")
    public Mono<CheckUserDto> checkUserExists(@RequestParam(required = true) String userId) throws InterruptedException {
        Mono<Member> memberM =  memberService.findUserByUserId(userId);
        return memberM.map(CheckUserDto::new);
    }
    @GetMapping("/check/user/id")
    public Mono<CheckUserDto> checkUserId(@RequestParam(required = true) String userId){
        return memberService.findUserProjectionByUserId(userId).flatMap(
                member -> {
            return Mono.just(new CheckUserDto(member));
        });
    }
    @GetMapping("/check/user/cache")
    public Mono<CheckUserDto> checkUserIdByCache(@RequestParam(required = true) String userId){
        return memberService.findUserByUserIdUseCache(userId)
                .map(CheckUserDto::new)
                .switchIfEmpty(Mono.just(new CheckUserDto()));
    }
    @GetMapping("/flux/range")
    public void parallel(){
//        trash 쓰레드 무한 생성됨
//        Scheduler customScheduler = Schedulers.newParallel("custom-parallel", 4); // 스레드 4개 사용
//        Flux.range(1, 10)
//                .parallel()
//                .runOn(customScheduler) // 여기에서 customScheduler 사용
//                .doOnNext(i -> {
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    log.info("Current Index : {}", i);
//                })
//                .sequential()
//                .subscribe();
//        // thread 수동 제거
//        customScheduler.dispose();

//        Flux.range(1, 10)
//                .parallel()
//                .runOn(SchedulerProvider.CUSTOM_PARALLEL) // 재사용되는 스케줄러
//                .doOnNext(i -> log.info("Current Index : {}", i))
//                .sequential()
//                .subscribe();

    }

}
