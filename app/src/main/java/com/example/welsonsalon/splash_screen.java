package com.example.welsonsalon;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splash_screen extends AppCompatActivity {

        private static final int SPLASH_SCREEN = 4000;
        //Variables
        Animation topAnim , bottomAnim;
        ImageView image;
        TextView logo,slogan;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_splash_screen);


            // Animation calling under project
            topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
            bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

            // hooks
            image = findViewById(R.id.App_Logo);
            logo = findViewById(R.id.logo_text);
            slogan = findViewById(R.id.slogan_text);


            // for setting animations
            image.setAnimation(topAnim);
            logo.setAnimation(topAnim);
            slogan.setAnimation(bottomAnim);


            //Adding Intent to go on the next screen
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(splash_screen.this, LoginPage.class);

                    // animation of 2 slides splash and login screen
                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(image, "logo_salon");
                    pairs[1] = new Pair<View, String>(logo, "logo_head");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(splash_screen.this, pairs);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        startActivity(new Intent(splash_screen.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    } else {
                        startActivity(intent, options.toBundle());
                        finish();
                    }
                }
            },SPLASH_SCREEN);



        }
}