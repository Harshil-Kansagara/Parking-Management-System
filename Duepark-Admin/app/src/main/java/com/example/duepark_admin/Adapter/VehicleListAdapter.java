package com.example.duepark_admin.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_admin.Model.ItemClickListener;
import com.example.duepark_admin.Model.ParkedVehicleList;
import com.example.duepark_admin.Model.VehicleList;
import com.example.duepark_admin.R;
import com.example.duepark_admin.Vehicle.DetailVehicleActivity;

import java.util.ArrayList;
import java.util.List;

public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.ParkedVehicleListViewHolder> {

    private Context ctx;
    private List<VehicleList> vehicleLists;
    private Activity myActivity;

    public VehicleListAdapter(Context ctx, List<VehicleList> vehicleLists, Activity myActivity) {
        this.ctx = ctx;
        this.vehicleLists = vehicleLists;
        this.myActivity = myActivity;
    }

    @NonNull
    @Override
    public ParkedVehicleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parked_vehicle_list, parent, false);
        return new ParkedVehicleListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkedVehicleListViewHolder holder, int position) {
        holder.vehicleNumberTV.setText(vehicleLists.get(position).getVehicleNumber());
        if(vehicleLists.get(position).getTotalAmount().equals("null")){
            holder.paidAmountTV.setText("Rs 00");
        }
        else{
            holder.paidAmountTV.setText("Rs "+vehicleLists.get(position).getTotalAmount());
        }
        holder.parkedTimeTV.setText(vehicleLists.get(position).getTotalTime());
        holder.parkedDateTV.setText(vehicleLists.get(position).getDate());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent i = new Intent(ctx, DetailVehicleActivity.class);
                i.putExtra("parkedVehicleId", vehicleLists.get(position).getParkedVehicleId());
                ctx.startActivity(i);
                myActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                myActivity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleLists.size();
    }

    public void filteredList(ArrayList<VehicleList> filteredVehicleList){
        vehicleLists = filteredVehicleList;
        notifyDataSetChanged();
    }

    class ParkedVehicleListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView vehicleNumberTV, paidAmountTV, parkedDateTV, parkedTimeTV;
        private ItemClickListener itemClickListener;

        ParkedVehicleListViewHolder(@NonNull View itemView){
            super(itemView);
            vehicleNumberTV = itemView.findViewById(R.id.vehicleNumberTV);
            paidAmountTV = itemView.findViewById(R.id.paidAmountTV);
            parkedDateTV = itemView.findViewById(R.id.parkedDateTV);
            parkedTimeTV = itemView.findViewById(R.id.parkedTimeTV);

            itemView.setOnClickListener(this);
        }

        void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }
}
