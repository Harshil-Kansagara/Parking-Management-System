package com.example.duepark_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.duepark_admin.AddParking.ActivatedParkingFragment;
import com.example.duepark_admin.AddParking.AddParkingFragment;
import com.example.duepark_admin.AddParking.AddParkingLocationFragment;
import com.example.duepark_admin.Menu.MenuFragment;
import com.example.duepark_admin.Partner.PartnerFragment;
import com.example.duepark_admin.Service.MyFirebaseInstanceService;
import com.example.duepark_admin.Service.SessionManager;
import com.example.duepark_admin.Vehicle.VehicleFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class HomeActivity extends AppCompatActivity{

    private String add, parkingid, menu, employeeId, employeeName, employeeMobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SessionManager sessionManager = new SessionManager(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            add = bundle.getString("add");
            parkingid = bundle.getString("id");
            menu = bundle.getString("menu");
            employeeId = bundle.getString("employeeId");
            employeeName = bundle.getString("employeeName");
            employeeMobileNumber = bundle.getString("employeeMobileNumber");
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener);

        if(add != null){
            if(add.equals("add")){
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                AddParkingLocationFragment addParkingLocationFragment = new AddParkingLocationFragment();
                Bundle args = new Bundle();
                args.putString("id", parkingid);
                args.putString("Address","null");
                args.putString("Acronym","null");
                args.putString("back","back");
                addParkingLocationFragment.setArguments(args);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, addParkingLocationFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            if(add.equals("parking")){
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                AddParkingFragment addParkingFragment = new AddParkingFragment();
                Bundle args = new Bundle();
                args.putString("employeeId", employeeId);
                args.putString("employeeName", employeeName);
                args.putString("employeeMobileNumber", employeeMobileNumber);
                args.putString("Address", "null");
                args.putString("Acronym", "null");
                args.putString("City", "null");
                addParkingFragment.setArguments(args);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, addParkingFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
        else if(menu!=null){
            bottomNavigationView.getMenu().getItem(3).setChecked(true);
            Fragment menuFragment = new MenuFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, menuFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PartnerFragment()).commit();
        }

        MyFirebaseInstanceService myFirebaseInstanceService = new MyFirebaseInstanceService(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                myFirebaseInstanceService.onNewToken(instanceIdResult.getToken());
            }
        });
        myFirebaseInstanceService.startBackgroundService();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;
            if (itemId == R.id.navigation_partner) {
                selectedFragment = new PartnerFragment();
            } else if (itemId == R.id.navigation_vehicle) {
                selectedFragment = new VehicleFragment();
            } else if (itemId == R.id.navigation_add_parking) {
                selectedFragment = new AddParkingFragment();
                //selectedFragment = new ActivatedParkingFragment();
            } else if (itemId == R.id.navigation_menu) {
                selectedFragment = new MenuFragment();
            }
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };
}
