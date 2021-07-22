package com.example.final_project.model;

import com.google.gson.annotations.SerializedName;

public class vedioData {
    @SerializedName("user_name")
    private String name;
    @SerializedName("_author")
    private String author;
    @SerializedName("image_url")
    private String coverimgURL;
    @SerializedName("video_url")
    private String videoURL;
    @SerializedName("student_id")
    private String stuid;
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setAuthor(String author){
        this.author = author;
    }
    public String getAuthor(){
        return author;
    }
    public void setCoverimgURL(String coverimgURL){
        this.coverimgURL = coverimgURL;
    }
    public String getCoverimgURL(){
        return coverimgURL;
    }
    public void setVideoURL(String videourl){ this.videoURL = videourl; }
    public String getVideoURL(){
        return videoURL;
    }
    public void setStuid(String studid){ this.stuid = studid; }
    public String getStuid(){
        return stuid;
    }
}
