package com.example.duepark_consumer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_consumer.Helper.ItemClickListener;
import com.example.duepark_consumer.Location.DetailLocationActivity;
import com.example.duepark_consumer.Location.LocationActivity;
import com.example.duepark_consumer.Model.LocationList;
import com.example.duepark_consumer.R;

import java.text.DecimalFormat;
import java.util.List;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.LocationListViewHolder>  {

    private List<LocationList> locationLists;
    private Context ctx;
    private static final String TAG = "LocationListAdapter";
    private Activity myActivity;

    public LocationListAdapter(List<LocationList> locationLists, Context ctx, Activity myActivity) {
        this.locationLists = locationLists;
        this.ctx = ctx;
        this.myActivity = myActivity;
    }

    @NonNull
    @Override
    public LocationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list, parent, false);
        return new LocationListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationListViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("000");
        String parkingid = locationLists.get(position).getParkingAcronym() + df.format(Integer.parseInt(locationLists.get(position).getGeneratedParkingId()));
        char char_locationId = (char) (Integer.parseInt(locationLists.get(position).getGeneratedLocationId()) + 'A' - 1);
        String location_id = parkingid + char_locationId;
        holder.locationNameTV.setText(locationLists.get(position).getLocationName());
        holder.locationIdTV.setText(location_id);
        holder.cardview.setBackgroundColor(ctx.getResources().getColor(R.color.recyclerViewColor));
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(ctx, DetailLocationActivity.class);
                intent.putExtra("locationId", locationLists.get(position).getLocationId());
                intent.putExtra("locationActiveState", locationLists.get(position).getLocationActiveState());
                intent.putExtra("parkingAcronym", locationLists.get(position).getParkingAcronym());
                intent.putExtra("generatedParkingId", locationLists.get(position).getGeneratedParkingId());
                ctx.startActivity(intent);
                myActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                myActivity.finish();
                /*Intent intent = new Intent(context, DetailLocationPartnerActivity.class);
                intent.putExtra("id", locationList.get(position).getId());
                intent.putExtra("locationName", locationList.get(position).getLocationName());
                ((LocationListPartnerActivity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                context.startActivity(intent);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationLists.size();
    }

    class LocationListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private CardView cardview;
        private TextView locationNameTV, locationIdTV;
        private ItemClickListener itemClickListener;

        LocationListViewHolder(@NonNull View itemView){
            super(itemView);
            cardview = itemView.findViewById(R.id.cardView);
            locationNameTV = itemView.findViewById(R.id.locationNameTV);
            locationIdTV = itemView.findViewById(R.id.locationIdTV);

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
