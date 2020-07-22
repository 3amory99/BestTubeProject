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


public class ReligiousFragment extends Fragment {


    private static final int PERMISSION_STORAGE_CODE = 1000;

    private DatabaseReference religiousReference, mono3atReference;
    private VideoRecyclerView religiousRecyclerView;
    private String videoTitle, downloadUrl;
    ArrayList<VideoItem> itemArrayList;
    private VideoRecyclerViewAdapter adapter;
    private VideoPlayerViewHolder myViewHolder;
    private TextView pageHasNoVideos;
    SnapHelper snapHelper;
    private ImageView pageHasNoVideosIcon;
    View view;

    public ReligiousFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_religious, container, false);
        initiation();

        religiousReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                religiousRecyclerView.setMediaItems(itemArrayList);
                adapter.notifyDataSetChanged();
                religiousRecyclerView.setAdapter(adapter);
                snapHelper.attachToRecyclerView(religiousRecyclerView);

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
        religiousRecyclerView = view.findViewById(R.id.recycler_view_religious);
        religiousRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        religiousRecyclerView.setLayoutManager(layoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        religiousRecyclerView.addItemDecoration(itemDecorator);
        itemArrayList = new ArrayList<>();
        snapHelper = new PagerSnapHelper();
        myViewHolder = new VideoPlayerViewHolder(view);
        pageHasNoVideos =  view.findViewById(R.id.video_player_text_religious);
        pageHasNoVideosIcon = view.findViewById(R.id.video_player_icon_religious);
        religiousReference = FirebaseDatabase.getInstance().getReference("video").child("religious");
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
        if (religiousRecyclerView != null)
            religiousRecyclerView.releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        religiousRecyclerView.pausePlayer();
    }
    @Override
    public void onResume() {
        super.onResume();
        religiousRecyclerView.startPlayer();
    }


}