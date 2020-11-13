package com.example.duepark_admin.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_admin.Model.EmployeeIncomeList;
import com.example.duepark_admin.R;

import java.util.List;


public class LocationEmployeeIncomeListAdapter extends RecyclerView.Adapter<LocationEmployeeIncomeListAdapter.LocationEmployeeIncomeListViewHolder> {

    private static final String TAG = "Adapter";
    private Context context;
    private List<EmployeeIncomeList> employeeIncomeLists;

    public LocationEmployeeIncomeListAdapter(Context context, List<EmployeeIncomeList> employeeIncomeLists) {
        this.context = context;
        this.employeeIncomeLists = employeeIncomeLists;
        Log.d(TAG, "LocationEmployeeIncomeListAdapter: "+this.employeeIncomeLists.size());
    }

    @NonNull
    @Override
    public LocationEmployeeIncomeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_income_list, parent, false);
        return new LocationEmployeeIncomeListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationEmployeeIncomeListViewHolder holder, int position) {
        holder.employeeNameTV.setText(employeeIncomeLists.get(position).getEmployeeName());
        holder.onlinePaymentTV.setText("Rs "+employeeIncomeLists.get(position).getOnlinePayment());
        holder.cashPaymentTV.setText("Rs "+employeeIncomeLists.get(position).getCashPayment());
    }

    @Override
    public int getItemCount() {
        return employeeIncomeLists.size();
    }

    class LocationEmployeeIncomeListViewHolder extends RecyclerView.ViewHolder{
        private TextView employeeNameTV, onlinePaymentTV, cashPaymentTV;

        public LocationEmployeeIncomeListViewHolder(@NonNull View itemView) {
            super(itemView);
            employeeNameTV = itemView.findViewById(R.id.employeeNameTV);
            onlinePaymentTV = itemView.findViewById(R.id.onlinePaymentTV);
            cashPaymentTV = itemView.findViewById(R.id.cashPaymentTV);
        }
    }
}
