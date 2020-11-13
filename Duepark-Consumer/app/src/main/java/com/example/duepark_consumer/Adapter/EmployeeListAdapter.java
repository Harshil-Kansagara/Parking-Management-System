package com.example.duepark_consumer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_consumer.Employee.DetailEmployeeActivity;
import com.example.duepark_consumer.Employee.EmployeeActivity;
import com.example.duepark_consumer.Helper.ItemClickListener;
import com.example.duepark_consumer.Model.Employee;
import com.example.duepark_consumer.Model.EmployeeList;
import com.example.duepark_consumer.Model.MonthlyPassList;
import com.example.duepark_consumer.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.EmployeeListViewHolder> {

    private List<EmployeeList> employeeLists;
    private Context ctx;
    private static final String TAG = "EmployeeListAdapter";
    private Activity myActivity;

    public EmployeeListAdapter(Context ctx, List<EmployeeList> employeeLists, Activity myActivity){
        this.employeeLists = employeeLists;
        this.ctx = ctx;
        this.myActivity = myActivity;
    }

    @NonNull
    @Override
    public EmployeeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_list, parent, false);
        return new EmployeeListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeListViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("000");
        String generatedEmployeeId = null;
        if(employeeLists.get(position).getRole().equals("Admin")){
            generatedEmployeeId = "A"+df.format(Integer.parseInt(employeeLists.get(position).getGeneratedEmployeeId()));
        }
        if(employeeLists.get(position).getRole().equals("Manager")){
            generatedEmployeeId = "M"+df.format(Integer.parseInt(employeeLists.get(position).getGeneratedEmployeeId()));
        }
        if(employeeLists.get(position).getRole().equals("Valet")){
            generatedEmployeeId = "V"+df.format(Integer.parseInt(employeeLists.get(position).getGeneratedEmployeeId()));
        }
        Random mRandom = new Random();
        final int color = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
        ((GradientDrawable) holder.icon.getBackground()).setColor(color);
        holder.icon.setText(employeeLists.get(position).getEmployeeName().substring(0, 1).toUpperCase());
        holder.generatedEmployeeIdTV.setText(generatedEmployeeId);
        holder.employeeNameTV.setText(employeeLists.get(position).getEmployeeName());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(ctx, DetailEmployeeActivity.class);
                intent.putExtra("EmployeeId", employeeLists.get(position).getId());
                intent.putExtra("IsEmployeeActive", employeeLists.get(position).getEmployeeActiveState());
                ctx.startActivity(intent);
                myActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                myActivity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return employeeLists.size();
    }

    public void filteredList(ArrayList<EmployeeList> filteredEmployeeList){
        this.employeeLists = filteredEmployeeList;
        notifyDataSetChanged();
    }

    class EmployeeListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView icon, employeeNameTV, generatedEmployeeIdTV;
        private ItemClickListener itemClickListener;

        EmployeeListViewHolder(@NonNull View itemView){
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            employeeNameTV = itemView.findViewById(R.id.employeeNameTV);
            generatedEmployeeIdTV = itemView.findViewById(R.id.generatedEmployeeIdTV);

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
