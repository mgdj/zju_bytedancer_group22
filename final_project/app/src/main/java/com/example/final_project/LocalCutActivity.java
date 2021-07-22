package com.example.final_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.cardemulation.CardEmulation;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.transformation.BitmapTransformation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocalCutActivity extends AppCompatActivity {
    private Uri videouri;
    private String path;
    private Uri imageuri;
    private VideoView video;
    private ImageView image;
    private SeekBar seekBar;
    private int totaltime;
    private int currenttime;
    private int REQUEST_CODE_RECORD=1002;
    private static final String TAG = "final_project";
    private MediaMetadataRetriever mmr;
    private Bitmap bitmap;
    private  boolean istouch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_cut);
        video = findViewById(R.id.vv_localplayer);
        image = findViewById(R.id.iv_localhead);
        videouri = Uri.parse(getIntent().getStringExtra("uristr"));
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(this,videouri);
        video.setVideoURI(videouri);
        Fresco.initialize(this);
        seekBar = findViewById(R.id.sb_local_select);
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                totaltime = video.getDuration();//毫秒
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(istouch){
                    currenttime = (int)(((float)progress / 100f) * totaltime);
                    video.seekTo(currenttime);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                istouch = true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                istouch = false;
                bitmap = mmr.getFrameAtTime((long) (currenttime * 1000), MediaMetadataRetriever.OPTION_CLOSEST);
                image.setImageBitmap(bitmap);
            }
        });
        findViewById(R.id.btn_local_cut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollBack();
            }
        });
    }
    private void rollBack(){
        String Targetpath = bitmapToFile(bitmap);
        image.setImageBitmap(bitmap);
        Intent intent = new Intent(LocalCutActivity.this, LocalUploadActivity.class);
        BitmapUtils.saveBitmap("cover",bitmap,this);
        intent.putExtra("cover",Targetpath);
        setResult(RESULT_OK,intent);
        finish();
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
}