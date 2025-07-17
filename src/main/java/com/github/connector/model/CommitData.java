package com.github.connector.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitData {


    private CommitDetails commit;

    public CommitDetails getCommit() {
        return commit;
    }
    public void setCommit(CommitDetails commit) {
        this.commit = commit;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommitDetails {
        private Author author;
        private String message;

        public Author getAuthor() {
            return author;
        }
        public void setAuthor(Author author) {
            this.author = author;
        }

        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Author {
            private String name;
            private Instant date;

            public String getName() {
                return name;
            }
            public void setName(String name) {
                this.name = name;
            }

            public Instant getDate() {
                return date;
            }
            public void setDate(Instant date) {
                this.date = date;
            }
        }
    }
}
