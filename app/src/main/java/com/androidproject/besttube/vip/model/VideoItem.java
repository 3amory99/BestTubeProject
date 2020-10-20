package com.androidproject.besttube.vip.model;

import java.util.Date;

public class VideoItem {

    public String videoUrl, thumbnail, videoTitle, categories, search, videoOwner, videoId;
    public Date videoDate;

    public VideoItem() {
    }

    public VideoItem(String videoUrl, String thumbnail, String videoTitle, String categories, String search, Date videoDate, String videoOwner, String videoId) {
        this.videoUrl = videoUrl;
        this.thumbnail = thumbnail;
        this.videoTitle = videoTitle;
        this.categories = categories;
        this.search = search;
        this.videoDate = videoDate;
        this.videoOwner = videoOwner;
        this.videoId = videoId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoOwner() {
        return videoOwner;
    }

    public void setVideoOwner(String videoOwner) {
        this.videoOwner = videoOwner;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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


    public Date getVideoDate() {
        return videoDate;
    }

    public void setVideoDate(Date videoDate) {
        this.videoDate = videoDate;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }
}

