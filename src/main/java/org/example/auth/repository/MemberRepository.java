package org.example.auth.repository;

import org.example.auth.model.Member;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MemberRepository extends ReactiveCrudRepository<Member,String> {
    Mono<Member> findByUserId(String userId);
}
