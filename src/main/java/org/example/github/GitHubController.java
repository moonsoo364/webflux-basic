package org.example.github;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class GitHubController {

    private final GitHubAccess gitHubAccess;

    @GetMapping("/auth/github/org/{organization}/repos")
    public Flux<Repository> repositories(@PathVariable String organization) {
        return gitHubAccess.getReposFor(organization);
    }
}

