package com.example.duepark_consumer.Valet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.duepark_consumer.R;
import com.example.duepark_consumer.Valet.Menu.MenuFragment;
import com.example.duepark_consumer.Valet.Pass.MonthlyPassFragment;
import com.example.duepark_consumer.Valet.ReleasedVehicle.ReleaseVehicleFragment;
import com.example.duepark_consumer.Valet.VehicleParked.AddNumberPlateVehicleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ValetHomeActivity extends AppCompatActivity {

    private boolean isValetMonthlyPass = false, isValetReleasedVehicle = false, isValetMenu = false;
    private int startingPosition = 0;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valet_home);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            isValetMonthlyPass = bundle.getBoolean("IsValetMonthlyPass");
            isValetReleasedVehicle = bundle.getBoolean("IsValetReleasedVehicle");
            isValetMenu = bundle.getBoolean("IsValetMenu");
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener);

        if(isValetReleasedVehicle){
            bottomNavigationView.getMenu().getItem(1).setChecked(true);
            ReleaseVehicleFragment releaseVehicleFragment = new ReleaseVehicleFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            fragmentTransaction.replace(R.id.fragment_container, releaseVehicleFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        else if(isValetMonthlyPass){
            bottomNavigationView.getMenu().getItem(2).setChecked(true);
            MonthlyPassFragment monthlyPassFragment = new MonthlyPassFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            fragmentTransaction.replace(R.id.fragment_container, monthlyPassFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        else if(isValetMenu){
            bottomNavigationView.getMenu().getItem(3).setChecked(true);
            MenuFragment menuFragment = new MenuFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            fragmentTransaction.replace(R.id.fragment_container, menuFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        else {
            loadFragment(new AddNumberPlateVehicleFragment(),0);
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddNumberPlateVehicleFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();
            int newPosition = 0;
            Fragment selectedFragment = null;
            if (itemId == R.id.navigation_park) {
                selectedFragment = new AddNumberPlateVehicleFragment();
                newPosition = 1;
            } else if (itemId == R.id.navigation_release) {
                selectedFragment = new ReleaseVehicleFragment();
                newPosition = 2;
            } else if (itemId == R.id.navigation_add_pass) {
                selectedFragment = new MonthlyPassFragment();
                newPosition = 3;
            } else if (itemId == R.id.navigation_menu) {
                selectedFragment = new MenuFragment();
                newPosition = 4;
            }
            //getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragment_container, selectedFragment).commit();
            //return true;
            return loadFragment(selectedFragment, newPosition);
        }
    };

    private boolean loadFragment(Fragment fragment, int newPosition){
        if(fragment != null){
            if(newPosition == 0){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddNumberPlateVehicleFragment()).commit();
            }
            else if(startingPosition > newPosition){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();
            }
            else if(startingPosition < newPosition){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();
            }
            startingPosition = newPosition;
            return true;
        }
        return  false;
    }

    @Override
    public void onBackPressed() {
        int selectedItemId = bottomNavigationView.getSelectedItemId();
        if(R.id.navigation_park != selectedItemId){
            loadFragment(new AddNumberPlateVehicleFragment(), 1);
            bottomNavigationView.setSelectedItemId(R.id.navigation_park);
        }
    }
}