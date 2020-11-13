package com.example.duepark_consumer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_consumer.Model.AssignVehicleNumberListForMonthlyPass;
import com.example.duepark_consumer.R;

import java.util.ArrayList;
import java.util.List;

public class AssignVehicleNumberListForMonthlyPassAdapter extends RecyclerView.Adapter<AssignVehicleNumberListForMonthlyPassAdapter.AssignVehicleNumberListForMonthlyPassViewHolder> {

    private List<AssignVehicleNumberListForMonthlyPass> assignVehicleNumberListForMonthlyPasses;
    private Context ctx;
    private List<Integer> removeVehicleNumberIdList;

    public AssignVehicleNumberListForMonthlyPassAdapter(List<AssignVehicleNumberListForMonthlyPass> assignVehicleNumberListForMonthlyPasses, Context ctx) {
        this.assignVehicleNumberListForMonthlyPasses = assignVehicleNumberListForMonthlyPasses;
        this.ctx = ctx;
        this.removeVehicleNumberIdList = new ArrayList<>();
    }

    @NonNull
    @Override
    public AssignVehicleNumberListForMonthlyPassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assign_monthly_pass_vehicle_number_list, parent, false);
        return new AssignVehicleNumberListForMonthlyPassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignVehicleNumberListForMonthlyPassViewHolder holder, int position) {
        holder.vehicleNumberTV.setText(assignVehicleNumberListForMonthlyPasses.get(position).getVehicleNumber());
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vehicleNumber = assignVehicleNumberListForMonthlyPasses.get(position).getVehicleNumber();
                removeVehicleNumberIdList.add(Integer.parseInt(assignVehicleNumberListForMonthlyPasses.get(position).getVehicleNumberMonthlyPassId()));
                assignVehicleNumberListForMonthlyPasses.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, assignVehicleNumberListForMonthlyPasses.size());
                Toast.makeText(ctx,"Removed : " + vehicleNumber,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return assignVehicleNumberListForMonthlyPasses.size();
    }

    public List<Integer> getRemoveVehicleNumberIdList(){
        return removeVehicleNumberIdList;
    }

    class AssignVehicleNumberListForMonthlyPassViewHolder extends RecyclerView.ViewHolder{
        private TextView vehicleNumberTV;
        private ImageView deleteBtn;

        AssignVehicleNumberListForMonthlyPassViewHolder(@NonNull View itemView){
            super(itemView);

            vehicleNumberTV = itemView.findViewById(R.id.vehicleNumberTV);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
