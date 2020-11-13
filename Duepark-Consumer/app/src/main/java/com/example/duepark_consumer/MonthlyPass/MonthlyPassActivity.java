package com.example.duepark_consumer.MonthlyPass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.duepark_consumer.Adapter.ViewPagerAdapter;
import com.example.duepark_consumer.HomeActivity;
import com.example.duepark_consumer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class MonthlyPassActivity extends AppCompatActivity {

    private static final String TAG = "MonthlyPassActivity";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private MaterialSearchView searchView;
    private int tabPosition;
    private ValidMonthlyPassListFragment validMonthlyPassListFragment;
    private ExpireMonthlyPassListFragment expireMonthlyPassListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_pass);

        validMonthlyPassListFragment = new ValidMonthlyPassListFragment();
        expireMonthlyPassListFragment = new ExpireMonthlyPassListFragment();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        searchView = findViewById(R.id.searchView);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FloatingActionButton addVehicleBtn = findViewById(R.id.addVehicleBtn);
        addVehicleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), AddMonthlyPassActivity.class));
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
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(tabPosition == 0){
                    validMonthlyPassListFragment.getMonthlyPassSearchString(query);
                }
                else{
                    expireMonthlyPassListFragment.getMonthlyPassSearchString(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(tabPosition == 0){
                    validMonthlyPassListFragment.getMonthlyPassSearchString(newText);
                }
                else{
                    expireMonthlyPassListFragment.getMonthlyPassSearchString(newText);
                }
                return true;
            }
        });
        searchView.clearFocus();
    }

    private void initViewPager(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(validMonthlyPassListFragment, "VALID PASS");
        adapter.addFragment(expireMonthlyPassListFragment, "EXPIRE PASS");

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