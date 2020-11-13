package com.example.duepark_admin.Adapter.Extra;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_admin.DetailPartner.Extra.DetailLocationPartnerActivity;
import com.example.duepark_admin.DetailPartner.Extra.LocationListPartnerActivity;
import com.example.duepark_admin.Model.ItemClickListener;
import com.example.duepark_admin.Model.Location;
import com.example.duepark_admin.R;

import java.text.DecimalFormat;
import java.util.List;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.LocationListViewHolder> {

    List<Location> locationList;
    Context context;

    public LocationListAdapter(List<Location> locationList, Context context) {
        this.locationList = locationList;
        this.context = context;
    }

    @NonNull
    @Override
    public LocationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list, parent, false);
        return new LocationListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationListViewHolder holder, int position) {
            /*DecimalFormat df = new DecimalFormat("000");
            String parkingid = locationList.get(position).getAcronym() + df.format(Integer.parseInt(locationList.get(position).getParkingId()));
            char locationid = (char) (Integer.parseInt(locationList.get(position).getLocationId()) + 'A' - 1);
            String location_id = parkingid + locationid;
            holder.locationName.setText(locationList.get(position).getLocationName());
            holder.locationId.setText(location_id);
            holder.cardview.setBackgroundColor(context.getResources().getColor(R.color.grey));
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Intent intent = new Intent(context, DetailLocationPartnerActivity.class);
                    intent.putExtra("id", locationList.get(position).getId());
                    intent.putExtra("locationName", locationList.get(position).getLocationName());
                    ((LocationListPartnerActivity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    context.startActivity(intent);
                }
            });*/
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    class LocationListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView cardview;
        TextView locationName, locationId;
        private ItemClickListener itemClickListener;

        LocationListViewHolder(@NonNull View itemView) {
            super(itemView);
            cardview = itemView.findViewById(R.id.cardView);
            locationName = itemView.findViewById(R.id.locationName);
            locationId = itemView.findViewById(R.id.locationId);

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
