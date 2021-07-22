package com.example.final_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class myspaceFragment extends Fragment {

    private vedioAdapter v_adapter = new vedioAdapter();
    private RecyclerView recyclerView;
    private List<vedioData> m_data;
    Context context;
    public myspaceFragment() {
        // Required empty public constructor
    }

    public interface MyOnItemClickListener {
        void OnItemClickListener(View itemView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_video, container, false);
        View view=inflater.inflate(R.layout.fragment_myspace,container,false);
        TextView vid = view.findViewById(R.id._id);
        vid.setText("用户id : "+Constants.STUDENT_ID);
        TextView vuname = view.findViewById(R.id._username);
        vuname.setText("用户名 : "+Constants.USER_NAME);
        context=view.getContext();
        recyclerView=view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(v_adapter);
        getData(Constants.STUDENT_ID);
        v_adapter.setOnItemClickListener(new vedioAdapter.IOnItemClickListener(){
            @Override
            public void onItemCLick(int position, vedioData data){
                vedioData nowdata = v_adapter.data.get(position);
                String videoURL = nowdata.getVideoURL();
                String Name = nowdata.getName();
                String Sid = nowdata.getStuid();
                Intent intent = new Intent(getActivity(),VideoPlayActivity.class);
                intent.putExtra("url",videoURL);
                intent.putExtra("name",Name);
                intent.putExtra("sid",Sid);
                startActivity(intent);
            }
        });
        return view;
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