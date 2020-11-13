package com.example.duepark_admin.Vehicle;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.duepark_admin.Adapter.ViewPagerAdapter;
import com.example.duepark_admin.HomeActivity;
import com.example.duepark_admin.R;
import com.google.android.material.tabs.TabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.Objects;

public class VehicleFragment extends Fragment {

    private Toolbar myToolbar;
    private CarVehicleFragment carVehicleFragment;
    private BikeVehicleFragment bikeVehicleFragment;
    private DownloadVehicleFragment downloadVehicleFragment;
    private int tabPosition;
    private MaterialSearchView searchView;

    public VehicleFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
        setHasOptionsMenu(true);

        carVehicleFragment = new CarVehicleFragment();
        bikeVehicleFragment = new BikeVehicleFragment();
        downloadVehicleFragment = new DownloadVehicleFragment();

        searchView = view.findViewById(R.id.search_view);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(carVehicleFragment, "CAR");
        adapter.addFragment(bikeVehicleFragment, "BIKE");
        adapter.addFragment(downloadVehicleFragment, "DOWNLOAD");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

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

        return view;
    }

    private void search(){
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(tabPosition == 0){
                    carVehicleFragment.getVehicleSearchString(query);
                }
                else if(tabPosition == 1){
                    bikeVehicleFragment.getVehicleSearchString(query);
                }
                else{

                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(tabPosition == 0){
                    carVehicleFragment.getVehicleSearchString(newText);
                }
                else if(tabPosition == 1){
                    bikeVehicleFragment.getVehicleSearchString(newText);
                }
                else{

                }
                return true;
            }
        });
        searchView.clearFocus();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_view, menu);
        MenuItem item = menu.findItem(R.id.search_button);
        searchView.setMenuItem(item);
    }

    private void backToHome() {
        startActivity(new Intent(getContext(), HomeActivity.class));
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        Objects.requireNonNull(getActivity()).finish();
    }
}
