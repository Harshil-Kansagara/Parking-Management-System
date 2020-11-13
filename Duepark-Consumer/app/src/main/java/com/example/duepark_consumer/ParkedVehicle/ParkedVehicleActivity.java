package com.example.duepark_consumer.ParkedVehicle;

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

import com.example.duepark_consumer.Adapter.ViewPagerAdapter;
import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.HomeActivity;
import com.example.duepark_consumer.ParkedVehicle.Add.AddNumberPlateVehicleActivity;
import com.example.duepark_consumer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.HashMap;

public class ParkedVehicleActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private MaterialSearchView searchView;
    private CarVehicleListFragment carVehicleListFragment;
    private BikeVehicleListFragment bikeVehicleListFragment;
    private DownloadParkedVehicleHistoryFragment downloadVehicleDetailFragment;
    private int tabPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parked_vehicle);

        carVehicleListFragment = new CarVehicleListFragment();
        bikeVehicleListFragment = new BikeVehicleListFragment();
        downloadVehicleDetailFragment = new DownloadParkedVehicleHistoryFragment();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        SessionManagerHelper sessionManagerHelper = new SessionManagerHelper(this);
        HashMap<String, String> employeeDetail = sessionManagerHelper.getEmployeeDetails();

        searchView = findViewById(R.id.searchView);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FloatingActionButton addVehicleBtn = findViewById(R.id.addVehicleBtn);
        if(employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("SuperAdmin")){
            addVehicleBtn.setVisibility(View.GONE);
        }
        addVehicleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // CHange to Add number plate vehicle
                /*Intent i = new Intent(view.getContext(), ActivatedParkedVehicleActivity.class);
                i.putExtra("ParkedVehicleId", "8");
                startActivity(i);*/

                startActivity(new Intent(view.getContext(), AddNumberPlateVehicleActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        initViewPager();
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
        search();

    }

    private void search(){
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(tabPosition == 0){
                    carVehicleListFragment.getParkedVehicleSearchString(query);
                }
                else if(tabPosition == 1){
                    bikeVehicleListFragment.getParkedVehicleSearchString(query);
                }
                else{

                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(tabPosition == 0){
                    carVehicleListFragment.getParkedVehicleSearchString(newText);
                }
                else if(tabPosition == 1){
                    bikeVehicleListFragment.getParkedVehicleSearchString(newText);
                }
                else{

                }
                return true;
            }
        });
    }

    private void initViewPager(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(carVehicleListFragment, "CAR");
        adapter.addFragment(bikeVehicleListFragment, "BIKE");
        adapter.addFragment(downloadVehicleDetailFragment, "DOWNLOAD");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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