package com.github.connector.service;


import com.github.connector.exception.RateLimitExceededException;
import com.github.connector.exception.UserNotFoundException;
import com.github.connector.model.CommitData;
import com.github.connector.model.Repo;
import com.github.connector.model.RepoInfoOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GitDataServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Executor executor;

    @InjectMocks
    private GitDataService gitDataService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchAll_UserNotFound() {
        String username = "invaliduser";
        int pageNum = 1;
        when(restTemplate.getForEntity(anyString(), eq(Repo[].class)))
                .thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> gitDataService.fetchAll(username, pageNum));
    }

    @Test
    void testFetchAll_RateLimitExceeded() {
        String username = "rahul";
        int pageNum = 1;
        when(restTemplate.getForEntity(anyString(), eq(Repo[].class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.FORBIDDEN, "Rate limit exceeded", null, null, null));

        assertThrows(RateLimitExceededException.class, () -> gitDataService.fetchAll(username, pageNum));
    }

    @Test
    void testFetchCommits_ValidResponse() {
        String username = "testuser";
        String repoName = "testrepo";

        CommitData.CommitDetails.Author author = new CommitData.CommitDetails.Author();
        author.setName("Author");
        author.setDate(Instant.parse("2023-01-01T00:00:00Z"));

        CommitData.CommitDetails detail = new CommitData.CommitDetails();
        detail.setMessage("Commit message");
        detail.setAuthor(author);

        CommitData commitData = new CommitData();
        commitData.setCommit(detail);

        CommitData[] commits = {commitData};
        ResponseEntity<CommitData[]> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(commits);
        when(restTemplate.getForEntity(anyString(), eq(CommitData[].class))).thenReturn(responseEntity);

        RepoInfoOutput result = gitDataService.fetchCommits(username, repoName);

        assertNotNull(result);
        assertEquals(repoName, result.getRepoName());
        assertEquals(1, result.getCommits().size());
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(CommitData[].class));
    }

    @Test
    void testFetchCommits_RateLimitExceeded() {
        String username = "testuser";
        String repoName = "testrepo";
        when(restTemplate.getForEntity(anyString(), eq(CommitData[].class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.FORBIDDEN, "Rate limit exceeded", null, null, null));

        assertThrows(RateLimitExceededException.class, () -> gitDataService.fetchCommits(username, repoName));
    }
}
