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


public class EducationFragment extends Fragment {



    private DatabaseReference educationReference;
    private VideoRecyclerView gamesRecyclerView;
    ArrayList<VideoItem> itemArrayList;
    private VideoRecyclerViewAdapter adapter;
    private VideoPlayerViewHolder myViewHolder;
    private String videoTitle, downloadUrl;
    SnapHelper snapHelper;
    private TextView pageHasNoVideos;
    private ImageView pageHasNoVideosIcon;

    View view;

    public EducationFragment() {
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

        Query query = educationReference.orderByChild("videoDate");
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
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(5);
        gamesRecyclerView.addItemDecoration(itemDecorator);
        itemArrayList = new ArrayList<>();
        snapHelper = new PagerSnapHelper();
        myViewHolder = new VideoPlayerViewHolder(view);
        pageHasNoVideos =  view.findViewById(R.id.video_player_text_games);
        pageHasNoVideosIcon = view.findViewById(R.id.video_player_icon_games);
        educationReference = FirebaseDatabase.getInstance().getReference("video").child("education");
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