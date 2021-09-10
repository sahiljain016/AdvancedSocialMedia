package com.gic.memorableplaces.DataModels;

import java.io.Serializable;

public class Photo implements Serializable {
    private String caption;
    private String font;
    private String font_color;
    private String ScaleType;
    private String imageUrl;
    private String date_added;
    private String photo_id;
    private String user_id;
    private String tags;

    public Photo() {

    }

    public Photo(String caption, String font, String font_color, String scaleType, String imageUrl,
                 String date_added, String photo_id, String user_id, String tags) {
        this.caption = caption;
        this.font = font;
        this.font_color = font_color;
        ScaleType = scaleType;
        this.imageUrl = imageUrl;
        this.date_added = date_added;
        this.photo_id = photo_id;
        this.user_id = user_id;
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

    public String getFont_color() {
        return font_color;
    }

    public void setFont_color(String font_color) {
        this.font_color = font_color;
    }

    public String getScaleType() {
        return ScaleType;
    }

    public void setScaleType(String scaleType) {
        ScaleType = scaleType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "caption='" + caption + '\'' +
                ", font='" + font + '\'' +
                ", font_color='" + font_color + '\'' +
                ", ScaleType='" + ScaleType + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", date_added='" + date_added + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
