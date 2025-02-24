package org.git.api.service;



import org.apache.commons.lang3.StringUtils;
import org.git.api.model.request.RepositoryRequest;
import org.git.api.model.response.BranchListResponse;
import org.git.api.model.response.CommitsResponse;
import org.git.api.model.response.GithubApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.git.api.utils.WebClientUtils.getWebClient;


@Service
public class GithubApiService {

    @Autowired
    private GitTokenService tokenService;

    public List<GithubApiResponse> getUserRepos(String user) {
        String accessToken = getAccessToken();
        return getWebClient("https://api.github.com")
                .get()
                .uri(uriBuilder -> uriBuilder.path("/users/{username}/repos")
                        .build(user))
                .header("Authorization", "Bearer "+accessToken)
                .retrieve()
                .bodyToFlux(GithubApiResponse.class)
                .collectList()
                .block();
    }

    private String getAccessToken() {
        String accessToken = tokenService.getGithubAccessToken();
        if(StringUtils.isNotBlank(accessToken)) {
            return accessToken;
        }
        return accessToken;
    }

    public List<CommitsResponse> getCommits(String user, String repo ) {
        String accessToken = getAccessToken();
        return getWebClient("https://api.github.com")
                .get()
                .uri(uriBuilder -> uriBuilder.path("/repos/{user}/{repo}/commits")
                        .build(user, repo))
                .header("Authorization", "Bearer "+accessToken)
                .retrieve()
                .bodyToFlux(CommitsResponse.class)
                .collectList()
                .block();
    }

    public List<BranchListResponse> getBranches(String user, String repo ) {
        String accessToken = getAccessToken();
        return getWebClient("https://api.github.com")
                .get()
                .uri(uriBuilder -> uriBuilder.path("/repos/{user}/{repo}/branches")
                        .build(user, repo))
                .header("Authorization", "Bearer "+accessToken)
                .retrieve()
                .bodyToFlux(BranchListResponse.class)
                .collectList()
                .block();
    }
}
