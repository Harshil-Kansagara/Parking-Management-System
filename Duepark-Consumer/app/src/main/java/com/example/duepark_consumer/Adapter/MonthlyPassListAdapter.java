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
import com.example.duepark_consumer.Model.MonthlyPass;
import com.example.duepark_consumer.Model.MonthlyPassList;
import com.example.duepark_consumer.MonthlyPass.DetailMonthlyPassActivity;
import com.example.duepark_consumer.MonthlyPass.MonthlyPassActivity;
import com.example.duepark_consumer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonthlyPassListAdapter extends RecyclerView.Adapter<MonthlyPassListAdapter.MonthlyPassListViewHolder> { //implements Filterable {

    private Context ctx;
    private List<MonthlyPassList> monthlyPassList;
    private Activity myActivity;
    private boolean isValet;

    public MonthlyPassListAdapter(Context ctx, List<MonthlyPassList> monthlyPassList, Activity myActivity, boolean isValet) {
        this.ctx = ctx;
        this.monthlyPassList = monthlyPassList;
        this.myActivity = myActivity;
        this.isValet = isValet;
    }

    @NonNull
    @Override
    public MonthlyPassListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.monthly_pass_list, parent, false);
        return new MonthlyPassListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthlyPassListViewHolder holder, int position) {
        Random mRandom = new Random();
        final int color = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
        ((GradientDrawable) holder.icon.getBackground()).setColor(color);
        holder.icon.setText(monthlyPassList.get(position).getPassUserName().substring(0, 1).toUpperCase());
        holder.generatedMonthlyPassIdTV.setText(monthlyPassList.get(position).getGeneratedLocationId()+""+monthlyPassList.get(position).getGeneratedMonthlyPassId());
        holder.passUserNameTV.setText(monthlyPassList.get(position).getPassUserName());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(ctx, DetailMonthlyPassActivity.class);
                intent.putExtra("monthlyPassId", monthlyPassList.get(position).getMonthlyPassId());
                intent.putExtra("isValet", isValet);
                ctx.startActivity(intent);
                myActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                myActivity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return monthlyPassList.size();
    }

    public void filteredList(ArrayList<MonthlyPassList> filteredMonthlyPassList){
        monthlyPassList = filteredMonthlyPassList;
        notifyDataSetChanged();
    }

    class MonthlyPassListViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        private TextView icon, passUserNameTV, generatedMonthlyPassIdTV;
        private ItemClickListener itemClickListener;

        MonthlyPassListViewHolder(@NonNull View itemView){
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            passUserNameTV = itemView.findViewById(R.id.passUserNameTV);
            generatedMonthlyPassIdTV = itemView.findViewById(R.id.generatedMonthlyPassIdTV);

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
