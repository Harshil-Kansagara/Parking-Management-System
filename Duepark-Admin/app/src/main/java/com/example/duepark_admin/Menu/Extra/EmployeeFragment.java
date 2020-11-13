package com.example.duepark_admin.Menu.Extra;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.duepark_admin.Adapter.ViewPagerAdapter;
import com.example.duepark_admin.Menu.Employee.AdminEmployeeListFragment;
import com.example.duepark_admin.Menu.Employee.ManagerEmployeeListFragment;
import com.example.duepark_admin.Menu.Employee.SaleEmployeeListFragment;
import com.example.duepark_admin.Menu.MenuFragment;
import com.example.duepark_admin.R;
import com.example.duepark_admin.Service.SessionManager;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Objects;

public class EmployeeFragment extends Fragment {

    private View view;
    private SessionManager sessionManager;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public EmployeeFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_employee, container, false);
        sessionManager = new SessionManager(getContext());

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        ImageView backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment menuFragment = new MenuFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, menuFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        initViewPager();

        return view;
    }

    private void initViewPager(){
        HashMap<String, String> user = sessionManager.getUserDetails();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

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
