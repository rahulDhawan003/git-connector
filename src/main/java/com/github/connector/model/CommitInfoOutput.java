package com.github.connector.model;

import java.time.Instant;

public class CommitInfoOutput {

    private String commitMessage;
    private String authorName;
    private String timestamp;

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public CommitInfoOutput(String commitMessage, String authorName, String timestamp) {
        this.commitMessage = commitMessage;
        this.authorName = authorName;
        this.timestamp = timestamp;
    }
}
