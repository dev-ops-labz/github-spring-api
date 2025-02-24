package org.git.api.controller;


import org.git.api.model.request.RepositoryRequest;
import org.git.api.model.response.BranchListResponse;
import org.git.api.model.response.CommitsResponse;
import org.git.api.model.response.GithubApiResponse;
import org.git.api.service.GithubApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GithubApiController {

    @Autowired
    private GithubApiService service;


    @RequestMapping(method = RequestMethod.GET,path = "/user/{username}/repos")
    public List<GithubApiResponse> getRepo(@PathVariable("username") String user) {
        return service.getUserRepos(user);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{user}/{repo}/commits")
    public List<CommitsResponse> getCommits(@PathVariable("user") String user, @PathVariable("repo") String repo) {
        return service.getCommits(user, repo);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/repos/{user}/{repo}/branches")
    public List<BranchListResponse> getBranches(@PathVariable("user") String user, @PathVariable("repo") String repo) {
        return service.getBranches(user, repo);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/org/{org}/repos")
    public List<GithubApiResponse> getBranches(@PathVariable("org") String org) {
        return service.getOrganizationRepos(org);
    }
}
