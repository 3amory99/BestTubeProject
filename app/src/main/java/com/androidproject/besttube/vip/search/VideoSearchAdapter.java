package com.androidproject.besttube.vip.search;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidproject.besttube.vip.R;
import com.androidproject.besttube.vip.model.VideoItem;
import com.androidproject.besttube.vip.video.VideoPlayerViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VideoSearchAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

    DatabaseReference mono3atReference, sportsReference, childrenReference, religiousReference, gamesReference, likeReference;
    private Boolean likeChecker = false;
    private FirebaseStorage videoStorage, thumbnailStorage;
    FirebaseUser user;
    FirebaseAuth auth;
    Context context;
    List<VideoItem> videoItemList;
    Dialog deleteDialog;
    VideoPlayerViewHolder myViewHolder;
    String deleteCategory, url, thumbnail;

    public VideoSearchAdapter(Context context, ArrayList<VideoItem> videoItemList) {
        this.context = context;
        this.videoItemList = videoItemList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.final_recycler_view_item, parent, false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mono3atReference = FirebaseDatabase.getInstance().getReference("video").child("mono3at");
        religiousReference = FirebaseDatabase.getInstance().getReference("video").child("religious");
        sportsReference = FirebaseDatabase.getInstance().getReference("video").child("sports");
        childrenReference = FirebaseDatabase.getInstance().getReference("video").child("children");
        gamesReference = FirebaseDatabase.getInstance().getReference("video").child("games");
        videoStorage = FirebaseStorage.getInstance();
        likeReference = FirebaseDatabase.getInstance().getReference("likes");


        thumbnailStorage = FirebaseStorage.getInstance();
        myViewHolder = new VideoPlayerViewHolder(view);
        deleteDialog = new Dialog(context);
        deleteDialog.setContentView(R.layout.delete_dialog);
        Objects.requireNonNull(deleteDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((VideoPlayerViewHolder) holder).onBind((VideoItem) videoItemList.get(position));
        VideoItem videoItem = (VideoItem) videoItemList.get(position);
        String userId = user.getUid();
        final String videoKey = videoItem.getVideoId();
        String thumb = ((VideoItem) videoItemList.get(position)).getThumbnail();
        try {
            Picasso.get().load(thumb).placeholder(R.drawable.logo).into(((VideoPlayerViewHolder) holder).thumbnail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((VideoPlayerViewHolder) holder).setLikeStatus(videoKey);
        ((VideoPlayerViewHolder) holder).likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeChecker = true;
                likeReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (likeChecker.equals(true)) {
                            if (snapshot.child(videoKey).hasChild(userId)) {
                                likeReference.child(videoKey).child(userId).removeValue();
                                likeChecker = false;
                            } else {
                                likeReference.child(videoKey).child(userId).setValue(true);
                                likeChecker = false;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        ((VideoPlayerViewHolder) holder).setOnClickListener(new VideoPlayerViewHolder.VideoClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
            @Override
            public void onItemLongClick(View view, int position) {

                Log.e("VideoCurrentPosition", "" + position);
                VideoItem videoItem = (VideoItem) videoItemList.get(position);
                url = videoItem.getVideoUrl();
                deleteCategory = videoItem.getCategories();


                if (url == null || videoItem == null) {
                    Toast.makeText(view.getContext(), "لقد تم مسح هذا الفيديو بالفعل، قم بتحديث الصفحة!", Toast.LENGTH_SHORT).show();
                    deleteDialog.dismiss();
                } else {
                    Button deleteBtn = deleteDialog.findViewById(R.id.delete_btn);
                    Button cancelBtn = deleteDialog.findViewById(R.id.cancel_btn);
                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (url == null || videoItem == null) {
                                Toast.makeText(view.getContext(), "لقد تم مسح هذا الفيديو بالفعل، قم بتحديث الصفحة!", Toast.LENGTH_SHORT).show();
                                deleteDialog.dismiss();
                            } else {
                                Query query = mono3atReference.orderByChild("videoUrl").equalTo(url);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) {
                                            removeVideoData();
                                        }
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            thumbnail = dataSnapshot.child("thumbnail").getValue().toString();
                                            StorageReference videoReference = videoStorage.getReferenceFromUrl(url);
                                            videoReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    StorageReference thumbnailReference = thumbnailStorage.getReferenceFromUrl(thumbnail);
                                                    thumbnailReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                notifyDataSetChanged();
                                                                                Toast.makeText(view.getContext(), "تم مسح الفيديو بنجاح من منوعات", Toast.LENGTH_SHORT).show();
                                                                                deleteDialog.dismiss();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                    removeVideoData();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
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
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return videoItemList.size();
    }

    public void removeVideoData() {
        switch (deleteCategory) {
            case "Mono3at":
                break;
            case "Religious":
                Query religious = religiousReference.orderByChild("videoUrl").equalTo(url);
                religious.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot rSnapshot) {
                        for (DataSnapshot religiousSnapshot : rSnapshot.getChildren()) {
                            religiousSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "تم مسح الفيديو بنجاح من الديني", Toast.LENGTH_SHORT).show();
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
                break;
            case "Games":
                Query games = gamesReference.orderByChild("videoUrl").equalTo(url);
                games.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot gSnapshot) {
                        for (DataSnapshot gamesSnapshot : gSnapshot.getChildren()) {
                            gamesSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "تم مسح الفيديو بنجاح من العاب", Toast.LENGTH_SHORT).show();
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
                break;
            case "Sports":
                Query sports = sportsReference.orderByChild("videoUrl").equalTo(url);
                sports.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot sSnapshot) {
                        for (DataSnapshot sportSnapshot : sSnapshot.getChildren()) {
                            sportSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "تم مسح الفيديو بنجاح من الرياضة", Toast.LENGTH_SHORT).show();
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
                break;
            case "Children":
                Query children = childrenReference.orderByChild("videoUrl").equalTo(url);
                children.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot cSnapshot) {
                        for (DataSnapshot childrenSnapshot : cSnapshot.getChildren()) {
                            childrenSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "تم مسح الفيديو بنجاح من الأطفال", Toast.LENGTH_SHORT).show();
                                        deleteDialog.dismiss();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ErrorDeleteVideoAdapter", "" + error.getMessage());
                    }
                });
                break;
            default:
                break;
        }
    }
}
