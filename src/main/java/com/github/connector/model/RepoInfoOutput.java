package com.github.connector.model;


import java.util.List;

public class RepoInfoOutput {

    private int totalPages;
    private int currentPage;
    private String repoName;
    private List<CommitInfoOutput> commits;


    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public List<CommitInfoOutput> getCommits() {
        return commits;
    }

    public void setCommits(List<CommitInfoOutput> commits) {
        this.commits = commits;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public RepoInfoOutput(String repoName, List<CommitInfoOutput> commits, int totalPages, int currentPage) {
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.repoName = repoName;
        this.commits = commits;

    }
}
