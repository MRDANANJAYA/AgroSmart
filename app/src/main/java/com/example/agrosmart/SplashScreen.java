package com.example.agrosmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.agrosmart.login.LoginActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {
    Timer timer;
    Animation topAnim, botAnim, opAnim, scaleAnim;
    ImageView logo, slogon, agrotext, leaf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        View decorView = getWindow().getDecorView();

        //animation

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        botAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        opAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_animation);
        scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale_animation);


        logo =findViewById(R.id.Logo);
        slogon =findViewById(R.id.Slogan);
        agrotext =findViewById(R.id.agroText);
        leaf =findViewById(R.id.BackgroundLeaf);

        logo.setAnimation(topAnim);
        agrotext.setAnimation(topAnim);
        slogon.setAnimation(botAnim);
        leaf.setAnimation(opAnim);



        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);




    }

}