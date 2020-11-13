package com.example.duepark_consumer.Employee;

import androidx.annotation.NonNull;
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
import com.example.duepark_consumer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.HashMap;
import java.util.Objects;

public class EmployeeActivity extends AppCompatActivity {

    private SessionManagerHelper sessionManagerHelper;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private MaterialSearchView searchView;
    private AdminEmployeeListFragment adminEmployeeListFragment;
    private ManagerEmployeeListFragment managerEmployeeListFragment;
    private ValetEmployeeListFragment valetEmployeeListFragment;
    private InactiveEmployeeListFragment inactiveEmployeeListFragment;
    private int tabPosition;
    private HashMap<String, String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        adminEmployeeListFragment = new AdminEmployeeListFragment();
        managerEmployeeListFragment = new ManagerEmployeeListFragment();
        valetEmployeeListFragment = new ValetEmployeeListFragment();
        inactiveEmployeeListFragment = new InactiveEmployeeListFragment();

        sessionManagerHelper = new SessionManagerHelper(this);
        user = sessionManagerHelper.getEmployeeDetails();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        searchView = findViewById(R.id.searchView);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(4);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FloatingActionButton addEmployeeBtn = findViewById(R.id.addEmployeeBtn);
        addEmployeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmployeeActivity.this, AddEmployeeActivity.class));
                /*Intent i = new Intent(EmployeeActivity.this, ActivatedEmployeeActivity.class);
                i.putExtra("EmployeeId",3);
                startActivity(i);*/
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
                if(Objects.equals(user.get(sessionManagerHelper.EmployeeRole), "SuperAdmin")){
                    if(tabPosition == 0){
                        adminEmployeeListFragment.getEmployeeSearchString(query);
                    }
                    else if(tabPosition == 1){
                        managerEmployeeListFragment.getEmployeeSearchString(query);
                    }
                    else if(tabPosition == 2) {
                        valetEmployeeListFragment.getEmployeeSearchString(query);
                    }
                    else{
                        inactiveEmployeeListFragment.getEmployeeSearchString(query);
                    }
                }
                else if(Objects.equals(user.get(sessionManagerHelper.EmployeeRole), "Admin")){
                    if(tabPosition == 0){
                        managerEmployeeListFragment.getEmployeeSearchString(query);
                    }
                    else if(tabPosition == 1) {
                        valetEmployeeListFragment.getEmployeeSearchString(query);
                    }
                    else{
                        inactiveEmployeeListFragment.getEmployeeSearchString(query);
                    }
                }
                else if(Objects.equals(user.get(sessionManagerHelper.EmployeeRole), "Manager")){
                    valetEmployeeListFragment.getEmployeeSearchString(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(Objects.equals(user.get(sessionManagerHelper.EmployeeRole), "SuperAdmin")){
                    if(tabPosition == 0){
                        adminEmployeeListFragment.getEmployeeSearchString(newText);
                    }
                    else if(tabPosition == 1){
                        managerEmployeeListFragment.getEmployeeSearchString(newText);
                    }
                    else if(tabPosition == 2) {
                        valetEmployeeListFragment.getEmployeeSearchString(newText);
                    }
                    else{
                        inactiveEmployeeListFragment.getEmployeeSearchString(newText);
                    }
                }
                else if(Objects.equals(user.get(sessionManagerHelper.EmployeeRole), "Admin")){
                    if(tabPosition == 0){
                        managerEmployeeListFragment.getEmployeeSearchString(newText);
                    }
                    else if(tabPosition == 1) {
                        valetEmployeeListFragment.getEmployeeSearchString(newText);
                    }
                    else{
                        inactiveEmployeeListFragment.getEmployeeSearchString(newText);
                    }
                }
                else if(Objects.equals(user.get(sessionManagerHelper.EmployeeRole), "Manager")){
                    valetEmployeeListFragment.getEmployeeSearchString(newText);
                }
                return true;
            }
        });
        searchView.clearFocus();
    }

    private void initViewPager(){

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if(Objects.equals(user.get(sessionManagerHelper.EmployeeRole), "SuperAdmin")){
            adapter.addFragment(adminEmployeeListFragment, "ADMIN");
            adapter.addFragment(managerEmployeeListFragment, "MANAGER");
            adapter.addFragment(valetEmployeeListFragment, "VALET");
            adapter.addFragment(inactiveEmployeeListFragment, "INACTIVE");
        }
        else if(Objects.equals(user.get(sessionManagerHelper.EmployeeRole), "Admin")){
            adapter.addFragment(managerEmployeeListFragment, "MANAGER");
            adapter.addFragment(valetEmployeeListFragment, "VALET");
            adapter.addFragment(inactiveEmployeeListFragment, "INACTIVE");
        }
        else if(Objects.equals(user.get(sessionManagerHelper.EmployeeRole), "Manager")){
            adapter.addFragment(valetEmployeeListFragment, "VALET");
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_view, menu);
        MenuItem item = menu.findItem(R.id.search_button);
        searchView.setMenuItem(item);
        return true;
    }
}