package com.example.final_project;

import com.example.final_project.model.UploadResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Vapi {
    @Multipart
    @POST("video")
    Call<UploadResponse> submitVideo(
            @Query("student_id") String student_id,
            @Query("user_name") String user_name,
            @Query("extra_value") String extra_value,
            @Part MultipartBody.Part cover_image,
            @Part MultipartBody.Part video,
            @Header("token") String token
    );
}
