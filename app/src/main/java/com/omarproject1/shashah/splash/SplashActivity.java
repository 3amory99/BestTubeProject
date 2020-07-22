package com.omarproject1.shashah.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.omarproject1.shashah.R;
import com.omarproject1.shashah.intro.IntroActivity;
import com.omarproject1.shashah.main.MainActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class SplashActivity extends AppCompatActivity {

    public static int SPLASH_TIMER = 3000;
    CircleImageView logoImg;
    ConstraintLayout constraintLayout;
    Animation logoAnim, layoutAnim;
//    SharedPreferences onBoarding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        initiation();

        new Handler().postDelayed(new Runnable() {
            @SuppressLint("WrongConstant")
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
//                onBoarding=getSharedPreferences("IntroScreen",MODE_PRIVATE);
//                boolean isFirstTime=onBoarding.getBoolean("firstTime",true);
//                if (isFirstTime){
//                    SharedPreferences.Editor editor=onBoarding.edit();
//                    editor.putBoolean("firstTime",false);
//                    editor.commit();
//                    startActivity(new Intent(SplashActivity.this, IntroActivity.class));
//                    finish();
//                }
//                else {
//                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                    finish();
//                }
            }
        }, SPLASH_TIMER);

        logoImg.startAnimation(logoAnim);
        constraintLayout.startAnimation(layoutAnim);

    }

    private void initiation() {
        logoImg = findViewById(R.id.logo_image);
        constraintLayout = findViewById(R.id.constraint_layout);
        logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
        layoutAnim = AnimationUtils.loadAnimation(this, R.anim.splash_gradient);


    }
}