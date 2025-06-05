package org.example.github.controller;


import org.example.github.dto.Repository;
import org.example.github.service.GitHubAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
public class GitHubController {

    private final GitHubAccess gitHubAccess;

    public GitHubController(@Autowired GitHubAccess gitHubAccess) {
        this.gitHubAccess = gitHubAccess;
    }

    @GetMapping("github/orga/{organization}/repos")
   public Mono<List<Repository>> repositories(@PathVariable("organization") String organization) {
       return gitHubAccess.getReposFor(organization).collectList().log();
   }

}
