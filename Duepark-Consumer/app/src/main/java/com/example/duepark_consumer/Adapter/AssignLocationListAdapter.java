package com.example.duepark_consumer.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_consumer.Model.LocationList;
import com.example.duepark_consumer.Model.SelectedLocation;
import com.example.duepark_consumer.R;

import java.util.ArrayList;

public class AssignLocationListAdapter extends RecyclerView.Adapter<AssignLocationListAdapter.AssignLocationListViewHolder> {

    private ArrayList<LocationList> locationLists;
    private RadioButton selected;
    private SelectedLocation selectedLocation;

    public AssignLocationListAdapter(ArrayList<LocationList> locationLists, Context ctx, SelectedLocation selectedLocation) {
        this.locationLists = locationLists;
        this.selectedLocation = selectedLocation;
    }

    @NonNull
    @Override
    public AssignLocationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assign_location_list, parent, false);
        return new AssignLocationListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignLocationListViewHolder holder, int position) {
        holder.locationName.setText(locationLists.get(position).getLocationName());
        if(selectedLocation.getLocationName()!=null){
            for(int i = 0; i<locationLists.size();i++){
                if(selectedLocation.getLocationName().equals(locationLists.get(i).getLocationName())){
                    if(position == i){
                        holder.radioBtn.setChecked(true);
                        selected = holder.radioBtn;
                    }
                }
            }
        }
        holder.radioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected != null){
                    selected.setChecked(false);
                }
                holder.radioBtn.setChecked(true);
                selected = holder.radioBtn;
                selectedLocation.setId(locationLists.get(position).getLocationId());
                selectedLocation.setLocationName(locationLists.get(position).getLocationName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationLists.size();
    }

    class AssignLocationListViewHolder extends RecyclerView.ViewHolder{
        private TextView locationName;
        private RadioButton radioBtn;

        AssignLocationListViewHolder(@NonNull View itemView){
            super(itemView);
            locationName = itemView.findViewById(R.id.locationName);
            radioBtn = itemView.findViewById(R.id.radioBtn);
        }
    }
}
