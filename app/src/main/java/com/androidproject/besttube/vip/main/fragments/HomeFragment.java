package com.androidproject.besttube.vip.main.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import com.androidproject.besttube.vip.R;
import com.androidproject.besttube.vip.model.VideoItem;
import com.androidproject.besttube.vip.video.VerticalSpacingItemDecorator;
import com.androidproject.besttube.vip.video.VideoRecyclerView;
import com.androidproject.besttube.vip.video.VideoRecyclerViewAdapter;
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

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {


    private DatabaseReference mono3atReference;

    private FirebaseStorage firebaseStorage;
    private VideoRecyclerView mono3atRecyclerView;
    private VideoRecyclerViewAdapter adapter;
    private ArrayList<VideoItem> itemArrayList;
    private TextView pageHasNoVideos;
    private ImageView pageHasNoVideosIcon;
    private Dialog deleteDialog;
    Button deleteBtn, cancelBtn;
    SnapHelper snapHelper;
    View view;
//    private String videoTitle, videoDescription, videoHashTag, videoUrl, videoLikes, downloadUrl;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initiation();
        Query query = mono3atReference.orderByChild("videoDate");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChildren()) {
                    pageHasNoVideos.setVisibility(View.VISIBLE);
                    pageHasNoVideosIcon.setVisibility(View.VISIBLE);
//                    Toast.makeText(view.getContext(), "No Videos", Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    VideoItem videoItem = dataSnapshot.getValue(VideoItem.class);
                    itemArrayList.add(0,videoItem);
                }
                adapter = new VideoRecyclerViewAdapter(view.getContext(), itemArrayList);
                mono3atRecyclerView.setMediaItems(itemArrayList);
                adapter.notifyDataSetChanged();
                mono3atRecyclerView.setAdapter(adapter);
                snapHelper.attachToRecyclerView(mono3atRecyclerView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(view.getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("VideosFromFirebase", "" + error.getMessage());
            }
        });
        return view;
    }
    private void initiation() {
        mono3atRecyclerView = view.findViewById(R.id.recycler_view_mono3at);
        mono3atRecyclerView.setHasFixedSize(true);
        itemArrayList = new ArrayList<>();
        snapHelper = new PagerSnapHelper();
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mono3atRecyclerView.setLayoutManager(layoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(5);
        mono3atRecyclerView.addItemDecoration(itemDecorator);
        pageHasNoVideos = view.findViewById(R.id.video_player_text_mono3at);
        pageHasNoVideosIcon = view.findViewById(R.id.video_player_icon_mono3at);
        mono3atReference = FirebaseDatabase.getInstance().getReference("video").child("mono3at");
        firebaseStorage = FirebaseStorage.getInstance();

    }

    private void deleteVideoDialog(String videoUrl) {

        deleteDialog = new Dialog(view.getContext());
        deleteDialog.setContentView(R.layout.delete_dialog);
        Objects.requireNonNull(deleteDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        deleteBtn = deleteDialog.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Query queryMono3atVideo = mono3atReference.orderByChild("videoUrl").equalTo(videoUrl);
                queryMono3atVideo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String storedVideo = snapshot.child("videoUrl").getValue().toString();
                            StorageReference videoReference = firebaseStorage.getReferenceFromUrl(storedVideo);
                            videoReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(view.getContext(), "تم مسح الفيديو بنجاح", Toast.LENGTH_SHORT).show();
                                                deleteDialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }

                    @SuppressLint("ShowToast")
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(view.getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT);
                        Log.e("ErrorInDeletingVideo", error.getMessage());
                    }
                });

            }
        });
        cancelBtn = deleteDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });
        deleteDialog.dismiss();
        deleteDialog.show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mono3atRecyclerView != null)
            mono3atRecyclerView.releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        mono3atRecyclerView.pausePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        mono3atRecyclerView.startPlayer();
    }
}