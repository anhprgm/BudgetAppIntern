package com.vvtvofficial.quanlychitieu.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.vvtvofficial.quanlychitieu.R;

public class SplashScreenActivity extends AppCompatActivity {
    public static int SPLASH = 2000;
    Animation animation;
    private ImageView logoSplashScreen;
    private TextView AppName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        binding();
        logoSplashScreen.setAnimation(animation);
        AppName.setAnimation(animation);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH);
    }
    private void binding() {
        logoSplashScreen = findViewById(R.id.logoSplashScreen);
        AppName = findViewById(R.id.AppName);
    }
}