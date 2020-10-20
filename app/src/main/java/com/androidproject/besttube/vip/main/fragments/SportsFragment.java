package com.androidproject.besttube.vip.main.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.androidproject.besttube.vip.video.VideoPlayerViewHolder;
import com.androidproject.besttube.vip.video.VideoRecyclerView;
import com.androidproject.besttube.vip.video.VideoRecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SportsFragment extends Fragment {


    private DatabaseReference sportsReference;
    private VideoRecyclerView sportsRecyclerView;
    private ArrayList<VideoItem> itemArrayList;
    private VideoRecyclerViewAdapter adapter;
    private VideoPlayerViewHolder myViewHolder;
    private TextView pageHasNoVideos;
    SnapHelper snapHelper;
    private ImageView pageHasNoVideosIcon;
    View view;

    public SportsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sports, container, false);
        initiation();

        Query query = sportsReference.orderByChild("videoDate");
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
                    itemArrayList.add(0, videoItem);
                }
                adapter = new VideoRecyclerViewAdapter(view.getContext(), itemArrayList);
                sportsRecyclerView.setMediaItems(itemArrayList);
                adapter.notifyDataSetChanged();
                sportsRecyclerView.setAdapter(adapter);
                snapHelper.attachToRecyclerView(sportsRecyclerView);
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
        sportsRecyclerView = view.findViewById(R.id.recycler_view_love);
        sportsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        sportsRecyclerView.setLayoutManager(layoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(5);
        sportsRecyclerView.addItemDecoration(itemDecorator);
        itemArrayList = new ArrayList<>();
        snapHelper = new PagerSnapHelper();
        myViewHolder = new VideoPlayerViewHolder(view);
        pageHasNoVideos = view.findViewById(R.id.video_player_text_love);
        pageHasNoVideosIcon = view.findViewById(R.id.video_player_icon_love);
        sportsReference = FirebaseDatabase.getInstance().getReference("video").child("sports");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sportsRecyclerView != null)
            sportsRecyclerView.releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        sportsRecyclerView.pausePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        sportsRecyclerView.startPlayer();
    }
}