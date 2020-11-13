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

import com.example.duepark_admin.Menu.Employee.DetailEmployeeActivity;
import com.example.duepark_admin.Model.Employee;
import com.example.duepark_admin.Model.ItemClickListener;
import com.example.duepark_admin.R;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.EmployeeListViewHolder> {

    private List<Employee> employeeList;
    private Context ctx;

    public EmployeeListAdapter(List<Employee> employeeList, Context ctx) {
        this.employeeList = employeeList;
        this.ctx = ctx;
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
        String employee_id = df.format(Integer.parseInt(employeeList.get(position).getGeneratedEmployeeId()));
        Random mRandom = new Random();
        final int color = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
        ((GradientDrawable) holder.icon.getBackground()).setColor(color);
        holder.icon.setText(employeeList.get(position).getUserName().substring(0, 1).toUpperCase());
        holder.employeeName.setText(employeeList.get(position).getUserName());
        holder.employeeId.setText(employee_id);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                openDetailEmployee(position);
            }
        });
    }

    private void openDetailEmployee(int position){
       Intent detailActivity = new Intent(ctx, DetailEmployeeActivity.class);
       detailActivity.putExtra("userid", employeeList.get(position).getId());
       detailActivity.putExtra("generatedemployeeid", employeeList.get(position).getGeneratedEmployeeId());
       detailActivity.putExtra("username", employeeList.get(position).getUserName());
       detailActivity.putExtra("back","notback");
       ctx.startActivity(detailActivity);
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public List<Employee> getEmployeeList() {return employeeList;}

    class EmployeeListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView icon, employeeName, employeeId;
        private ItemClickListener itemClickListener;

        public EmployeeListViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            employeeName = itemView.findViewById(R.id.employeeName);
            employeeId = itemView.findViewById(R.id.employeeId);

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
