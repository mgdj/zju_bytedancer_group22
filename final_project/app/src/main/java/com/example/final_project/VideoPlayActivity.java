package com.example.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.media.MediaPlayer;

import com.airbnb.lottie.LottieAnimationView;
import com.ldoublem.thumbUplib.ThumbUpView;
import com.liji.circleimageview.CircleImageView;


public class VideoPlayActivity extends AppCompatActivity {
    private GestureDetector mGestureDetector;
    private LottieAnimationView lview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplay);
        Intent intent = getIntent();
        String videoURL =intent.getStringExtra("url");
        String aut = intent.getStringExtra("sid");
        String uname = intent.getStringExtra("name");
        TextView vid =  findViewById(R.id._id);
        lview = findViewById(R.id.animation_view);
        vid.setText(aut);
        TextView vuname = findViewById(R.id._username);
        vuname.setText(uname);
        vid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aut!=null){
                    Intent intent = new Intent(VideoPlayActivity.this,Others.class);
                    intent.putExtra("name",uname);
                    intent.putExtra("id",aut);
                    startActivity(intent);
                }
            }
        });
        VideoView videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse(videoURL));
        ThumbUpView mThumbUpView = findViewById(R.id.tpv);
        mThumbUpView.setUnLikeType(ThumbUpView.LikeType.broken);
        mThumbUpView.setFillColor(Color.rgb(200, 20, 77));
        mThumbUpView.setCracksColor(Color.rgb(22, 33, 44));
        mThumbUpView.setEdgeColor(Color.rgb(33, 3, 219));
        //判断是否点赞
        videoView.setAlpha(0.0f);
        mThumbUpView.setAlpha(0.0f);
        vid.setAlpha(0.0f);
        vuname.setAlpha(0.0f);
        lview.setAlpha(1.0f);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                lview.setAlpha(0.0f);
                videoView.setAlpha(1.0f);
                mThumbUpView.setAlpha(1.0f);
                vid.setAlpha(1.0f);
                vuname.setAlpha(1.0f);
                mp.start();
                mp.setLooping(true);
            }
        });
        mThumbUpView.setOnThumbUp(new ThumbUpView.OnThumbUp() {
            @Override
            public void like(boolean like ) {
                if (like == true) {
                } else {
                }
            }
        });
        mThumbUpView.UnLike();
        //videoView.setMediaController(new MediaController(this));
        mGestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap( MotionEvent e) {
                        mThumbUpView.Like();
                        mThumbUpView.UnLike();
                        return super.onDoubleTap(e);
                    }
                    @Override
                    public boolean onSingleTapConfirmed( MotionEvent e) {
                        CircleImageView cc = findViewById(R.id.circleIV4);
                        if (videoView.isPlaying()) {
                            videoView.pause();
                            cc.setAlpha(1f);
                        } else {
                            videoView.start();
                            cc.setAlpha(0f);
                        }
                        return super.onSingleTapConfirmed(e);
                    }
                }
        );
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
}
