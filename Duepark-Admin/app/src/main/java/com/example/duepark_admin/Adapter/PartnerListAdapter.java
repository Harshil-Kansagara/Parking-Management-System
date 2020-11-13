package com.example.duepark_admin.Adapter;

import android.app.Activity;
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

import com.example.duepark_admin.DetailPartner.DetailPartnerActivity;
import com.example.duepark_admin.Model.ItemClickListener;
import com.example.duepark_admin.Model.Parking;
import com.example.duepark_admin.R;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

public class PartnerListAdapter extends RecyclerView.Adapter<PartnerListAdapter.PartnerListViewHolder> {//implements Filterable {

    private List<Parking> parkingList;
    private Context context;
    //private List<Parking> parkingListFilter;
    private Activity myActivity;

    public PartnerListAdapter(List<Parking> parkingList, Context context, Activity myActivity) {
        this.parkingList = parkingList;
        this.context = context;
        this.myActivity = myActivity;
        //this.parkingListFilter = parkingList;
    }

    @NonNull
    @Override
    public PartnerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.partner_list, parent, false);
        return new PartnerListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartnerListViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("000");
        String parking_id = parkingList.get(position).getAcronym() + df.format(Integer.parseInt(parkingList.get(position).getParkingId()));
        Random mRandom = new Random();
        final int color = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
        ((GradientDrawable) holder.icon.getBackground()).setColor(color);
        holder.icon.setText(parkingList.get(position).getParkingName().substring(0, 1).toUpperCase());
        holder.parkingName.setText(parkingList.get(position).getParkingName());
        holder.parkingId.setText(parking_id);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(parkingList.get(position).getParkingActiveState().equals("1")) {
                    openDetailOfPartner(position);
                }else if(parkingList.get(position).getParkingActiveState().equals("0")){
                    openDetailOfPartner(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return parkingList.size();
    }

    /*@Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                Log.d("Search Filter", charString);
                if(charString.isEmpty()){
                    parkingListFilter = parkingList;
                }
                else{
                    Log.d("Search Else", charString);
                    List<Parking> filteredList = new ArrayList<>();
                    for(Parking item: parkingList){
                        Log.d("Search for", charString);
                        if(item.getParkingName().toLowerCase().contains(charString.toLowerCase().trim()))
                        {
                            Log.d("Search If", charString);
                            filteredList.add(item);
                        }
                    }
                    parkingListFilter = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = parkingListFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                parkingListFilter = (ArrayList<Parking>) results.values;
                notifyDataSetChanged();
            }
        };
        //return parkingFilter;
    }*/

    private void openDetailOfPartner(int position){
        Intent intent = new Intent(context, DetailPartnerActivity.class);
        intent.putExtra("Id", parkingList.get(position).getId());
        intent.putExtra("ParkingName", parkingList.get(position).getParkingName());
        intent.putExtra("ActivationState", parkingList.get(position).getParkingActiveState());
        context.startActivity(intent);
        myActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        myActivity.finish();
    }

    class PartnerListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView icon, parkingName, parkingId;
        private ItemClickListener itemClickListener;

        public PartnerListViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.icon);
            parkingName = itemView.findViewById(R.id.parkingName);
            parkingId = itemView.findViewById(R.id.parkingId);

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
