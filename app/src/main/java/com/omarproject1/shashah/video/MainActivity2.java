package com.omarproject1.shashah.video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omarproject1.shashah.R;
import com.omarproject1.shashah.model.VideoItem;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    private VideoRecyclerView videoRecyclerView;
    private DatabaseReference mono3atReference;
    ArrayList<VideoItem> itemArrayList;
    VideoRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        videoRecyclerView = findViewById(R.id.recycler_view);
        initiation();
//        Query query = mono3atReference.orderByChild("videoUrl").limitToFirst(2);

        mono3atReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChildren()) {
                    Toast.makeText(MainActivity2.this, "No videos", Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    VideoItem videoItem = dataSnapshot.getValue(VideoItem.class);
                    itemArrayList.add(videoItem);
                }
                adapter = new VideoRecyclerViewAdapter(MainActivity2.this, itemArrayList);
                videoRecyclerView.setMediaItems(itemArrayList);
//                adapter.notifyItemRemoved(adapter.positionRemoved);
                adapter.notifyDataSetChanged();
                videoRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity2.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("VideosFromFirebase", "" + error.getMessage());
            }
        });

    }

    private void initiation() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        videoRecyclerView.setLayoutManager(layoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        videoRecyclerView.addItemDecoration(itemDecorator);
        itemArrayList = new ArrayList<>();
        mono3atReference = FirebaseDatabase.getInstance().getReference("video").child("mono3at");

    }

    @Override
    protected void onDestroy() {
        if (videoRecyclerView != null)
            videoRecyclerView.releasePlayer();
        super.onDestroy();
    }

}