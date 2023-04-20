package com.siwonkh.cleangpt_v1.model;

public class CreatorVideo {

    private String videoId;
    private String title;
    private String description;
    private String thumbnail;
    private String publishedAt;

    public CreatorVideo(String videoId, String title, String description, String thumbnail, String publishedAt) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
