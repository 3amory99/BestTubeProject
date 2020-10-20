package com.androidproject.besttube.vip.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.androidproject.besttube.vip.R;
import com.androidproject.besttube.vip.main.MainActivity;
import com.androidproject.besttube.vip.signUp.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class SplashActivity extends AppCompatActivity {

    public static int SPLASH_TIMER = 2500;
    CircleImageView logoImg;
    ConstraintLayout constraintLayout;
    Animation logoAnim, layoutAnim;
    FirebaseAuth auth;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        initiation();
        logoImg.startAnimation(logoAnim);
        constraintLayout.startAnimation(layoutAnim);

    }

    private void initiation() {
        logoImg = findViewById(R.id.logo_image);
        constraintLayout = findViewById(R.id.constraint_layout);
        logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
        layoutAnim = AnimationUtils.loadAnimation(this, R.anim.splash_gradient);


    }

    @Override
    protected void onStart() {
        super.onStart();

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @SuppressLint("WrongConstant")
            @Override
            public void run() {
                if (currentUser != null) {

                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
                    finish();
                }
            }
        }, SPLASH_TIMER);

    }
}