package org.example.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.auth.dto.AuthRequest;
import org.example.auth.dto.AuthResponse;
import org.example.auth.jwt.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<AuthResponse> authenticate(AuthRequest authRequest) {
        return memberService.findByUserId(authRequest.getUserId())
                .filter(member -> passwordEncoder.matches(
                        authRequest.getPassword(),
                        member.getPassword()))
                .map(member -> new AuthResponse
                        (
                            jwtUtil.generateToken(member.getUserId(),
                            member.getUserRole()),member.getUserId()
                        ))
                .switchIfEmpty(Mono.error(new BadCredentialsException("InValid credentials")));
    }
}
