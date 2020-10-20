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


public class ChildrenFragment extends Fragment {

    private DatabaseReference childrenReference;
    private VideoRecyclerView childrenRecyclerView;
    private ArrayList<VideoItem> itemArrayList;
    private TextView pageHasNoVideos;
    private ImageView pageHasNoVideosIcon;
    SnapHelper snapHelper;
    VideoRecyclerViewAdapter adapter;
    VideoPlayerViewHolder myViewHolder;


    View view;


//    private Dialog deleteDialog;
//    private Button deleteBtn, cancelBtn;
//    private String downloadTitle, downloadUrl;

    public ChildrenFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_children, container, false);

        initiation();
        Query query = childrenReference.orderByChild("videoDate");
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
                childrenRecyclerView.setMediaItems(itemArrayList);
                adapter.notifyDataSetChanged();
                childrenRecyclerView.setAdapter(adapter);
                snapHelper.attachToRecyclerView(childrenRecyclerView);
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
        childrenRecyclerView = view.findViewById(R.id.recycler_view_children);
        childrenRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        childrenRecyclerView.setLayoutManager(layoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(5);
        childrenRecyclerView.addItemDecoration(itemDecorator);
        itemArrayList = new ArrayList<>();
        snapHelper = new PagerSnapHelper();
//        myViewHolder = new VideoPlayerViewHolder(view);
        pageHasNoVideos =  view.findViewById(R.id.video_player_text_children);
        pageHasNoVideosIcon = view.findViewById(R.id.video_player_icon_children);
        childrenReference = FirebaseDatabase.getInstance().getReference("video").child("children");
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        if (childrenRecyclerView != null)
            childrenRecyclerView.releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        childrenRecyclerView.pausePlayer();
    }
    @Override
    public void onResume() {
        super.onResume();
        childrenRecyclerView.startPlayer();
    }

}
