package com.omarproject1.shashah;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.omarproject1.shashah.download.DownloadActivity;
import com.omarproject1.shashah.model.VideoItem;
import com.omarproject1.shashah.splash.SplashActivity;

import java.util.List;
import java.util.Objects;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    Context context;
    List<VideoItem> videoItemList;
    Dialog deleteDialog;
    MyViewHolder myViewHolder;
    DatabaseReference reference;
    String videoTitle, videoUrl;

    public RecyclerViewAdapter(Context context, List<VideoItem> videoItemList) {
        this.context = context;
        this.videoItemList = videoItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_row, parent, false);
        reference = FirebaseDatabase.getInstance().getReference("video").child("mono3at");
        myViewHolder = new MyViewHolder(view);
        deleteDialog = new Dialog(context);
        deleteDialog.setContentView(R.layout.delete_dialog);
        Objects.requireNonNull(deleteDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        myViewHolder.title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(videoUrl == null){
                    Toast.makeText(context, "null url", Toast.LENGTH_SHORT).show();
                }
                Button deleteBtn = deleteDialog.findViewById(R.id.delete_btn);
                Button cancelBtn = deleteDialog.findViewById(R.id.cancel_btn);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Query query = reference.orderByChild("videoUrl").equalTo(videoUrl);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                notifyDataSetChanged();
                                                Toast.makeText(view.getContext(), "تم مسح الفيديو بنجاح", Toast.LENGTH_SHORT).show();
                                                deleteDialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDialog.dismiss();
                    }
                });
                deleteDialog.dismiss();
                deleteDialog.show();


                return false;
            }
        });


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        VideoItem videoItem = videoItemList.get(position);
        MediaController mediaController = new MediaController(context);
        videoTitle = videoItem.getVideoTitle();
        videoUrl = videoItem.getVideoUrl();

        holder.videoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                return true;
            }
        });
        holder.setOnClickListener(new VideoHolder.VideoClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onItemLongClick(View view, int position) {
//                Toast.makeText(context, "long click", Toast.LENGTH_SHORT).show();
            }
        });


        holder.layout.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
        holder.layout.requestFocus();

        if (videoItem.getVideoUrl() != null) {
            holder.videoView.setVideoURI(Uri.parse(videoItem.getVideoUrl()));
            holder.title.setText(videoItem.getVideoTitle());
            holder.description.setText(videoItem.getVideoDescription());
            holder.videoView.setTag(videoItem.getVideoUrl());
            holder.videoView.seekTo(100);
            holder.videoView.requestFocus();

//            mediaController.setAnchorView(holder.videoView);
            holder.videoView.setMediaController(mediaController);

            holder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DownloadActivity.class);
                    intent.putExtra("goDownloadUrl", videoItem.getVideoUrl());
                    intent.putExtra("goDownloadTitle", videoItem.getVideoTitle());
                    context.startActivity(intent);
                }
            });
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "you liked video " + getItemId(position), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return videoItemList.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layout, deleteLayout;
        VideoView videoView;
        TextView title, description;
        ImageButton like, share, download;


        @SuppressLint("CutPasteId")
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(view, getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    clickListener.onItemLongClick(view, getAdapterPosition());
                    return true;
                }
            });

            layout = itemView.findViewById(R.id.layout_row);
            videoView = itemView.findViewById(R.id.video_row);
            title = itemView.findViewById(R.id.videoTestTitle);
            description = itemView.findViewById(R.id.videoTestDescr);
            like = itemView.findViewById(R.id.like_video_row);
            download = itemView.findViewById(R.id.download_video_row);
            share = itemView.findViewById(R.id.share_video_row);
            deleteLayout = itemView.findViewById(R.id.delete_layout);


        }

        private VideoHolder.VideoClickListener clickListener;

        public interface VideoClickListener {
            void onItemClick(View view, int position);

            void onItemLongClick(View view, int position);
        }

        public void setOnClickListener(VideoHolder.VideoClickListener videoClickListener) {
            clickListener = videoClickListener;
        }
    }


}



