package com.example.duepark_admin.Partner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.duepark_admin.Adapter.ViewPagerAdapter;
import com.example.duepark_admin.R;
import com.google.android.material.tabs.TabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;


public class PartnerFragment extends Fragment {

    private View view;
    private int tabPosition;
    private ActivePartnerFragment activePartnerFragment;
    private InactivePartnerFragment inactivePartnerFragment;
    private RequestPartnerFragment requestPartnerFragment;
    private MaterialSearchView searchView;


    public PartnerFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_partner, container, false);
        /*setHasOptionsMenu(true);
        myToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(myToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");*/

        activePartnerFragment = new ActivePartnerFragment();
        inactivePartnerFragment = new InactivePartnerFragment();
        requestPartnerFragment = new RequestPartnerFragment();

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);

        //searchView = view.findViewById(R.id.search_view);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(activePartnerFragment, "ACTIVE");
        adapter.addFragment(inactivePartnerFragment, "INACTIVE");
        adapter.addFragment(requestPartnerFragment, "REQUESTS");

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

        //search();
        return view;
    }

/*    private void search(){
        //Fragment fragment;
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("Search Partner", query);
                //activePartnerFragment.search_query(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Log.d("Search Partner", searchQuery);
                activePartnerFragment.search_query(newText);

                return false;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_view, menu);
        MenuItem item = menu.findItem(R.id.search_button);
        searchView.setMenuItem(item);
    }*/

}
