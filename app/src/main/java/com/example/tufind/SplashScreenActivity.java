package com.example.tufind;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class SplashScreenActivity extends AppCompatActivity {

    private static  int SPLASH_TIME_OUT = 5000;
    Animation firstAnimation,secondAnimation,thirdAnimation,fourthAnimation  ;
    private TextView t,u,find,detail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        firstAnimation = AnimationUtils.loadAnimation(this,R.anim.firstscreen_animation);
        secondAnimation = AnimationUtils.loadAnimation(this,R.anim.secondscreen_animation);
        thirdAnimation = AnimationUtils.loadAnimation(this,R.anim.thirdscreen_animation);
        fourthAnimation = AnimationUtils.loadAnimation(this,R.anim.fourthscreen_animation);

        t = findViewById(R.id.first);
        u = findViewById(R.id.two);
        find = findViewById(R.id.three);
        detail = findViewById(R.id.four);

        t.startAnimation(firstAnimation);
        u.startAnimation(secondAnimation);
        find.startAnimation(thirdAnimation);
        detail.startAnimation(fourthAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME_OUT);

    }
}
