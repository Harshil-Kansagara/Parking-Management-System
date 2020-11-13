package com.example.duepark_consumer.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_consumer.Helper.ItemClickListener;
import com.example.duepark_consumer.Model.EmployeeList;
import com.example.duepark_consumer.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AssignEmployeeListAdapter extends RecyclerView.Adapter<AssignEmployeeListAdapter.AssignEmployeeListViewHolder> {

    private List<EmployeeList> employeeLists;
    private Context ctx;
    private List<Integer> removeEmployeeIdList;

    public AssignEmployeeListAdapter(Context ctx, List<EmployeeList> employeeLists){
        this.employeeLists = employeeLists;
        this.ctx = ctx;
        removeEmployeeIdList = new ArrayList<>();
    }

    @NonNull
    @Override
    public AssignEmployeeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assign_employee_list, parent, false);
        return new AssignEmployeeListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignEmployeeListViewHolder holder, int position) {
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

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String employeeName = employeeLists.get(position).getEmployeeName();
                removeEmployeeIdList.add(Integer.parseInt(employeeLists.get(position).getId()));
                employeeLists.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, employeeLists.size());
                Toast.makeText(ctx,"Removed : " + employeeName,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return employeeLists.size();
    }

    public List<Integer> getRemoveEmployeeIdList(){
        return removeEmployeeIdList;
    }

    class AssignEmployeeListViewHolder extends RecyclerView.ViewHolder {//implements View.OnClickListener {
        private TextView icon, employeeNameTV, generatedEmployeeIdTV;
        private ItemClickListener itemClickListener;
        private ImageView deleteBtn;

        AssignEmployeeListViewHolder(@NonNull View itemView){
            super(itemView);

            icon = itemView.findViewById(R.id.icon);
            employeeNameTV = itemView.findViewById(R.id.employeeNameTV);
            generatedEmployeeIdTV = itemView.findViewById(R.id.generatedEmployeeIdTV);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }

    }
}
