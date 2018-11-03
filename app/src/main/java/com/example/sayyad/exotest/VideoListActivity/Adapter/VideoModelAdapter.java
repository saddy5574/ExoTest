package com.example.sayyad.exotest.VideoListActivity.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sayyad.exotest.R;
import com.example.sayyad.exotest.VideoListActivity.Model.VideoModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by SAYYAD on 08-Apr-18.
 */

public class VideoModelAdapter extends RecyclerView.Adapter<VideoModelAdapter.MyViewHolder> {

    private List<VideoModel> videosList;
    private LayoutInflater inflater;
    Context context;

    private ImageView imageView;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mListener = onItemClickListener;
    }

    public VideoModelAdapter(Context context,List<VideoModel> videosList) {
        this.videosList = videosList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int postion) {
        View view = inflater.inflate(R.layout.video_card,parent,false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        VideoModel videoModel = videosList.get(position);
        holder.setData(videoModel);
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView duration;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            name = (TextView) itemView.findViewById(R.id.title);
            duration = (TextView) itemView.findViewById(R.id.duration);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });

        }

        public void setData(VideoModel current) {
            this.name.setText(current.getTitle());
            this.duration.setText(new SimpleDateFormat("mm:ss").format(new Date(current.getDuration())));
            imageView.setImageBitmap(current.getThumbnail());
        }
    }
}
