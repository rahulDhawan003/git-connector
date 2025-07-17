package com.github.connector.exception;

public class GitHubApiException extends RuntimeException{

    public GitHubApiException(String msg) {
        super(msg);
    }
}
