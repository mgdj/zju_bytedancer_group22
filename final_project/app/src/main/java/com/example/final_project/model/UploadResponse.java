package com.example.final_project.model;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    @SerializedName("result")
    public vedioData vediodata;
    @SerializedName("success")
    public boolean success;

    @SerializedName("error")
    public String error;
}
