package com.example.duepark_consumer.Valet.ReleasedVehicle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.duepark_consumer.Adapter.ViewPagerAdapter;
import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.R;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;

public class ReleaseVehicleFragment extends Fragment {

    private int tabPosition;
    private SessionManagerHelper sessionManagerHelper;
    private HashMap<String, String> employeeDetail;

    public ReleaseVehicleFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_released_vehicle, container, false);

        sessionManagerHelper = new SessionManagerHelper(getContext());
        employeeDetail = sessionManagerHelper.getEmployeeDetails();

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);

        //searchView = view.findViewById(R.id.search_view);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        if(employeeDetail.get(SessionManagerHelper.EmployeeVehicleType).equals("Car")){
            adapter.addFragment(new ValetCarVehicleListFragment(), "CAR");
        }
        else if(employeeDetail.get(SessionManagerHelper.EmployeeVehicleType).equals("Bike")){
            adapter.addFragment(new ValetBikeVehicleListFragment(), "BIKE");
        }
        else{
            adapter.addFragment(new ValetCarVehicleListFragment(), "CAR");
            adapter.addFragment(new ValetBikeVehicleListFragment(), "BIKE");
        }
        adapter.addFragment(new ValetDownloadFragment(), "DOWNLOAD");

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

        return view;
    }
}
