package com.example.duepark_admin.DetailPartner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duepark_admin.Adapter.ViewPagerAdapter;
import com.example.duepark_admin.DetailPartner.VehicleParkedPartner.BikeVehicleParkedPartnerFragment;
import com.example.duepark_admin.DetailPartner.VehicleParkedPartner.CarVehicleParkedPartnerFragment;
import com.example.duepark_admin.DetailPartner.VehicleParkedPartner.DownloadVehicleParkedPartnerFragment;
import com.example.duepark_admin.R;
import com.google.android.material.tabs.TabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class VehicleParkedPartnerActivity extends AppCompatActivity {

    private String id, parkingname;
    private CarVehicleParkedPartnerFragment carVehicleParkedPartnerFragment;
    private BikeVehicleParkedPartnerFragment bikeVehicleParkedPartnerFragment;
    private  DownloadVehicleParkedPartnerFragment downloadVehicleParkedPartnerFragment;
    private int tabPosition;
    private MaterialSearchView searchView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_parked_partner);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            id = bundle.getString("Id");
            parkingname = bundle.getString("ParkingName");
        }

        carVehicleParkedPartnerFragment = new CarVehicleParkedPartnerFragment(id);
        bikeVehicleParkedPartnerFragment = new BikeVehicleParkedPartnerFragment(id);
        downloadVehicleParkedPartnerFragment = new DownloadVehicleParkedPartnerFragment();
        //downloadVehicleParkedPartnerFragment = new DownloadVehicleParkedPartnerFragment(id);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        searchView = findViewById(R.id.searchView);
        TextView parking_name = findViewById(R.id.parking_name);
        parking_name.setText(parkingname);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(carVehicleParkedPartnerFragment, "CAR");
        adapter.addFragment(bikeVehicleParkedPartnerFragment, "BIKE");
        adapter.addFragment(downloadVehicleParkedPartnerFragment, "DOWNLOAD");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        search();
    }

    private void search(){
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(tabPosition == 0){
                    carVehicleParkedPartnerFragment.getParkedVehicleSearchString(query);
                }
                else if(tabPosition == 1){
                    bikeVehicleParkedPartnerFragment.getParkedVehicleSearchString(query);
                }
                else{

                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(tabPosition == 0){
                    carVehicleParkedPartnerFragment.getParkedVehicleSearchString(newText);
                }
                else if(tabPosition == 1){
                    bikeVehicleParkedPartnerFragment.getParkedVehicleSearchString(newText);
                }
                else{

                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        startActivity(new Intent(VehicleParkedPartnerActivity.this, DetailPartnerActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_view, menu);
        MenuItem item = menu.findItem(R.id.search_button);
        searchView.setMenuItem(item);
        return true;
    }
}
