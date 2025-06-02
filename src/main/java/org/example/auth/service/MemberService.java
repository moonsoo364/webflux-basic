package org.example.auth.service;

import org.example.auth.dto.MemberDto;
import org.example.auth.model.Member;
import reactor.core.publisher.Mono;

public interface MemberService {
    public Mono<Member> registerMember(MemberDto dto);

    public Mono<Member> findByUserId(String userId);
}
