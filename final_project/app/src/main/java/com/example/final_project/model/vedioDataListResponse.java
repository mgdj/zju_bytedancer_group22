package com.example.final_project.model;

import com.example.final_project.model.vedioData;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class vedioDataListResponse {
    @SerializedName("feeds")
    public List<vedioData> feeds;
    @SerializedName("success")
    public boolean success;
}
