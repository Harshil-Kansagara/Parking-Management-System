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
import com.example.duepark_admin.R;

import java.util.ArrayList;
import java.util.List;

public class ParkedVehicleListAdapter extends RecyclerView.Adapter<ParkedVehicleListAdapter.ParkedVehicleListViewHolder> {

    private Context ctx;
    private List<ParkedVehicleList> parkedVehicleLists;
    private Activity myActivity;
    //private boolean isValet = false;

    public ParkedVehicleListAdapter(Context ctx, List<ParkedVehicleList> parkedVehicleLists, Activity myActivity) {
        this.ctx = ctx;
        this.parkedVehicleLists = parkedVehicleLists;
        this.myActivity = myActivity;
    }

    /*public ParkedVehicleListAdapter(Context ctx, List<ParkedVehicleList> parkedVehicleLists, Activity myActivity, boolean isValet){
        this.ctx = ctx;
        this.parkedVehicleLists = parkedVehicleLists;
        this.myActivity = myActivity;
        this.isValet = isValet;
    }*/

    @NonNull
    @Override
    public ParkedVehicleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parked_vehicle_list, parent, false);
        return new ParkedVehicleListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkedVehicleListViewHolder holder, int position) {
        holder.vehicleNumberTV.setText(parkedVehicleLists.get(position).getVehicleNumber());
        if(parkedVehicleLists.get(position).getPaidAmount().equals("null")){
            holder.paidAmountTV.setText("Rs 00");
        }
        else{
            holder.paidAmountTV.setText("Rs "+parkedVehicleLists.get(position).getPaidAmount());
        }
        holder.parkedTimeTV.setText(parkedVehicleLists.get(position).getParkedTime());
        holder.parkedDateTV.setText(parkedVehicleLists.get(position).getParkedDate());
        /*holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent i = new Intent(ctx, DetailParkedVehicleActivity.class);
                i.putExtra("parkedVehicleId", parkedVehicleLists.get(position).getId());
                i.putExtra("vehicleType", parkedVehicleLists.get(position).getVehicleType());
                i.putExtra("vehicleNumber", parkedVehicleLists.get(position).getVehicleNumber());
                i.putExtra("isValet", isValet);
                ctx.startActivity(i);
                myActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                myActivity.finish();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return parkedVehicleLists.size();
    }

    public void filteredList(ArrayList<ParkedVehicleList> filteredParkedVehicleList){
       parkedVehicleLists = filteredParkedVehicleList;
        notifyDataSetChanged();
    }

    class ParkedVehicleListViewHolder extends RecyclerView.ViewHolder {//implements View.OnClickListener{

        private TextView vehicleNumberTV, paidAmountTV, parkedDateTV, parkedTimeTV;
        private ItemClickListener itemClickListener;

        ParkedVehicleListViewHolder(@NonNull View itemView){
            super(itemView);
            vehicleNumberTV = itemView.findViewById(R.id.vehicleNumberTV);
            paidAmountTV = itemView.findViewById(R.id.paidAmountTV);
            parkedDateTV = itemView.findViewById(R.id.parkedDateTV);
            parkedTimeTV = itemView.findViewById(R.id.parkedTimeTV);

            //itemView.setOnClickListener(this);
        }

        /*void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }*/

        /*@Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }*/
    }
}
