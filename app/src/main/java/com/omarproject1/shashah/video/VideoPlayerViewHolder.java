package com.omarproject1.shashah.video;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.omarproject1.shashah.R;
import com.omarproject1.shashah.VideoHolder;
import com.omarproject1.shashah.model.VideoItem;

public class VideoPlayerViewHolder extends RecyclerView.ViewHolder {


    FrameLayout media_container;
    TextView title;
    ImageView  volumeControl;
    public ImageButton downloadBtn;
    ProgressBar progressBar;
    ConstraintLayout deleteLayout;
    ImageView playBtn;
    View parent;


    public VideoPlayerViewHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(view,getAdapterPosition());

            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clickListener.onItemLongClick(view,getAdapterPosition());
                return true;
            }
        });


        parent = itemView;
        downloadBtn = itemView.findViewById(R.id.download_video_row);
//        deleteBtn = itemView.findViewById(R.id.delete_video_row);
        media_container = itemView.findViewById(R.id.media_container);
//        thumbnail = itemView.findViewById(R.id.thumbnail);
        title = itemView.findViewById(R.id.mitch_video_title);
        progressBar = itemView.findViewById(R.id.mitch_progressBar);
        volumeControl = itemView.findViewById(R.id.volume_control);
        deleteLayout = itemView.findViewById(R.id.delete_layout);
        playBtn = itemView.findViewById(R.id.play_btn);
    }
    public void onBind(VideoItem videoItem){
        title.setText(videoItem.getVideoTitle());
        parent.setTag(this);
    }

    private VideoHolder.VideoClickListener clickListener;
    public interface VideoClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view,int position);
    }
    public void setOnClickListener(VideoHolder.VideoClickListener videoClickListener){
        clickListener = videoClickListener;
    }


//    public VideoHolder.DownClickListener downClickListener;
//    public interface DownClickListener{
//        void onItemClick(View view, int position);
//    }
//    public void setOnClickListener(VideoPlayerViewHolder.DownClickListener downClickListener){
//        downClickListener = downClickListener;
//    }




}
