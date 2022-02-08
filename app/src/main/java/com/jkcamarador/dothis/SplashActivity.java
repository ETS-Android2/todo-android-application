package com.jkcamarador.dothis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    //ImageView imgBkg, logo;
    //LottieAnimationView lottieAnimationView;
    //Boolean loop=false;
    Integer SPLASH_DELAYED=3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //imgBkg = findViewById(R.id.bkgImag);
       // logo = findViewById(R.id.logo);
        //lottieAnimationView = findViewById(R.id.lottie);

       // imgBkg.animate().translationY(-1600).setDuration(1000).setStartDelay(4000);
       // logo.animate().translationY(1300).setDuration(1000).setStartDelay(4000);
       // lottieAnimationView.animate().translationY(1400).setDuration(1000).setStartDelay(4000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean firstRun = getSharedPreferences("preferences", MODE_PRIVATE).getBoolean("firstrun", true);
                if(firstRun){
                    //set the firstrun to false so the next run can see it.

                    getSharedPreferences("preferences", MODE_PRIVATE).edit().putBoolean("firstrun", false).commit();
                    //Toast.makeText(getApplicationContext(), "First time to open the app", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Toast.makeText(getApplicationContext(), "Not the first time to open it", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_DELAYED);
    }
}