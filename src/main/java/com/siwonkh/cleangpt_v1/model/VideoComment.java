package com.siwonkh.cleangpt_v1.model;

public class VideoComment {
    private String comment;
    private String author;
    private String url;

    public VideoComment(String comment, String author, String url) {
        this.comment = comment;
        this.author = author;
        this.url = url;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
