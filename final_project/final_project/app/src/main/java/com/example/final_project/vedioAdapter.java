package com.example.final_project;

import android.graphics.Color;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.model.vedioData;
import com.facebook.drawee.view.SimpleDraweeView;


import java.util.List;

public class vedioAdapter extends RecyclerView.Adapter<vedioAdapter.VideoViewHolder>{
    public List<vedioData> data;
    private IOnItemClickListener mItemClickListener;
    public  void setData(List<vedioData> messageList){
        data = messageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root =LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_recycler,parent,false);
        return new VideoViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.bind(data.get(position));
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick(position, data.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }
    public void setOnItemClickListener(IOnItemClickListener listener) {
        mItemClickListener = listener;
    }
    public interface IOnItemClickListener {
        void onItemCLick(int position, vedioData data);

    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder{
        private SimpleDraweeView coverSD;
        private TextView title;
        private TextView id;
        private View contentView;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            coverSD = itemView.findViewById(R.id.sd_cover);
            title = itemView.findViewById(R.id.title);
            id = itemView.findViewById(R.id.author);
            contentView = itemView;
        }
        public void bind(vedioData message){
            coverSD.setImageURI(message.getCoverimgURL());
            title.setText(message.getName());
            id.setText(message.getStuid());
        }
        public void setOnClickListener(View.OnClickListener listener) {
            if (listener != null) {
                contentView.setOnClickListener(listener);
            }
        }

    }
    public void onItemCLick(int position, vedioData data) {
        //Toast.makeText(getActivity(), "点击了第" + position + "条", Toast.LENGTH_SHORT).show();
        //mAdapter.addData(position + 1, new TestData("新增头条", "0w"));
    }
}
