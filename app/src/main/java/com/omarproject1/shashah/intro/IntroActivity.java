package com.omarproject1.shashah.intro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.omarproject1.shashah.R;
import com.omarproject1.shashah.main.MainActivity;
import com.omarproject1.shashah.model.IntroScreenItem;
import com.omarproject1.shashah.splash.SplashActivity;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager introSlider;
    TabLayout tabLayout;
    Button nextBtn, skipBtn, getStartBtn;
    int position = 0;
    private Animation getStartAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (returnPreference()) {
            startActivity(new Intent(getApplicationContext(), SplashActivity.class));
            finish();
        }
        setContentView(R.layout.activity_intro);

        initiation();
//        getSupportActionBar().hide();

        final List<IntroScreenItem> myIntroList = new ArrayList<>();
        myIntroList.add(new IntroScreenItem(getResources().getString(R.string.mono3at_intro_title),getResources().getString(R.string.mono3at_intro_description) , R.drawable.ic_intro_mono3at));
        myIntroList.add(new IntroScreenItem(getResources().getString(R.string.children_intro_title), getResources().getString(R.string.children_intro_description), R.drawable.ic_intro_children));
        myIntroList.add(new IntroScreenItem(getResources().getString(R.string.games_intro_title), getResources().getString(R.string.games_intro_description), R.drawable.ic_intro_games));
        myIntroList.add(new IntroScreenItem(getResources().getString(R.string.religious_intro_title), getResources().getString(R.string.religious_intro_description), R.drawable.ic_intro_religious));
        myIntroList.add(new IntroScreenItem(getResources().getString(R.string.love_intro_title), getResources().getString(R.string.love_intro_description), R.drawable.ic_intro_love));

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = introSlider.getCurrentItem();
                if (position < myIntroList.size()) {
                    position++;
                    introSlider.setCurrentItem(position);
                }
                if (position == myIntroList.size() - 1) {
                    getStartScreen();
                }
            }
        });
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                savePreference();
                finish();
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == myIntroList.size() - 1) {
                    getStartScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == myIntroList.size() - 2) {

                }
            }
        });

        getStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                savePreference();
                finish();

            }
        });

        IntroAdapter introAdapter = new IntroAdapter(this,myIntroList);
        introSlider.setAdapter(introAdapter);
        tabLayout.setupWithViewPager(introSlider);


    }

    private void initiation() {
        tabLayout = findViewById(R.id.tablayout_indicator);
        introSlider = findViewById(R.id.intro_slides);
        nextBtn = findViewById(R.id.next_btn);
        skipBtn = findViewById(R.id.skip_btn);
        getStartBtn = findViewById(R.id.getStart_Btn);

    }

    private void getStartScreen() {
        getStartBtn.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        skipBtn.setVisibility(View.INVISIBLE);
        tabLayout.setVisibility(View.INVISIBLE);
        getStartAnim = AnimationUtils.loadAnimation(this, R.anim.get_start_button_anim);
        getStartBtn.startAnimation(getStartAnim);
    }

    private void savePreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("checkIntro", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isChecked", true);
        if (!editor.commit()) {
            editor.commit();
        }
    }

    private boolean returnPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("checkIntro", MODE_PRIVATE);
        Boolean isIntroOpenedBefore = sharedPreferences.getBoolean("isChecked", false);
        return isIntroOpenedBefore;
    }



}