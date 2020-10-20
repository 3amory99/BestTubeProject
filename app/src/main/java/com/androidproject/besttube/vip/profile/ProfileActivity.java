package com.androidproject.besttube.vip.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidproject.besttube.vip.R;
import com.androidproject.besttube.vip.model.VideoItem;
import com.androidproject.besttube.vip.signUp.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private ImageView editBtn, noVideosImg;
    private TextView username, bio, videosNumber, noVideosTxt;
    private DatabaseReference userReference, videosReference;
    private RecyclerView videoPosts;
    private List<VideoItem> videoItems;
    ProfileVideosAdapter profileVideosAdapter;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initiation();
        String userUid = getIntent().getStringExtra("UserUid");
        String userFromAdapter = getIntent().getStringExtra("uidFromAdapter");
        String userUidFromUsers = getIntent().getStringExtra("userUidIntent");
        if (userUidFromUsers != null) {
            editBtn.setVisibility(View.INVISIBLE);
            getVideosData(userUidFromUsers);
            getUserPosts(userUidFromUsers);
        } else {
            if (userUid != null) {
                editBtn.setVisibility(View.INVISIBLE);
                getVideosData(userUid);
                getUserPosts(userUid);
            } else if (userFromAdapter != null) {
                auth = FirebaseAuth.getInstance();
                currentUser = auth.getCurrentUser();
                getVideosData(currentUser.getUid());
                getUserPosts(currentUser.getUid());
                editBtn.setVisibility(View.VISIBLE);
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
                        finish();
                    }
                });

            } else {
                auth = FirebaseAuth.getInstance();
                currentUser = auth.getCurrentUser();
                getVideosData(currentUser.getUid());
                getUserPosts(currentUser.getUid());
                editBtn.setVisibility(View.VISIBLE);
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
                        finish();
                    }
                });
            }
        }
    }

    private void getUserPosts(String userUid) {
        String videos = " " + getResources().getString(R.string.videos);
        videosReference = FirebaseDatabase.getInstance().getReference("video").child("mono3at");
        videosReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
//                    Toast.makeText(ProfileActivity.this, "null reference", Toast.LENGTH_SHORT).show();
                    videosNumber.setText(Integer.toString(profileVideosAdapter.getItemCount()) + videos);
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        VideoItem videoItem = dataSnapshot.getValue(VideoItem.class);
                        if (videoItem.getVideoOwner().equals(userUid)) {
                            videoItems.add(videoItem);
                        }
                    }
                    if (videoItems.size() == 0) {
                        noVideosImg.setVisibility(View.VISIBLE);
                        noVideosTxt.setVisibility(View.VISIBLE);
                    }
                    profileVideosAdapter.notifyDataSetChanged();
                    videosNumber.setText(Integer.toString(profileVideosAdapter.getItemCount()) + videos);
                    videoPosts.setAdapter(profileVideosAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void getVideosData(String userUid) {
        userReference = FirebaseDatabase.getInstance().getReference("users").child(userUid);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(getApplicationContext(), "Failure process", Toast.LENGTH_SHORT).show();
                    videosNumber.setText(getResources().getString(R.string.videos_number));
                    Log.e("", "Failure process : Retrieve data");
                } else {
                    String userBio = "" + dataSnapshot.child("about").getValue();
                    String userImage = "" + dataSnapshot.child("image").getValue();
                    String userName = "" + dataSnapshot.child("name").getValue();
                    username.setText(userName);
                    bio.setText(userBio);
                    try {
                        if (userImage == "") {
                            profileImage.setImageResource(R.drawable.user);
                        } else {
                            Picasso.get().load(userImage).placeholder(R.drawable.user).into(profileImage);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        profileImage.setImageResource(R.drawable.user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void initiation() {
        noVideosImg = findViewById(R.id.profile_no_posts_icon);
        noVideosTxt = findViewById(R.id.profile_no_posts_txt);
        profileImage = findViewById(R.id.profile_main_image);
        username = findViewById(R.id.user_name_main);
        bio = findViewById(R.id.bio_main);
        videosNumber = findViewById(R.id.number_of_videos);
        editBtn = findViewById(R.id.edit_profile);
        videoPosts = findViewById(R.id.videos_posts);
        videoItems = new ArrayList<>();
        videoPosts.setLayoutManager(new GridLayoutManager(this, 3));
        videoPosts.setHasFixedSize(true);
        profileVideosAdapter = new ProfileVideosAdapter(ProfileActivity.this, videoItems);


    }
}