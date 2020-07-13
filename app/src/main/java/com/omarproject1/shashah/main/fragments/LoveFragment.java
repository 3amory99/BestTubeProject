package com.omarproject1.shashah.main.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.omarproject1.shashah.R;
import com.omarproject1.shashah.VideoHolder;
import com.omarproject1.shashah.model.VideoItem;

import java.util.Objects;

public class LoveFragment extends Fragment {

    private DatabaseReference loveReference;
    private RecyclerView loveRecyclerView;
    private FirebaseRecyclerOptions<VideoItem> loveRecyclerOptions;
    private FirebaseRecyclerAdapter<VideoItem, VideoHolder> loveRecyclerAdapter;
    ProgressBar loveProgressBar;
    private Dialog deleteDialog;
    private Button deleteBtn, cancelBtn;

    View view;
    public LoveFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_love, container, false);
        initiation();
        return view;
    }

    private void initiation() {
        loveProgressBar = view.findViewById(R.id.progress_circle_love);
        loveRecyclerView = view.findViewById(R.id.love_recyclerView);
        loveRecyclerView.setHasFixedSize(true);
        loveRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        loveReference = FirebaseDatabase.getInstance().getReference("video").child("love");

    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = loveReference.orderByKey();

        loveRecyclerOptions = new FirebaseRecyclerOptions.Builder<VideoItem>()
                .setQuery(query, VideoItem.class)
                .build();
        loveRecyclerAdapter = new FirebaseRecyclerAdapter<VideoItem, VideoHolder>(loveRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull VideoHolder holder, int position, @NonNull VideoItem model) {
                holder.setExoplayer(view.getContext(), model.getVideoTitle(), model.getVideoDescription(), model.getHashTags(), model.getVideoUrl());
                holder.setOnClickListener(new VideoHolder.VideoClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        String videoTitle = getItem(position).getVideoTitle();
                        if (videoTitle != null) {
                            DeleteVideoDialog(videoTitle);
                        } else {
                            Toast.makeText(view.getContext(), "ssa", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                if (holder.description.length() < 100) {
                    holder.showMore.setVisibility(View.GONE);
                }
                holder.showMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.description.length() > 100) {
                            holder.showMore.setText("Show less");
                            TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                        } else {
                        }

                    }
                });
            }

            @NonNull
            @Override
            public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_main_activity, parent, false);
                return new VideoHolder(view);
            }
        };
        loveRecyclerAdapter.startListening();
        loveRecyclerView.setAdapter(loveRecyclerAdapter);

    }

    private void DeleteVideoDialog(String videoTitle) {

        deleteDialog = new Dialog(view.getContext());
        deleteDialog.setContentView(R.layout.delete_dialog);
//        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Objects.requireNonNull(deleteDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        deleteBtn = deleteDialog.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Query query = loveReference.orderByChild("videoTitle").equalTo(videoTitle);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(view.getContext(), "تم مسح الفيديو بنجاح", Toast.LENGTH_SHORT).show();
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
        cancelBtn = deleteDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });
        deleteDialog.show();
        deleteDialog.dismiss();

    }
}