package org.example.auth.service;

import org.example.auth.dto.CheckUserDto;
import org.example.auth.dto.MemberDto;
import org.example.auth.model.Member;
import reactor.core.publisher.Mono;

public interface MemberService {
    Mono<Member> registerMember(MemberDto dto);

    Mono<Member> findByUserId(String userId);

    Mono<Boolean> existsById(String userId);

    Mono<Member> findUserByUserId(String userId);

    Mono<Member> findUserProjectionByUserId(String userId);
}
