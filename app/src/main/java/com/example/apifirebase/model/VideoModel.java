package com.example.apifirebase.model;

public class VideoModel {
    private String videoId;     // ID riêng biệt của video
    private String title;       // Tiêu đề/Caption video
    private String videoUrl;    // Đường dẫn video trên Firebase Storage
    private String uploaderId;  // ID của người đăng (để biết ai đăng)
    private long timestamp;     // Thời gian đăng (để sắp xếp mới nhất)

    public VideoModel(){}
    public VideoModel(String videoId, String title, String videoUrl, String uploaderId, long timestamp) {
        this.videoId = videoId;
        this.title = title;
        this.videoUrl = videoUrl;
        this.uploaderId = uploaderId;
        this.timestamp = timestamp;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
