package com.example.duepark_consumer.Location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.duepark_consumer.Adapter.ViewPagerAdapter;
import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.HomeActivity;
import com.example.duepark_consumer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Objects;

public class LocationActivity extends AppCompatActivity {

    private SessionManagerHelper sessionManagerHelper;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        sessionManagerHelper = new SessionManagerHelper(this);
        HashMap<String, String> employeeDetail = sessionManagerHelper.getEmployeeDetails();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FloatingActionButton addLocationBtn = findViewById(R.id.addLocationBtn);
        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LocationActivity.this, AddLocationActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        if(employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("Manager")){
            addLocationBtn.setVisibility(View.GONE);
        }

        initViewPager();
    }

    private void initViewPager(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new ActiveLocationListFragment(), "ACTIVE");
        adapter.addFragment(new InactiveLocationListFragment(), "INACTIVE");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}