package com.example.duepark_admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_admin.Model.LocationEmployeeCount;
import com.example.duepark_admin.R;

import java.text.DecimalFormat;
import java.util.List;

public class LocationEmployeeCountAdapter extends RecyclerView.Adapter<LocationEmployeeCountAdapter.LocationEmployeeCountViewHolder> {
    private Context ctx;
    private List<LocationEmployeeCount> locationEmployeeCountList;

    public LocationEmployeeCountAdapter(Context ctx, List<LocationEmployeeCount> locationEmployeeCountList) {
        this.ctx = ctx;
        this.locationEmployeeCountList = locationEmployeeCountList;
    }

    @NonNull
    @Override
    public LocationEmployeeCountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_location_partner_list, parent, false);
        return new LocationEmployeeCountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationEmployeeCountViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("000");
        String parkingid = locationEmployeeCountList.get(position).getParkingAcronym() + df.format(Integer.parseInt(locationEmployeeCountList.get(position).getGeneratedParkingId()));
        char char_locationId = (char) (Integer.parseInt(locationEmployeeCountList.get(position).getGeneratedLocationId()) + 'A' - 1);
        String location_id = parkingid + char_locationId;
        holder.locationName.setText(locationEmployeeCountList.get(position).getLocationName());
        holder.employeesCount.setText(location_id+" | Manager:"+locationEmployeeCountList.get(position).getLocationManagerCount()+" | Valet:"+locationEmployeeCountList.get(position).getLocationValetCount());
    }

    @Override
    public int getItemCount() {
        return locationEmployeeCountList.size();
    }


    class LocationEmployeeCountViewHolder extends RecyclerView.ViewHolder{
        private TextView locationName, employeesCount;

        public LocationEmployeeCountViewHolder(@NonNull View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.locationName);
            employeesCount = itemView.findViewById(R.id.employeesCount);
        }
    }
}
