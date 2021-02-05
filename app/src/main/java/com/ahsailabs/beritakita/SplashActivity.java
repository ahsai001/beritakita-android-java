package com.ahsailabs.beritakita;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //cara 1 : Handler delay
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //TODO : set destination after time is up
                openMainPage();
            }
        }, 3000);*/

        //cara 2 :
        new CountDownTimer(3000, 1000){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                //TODO : set destination after time is up
                openMainPage();
            }
        }.start();
    }

    private void openMainPage() {
        MainActivity.start(SplashActivity.this);
        finish();
    }
}