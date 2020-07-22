package com.omarproject1.shashah.detailsactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.omarproject1.shashah.download.DownloadActivity;
import com.omarproject1.shashah.R;

public class VideoDetailsActivity extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 1000;

    private SimpleExoPlayer player;
    private PlayerView playerView;
    private TextView videoTitle,videoDescription ,videoHashTag, videoLikes;
    private String videoUrl, title;
    private boolean playWhenReady= false;
    private int currentWindow = 0;
    private long playBackPosition = 0;
    private ImageButton downloadBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.post_detatils));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        initiation();
        videoUrl = getIntent().getStringExtra("vUrl");
        setData();
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(view.getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {
                            goDownloadingActivity(videoUrl,title);
//                    Toast.makeText(view.getContext(),"You have already granted this permission",Toast.LENGTH_SHORT).show();
                        } else {
                            requestPermission((Activity) view.getContext());
                        }
                    }

        });

    }

    private void setData() {
        title = getIntent().getStringExtra("vTitle");
        videoTitle.setText(title);
        String description = getIntent().getStringExtra("vDescription");
        videoDescription.setText(description);
        String hashTag = getIntent().getStringExtra("vHashTag");
        videoHashTag.setText(hashTag);
    }

    private void initiation() {

        playerView =findViewById(R.id.video_in_details_activity);
        videoTitle = findViewById(R.id.title_details_activity);
        videoDescription = findViewById(R.id.description_details_activity);
        videoHashTag = findViewById(R.id.video_hashTag_details_activity);
        videoLikes = findViewById(R.id.like_counter_details_activity);
        downloadBtn = findViewById(R.id.download_video_details_activity);
    }

    private MediaSource createMediaSource(Uri uri){
        DataSource.Factory factory = new DefaultHttpDataSourceFactory("video");
        return new ProgressiveMediaSource.Factory(factory).createMediaSource(uri);
    }
    private void initializeMediaPlayer(){
        player= ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        Uri uri =Uri.parse(videoUrl);
        MediaSource mediaSource =createMediaSource(uri);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow,playBackPosition);
        player.prepare(mediaSource,false,false);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Util.SDK_INT >= 26 ){
            initializeMediaPlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Util.SDK_INT >= 26 || player == null ){
//            initializeMediaPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Util.SDK_INT > 26 ){
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(Util.SDK_INT >= 26 ){
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if(player != null){
            playWhenReady = player.getPlayWhenReady();
            playBackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.stop();
            player = null;
        }

    }


    public void  goDownloadingActivity(String videoUrl, String title) {

        Intent intent = new Intent(VideoDetailsActivity.this, DownloadActivity.class);
        intent.putExtra("goDownloadUrl",videoUrl);
        intent.putExtra("goDownloadTitle",title);
        startActivity(intent);
    }

    private void requestPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(VideoDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(VideoDetailsActivity.this)
                    .setTitle("Sorry permission needed")
                    .setMessage("Write storage permission is needed to complete uploading videos")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(VideoDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(VideoDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(VideoDetailsActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                goDownloadingActivity(videoUrl,title);

            } else {
                Toast.makeText(VideoDetailsActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                requestPermission(VideoDetailsActivity.this);

            }
        }
    }



}