package com.github.connector.controller;

import com.github.connector.exception.MandatoryFieldMissingException;
import com.github.connector.model.RepoInfoOutput;
import com.github.connector.service.GitDataService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/git")
public class GitConnectorController {


    private final GitDataService gitDataService;

    public GitConnectorController(GitDataService gitDataService) {
        this.gitDataService = gitDataService;
    }

    @GetMapping("/commits")
    public List<RepoInfoOutput> getUserCommits(@RequestParam(name = "username") String username, @RequestParam(name = "page",defaultValue = "1") int pageNum ) {
        if(username == null || username.isEmpty()) {
            throw new MandatoryFieldMissingException("Missing mandatory field - username");
        }
        return gitDataService.fetchAll(username,pageNum);
    }
}
