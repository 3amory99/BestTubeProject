package com.omarproject1.shashah.video;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.omarproject1.shashah.R;
import com.omarproject1.shashah.VideoHolder;
import com.omarproject1.shashah.download.DownloadActivity;
import com.omarproject1.shashah.model.VideoItem;

import java.util.List;
import java.util.Objects;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    DatabaseReference reference;
    private FirebaseStorage firebaseStorage;

    Context context;
    List<VideoItem> videoItemList;
    public String videoTitle, videoUrl;
    Dialog deleteDialog;
    VideoPlayerViewHolder myViewHolder;
    public int positionRemoved;
    private static final int PERMISSION_STORAGE_CODE = 1000;

    public VideoRecyclerViewAdapter(Context context, List<VideoItem> videoItemList) {
        this.context = context;
        this.videoItemList = videoItemList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.final_recycler_view_item, parent, false);
        reference = FirebaseDatabase.getInstance().getReference("video").child("mono3at");
        firebaseStorage = FirebaseStorage.getInstance();
        myViewHolder = new VideoPlayerViewHolder(view);
        deleteDialog = new Dialog(context);
        deleteDialog.setContentView(R.layout.delete_dialog);
        Objects.requireNonNull(deleteDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((VideoPlayerViewHolder) holder).onBind(videoItemList.get(position));
//        VideoItem videoItem = videoItemList.get(position);
//        videoTitle = videoItem.getVideoTitle();
//        videoUrl = videoItem.getVideoUrl();
//
//        positionRemoved = position;

/////////////////////////////////////////////////

        ((VideoPlayerViewHolder) holder).setOnClickListener(new VideoHolder.VideoClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, int position) {

                Log.e("VideoCurrentPosition", "" + position);
                VideoItem videoItem = videoItemList.get(position);
                String url = videoItem.getVideoUrl();

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
                                Query query = reference.orderByChild("videoUrl").equalTo(url);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            String storedVideo = dataSnapshot.child("videoUrl").getValue().toString();
                                            StorageReference videoReference = firebaseStorage.getReferenceFromUrl(storedVideo);
                                            videoReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                notifyItemRemoved(positionRemoved);
                                                                notifyDataSetChanged();
                                                                Toast.makeText(view.getContext(), "تم مسح الفيديو بنجاح", Toast.LENGTH_SHORT).show();

                                                                deleteDialog.dismiss();
                                                            }
                                                        }

                                                    });

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


        ((VideoPlayerViewHolder) holder).downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(view.getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    goDownloadingActivity(videoUrl, videoTitle);
                } else {
                    requestPermission((Activity) context);
                }
            }
        });

        ///////////////////////////////////////////////////

//        ((VideoPlayerViewHolder) holder).deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (videoUrl == null) {
//                    Toast.makeText(context, "null url", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(context, "" + positionRemoved, Toast.LENGTH_SHORT).show();
//                    Button deleteBtn = deleteDialog.findViewById(R.id.delete_btn);
//                    Button cancelBtn = deleteDialog.findViewById(R.id.cancel_btn);
//                    deleteBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Query query = reference.orderByChild("videoUrl").equalTo(videoUrl);
//                            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                        dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//                                                    notifyItemRemoved(positionRemoved);
//                                                    notifyDataSetChanged();
//                                                    Toast.makeText(view.getContext(), "تم مسح الفيديو بنجاح", Toast.LENGTH_SHORT).show();
//                                                    deleteDialog.dismiss();
//                                                }
//                                            }
//                                        });
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                        }
//                    });
//                    cancelBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            deleteDialog.dismiss();
//                        }
//                    });
//                    deleteDialog.dismiss();
//                    deleteDialog.show();
//                }
//                notifyDataSetChanged();
//                notifyItemChanged(myViewHolder.getPosition());
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return videoItemList.size();
    }

    public void requestPermission(Activity context) {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(context)
                    .setTitle("Sorry permission needed")
                    .setMessage("Write storage permission is needed to complete uploading videos")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
        }
    }

    public void goDownloadingActivity(String videoUrl, String videoTitle) {
        Intent intent = new Intent(context, DownloadActivity.class);
        intent.putExtra("goDownloadUrl", videoUrl);
        intent.putExtra("goDownloadTitle", videoTitle);
        context.startActivity(intent);
    }


}
