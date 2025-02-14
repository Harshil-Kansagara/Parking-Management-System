package com.example.duepark_consumer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread timer = new Thread()
        {
            @Override
            public void run( )
            {
                try{
                    sleep(1000);

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        timer.start();
    }
}
