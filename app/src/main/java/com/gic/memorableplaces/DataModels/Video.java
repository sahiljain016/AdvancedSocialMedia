package com.gic.memorableplaces.DataModels;

import java.io.Serializable;

public class Video implements Serializable {

    private String caption;
    private String font;
    private String videoUrl;
    private String date_added;
    private String video_id;
    private String user_id;
    private String font_color;
    private String tags;

    public Video() {

    }

    public Video(String caption, String font, String videoUrl, String date_added, String video_id,
                 String user_id, String font_color, String tags) {
        this.caption = caption;
        this.font = font;
        this.videoUrl = videoUrl;
        this.date_added = date_added;
        this.video_id = video_id;
        this.user_id = user_id;
        this.font_color = font_color;
        this.tags = tags;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFont_color() {
        return font_color;
    }

    public void setFont_color(String font_color) {
        this.font_color = font_color;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Video{" +
                "caption='" + caption + '\'' +
                ", font='" + font + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", date_added='" + date_added + '\'' +
                ", video_id='" + video_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", font_color='" + font_color + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
