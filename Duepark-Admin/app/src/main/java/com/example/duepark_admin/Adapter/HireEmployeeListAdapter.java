package com.example.duepark_admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_admin.Model.HireEmployee;
import com.example.duepark_admin.R;

import java.util.List;

public class HireEmployeeListAdapter extends RecyclerView.Adapter<HireEmployeeListAdapter.HireEmployeeListViewHolder> {

    private Context ctx;
    private List<HireEmployee> hireEmployeeList;

    public HireEmployeeListAdapter(Context ctx, List<HireEmployee> hireEmployeeList) {
        this.ctx = ctx;
        this.hireEmployeeList = hireEmployeeList;
    }

    @NonNull
    @Override
    public HireEmployeeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hire_employee_list, parent, false);
        return new HireEmployeeListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HireEmployeeListViewHolder holder, int position) {
        holder.name.setText(hireEmployeeList.get(position).getEmployeeName());
        holder.checkbox.setChecked(hireEmployeeList.get(position).isChecked());
        holder.checkbox.setTag(hireEmployeeList.get(position));
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                HireEmployee hireEmployee = (HireEmployee) cb.getTag();
                hireEmployee.setChecked(cb.isChecked());
                hireEmployeeList.get(position).setChecked(cb.isChecked());
            }
        });
//        holder.setItemClickListener(new ItemClickListener() {
//            @Override
//            public void onClick(View view, int position, boolean isLongClick) {
//                CheckBox cb = (CheckBox) view;
//                HireEmployee hireEmployee = (HireEmployee) cb.getTag();
//                hireEmployee.setChecked(cb.isChecked());
//                hireEmployeeList.get(position).setChecked(cb.isChecked());
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return hireEmployeeList.size();
    }

    public List<HireEmployee> getHireEmployeeList(){
        return hireEmployeeList;
    }

    class HireEmployeeListViewHolder extends RecyclerView.ViewHolder //implements View.OnClickListener
    {
        //private ItemClickListener itemClickListener;
        private TextView name;
        private CheckBox checkbox;

        HireEmployeeListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.employeeName);
            checkbox = itemView.findViewById(R.id.checkBox);
            //itemView.setOnClickListener(this);
        }

       /* void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }*/
    }
}
