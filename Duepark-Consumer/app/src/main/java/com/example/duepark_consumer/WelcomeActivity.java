package com.example.duepark_consumer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WelcomeActivity extends AppCompatActivity {

    private SessionManagerHelper sessionManagerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        sessionManagerHelper = new SessionManagerHelper(this);
        FloatingActionButton welcomeBtn = findViewById(R.id.welcomeBtn);
        welcomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManagerHelper.logoutEmployee();
            }
        });
    }

    @Override
    public void onBackPressed() {
        sessionManagerHelper.logoutEmployee();
    }
}