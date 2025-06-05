package org.example.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.auth.dto.AuthRequest;
import org.example.auth.dto.AuthResponse;
import org.example.auth.jwt.JwtUtil;
import org.example.auth.repository.MemberRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<AuthResponse> authenticate(AuthRequest authRequest) {
        return memberRepository.findByUserId(authRequest.userId())
                .filter(member -> passwordEncoder.matches(
                        authRequest.password(),
                        member.getPassword()))
                .map(member -> AuthResponse.builder()
                        .token(jwtUtil.generateToken(member))
                        .memberName(member.getMemberName())
                        .localeCode(member.getLocaleCode())
                        .build()
                        )
                .switchIfEmpty(Mono.error(new BadCredentialsException("InValid credentials")));
    }
}
