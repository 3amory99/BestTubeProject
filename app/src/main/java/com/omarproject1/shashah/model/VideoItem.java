package com.omarproject1.shashah.model;

public class VideoItem {

    public String videoUrl, videoTitle, videoDescription, categories, hashTags, search;

    public VideoItem() {
    }

    public VideoItem(String videoUrl, String videoTitle, String videoDescription, String categories, String hashTags, String search) {
        this.videoUrl = videoUrl;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.categories = categories;
        this.hashTags = hashTags;
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getHashTags() {
        return hashTags;
    }

    public void setHashTags(String hashTags) {
        this.hashTags = hashTags;
    }
}

