package com.example.duepark_admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_admin.HomeActivity;
import com.example.duepark_admin.Model.ItemClickListener;
import com.example.duepark_admin.Model.RequestParking;
import com.example.duepark_admin.R;

import java.util.List;
import java.util.Random;

public class RequestPartnerListAdapter extends RecyclerView.Adapter<RequestPartnerListAdapter.RequestPartnerListViewHolder> {

    private List<RequestParking> requestParkingList;
    private Context context;

    public RequestPartnerListAdapter(List<RequestParking> requestParkingList, Context context) {
        this.requestParkingList = requestParkingList;
        this.context = context;
    }

    @NonNull
    @Override
    public RequestPartnerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_partner_list, parent, false);
        return new RequestPartnerListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestPartnerListViewHolder holder, int position) {
        Random mRandom = new Random();
        final int color = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
        ((GradientDrawable) holder.icon.getBackground()).setColor(color);
        holder.icon.setText(requestParkingList.get(position).getEmployeeName().substring(0, 1).toUpperCase());
        holder.employeeName.setText(requestParkingList.get(position).getEmployeeName());
        holder.employeeMobileNumber.setText(requestParkingList.get(position).getEmployeeMobileNumber());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                addNewParking(position);
            }
        });
    }

    private void addNewParking(int position){
        Intent i = new Intent(context, HomeActivity.class);
        i.putExtra("employeeId", requestParkingList.get(position).getId());
        i.putExtra("employeeName", requestParkingList.get(position).getEmployeeName());
        i.putExtra("employeeMobileNumber", requestParkingList.get(position).getEmployeeMobileNumber());
        i.putExtra("add","parking");
        i.putExtra("parkingid","null");
        i.putExtra("menu","null");
        context.startActivity(i);
    }

    @Override
    public int getItemCount() {
        return requestParkingList.size();
    }

    class RequestPartnerListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ItemClickListener itemClickListener;
        TextView employeeName, employeeMobileNumber, icon;

        public RequestPartnerListViewHolder(@NonNull View itemView){
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            employeeName = itemView.findViewById(R.id.employeeName);
            employeeMobileNumber = itemView.findViewById(R.id.employeeMobileNumber);

            itemView.setOnClickListener(this);
        }

        void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }
    }
}
