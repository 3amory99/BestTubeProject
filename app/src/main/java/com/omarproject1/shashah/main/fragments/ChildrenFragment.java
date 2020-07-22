package com.omarproject1.shashah.main.fragments;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omarproject1.shashah.download.DownloadActivity;
import com.omarproject1.shashah.R;
import com.omarproject1.shashah.video.VerticalSpacingItemDecorator;
import com.omarproject1.shashah.video.VideoPlayerViewHolder;
import com.omarproject1.shashah.video.VideoRecyclerView;
import com.omarproject1.shashah.video.VideoRecyclerViewAdapter;
import com.omarproject1.shashah.model.VideoItem;

import java.util.ArrayList;


public class ChildrenFragment extends Fragment {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private DatabaseReference childrenReference, mono3atReference;
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
        childrenReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        childrenRecyclerView.addItemDecoration(itemDecorator);
        itemArrayList = new ArrayList<>();
        snapHelper = new PagerSnapHelper();
//        myViewHolder = new VideoPlayerViewHolder(view);
        pageHasNoVideos =  view.findViewById(R.id.video_player_text_children);
        pageHasNoVideosIcon = view.findViewById(R.id.video_player_icon_children);
        childrenReference = FirebaseDatabase.getInstance().getReference("video").child("children");
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
