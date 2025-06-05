package org.example.config.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dao.MemberDao;
import org.example.auth.jwt.JwtUtil;
import org.example.auth.model.Member;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final MemberDao memberDao;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> {
                    log.info("## SecurityContext exists : Member = {}", (Member)context.getAuthentication().getPrincipal());
                    return chain.filter(exchange);
                })
                .switchIfEmpty(Mono.defer(()-> {
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        String username = jwtUtil.getUsernameFromToken(token);

                        return memberDao.findUserProjectionByUserId(username)
                                .filter(user -> jwtUtil.validateToken(token, username))
                                .flatMap(user -> {
                                    Authentication auth = new UsernamePasswordAuthenticationToken(
                                            user, null, user.getAuthorities());
                                    SecurityContext context = new SecurityContextImpl(auth);
                                    return chain.filter(exchange)
                                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                                });

                    }
                    return chain.filter(exchange);
                }));
        }
}
