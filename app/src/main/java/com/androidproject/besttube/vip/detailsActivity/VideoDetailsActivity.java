package com.androidproject.besttube.vip.detailsActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidproject.besttube.vip.R;
import com.androidproject.besttube.vip.model.VideoItem;
import com.androidproject.besttube.vip.video.VerticalSpacingItemDecorator;
import com.androidproject.besttube.vip.video.VideoPlayerViewHolder;
import com.androidproject.besttube.vip.video.VideoRecyclerView;
import com.androidproject.besttube.vip.video.VideoRecyclerViewAdapter;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.androidproject.besttube.vip.download.DownloadActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class VideoDetailsActivity extends AppCompatActivity {

    private DatabaseReference mono3atReference;

    private VideoRecyclerView videoDetailsRecyclerView;
    private VideoRecyclerViewAdapter videoDetailsAdapter;
    private ArrayList<VideoItem> itemArrayList;
    private TextView pageHasNoVideos;
    private ImageView pageHasNoVideosIcon;
    SnapHelper snapHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.post_detatils));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        initiation();
        String ownerUid = getIntent().getStringExtra("videoOwner");
        String videoUid = getIntent().getStringExtra("VideoUid");
        Query query = mono3atReference.orderByChild("videoId").equalTo(videoUid);
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
                videoDetailsAdapter = new VideoRecyclerViewAdapter(VideoDetailsActivity.this, itemArrayList);
                videoDetailsRecyclerView.setMediaItems(itemArrayList);
                videoDetailsAdapter.notifyDataSetChanged();
                videoDetailsRecyclerView.setAdapter(videoDetailsAdapter);
                snapHelper.attachToRecyclerView(videoDetailsRecyclerView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VideoDetailsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("VideosFromFirebase", "" + error.getMessage());
            }
        });


    }

    private void initiation() {
        videoDetailsRecyclerView = findViewById(R.id.video_details_recycler_view);
        videoDetailsRecyclerView.setHasFixedSize(true);
        snapHelper = new PagerSnapHelper();
        itemArrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        videoDetailsRecyclerView.setLayoutManager(layoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(5);
        videoDetailsRecyclerView.addItemDecoration(itemDecorator);
        pageHasNoVideos = findViewById(R.id.video_player_text_details_activity);
        pageHasNoVideosIcon = findViewById(R.id.video_player_icon_details_activity);
        mono3atReference = FirebaseDatabase.getInstance().getReference("video").child("mono3at");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoDetailsRecyclerView != null)
            videoDetailsRecyclerView.releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoDetailsRecyclerView.pausePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoDetailsRecyclerView.startPlayer();
    }


}