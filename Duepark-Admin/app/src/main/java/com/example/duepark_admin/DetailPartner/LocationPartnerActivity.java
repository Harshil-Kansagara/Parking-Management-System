package com.example.duepark_admin.DetailPartner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duepark_admin.Adapter.ViewPagerAdapter;
import com.example.duepark_admin.DetailPartner.Extra.LocationListPartnerActivity;
import com.example.duepark_admin.DetailPartner.Location.ActiveLocationListFragment;
import com.example.duepark_admin.DetailPartner.Location.InactiveLocationListFragment;
import com.example.duepark_admin.HomeActivity;
import com.example.duepark_admin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class LocationPartnerActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String parkingid, parkingName;
    private TextView parking_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_partner);

        sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.remove("Data");
        editor.apply();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            parkingid = bundle.getString("Id");
            parkingName = bundle.getString("ParkingName");
        }

        if(parkingName == null) {
            retrieveData();
        }
        if(parkingName!=null){
            storeData();
        }

        parking_name = findViewById(R.id.parking_name);
        parking_name.setText(parkingName);

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
                Intent homeIntent = new Intent(view.getContext(), HomeActivity.class);
                storeData();
                homeIntent.putExtra("id", parkingid);
                homeIntent.putExtra("add","add");
                startActivity(homeIntent);
                finish();
            }
        });

        initViewPager();
    }

    private void retrieveData(){
        if(sharedPreferences.contains("ParkingName")){
            parkingName = sharedPreferences.getString("ParkingName", "");
        }
        if(sharedPreferences.contains("ParkingId")){
            parkingid = sharedPreferences.getString("ParkingId","");
        }
    }

    private void storeData(){
        if(parkingName !=null){
            editor.remove("ParkingName");
            editor.apply();
            editor.putString("ParkingName", parkingName);
            editor.apply();
        }
        if(parkingid !=null){
            editor.remove("ParkingId");
            editor.apply();
            editor.putString("ParkingId", parkingid);
            editor.apply();
        }
    }

    private void initViewPager(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new ActiveLocationListFragment(parkingid), "ACTIVE");
        adapter.addFragment(new InactiveLocationListFragment(parkingid), "INACTIVE");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        editor.remove("Data");
        editor.apply();
        Intent intent = new Intent(this, DetailPartnerActivity.class);
        /*intent.putExtra("Id", parkingid);
        intent.putExtra("ParkingName", parkingName);*/
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        startActivity(intent);
        finish();
    }
}