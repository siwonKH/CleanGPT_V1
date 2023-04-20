package com.siwonkh.cleangpt_v1.model;

public class CreatorProfile {
    private String title;
    private String description;
    private String url;
    private String channelId;

    public CreatorProfile(String title, String description, String url, String channelId) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
