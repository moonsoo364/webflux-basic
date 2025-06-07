package org.example.github;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class GitHubAccess {

    private final WebClient webClient;

    public GitHubAccess() {
        this.webClient = WebClient.builder().baseUrl("https://api.github.com").build();
    }

    // now a Flux instead of Mono<List>
    public Flux<Repository> getReposFor(String organization) {
        return webClient.get()
                .uri("/orgs/{organization}/repos", organization)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Client Error: " + body)))
                )
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Server Error: " + body)))
                )
                .bodyToFlux(Repository.class)
                .onErrorResume(ex -> {
                    // 필요시 빈 Flux 반환하거나 에러 전파
                    log.error("WebClient Error: {}", ex.getMessage());
                    return Flux.error(ex); // 또는 Flux.empty()
                });
    }




}
