package org.example.auth.service;

import org.example.auth.dto.AuthRequest;
import org.example.auth.dto.AuthResponse;
import reactor.core.publisher.Mono;

public interface AuthService {

    public Mono<AuthResponse> authenticate(AuthRequest authRequest);
}
