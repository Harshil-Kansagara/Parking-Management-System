package com.example.duepark_admin.DetailPartner.VehicleParkedPartner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.duepark_admin.R;

public class DownloadVehicleParkedPartnerFragment extends Fragment {

    private View view;

    public DownloadVehicleParkedPartnerFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_download_vehicle_parked_partner, container, false);
        return view;
    }
}
