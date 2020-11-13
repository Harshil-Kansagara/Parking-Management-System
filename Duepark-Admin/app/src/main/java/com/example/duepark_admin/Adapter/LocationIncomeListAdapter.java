package com.example.duepark_admin.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_admin.Model.EmployeeIncomeList;
import com.example.duepark_admin.Model.IncomeList;
import com.example.duepark_admin.Model.ItemClickListener;
import com.example.duepark_admin.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class LocationIncomeListAdapter extends RecyclerView.Adapter<LocationIncomeListAdapter.LocationIncomeListViewHolder> {

    private static final String TAG = "LocationIncome";
    private Context context;
    private List<IncomeList> incomeLists;
    private List<EmployeeIncomeList> employeeIncomeLists;
    private List<EmployeeIncomeList> locationEmployeeIncomeLists, actualLocationEmployeeIncomeLists;
    private Dialog dialog;
    private LocationEmployeeIncomeListAdapter locationEmployeeIncomeListAdapter;

    public LocationIncomeListAdapter(Context context, List<IncomeList> incomeLists, List<EmployeeIncomeList> employeeIncomeLists) {
        this.context = context;
        this.incomeLists = incomeLists;
        this.employeeIncomeLists = employeeIncomeLists;
        locationEmployeeIncomeLists = new ArrayList<>();
        actualLocationEmployeeIncomeLists = new ArrayList<>();
        locationEmployeeIncomeListAdapter = new LocationEmployeeIncomeListAdapter(context,actualLocationEmployeeIncomeLists);
        dialog = new Dialog(context);
    }

    @NonNull
    @Override
    public LocationIncomeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_list_group, parent, false);
        return new LocationIncomeListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationIncomeListViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("000");
        DecimalFormat formatter = new DecimalFormat("#,###");
        String parkingid = incomeLists.get(position).getParkingAcronym() + df.format(Integer.parseInt(incomeLists.get(position).getGeneratedParkingId()));
        char char_locationId = (char) (Integer.parseInt(incomeLists.get(position).getGeneratedLocationId()) + 'A' - 1);
        String location_id = parkingid + char_locationId;
        holder.locationNameTV.setText(incomeLists.get(position).getLocationName());
        holder.locationIdTV.setText(location_id);
        int totalCashPayment = 0, totalOnlinePayment = 0, totalPayment = 0;
        for (int i = 0; i < employeeIncomeLists.size(); i++) {
            if(incomeLists.get(position).getLocationId().equals(employeeIncomeLists.get(i).getLocationId())){
                totalCashPayment = totalCashPayment + Integer.parseInt(employeeIncomeLists.get(i).getCashPayment());
                totalOnlinePayment = totalOnlinePayment + Integer.parseInt(employeeIncomeLists.get(i).getOnlinePayment());
            }
        }
        totalPayment = totalCashPayment + totalOnlinePayment;
        holder.incomeTV.setText("Rs "+formatter.format(totalPayment));
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                locationEmployeeIncomeLists.clear();
                actualLocationEmployeeIncomeLists.clear();
                int totalCashPayment = 0, totalOnlinePayment = 0;
                for (int i = 0;i<employeeIncomeLists.size();i++){
                    if(employeeIncomeLists.get(i).getLocationId().equals(incomeLists.get(position).getLocationId())){
                        locationEmployeeIncomeLists.add(employeeIncomeLists.get(i));
                        //tempLocationEmployeeIncomeLists.add(employeeIncomeLists.get(i));
                    }
                }

                for (int i=0;i<locationEmployeeIncomeLists.size();i++){
                    int cashPayment = 0, onlinePayment = 0;
                    for(int j=0;j<locationEmployeeIncomeLists.size();j++){
                        if(locationEmployeeIncomeLists.get(i).getEmployeeId().equals(locationEmployeeIncomeLists.get(j).getEmployeeId())){
                            cashPayment = cashPayment + Integer.parseInt(locationEmployeeIncomeLists.get(j).getCashPayment());
                            onlinePayment = onlinePayment + Integer.parseInt(locationEmployeeIncomeLists.get(j).getOnlinePayment());
                        }
                    }
                    EmployeeIncomeList employeeIncomeList = new EmployeeIncomeList(locationEmployeeIncomeLists.get(i).getLocationId(),
                            locationEmployeeIncomeLists.get(i).getEmployeeId(), locationEmployeeIncomeLists.get(i).getEmployeeName(),
                            String.valueOf(cashPayment), String.valueOf(onlinePayment));
                    if(actualLocationEmployeeIncomeLists.size() == 0){
                        totalCashPayment = cashPayment +totalCashPayment;
                        totalOnlinePayment = onlinePayment + totalOnlinePayment;
                        actualLocationEmployeeIncomeLists.add(employeeIncomeList);
                    }
                    else{
                        boolean isEmployeeExists = false;
                        for(int k = 0;k<actualLocationEmployeeIncomeLists.size();k++){
                            if(actualLocationEmployeeIncomeLists.get(k).getEmployeeId().equals(employeeIncomeList.getEmployeeId())){
                                isEmployeeExists = true;
                            }
                        }
                        if(!isEmployeeExists){
                            totalCashPayment = cashPayment +totalCashPayment;
                            totalOnlinePayment = onlinePayment + totalOnlinePayment;
                            actualLocationEmployeeIncomeLists.add(employeeIncomeList);
                        }
                    }
                }
                locationEmployeeIncomeListAdapter.notifyDataSetChanged();
                dialog.setContentView(R.layout.location_income_popup);
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
                LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(context,
                        LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(mLinearLayoutManager);
                recyclerView.setAdapter(locationEmployeeIncomeListAdapter);
                TextView totalCashPaymentTV = dialog.findViewById(R.id.totalCashPaymentTV);
                TextView totalOnlinePaymentTV = dialog.findViewById(R.id.totalOnlinePaymentTV);
                totalCashPaymentTV.setText("Rs "+decimalFormat.format(totalCashPayment));
                totalOnlinePaymentTV.setText("Rs "+decimalFormat.format(totalOnlinePayment));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                //Log.d(TAG, "onClick: "+locationEmployeeIncomeLists);
            }
        });
    }

    @Override
    public int getItemCount() {
        return incomeLists.size();
    }

    class LocationIncomeListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView locationNameTV, locationIdTV, incomeTV;
        private ItemClickListener itemClickListener;

        LocationIncomeListViewHolder(@NonNull View itemView){
            super(itemView);
            locationNameTV = itemView.findViewById(R.id.locationNameTV);
            locationIdTV = itemView.findViewById(R.id.locationIdTV);
            incomeTV = itemView.findViewById(R.id.incomeTV);

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
