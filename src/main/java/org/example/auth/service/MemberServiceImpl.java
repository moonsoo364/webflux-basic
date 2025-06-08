package org.example.auth.service;

import org.example.auth.dao.MemberDao;
import org.example.auth.dto.MemberDto;
import org.example.auth.model.Member;
import org.example.auth.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberDao memberDao;
    private final ReactiveRedisTemplate<String,MemberDto> redisTemplate;
    private final Integer cacheTime;

    public MemberServiceImpl(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            MemberDao memberDao,
            @Qualifier("redisMemberCache") ReactiveRedisTemplate<String, MemberDto> redisTemplate,
            @Value("${spring.data.redis.cache-time}") Integer cacheTime) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.memberDao = memberDao;
        this.redisTemplate = redisTemplate;
        this.cacheTime = cacheTime;
    }

    @Override
    public Mono<Member> registerMember(MemberDto dto){
        return memberRepository.findByUserId(dto.getUserId())
                .flatMap(existUser ->Mono.error(new IllegalArgumentException("UserId already Exists")))
                .switchIfEmpty(Mono.defer(()-> {
                    dto.setPassword(
                            passwordEncoder.encode(dto.getPassword())
                    );
                    return memberRepository.save(new Member(dto));
                })).cast(Member.class);
    }

    @Override
    public Mono<Member> findByUserId(String userId){
        return memberRepository.findByUserId(userId);
    }

    @Override
    public Mono<Boolean> existsById(String userId) {
        return memberRepository.existsById(userId);
    }

    @Override
    public Mono<Member> findUserByUserId(String userId) {
        return memberDao.findUserByUserId(userId);
    }

    //마리아 디비 조회
    @Override
    public Mono<MemberDto> findUserProjectionByUserId(String userId) {
        return memberDao.findUserProjectionByUserId(userId);
    }

    //마리아 디비 1회 조회 후 레디스 캐시
    @Override
    public Mono<MemberDto> findUserByUserIdUseCache(String userId) {
        return redisTemplate.opsForValue().get(userId)
                .switchIfEmpty(
                        memberDao.findUserProjectionByUserId(userId)
                                .flatMap(member -> {
                                            if(member == null) return Mono.empty();// null cache 방지
                                            return redisTemplate.opsForValue()
                                                    .set(userId, member, Duration.ofMinutes(cacheTime))
                                                    .thenReturn(member);
                                        }

                                ).onErrorResume(e ->
                                {
                                    return memberDao.findUserProjectionByUserId(userId);
                                })

                );
    }

}
