package com.example.duepark_admin.Adapter.Extra;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_admin.DetailPartner.Extra.ActivationStatePartnerActivity;
import com.example.duepark_admin.Model.ItemClickListener;
import com.example.duepark_admin.Model.Location;
import com.example.duepark_admin.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;

public class ActivationStateListAdapter extends RecyclerView.Adapter<ActivationStateListAdapter.ActivationStateListViewHolder>
{
    private Context ctx;
    private List<Location> locationList;
    //private ActivationStatePartnerActivity activationStatePartnerActivity;

    public ActivationStateListAdapter(Context ctx, List<Location> locationList) {
        this.ctx = ctx;
        this.locationList = locationList;
        //activationStatePartnerActivity = new ActivationStatePartnerActivity();
    }

    @NonNull
    @Override
    public ActivationStateListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list, parent, false);
        return new ActivationStateListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivationStateListViewHolder holder, int position) {
        /*DecimalFormat df = new DecimalFormat("000");
        String parkingid = locationList.get(position).getAcronym() + df.format(Integer.parseInt(locationList.get(position).getParkingId()));
        char locationid = (char) (Integer.parseInt(locationList.get(position).getLocationId()) + 'A' - 1);
        String locationId = parkingid + locationid;
        holder.location_name.setText(locationList.get(position).getLocationName());
        holder.location_name.setTextColor(ctx.getResources().getColor(R.color.white));
        holder.location_id.setText(locationId);
        holder.location_id.setTextColor(ctx.getResources().getColor(R.color.white));
        holder.id.setTextColor(ctx.getResources().getColor(R.color.white));
        if(locationList.get(position).getLocationActiveState().equals("yes")){
            holder.cardview.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimary));
        }else if(locationList.get(position).getLocationActiveState().equals("no")){
            holder.cardview.setBackgroundColor(ctx.getResources().getColor(R.color.red));
        }
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //Toast.makeText(ctx, locationList.get(position).getId(), Toast.LENGTH_SHORT).show();

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctx);
                if(locationList.get(position).getLocationActiveState().equals("yes")){
                    alertBuilder.setTitle("Deactivate Location");
                    alertBuilder.setMessage("Are you sure you want to deactivate location ?");
                }
                else if(locationList.get(position).getLocationActiveState().equals("no")){
                    alertBuilder.setTitle("Activate Location");
                    alertBuilder.setMessage("Are you sure you want to activate location ?");
                }
                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BackgroundTask backgroundTask = new BackgroundTask(ctx);
                        backgroundTask.execute("deactivateLocation", locationList.get(position).getId());

                        dialog.cancel();

                    }
                });

                alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    class ActivationStateListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        CardView cardview;
        TextView location_name, location_id,id;
        private ItemClickListener itemClickListener;

        public ActivationStateListViewHolder(@NonNull View itemView) {
            super(itemView);
            cardview = itemView.findViewById(R.id.cardView);
            location_name = itemView.findViewById(R.id.locationName);
            location_id = itemView.findViewById(R.id.locationId);
            id = itemView.findViewById(R.id.id);
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

    class BackgroundTask extends AsyncTask<String, Void, String>{

        Context ctx;
        ProgressDialog progressDialog;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String keyword = params[0];
            if(keyword.equals("deactivateLocation")){
                progressDialog.setMessage("Updating Location activation state...");
                String id = params[1];
                String deactivate_url = "https://duepark.000webhostapp.com/update_locationActiveState.php";
                Log.d("Result-ID", id);
                try{
                    URL url = new URL(deactivate_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8");
                    bw.write(data);
                    bw.flush();
                    bw.close();
                    ops.close();

                    InputStream ips = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(ips,"iso-8859-1"));
                    StringBuilder result= new StringBuilder();
                    String line = "";
                    while((line=br.readLine())!=null){
                        result.append(line);
                    }
                    br.close();
                    ips.close();
                    httpURLConnection.disconnect();
                    Log.d("Result", result.toString());
                    return result.toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("deactivate")){
                Toast.makeText(ctx, "Location is deactivated successfully", Toast.LENGTH_SHORT).show();
                //ctx.startActivity(new Intent(ctx, DetailPartnerActivity.class));
                if(ctx instanceof ActivationStatePartnerActivity){
                    ((ActivationStatePartnerActivity)ctx).getLocationData();
                }
            }else if(result.equals("notdeactivate")){
                Toast.makeText(ctx, "Location cannot deactivated successfully.. Try again after sometime.", Toast.LENGTH_SHORT).show();
            }else if(result.equals("activate")){
                Toast.makeText(ctx, "Location is activated successfully", Toast.LENGTH_SHORT).show();
                //ctx.startActivity(new Intent(ctx, DetailPartnerActivity.class));
                if(ctx instanceof ActivationStatePartnerActivity){
                    ((ActivationStatePartnerActivity)ctx).getLocationData();
                }
            }else if(result.equals("notactivate")){
                Toast.makeText(ctx, "Location is cannot activated successfully.. Try again after sometime.", Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("invalid")){
                Toast.makeText(ctx, "Cannot activate location because Partner is deactivate.", Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        }
    }
}
