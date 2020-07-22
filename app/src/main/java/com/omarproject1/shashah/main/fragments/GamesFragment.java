package com.omarproject1.shashah.main.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.omarproject1.shashah.download.DownloadActivity;
import com.omarproject1.shashah.R;
import com.omarproject1.shashah.detailsactivity.VideoDetailsActivity;
import com.omarproject1.shashah.VideoHolder;
import com.omarproject1.shashah.model.VideoItem;
import com.omarproject1.shashah.video.VerticalSpacingItemDecorator;
import com.omarproject1.shashah.video.VideoPlayerViewHolder;
import com.omarproject1.shashah.video.VideoRecyclerView;
import com.omarproject1.shashah.video.VideoRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Objects;


public class GamesFragment extends Fragment {


    private static final int PERMISSION_STORAGE_CODE = 1000;

    private DatabaseReference gamesReference, mono3atReference;
    private VideoRecyclerView gamesRecyclerView;
    ArrayList<VideoItem> itemArrayList;
    private VideoRecyclerViewAdapter adapter;
    private VideoPlayerViewHolder myViewHolder;
    private String videoTitle, downloadUrl;
    SnapHelper snapHelper;
    private TextView pageHasNoVideos;
    private ImageView pageHasNoVideosIcon;

    View view;

    public GamesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_games, container, false);
        initiation();

        gamesReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                gamesRecyclerView.setMediaItems(itemArrayList);
                adapter.notifyDataSetChanged();
                gamesRecyclerView.setAdapter(adapter);
                snapHelper.attachToRecyclerView(gamesRecyclerView);

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

        gamesRecyclerView = view.findViewById(R.id.recycler_view_games);
        gamesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        gamesRecyclerView.setLayoutManager(layoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        gamesRecyclerView.addItemDecoration(itemDecorator);
        itemArrayList = new ArrayList<>();
        snapHelper = new PagerSnapHelper();
        myViewHolder = new VideoPlayerViewHolder(view);
        pageHasNoVideos =  view.findViewById(R.id.video_player_text_games);
        pageHasNoVideosIcon = view.findViewById(R.id.video_player_icon_games);
        gamesReference = FirebaseDatabase.getInstance().getReference("video").child("games");
        mono3atReference = FirebaseDatabase.getInstance().getReference("video").child("mono3at");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(view.getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                adapter.goDownloadingActivity(adapter.videoUrl, adapter.videoTitle);

            } else {
                Toast.makeText(view.getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                adapter.requestPermission((Activity) view.getContext());
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gamesRecyclerView != null)
            gamesRecyclerView.releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        gamesRecyclerView.pausePlayer();
    }
    @Override
    public void onResume() {
        super.onResume();
        gamesRecyclerView.startPlayer();
    }


}