package com.github.connector.service;

import com.github.connector.exception.GitHubApiException;
import com.github.connector.exception.RateLimitExceededException;
import com.github.connector.exception.UserNotFoundException;
import com.github.connector.model.CommitData;
import com.github.connector.model.CommitInfoOutput;
import com.github.connector.model.Repo;
import com.github.connector.model.RepoInfoOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class GitDataService {

    private static final Logger log = LoggerFactory.getLogger(GitDataService.class);
    private final RestTemplate restTemplate;
    private final Executor executor;
    private static final String BASE_URL = "https://api.github.com";
    private int totalPages = 1;
    private int currentPage = 1;
    SimpleDateFormat localTime = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
    Date date;

    public GitDataService(RestTemplate rt, @Qualifier("taskExecutor") Executor ex) {
        this.restTemplate = rt;
        this.executor = ex;
        localTime.setTimeZone(TimeZone.getDefault());
    }

    public List<RepoInfoOutput> fetchAll(String username, int pageNum) {
        log.info("request with username -{} and pageNum - {} received for processing", username,pageNum);
        List<Repo> allRepos = new ArrayList<>();
        currentPage = pageNum;

            String url = BASE_URL + "/users/" + username + "/repos?per_page=20&page=" + pageNum;
            try {

                ResponseEntity<Repo[]> resp = restTemplate.getForEntity(url, Repo[].class);
                HttpHeaders headers = resp.getHeaders();
                totalPages = getLastPageFromLinkHeader(headers);
                if(pageNum > totalPages){
                    throw new GitHubApiException("Requested page number doesn't exist");
                }
                log.info("Total number of pages for username {} are {}", username,totalPages);
                List<Repo> pageRepos = List.of(Optional.ofNullable(resp.getBody()).orElse(new Repo[0]));
                allRepos.addAll(pageRepos);


            } catch (HttpClientErrorException.NotFound e) {
                log.error("Username {} not found",username);
                throw new UserNotFoundException("User not found: " + username);
            } catch (HttpClientErrorException.Forbidden e) {
                log.error("Rate limit exceeded");
                throw new RateLimitExceededException("Rate limit exceeded", 60);
            }


        List<CompletableFuture<RepoInfoOutput>> futures = allRepos.stream()
                .map(repo -> CompletableFuture.supplyAsync(() -> fetchCommits(username, repo.getName()), executor)).toList();

        return futures.stream().map(CompletableFuture::join).toList();
    }

    private int getLastPageFromLinkHeader(HttpHeaders headers) {
        String linkHeader = headers.getFirst("Link");
        log.info("Header Link - {}", linkHeader);
        if (linkHeader == null) {
            return 1;
        }

        String[] links = linkHeader.split(",");
        for (String link : links) {
            String[] parts = link.split(";");
            if (parts.length < 2) continue;
            String url = parts[0].trim();
            String rel = parts[1].trim();
            if ("rel=\"last\"".equals(rel)) {
                int pageIndex = url.indexOf("&page=");
                if (pageIndex != -1) {
                    String pageParam = url.substring(pageIndex + 5).replaceAll("[^0-9]", "");
                    return Integer.parseInt(pageParam);
                }
            }
        }

        return 1;
    }

    public RepoInfoOutput fetchCommits(String user, String repo) {
        List<CommitInfoOutput> commits = new ArrayList<>();


            String url = BASE_URL + "/repos/" + user + "/" + repo + "/commits?per_page=20";
            try {
                ResponseEntity<CommitData[]> resp = restTemplate.getForEntity(url, CommitData[].class);

                List<CommitData> pageCommits = List.of(Optional.ofNullable(resp.getBody()).orElse(new CommitData[0]));


                pageCommits.stream()
                        .limit(20 )
                        .map(c -> {
                                date = Date.from(c.getCommit().getAuthor().getDate());
                                String dateString = localTime.format(date);
                                return new CommitInfoOutput(
                                c.getCommit().getMessage(),
                                c.getCommit().getAuthor().getName(),
                                dateString);
                        })
                        .forEach(commits::add);

            } catch (HttpClientErrorException.Forbidden e) {
                log.error("Rate limit exceeded while fetching commits for user - {} and repo - {}",user,repo);
                throw new RateLimitExceededException("Rate limit exceeded", 60);
            }catch (Exception e){
                log.error("Error occurred while fetching commits for user - {} and repo - {}",user,repo);
            }


        return new RepoInfoOutput(repo, commits,totalPages,currentPage);
    }
}
