package com.androidproject.besttube.vip.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidproject.besttube.vip.R;
import com.androidproject.besttube.vip.detailsActivity.VideoDetailsActivity;
import com.androidproject.besttube.vip.model.VideoItem;
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

import java.util.List;
import java.util.Objects;

public class ProfileVideosAdapter extends RecyclerView.Adapter<ProfileVideosAdapter.PostsViewHolder> {

    private Context context;
    private List<VideoItem> videoPostItemList;
    private DatabaseReference checkReference, sportsReference, childrenReference, religiousReference, educationReference, likeReference;
    private FirebaseStorage videoStorage, thumbnailStorage;
    private Dialog deleteDialog, loaderDialog;
    private String deleteCategory, url, thumbnail;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    String owner;


    public ProfileVideosAdapter(Context context, List<VideoItem> videoPostItemList) {
        this.context = context;
        this.videoPostItemList = videoPostItemList;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_post_item, parent, false);
        checkReference = FirebaseDatabase.getInstance().getReference("video").child("mono3at");
        likeReference = FirebaseDatabase.getInstance().getReference("likes");
        religiousReference = FirebaseDatabase.getInstance().getReference("video").child("religious");
        sportsReference = FirebaseDatabase.getInstance().getReference("video").child("sports");
        childrenReference = FirebaseDatabase.getInstance().getReference("video").child("children");
        educationReference = FirebaseDatabase.getInstance().getReference("video").child("education");
        videoStorage = FirebaseStorage.getInstance();
        thumbnailStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        deleteDialog = new Dialog(context);
        deleteDialog.setContentView(R.layout.delete_dialog);
        Objects.requireNonNull(deleteDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return new PostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        final VideoItem videoItem = videoPostItemList.get(position);
        try {
            Picasso.get().load(videoItem.getThumbnail()).into(holder.videoThumb);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.videoThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String videoUid = videoItem.getVideoId();
                String videoOwner = videoItem.getVideoOwner();
                Intent intent = new Intent(context, VideoDetailsActivity.class);
                intent.putExtra("VideoUid", videoUid);
                intent.putExtra("videoOwner", videoOwner);
                context.startActivity(intent);
            }
        });
        holder.videoThumb.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                checkReference.child(videoItem.videoId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(context, "null reference", Toast.LENGTH_SHORT).show();
                        } else {
                            owner = "" + snapshot.child("videoOwner").getValue();
                            if (owner.equals(currentUser.getUid())) {
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
                                                Toast.makeText(context, "لقد تم مسح هذا الفيديو بالفعل، قم بتحديث الصفحة!", Toast.LENGTH_SHORT).show();
                                                deleteDialog.dismiss();
                                            }
                                            else {
                                                Query query = checkReference.orderByChild("videoUrl").equalTo(url);
                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (!snapshot.exists()) {
//                                                            removeVideoData();
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
                                                                                               likeReference.child(Objects.requireNonNull(dataSnapshot.getKey())).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                   @Override
                                                                                                   public void onComplete(@NonNull Task<Void> task) {
                                                                                                       Toast.makeText(context, "تم مسح الفيديو بنجاح من منوعات", Toast.LENGTH_SHORT).show();
                                                                                                       deleteDialog.dismiss();
                                                                                                   }
                                                                                               });

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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                return false;

            }
        });
    }
    public void removeVideoData() {
        switch (deleteCategory) {
            case "منوعات":
                updateReference(owner);
                break;
            case "ديني":
                Query religious = religiousReference.orderByChild("videoUrl").equalTo(url);
                religious.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot rSnapshot) {
                        for (DataSnapshot religiousSnapshot : rSnapshot.getChildren()) {
                            religiousSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        updateReference(owner);
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
            case "تعليمي":
                Query education = educationReference.orderByChild("videoUrl").equalTo(url);
                education.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot gSnapshot) {
                        for (DataSnapshot gamesSnapshot : gSnapshot.getChildren()) {
                            gamesSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        updateReference(owner);
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
            case "رياضة":
                Query sports = sportsReference.orderByChild("videoUrl").equalTo(url);
                sports.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot sSnapshot) {
                        for (DataSnapshot sportSnapshot : sSnapshot.getChildren()) {
                            sportSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        updateReference(owner);
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
            case "أطفال":
                Query children = childrenReference.orderByChild("videoUrl").equalTo(url);
                children.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot cSnapshot) {
                        for (DataSnapshot childrenSnapshot : cSnapshot.getChildren()) {
                            childrenSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        updateReference(owner);
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

    private void updateReference(String owner) {
        loaderDialog = new Dialog(context);
        loaderDialog.setContentView(R.layout.loader_dialog);
        Objects.requireNonNull(loaderDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loaderDialog.show();
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("WrongConstant")
            @Override
            public void run() {
                if (owner != null) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("uidFromAdapter",owner);
                    loaderDialog.dismiss();
                    context.startActivity(intent);
                    ((Activity)context).finish();

                }
            }
        }, 3000);
    }

    @Override
    public int getItemCount() {
        return videoPostItemList.size();
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumb, playIcon;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumb = itemView.findViewById(R.id.profile_image_video_item);
            playIcon = itemView.findViewById(R.id.profile_play_icon);
        }
    }
}
