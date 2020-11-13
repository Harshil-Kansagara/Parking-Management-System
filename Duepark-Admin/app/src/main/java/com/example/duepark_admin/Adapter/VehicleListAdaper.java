package com.example.duepark_admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_admin.Model.ItemClickListener;
import com.example.duepark_admin.Data.VehicleListData;
import com.example.duepark_admin.R;

import java.util.List;

public class VehicleListAdaper extends RecyclerView.Adapter<VehicleListAdaper.VehicleListViewHolder> {

    private List<VehicleListData> vehicleListData;
    private Context context;

    public VehicleListAdaper(List<VehicleListData> vehicleListData, Context context) {
        this.vehicleListData = vehicleListData;
        this.context = context;
    }

    @NonNull
    @Override
    public VehicleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_list, parent, false);
        return new VehicleListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleListViewHolder holder, int position) {
        //check in time and out time while display
        String intime = vehicleListData.get(position).getInTime();
        String outtime = vehicleListData.get(position).getOutTime();
        String time = intime +"-"+outtime;
        holder.vehicle_number.setText(vehicleListData.get(position).getNumber());
        holder.vehicle_fair.setText(vehicleListData.get(position).getFair());
        holder.vehicle_date.setText(vehicleListData.get(position).getDate());
        holder.vehicle_time.setText(time);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleListData.size();
    }

    class VehicleListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ItemClickListener itemClickListener;
        TextView vehicle_number, vehicle_fair, vehicle_time, vehicle_date;

        public VehicleListViewHolder(@NonNull View itemView) {
            super(itemView);

            vehicle_number = itemView.findViewById(R.id.vehicleNumber);
            vehicle_fair = itemView.findViewById(R.id.vehicleFair);
            vehicle_time = itemView.findViewById(R.id.vehicleTime);
            vehicle_date = itemView.findViewById(R.id.vehicleDate);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }
    }
}
