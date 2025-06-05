package org.example.github.service;


import org.example.github.dto.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
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
                .bodyToFlux(Repository.class);
    }

}
