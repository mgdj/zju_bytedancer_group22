package com.example.final_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.final_project.model.UploadResponse;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.final_project.Constants.Base_URL;
import static com.example.final_project.Constants.STUDENT_ID;
import static com.example.final_project.Constants.USER_NAME;
import static com.example.final_project.Constants.token;

public class LocalUploadActivity extends AppCompatActivity {
    private Vapi api;
    private Uri coverImgUri;
    private SimpleDraweeView coverSd;
    private Uri videoUri;
    private VideoView lvv;
    private String path;
    private static final String TAG = "final_project";
    private static final long MAX_IMAGE_FILE_SIZE = 10 * 1024 * 1024;
    private static final int REQUEST_CODE_VIDEO = 1002;
    private static final int REQUEST_CODE_IMAGE = 102;
    private static final String VIDEO_TYPE = "video/*";
    private static final String IMAGE_TYPE = "image/*";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNetwork();
        Fresco.initialize(this);
        setContentView(R.layout.activity_local_upload);
        coverSd = findViewById(R.id.sd_cover);
        lvv = findViewById(R.id.lvv_detail);
        findViewById(R.id.btn_localvideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVideoFile(REQUEST_CODE_VIDEO,VIDEO_TYPE,"选择视频");
            }
        });
        findViewById(R.id.btn_localcover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFile(REQUEST_CODE_IMAGE,IMAGE_TYPE,"选择图片");
            }
        });
        findViewById(R.id.btn_localup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        findViewById(R.id.btn_cutcover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cutCover();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2){
            String imgpath = data.getStringExtra("cover");
            coverImgUri = PathUtils.getUriForFile(this,imgpath);
            coverSd.setImageURI(coverImgUri);
            lvv.start();
        }
        else if (REQUEST_CODE_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImgUri = data.getData();
                coverSd.setImageURI(coverImgUri);
                lvv.start();
                if (coverImgUri != null) {
                    Log.d(TAG, "pick cover image " + coverImgUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }else if(REQUEST_CODE_VIDEO == requestCode){
            if(resultCode == Activity.RESULT_OK){
                videoUri = data.getData();
                //path = getRealPath(videoUri);
                lvv.setVideoURI(videoUri);
                lvv.start();
                if(videoUri != null){
                    Log.d(TAG, "pick video" + videoUri.toString());
                }else{
                    Log.d(TAG, "uri2File fail" + data.getData());
                }
            }else{
                Log.d(TAG, "file pick fail");
            }
        }
    }
    private String getRealPath( Uri fileUrl ) {
        String fileName = null;
        if( fileUrl != null ) {
            if( fileUrl.getScheme( ).toString( ).compareTo( "content" ) == 0 ) // content://开头的uri
            {
                Cursor cursor = this.getContentResolver( ).query( fileUrl, null, null, null, null );
                if( cursor != null && cursor.moveToFirst( ) ) {
                    try {
                        int column_index = cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATA );
                        fileName = cursor.getString( column_index ); // 取出文件路径
                    } catch( IllegalArgumentException e ) {
                        e.printStackTrace();
                    }finally{
                        cursor.close( );
                    }
                }
            } else if( fileUrl.getScheme( ).compareTo( "file" ) == 0 ) // file:///开头的uri
            {
                fileName = fileUrl.getPath( );
            }
        }
        return fileName;
    }
    private void initNetwork() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        api = retrofit.create(Vapi.class);
    }
    private void getVideoFile(int requestCode, String type,String title){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
        intent.putExtra(Intent.EXTRA_TITLE,title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,requestCode);
    }
    private void getImageFile(int requestCode, String type,String title){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
        intent.putExtra(Intent.EXTRA_TITLE,title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,requestCode);
    }
    private void cutCover(){
        Intent intent = new Intent(LocalUploadActivity.this,LocalCutActivity.class);
        intent.putExtra("uristr",videoUri.toString());
        startActivityForResult(intent,2);
    }
    private void submit(){
        byte[] videoData = readDataFromUri(videoUri);
        if(videoData == null || videoData.length == 0){
            Toast.makeText(this,"视频不存在",Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] imageData = readDataFromUri(coverImgUri);
        if(imageData == null || imageData.length == 0){
            Toast.makeText(this,"图片不存在",Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageData.length >= MAX_IMAGE_FILE_SIZE) {
            Toast.makeText(this, "图片文件过大", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "hi");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<UploadResponse> uploadResponse = api.submitVideo(STUDENT_ID,USER_NAME,"",
                        MultipartBody.Part.createFormData("video","vlog.mp4", RequestBody.create(MediaType.parse("Multipart/form_data"),videoData)),
                        MultipartBody.Part.createFormData("cover_image","cover.png",RequestBody.create(MediaType.parse("Multipart/form_data"),imageData)),
                        token);
                uploadResponse.enqueue(new Callback<UploadResponse>() {
                    @Override
                    public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                        if(!response.isSuccessful()){
                            Toast.makeText(LocalUploadActivity.this,"收到回应失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        final UploadResponse upResponse = response.body();
                        if(upResponse == null){
                            Toast.makeText(LocalUploadActivity.this,"收到回应为空",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(upResponse.success){
                            Log.d(TAG, "Success");
                            Toast.makeText(LocalUploadActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LocalUploadActivity.this.finish();
                                }
                            });
                        }else{
                            Log.d("UploadResponse Error",upResponse.error);
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadResponse> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(LocalUploadActivity.this,"提交失败"+t.toString(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).start();
    }
    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}