package com.example.final_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.final_project.model.vedioData;
import com.example.final_project.model.vedioDataListResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static android.os.Looper.getMainLooper;

public class Others extends AppCompatActivity {
    private vedioAdapter v_adapter = new vedioAdapter();
    private RecyclerView recyclerView;
    private List<vedioData> m_data;
    String userid;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others);
        Intent intent = getIntent();
        userid = intent.getStringExtra("id");
        username = intent.getStringExtra("name");
        TextView vid = findViewById(R.id._id);
        vid.setText("用户id : "+userid);
        TextView vuname = findViewById(R.id._username);
        vuname.setText("用户名 : "+username);
        recyclerView=findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(v_adapter);
        getData(userid);
        v_adapter.setOnItemClickListener(new vedioAdapter.IOnItemClickListener(){
            @Override
            public void onItemCLick(int position, vedioData data){
                vedioData nowdata = v_adapter.data.get(position);
                String videoURL = nowdata.getVideoURL();
                String Name = nowdata.getName();
                String Sid = nowdata.getStuid();
                Intent intent = new Intent(Others.this,VideoPlayActivity.class);
                intent.putExtra("url",videoURL);
                intent.putExtra("name",Name);
                intent.putExtra("sid",Sid);
                startActivity(intent);
            }
        });
    }

    public void getData(String studentId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<vedioData> video_data = baseGetMessFromRemote(studentId,"WkpVLWJ5dGVkYW5jZS1hbmRyb2lk");
                if(video_data!=null && !video_data.isEmpty()){
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            v_adapter.setData(video_data);
                        }
                    });
                }
            }
        }).start();
    }


    public List<vedioData> baseGetMessFromRemote(String studentId, String token ){
        String urlS;
        if(studentId == null)  {
            urlS=String.format("https://api-android-camp.bytedance.com/zju/invoke/video");
        }
        else {
            urlS=String.format("https://api-android-camp.bytedance.com/zju/invoke/video?student_id=%s",studentId);
        }
        vedioDataListResponse mess= null;
        try{
            URL url=new URL(urlS);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("token",token);
            if(conn.getResponseCode()==200){
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                mess = new Gson().fromJson(reader,new TypeToken<vedioDataListResponse>(){}.getType());
                reader.close();
                in.close();
            }else{
            }
        }
        catch (Exception e){
            e.printStackTrace();
            //Toast.makeText(this,"网络异常"+e.toString(),Toast.LENGTH_SHORT).show();
        }
        return mess.feeds;
    }
}
