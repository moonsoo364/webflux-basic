package org.example.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.auth.dto.MemberDto;
import org.example.auth.model.Member;
import org.example.auth.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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

    public Mono<Member> findByUserId(String userId){
        return memberRepository.findByUserId(userId);
    }
}
