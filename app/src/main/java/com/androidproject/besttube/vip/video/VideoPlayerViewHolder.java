package com.androidproject.besttube.vip.video;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.androidproject.besttube.vip.R;
import com.androidproject.besttube.vip.model.VideoItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tuyenmonkey.mkloader.MKLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoPlayerViewHolder extends RecyclerView.ViewHolder {


    FrameLayout media_container;
    TextView title, ownerName, likesDisplay;
    public ImageView likeBtn;
    public ImageView thumbnail;
    ImageView volumeControl;
    CircleImageView ownerImage;
    int likeCount;
    public ImageButton downloadBtn;
    MKLoader progressBar;
    ConstraintLayout deleteLayout;
    ImageView playBtn;
    View parent;
    DatabaseReference likesRef,userReference;

    public VideoPlayerViewHolder(@NonNull View itemView) {
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

        parent = itemView;
        downloadBtn = itemView.findViewById(R.id.download_video_row);
        media_container = itemView.findViewById(R.id.media_container);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        title = itemView.findViewById(R.id.video_title);
        progressBar = itemView.findViewById(R.id.mitch_progressBar);
        volumeControl = itemView.findViewById(R.id.volume_control);
        deleteLayout = itemView.findViewById(R.id.delete_layout);
        playBtn = itemView.findViewById(R.id.play_btn);
        ownerImage = itemView.findViewById(R.id.user_video_icon);
        ownerName = itemView.findViewById(R.id.owner_name);
    }

    public void onBind(VideoItem videoItem) {

        title.setText(videoItem.getVideoTitle());
        userReference = FirebaseDatabase.getInstance().getReference("users").child(videoItem.getVideoOwner());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                        String image = "" + snapshot.child("image").getValue();
                        String name = "" + snapshot.child("name").getValue();
                        ownerName.setText(name);
                        try {
                            Picasso.get().load(image).into(ownerImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ownerImage.setImageResource(R.drawable.logo);
                        }

                } else {
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        parent.setTag(this);
    }
    public void setLikeStatus(final String videoKey) {
        likeBtn = itemView.findViewById(R.id.video_like);
        likesDisplay = itemView.findViewById(R.id.like_number);
        likesRef = FirebaseDatabase.getInstance().getReference("likes");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        String likes = " likes";
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                } else {
                    if (snapshot.child(videoKey).hasChild(userId)) {
                        likeCount = (int) snapshot.child(videoKey).getChildrenCount();
                        likeBtn.setImageResource(R.drawable.ic_like);
                        likesDisplay.setText(Integer.toString(likeCount) + likes);
                    } else {
                        likeCount = (int) snapshot.child(videoKey).getChildrenCount();
                        likeBtn.setImageResource(R.drawable.ic_dislike);
                        likesDisplay.setText(Integer.toString(likeCount) + likes);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private VideoPlayerViewHolder.VideoClickListener clickListener;
    public interface VideoClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }
    public void setOnClickListener(VideoPlayerViewHolder.VideoClickListener videoClickListener) {
        clickListener = videoClickListener;
    }


    private VideoPlayerViewHolder.GoProfileClickListener goProfileClickListener;
    public interface GoProfileClickListener {
        void onItemClick(View view, int position);
    }
    public void setClickListener(VideoPlayerViewHolder.GoProfileClickListener clickToGoProfile) {
        goProfileClickListener = clickToGoProfile;
    }


}
