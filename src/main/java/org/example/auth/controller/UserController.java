package org.example.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('USER_ROLE', 'ADMIN_ROLE')")
    public Mono<ResponseEntity<String>> getCurrentUser(Mono<Principal> principal) {
        return principal
                .map(Principal::getName)
                .map(name -> ResponseEntity.ok("Hello, " + name + "! You are authenticated."))
                .defaultIfEmpty(ResponseEntity.status(401).body("Unauthorized"));
    }

    @GetMapping("/admin-only")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public Mono<ResponseEntity<String>> adminOnly() {
        return Mono.just(ResponseEntity.ok("This is an admin-only endpoint."));
    }
}
