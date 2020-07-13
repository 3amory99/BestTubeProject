package com.omarproject1.shashah.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.omarproject1.shashah.R;
import com.omarproject1.shashah.VideoAdapter;
import com.omarproject1.shashah.model.VideoItem;
import com.omarproject1.shashah.upload.UploadActivity;

import java.util.ArrayList;
import java.util.List;

public class PagerTest extends AppCompatActivity {

    FloatingActionButton addVideo;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_test);
        addVideo=findViewById(R.id.add_btn);
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PagerTest.this, UploadActivity.class));
            }
        });

        viewPager = findViewById(R.id.videosPager);
        List<VideoItem> videoItems = new ArrayList<>();

        VideoItem item1 = new VideoItem();
        item1.videoUrl = "https://www.infinityandroid.com/videos/video1.mp4";
        item1.videoTitle = "Celeberation";
        item1.videoDescription = "Celebrate who you are in your deepest heart. Love yourself and the world will love you..";
        videoItems.add(item1);

        VideoItem item2 = new VideoItem();
        item2.videoUrl = "https://www.infinityandroid.com/videos/video2.mp4";
        item2.videoTitle = "Party";
        item2.videoDescription = "you gotta have life your way..";
        videoItems.add(item2);

        VideoItem item3 = new VideoItem();
        item3.videoUrl = "https://www.infinityandroid.com/videos/video3.mp4";
        item3.videoTitle = "Exercise";
        item3.videoDescription = "Whenever I feel the need to exercise. I lie down until it goes away.";
        videoItems.add(item3);

        VideoItem item4 = new VideoItem();
        item4.videoUrl = "https://www.infinityandroid.com/videos/video4.mp4";
        item4.videoTitle = "Nature";
        item4.videoDescription = "In ever walk in with nature one receives far more than he seeks.";
        videoItems.add(item4);

        VideoItem item5 = new VideoItem();
        item5.videoUrl = "https://www.infinityandroid.com/videos/video5.mp4";
        item5.videoTitle = "Travel";
        item5.videoDescription = "It is better to travel than to arrive.";
        videoItems.add(item5);

        VideoItem item6 = new VideoItem();
        item6.videoUrl = "https://www.infinityandroid.com/videos/video6.mp4";
        item6.videoTitle = "Chill";
        item6.videoDescription = "Life is so much easier than you just chill out.";
        videoItems.add(item6);

        VideoItem item7 = new VideoItem();
        item7.videoUrl = "https://www.infinityandroid.com/videos/video7.mp4";
        item7.videoTitle = "Love";
        item7.videoDescription = "The best thing to hold onto in life is each other.";
        videoItems.add(item7);

        VideoItem item8 = new VideoItem();
        item8.videoUrl = "https://firebasestorage.googleapis.com/v0/b/shashah-47b13.appspot.com/o/video%2F1594174789231.mp4?alt=media&token=f9ca3ba5-5663-4210-acc2-d6e05e2cd93d";
        item8.videoTitle = "Love";
        item8.videoDescription = "The best thing to hold onto in life is each other.";
        videoItems.add(item8);


        viewPager.setAdapter(new VideoAdapter(videoItems));

    }



}