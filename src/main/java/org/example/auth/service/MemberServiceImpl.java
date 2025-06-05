package org.example.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.auth.dao.MemberDao;
import org.example.auth.dto.CheckUserDto;
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
    private final MemberDao memberDao;

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

    @Override
    public Mono<Member> findUserProjectionByUserId(String userId) {
        return memberDao.findUserProjectionByUserId(userId);
    }

}
