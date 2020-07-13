package com.omarproject1.shashah.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.omarproject1.shashah.R;
import com.omarproject1.shashah.VideoHolder;
import com.omarproject1.shashah.model.VideoItem;

import java.util.Locale;
import java.util.Objects;

public class VideosShow extends AppCompatActivity {


    DatabaseReference reference;
    RecyclerView recyclerView;
    private Button languageBtn, shareAppBtn, privacyBtn, arabicBtn, englishBtn;
    private ProgressBar mProgressCircle;
    private Dialog dialog, languageDialog;
    private SharedPreferences sharedPreferences;

    Intent shareIntent, chooser;
    private String strAppLink = "";
    private String shareBody = "Hello ! you can download our app service for free and enjoy.";
    ImageView optionMenu;

    FirebaseRecyclerOptions<VideoItem> recyclerOptions;
    FirebaseRecyclerAdapter<VideoItem, VideoHolder> recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos_show);
        initiation();
        loadLocale();
        optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionDialog();
            }
        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void initiation() {

        mProgressCircle = findViewById(R.id.progress_circle);
        recyclerView = findViewById(R.id.video_show_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference("video").child("mono3at");
        optionMenu = findViewById(R.id.option_menu);

    }

    private void showOptionDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        languageBtn = dialog.findViewById(R.id.language);
        languageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLanguageDialog();
                dialog.dismiss();
            }
        });
        shareAppBtn = dialog.findViewById(R.id.share_app);
        shareAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setData(Uri.parse("mailto:"));
                final String appPackageName = getApplicationContext().getPackageName();
                strAppLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
                shareIntent.setType("text/link");
                shareBody = shareBody + "/n" + " " + strAppLink;
                String shareSub = "Shashah App";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                chooser = Intent.createChooser(shareIntent, "Share Our Application Link");

                startActivity(chooser);
            }
        });
        dialog.dismiss();
        dialog.show();
    }

    private void showLanguageDialog() {
        languageDialog = new Dialog(this);
        languageDialog.setContentView(R.layout.language_dialog);
        Objects.requireNonNull(languageDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        arabicBtn = languageDialog.findViewById(R.id.arabic_lang);
        arabicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("ar");
                recreate();
                languageDialog.dismiss();
                Toast.makeText(VideosShow.this, "Arabic", Toast.LENGTH_SHORT).show();

            }
        });
        englishBtn = languageDialog.findViewById(R.id.english_lang);
        englishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("");
                recreate();
                languageDialog.dismiss();
                Toast.makeText(view.getContext(), "English", Toast.LENGTH_SHORT).show();
            }
        });
        languageDialog.dismiss();
        languageDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();


        Query query = reference.orderByKey();

        recyclerOptions = new FirebaseRecyclerOptions.Builder<VideoItem>()
                .setQuery(query, VideoItem.class)
                .build();
        recyclerAdapter = new FirebaseRecyclerAdapter<VideoItem, VideoHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull VideoHolder holder, int position, @NonNull VideoItem model) {
                holder.setExoplayer(getApplication(), model.getVideoTitle(), model.getVideoDescription(), model.getHashTags(), model.getVideoUrl());
                if (holder.description.length() < 100) {
                    holder.showMore.setVisibility(View.GONE);
                }
                holder.showMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.description.length() > 100) {
                            holder.showMore.setText("Show less");
                            TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                        } else {
                        }

                    }
                });
            }

            @NonNull
            @Override
            public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_main_activity, parent, false);
                return new VideoHolder(view);
            }
        };

        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(recyclerAdapter != null) {
            recyclerAdapter.stopListening();
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        recyclerAdapter = new FirebaseRecyclerAdapter<VideoItem, VideoHolder>(recyclerOptions) {
            @NonNull
            @Override
            public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }
            @Override
            protected void onBindViewHolder(@NonNull VideoHolder holder, int position, @NonNull VideoItem model) {
                if (holder.player.isPlaying()) {
                    // Argument equals true to notify the system that the activity
                    // wishes to be visible behind other translucent activities
                    if (!requestVisibleBehind(true)) {
                        // App-specific method to stop playback and release resources
                        // because call to requestVisibleBehind(true) failed
                        stopPlayback();
                        holder.player.stop();
                    }
                } else {
                    // Argument equals false because the activity is not playing
                    requestVisibleBehind(false);
                }
            }
        };
    }
///////////
    @Override
    public void onVisibleBehindCanceled() {
        // App-specific method to stop playback and release resources
        stopPlayback();
        super.onVisibleBehindCanceled();
    }

    private void stopPlayback() {

    }

    private void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("myLanguage", lang);
        editor.apply();


    }

    private void loadLocale() {

        SharedPreferences sharedPreferences2 = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String Language = sharedPreferences2.getString("myLanguage", "");
        setLocale(Language);

    }
}