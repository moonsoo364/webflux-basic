package org.example.config.auth;

import lombok.RequiredArgsConstructor;
import org.example.auth.jwt.JwtUtil;
import org.example.auth.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.reactive.config.EnableWebFlux;

@Configuration
@EnableWebFlux
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final MemberRepository repository;
    private final JwtUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService() {
        return userId -> repository.findByUserId(userId)
                .cast(UserDetails.class);
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

    @Bean
    public ServerSecurityContextRepository securityContextRepository() {
        return new JwtServerSecurityContextRepository(jwtUtil, reactiveUserDetailsService());
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http){
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(authenticationManager(reactiveUserDetailsService(),passwordEncoder()))
                .authorizeExchange(exchanges-> exchanges
                        .pathMatchers("/auth/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
    @Bean
    public AuthenticationWebFilter jwtAuthenticationWebFilter(){
        AuthenticationWebFilter webFilter = new AuthenticationWebFilter(authenticationManager(reactiveUserDetailsService(),passwordEncoder()));
        webFilter.setServerAuthenticationConverter(new JwtServerAuthenticationConverter(jwtUtil));
        // 서버 세션 미사용으로 주석
        //webFilter.setSecurityContextRepository(new WebSessionServerSecurityContextRepository());
        return webFilter;
    }

}
