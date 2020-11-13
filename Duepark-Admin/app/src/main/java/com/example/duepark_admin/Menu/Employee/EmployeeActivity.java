package com.example.duepark_admin.Menu.Employee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.duepark_admin.Adapter.ViewPagerAdapter;
import com.example.duepark_admin.HomeActivity;
import com.example.duepark_admin.R;
import com.example.duepark_admin.Service.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Objects;

public class EmployeeActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        sessionManager = new SessionManager(this);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuIntent = new Intent(EmployeeActivity.this, HomeActivity.class);
                menuIntent.putExtra("menu","menu");
                startActivity(menuIntent);
                finish();
            }
        });

        FloatingActionButton addEmployeeBtn = findViewById(R.id.addEmployeeBtn);
        addEmployeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addEmployeeIntent = new Intent(EmployeeActivity.this, AddEmployeeActivity.class);
                /*Intent addEmployeeIntent = new Intent(EmployeeActivity.this, ActivatedEmployeeActivity.class);
                addEmployeeIntent.putExtra("userid","3");*/
                startActivity(addEmployeeIntent);
                finish();
            }
        });

        initViewPager();
    }

    private void initViewPager(){
        HashMap<String, String> user = sessionManager.getUserDetails();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if(Objects.equals(user.get(sessionManager.EmployeeRole), "SuperAdmin")){
            adapter.addFragment(new AdminEmployeeListFragment(), "ADMIN");
            adapter.addFragment(new ManagerEmployeeListFragment(), "MANAGER");
            adapter.addFragment(new SaleEmployeeListFragment(), "SALE");
        }
        else if(Objects.equals(user.get(sessionManager.EmployeeRole), "Admin")){
            adapter.addFragment(new ManagerEmployeeListFragment(), "MANAGER");
            adapter.addFragment(new SaleEmployeeListFragment(), "SALE");
        }
        else if(Objects.equals(user.get(sessionManager.EmployeeRole), "Manager")){
            adapter.addFragment(new SaleEmployeeListFragment(), "SALE");
        }

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
