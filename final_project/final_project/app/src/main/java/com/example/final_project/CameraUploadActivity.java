package com.example.final_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.final_project.model.UploadResponse;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

public class CameraUploadActivity extends AppCompatActivity {
    private SimpleDraweeView coverSd;
    private Vapi api;
    private Uri coverImgUri;
    private final static int PERMISSION_REQUEST_CODE = 1001;
    private final static int REQUEST_CODE_RECORD = 1002;
    private static final String IMAGE_TYPE = "image/*";
    private static final int REQUEST_CODE_IMAGE = 102;
    private String mp4Path = "";
    private String picpath;
    private Uri videoUri;
    private static final String TAG = "final_project";
    private VideoView mVideoView;
    private MediaMetadataRetriever mmr;
    private Bitmap bitmap;
    private static final long MAX_IMAGE_FILE_SIZE =20 * 1024 * 1024;
    public static void startUI(Context context) {
        Intent intent = new Intent(context, CameraUploadActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_upload);
        coverSd = findViewById(R.id.cd_cover);
        initNetwork();
        mVideoView = findViewById(R.id.cvv_detail);
        findViewById(R.id.btn_cameravideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record();
            }
        });
        findViewById(R.id.btn_cameracover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFile(REQUEST_CODE_IMAGE,IMAGE_TYPE,"选择图片");
            }
        });
        findViewById(R.id.btn_cameracutcover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cutcover();
            }
        });
        findViewById(R.id.btn_cameraup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        findViewById(R.id.btn_uploadauto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record();
                getFirstImg();
                submit();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            String imgpath = data.getStringExtra("cover");
            coverImgUri = PathUtils.getUriForFile(this,imgpath);
            coverSd.setImageURI(coverImgUri);
        }
        else if (REQUEST_CODE_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImgUri = data.getData();
                coverSd.setImageURI(coverImgUri);
                if (coverImgUri != null) {
                    Log.d(TAG, "pick cover image " + coverImgUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }else if(requestCode == REQUEST_CODE_RECORD && resultCode == RESULT_OK){
            play();
        }
    }
    private void initNetwork() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        api = retrofit.create(Vapi.class);
    }
    private void getFirstImg(){
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(mp4Path);
        bitmap = mmr.getFrameAtTime(0,MediaMetadataRetriever.OPTION_CLOSEST);
        coverSd.setImageBitmap(bitmap);
        picpath = bitmapToFile(bitmap);
        coverImgUri = PathUtils.getUriForFile(this,picpath);
        coverSd.setImageURI(coverImgUri);
    }
    public String bitmapToFile(Bitmap signatureBitmap) {
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".jpg");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(mediaFile));
            signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        pics_files.add(loadPic_file);
        return mediaFile.getAbsolutePath();
    }
    private void getImageFile(int requestCode, String type,String title){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
        intent.putExtra(Intent.EXTRA_TITLE,title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,requestCode);
    }
    private void cutcover(){
        Intent intent = new Intent(CameraUploadActivity.this,CutActivity.class);
        intent.putExtra("path",mp4Path);
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
                            Toast.makeText(CameraUploadActivity.this,"收到回应失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        final UploadResponse upResponse = response.body();
                        if(upResponse == null){
                            Toast.makeText(CameraUploadActivity.this,"收到回应为空",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(upResponse.success){
                            Log.d(TAG, "Success");
                            Toast.makeText(CameraUploadActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CameraUploadActivity.this.finish();
                                }
                            });
                        }else{
                            Log.d("UploadResponse Error",upResponse.error);
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadResponse> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(CameraUploadActivity.this,"提交失败"+t.toString(),Toast.LENGTH_SHORT).show();

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
    public void record() {
        requestPermission();
    }

    private void requestPermission() {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if (hasCameraPermission && hasAudioPermission) {
            recordVideo();
        } else {
            List<String> permission = new ArrayList<String>();
            if (!hasCameraPermission) {
                permission.add(Manifest.permission.CAMERA);
            }
            if (!hasAudioPermission) {
                permission.add(Manifest.permission.RECORD_AUDIO);
            }
            ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), PERMISSION_REQUEST_CODE);
        }

    }

    private void recordVideo() {
        // todo 2.1 唤起视频录制 intent 并设置视频地址
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mp4Path = getOutputMediaPath();
        videoUri = PathUtils.getUriForFile(this,mp4Path);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,PathUtils.getUriForFile(this,mp4Path));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,5);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,REQUEST_CODE_RECORD);
        }
    }

    private String getOutputMediaPath() {
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".mp4");
        if (!mediaFile.exists()) {
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermission = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
                break;
            }
        }
        if (hasPermission) {
            recordVideo();
        } else {
            Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void play(){
        mVideoView.setVideoPath(mp4Path);
        mVideoView.start();
    }
}