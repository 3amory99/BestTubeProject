package com.omarproject1.shashah.main.fragments;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omarproject1.shashah.R;
import com.omarproject1.shashah.model.VideoItem;
import com.omarproject1.shashah.video.VerticalSpacingItemDecorator;
import com.omarproject1.shashah.video.VideoPlayerViewHolder;
import com.omarproject1.shashah.video.VideoRecyclerView;
import com.omarproject1.shashah.video.VideoRecyclerViewAdapter;

import java.util.ArrayList;

public class LoveFragment extends Fragment {


    private static final int PERMISSION_STORAGE_CODE = 1000;

    private DatabaseReference loveReference, mono3atReference;
    private VideoRecyclerView loveRecyclerView;
    private String videoTitle,downloadUrl;
    private ArrayList<VideoItem> itemArrayList;
    private VideoRecyclerViewAdapter adapter;
    private VideoPlayerViewHolder myViewHolder;
    private TextView pageHasNoVideos;
    SnapHelper snapHelper;
    private ImageView pageHasNoVideosIcon;
    View view;
    public LoveFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_love, container, false);
        initiation();

        loveReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChildren()) {
                    pageHasNoVideos.setVisibility(View.VISIBLE);
                    pageHasNoVideosIcon.setVisibility(View.VISIBLE);
//                    Toast.makeText(view.getContext(), "No Videos", Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    VideoItem videoItem = dataSnapshot.getValue(VideoItem.class);
                    itemArrayList.add(videoItem);
                }
                adapter = new VideoRecyclerViewAdapter(view.getContext(), itemArrayList);
                loveRecyclerView.setMediaItems(itemArrayList);
                adapter.notifyDataSetChanged();
                loveRecyclerView.setAdapter(adapter);
                snapHelper.attachToRecyclerView(loveRecyclerView);
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
        loveRecyclerView = view.findViewById(R.id.recycler_view_love);
        loveRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        loveRecyclerView.setLayoutManager(layoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        loveRecyclerView.addItemDecoration(itemDecorator);
        itemArrayList = new ArrayList<>();
        snapHelper = new PagerSnapHelper();
        myViewHolder = new VideoPlayerViewHolder(view);
        pageHasNoVideos =  view.findViewById(R.id.video_player_text_love);
        pageHasNoVideosIcon = view.findViewById(R.id.video_player_icon_love);
        loveReference = FirebaseDatabase.getInstance().getReference("video").child("love");
        mono3atReference = FirebaseDatabase.getInstance().getReference("video").child("mono3at");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(view.getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                adapter.goDownloadingActivity(adapter.videoUrl,adapter.videoTitle);

            } else {
                Toast.makeText(view.getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                adapter.requestPermission((Activity) view.getContext());

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loveRecyclerView != null)
            loveRecyclerView.releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        loveRecyclerView.pausePlayer();
    }
    @Override
    public void onResume() {
        super.onResume();
        loveRecyclerView.startPlayer();
    }



}