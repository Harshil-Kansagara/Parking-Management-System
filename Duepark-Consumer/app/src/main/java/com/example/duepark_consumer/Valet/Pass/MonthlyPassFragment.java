package com.example.duepark_consumer.Valet.Pass;

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

import com.example.duepark_consumer.Adapter.ViewPagerAdapter;
import com.example.duepark_consumer.MonthlyPass.AddMonthlyPassActivity;
import com.example.duepark_consumer.MonthlyPass.ExpireMonthlyPassListFragment;
import com.example.duepark_consumer.MonthlyPass.ValidMonthlyPassListFragment;
import com.example.duepark_consumer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class MonthlyPassFragment extends Fragment {

    private static final String TAG = "MonthlyPassActivity";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private MaterialSearchView searchView;
    private int tabPosition;
    private ValidMonthlyPassListFragment validMonthlyPassListFragment;
    private ExpireMonthlyPassListFragment expireMonthlyPassListFragment;

    public MonthlyPassFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_pass, container, false);

        validMonthlyPassListFragment = new ValidMonthlyPassListFragment(true);
        expireMonthlyPassListFragment = new ExpireMonthlyPassListFragment(true);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
        setHasOptionsMenu(true);

        searchView = view.findViewById(R.id.searchView);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);

        FloatingActionButton addVehicleBtn = view.findViewById(R.id.addVehicleBtn);
        addVehicleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AddMonthlyPassActivity.class);
                i.putExtra("IsValet", true);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                getActivity().finish();
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

        return view;
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
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        adapter.addFragment(validMonthlyPassListFragment, "VALID PASS");
        adapter.addFragment(expireMonthlyPassListFragment, "EXPIRE PASS");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_view, menu);
        MenuItem item = menu.findItem(R.id.search_button);
        searchView.setMenuItem(item);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_view, menu);
        MenuItem item = menu.findItem(R.id.search_button);
        searchView.setMenuItem(item);
        return true;
    }*/
}
