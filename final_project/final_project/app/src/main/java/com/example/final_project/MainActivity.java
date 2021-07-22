package com.example.final_project;
import com.example.final_project.model.vedioData;
import com.example.final_project.model.vedioDataListResponse;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "final project";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(MainActivity.this);
        setContentView(R.layout.activity_main);
        ViewPager view=findViewById(R.id.view_pager);
        TabLayout tab=findViewById(R.id.tab_layout);

        view.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if(position == 0){
                   // getData(null);
                    return new videoFragment();
                }else if(position == 1){
                    return new uploadFragment();
                }else{
                    return new myspaceFragment();
                }
            }
            @Override
            public int getCount() {
                return 3;
            }
            @Override
            public CharSequence getPageTitle(int pos){
                if(pos==0){
                    return "主页";
                }else if(pos==1){
                    return "上传";
                }else{
                    return "我的";
                }
            }
        });
        tab.setupWithViewPager(view);
    }

}