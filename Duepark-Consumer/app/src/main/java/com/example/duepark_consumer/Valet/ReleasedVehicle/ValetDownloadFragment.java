package com.example.duepark_consumer.Valet.ReleasedVehicle;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.duepark_consumer.History.HistoryActivity;
import com.example.duepark_consumer.R;

public class ValetDownloadFragment extends Fragment {

    public ValetDownloadFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_parked_vehicle_history, container, false);

        Button downloadHistoryBtn = view.findViewById(R.id.downloadHistoryBtn);
        downloadHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), HistoryActivity.class);
                i.putExtra("IsValetParkedVehicleHistory", true);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                getActivity().finish();
            }
        });

        return view;
    }
}
