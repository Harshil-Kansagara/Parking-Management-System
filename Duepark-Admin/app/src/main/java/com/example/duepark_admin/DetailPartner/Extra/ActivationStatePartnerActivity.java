package com.example.duepark_admin.DetailPartner.Extra;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_admin.Adapter.Extra.ActivationStateListAdapter;
import com.example.duepark_admin.DetailPartner.DetailPartnerActivity;
import com.example.duepark_admin.Model.Location;
import com.example.duepark_admin.R;

import org.json.JSONArray;
import org.json.JSONObject;

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
import java.util.ArrayList;

public class ActivationStatePartnerActivity extends AppCompatActivity {

    private String base_url;
    private String id, parkingname;
    private TextView parkingName, parkingId;
    private RelativeLayout relLayout;
    private String parking_id, acronym, activation_state, parking;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation_state_partner);

        base_url = getResources().getString(R.string.base_url);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            id = bundle.getString("Id");
            parkingname = bundle.getString("ParkingName");
        }

        TextView parking_name = findViewById(R.id.parking_name);
        parking_name.setText(parkingname);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        parkingName = findViewById(R.id.parkingName);
        parkingId = findViewById(R.id.parkingId);
        relLayout = findViewById(R.id.relLayout1);

        relLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ActivationStatePartnerActivity.this);
                if(activation_state.equals("yes")){
                    alertBuilder.setTitle("Deactivate Partner");
                    alertBuilder.setMessage("Are you sure you want to deactivate partner ?");
                }
                else if(activation_state.equals("no")){
                    alertBuilder.setTitle("Activate Partner");
                    alertBuilder.setMessage("Are you sure you want to activate partner ?");
                }
                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BackgroundTask backgroundTask = new BackgroundTask(ActivationStatePartnerActivity.this);
                        backgroundTask.execute("deactivate", id);
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
        });

        getParkingData();

        getLocationData();

    }

    private void getParkingData(){
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("parkingData", id);

    }

    public void getLocationData(){
        BackgroundTask backgroundTask1 = new BackgroundTask(this);
        backgroundTask1.execute("locationData", id);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        startActivity(new Intent(ActivationStatePartnerActivity.this, DetailPartnerActivity.class));
        finish();
    }

    public class BackgroundTask extends AsyncTask<String, Location, String>{

        private Context ctx;
        private ProgressDialog progressDialog;
        private RecyclerView recyclerView;
        private ActivationStateListAdapter activationStateListAdapter;
        private ArrayList<Location> locationList;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            locationList = new ArrayList<>();
            progressDialog = new ProgressDialog(this.ctx);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            recyclerView = findViewById(R.id.recyclerView);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(ctx,
                    LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(mLinearLayoutManager);
            activationStateListAdapter = new ActivationStateListAdapter(ctx, locationList);
            recyclerView.setAdapter(activationStateListAdapter);

            progressDialog.setMessage("Loading Data...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String keyword = params[0];
            if(keyword.equals("parkingData")){
                String primaryId = params[1];
                String parking_url = base_url+"get_parking.php?id="+primaryId;
                //String parking_url = "https://duepark.000webhostapp.com/get_parkingData.php?id="+primaryId;
                try{
                    URL url = new URL(parking_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while((line = bufferedReader.readLine())!=null){
                        stringBuilder.append(line+"\n");
                    }
                    httpURLConnection.disconnect();

                    String jsonString = stringBuilder.toString().trim();

                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    while(count<jsonArray.length()){
                        JSONObject jo = jsonArray.getJSONObject(count);
                        parking_id = jo.getString("ParkingId");
                        acronym = jo.getString("Acronym");
                        parking = jo.getString("ParkingName");
                        activation_state = jo.getString("ActivationState");
                        count++;

                    }
                    Log.d("JSON-STRING-Parking", jsonString);
                    return "setParking";
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else if(keyword.equals("locationData")){
                String parkingId = params[1];
                String location_url = base_url+"get_allLocationListByParking.php?parkingId="+parkingId;
                //String location_url = "https://duepark.000webhostapp.com/get_allLocationListByParking.php?parkingId="+parkingId;
                try{
                    URL url = new URL(location_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while((line = bufferedReader.readLine())!=null){
                        stringBuilder.append(line+"\n");
                    }

                    httpURLConnection.disconnect();

                    String json_string = stringBuilder.toString().trim();
                    if(!json_string.equals("0")) {
                        JSONObject jsonObject = new JSONObject(json_string);
                        JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                        int count = 0;
                        while (count < jsonArray.length()) {
                            JSONObject jo = jsonArray.getJSONObject(count);
                            /*Location location = new Location(jo.getString("id"), jo.getString("LocationId"), jo.getString("Acronym"),
                                    jo.getString("ParkingId"), jo.getString("LocationName"), jo.getString("LocationActiveState"));*/
                            //publishProgress(location);
                            count++;
                        }
                        Log.d("JSON-STRING-Location", json_string);
                        return "setLocation";
                    }else{
                        return "0";
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else if(keyword.equals("deactivate")){
                progressDialog.setMessage("Updating Partner activation state...");
                String id = params[1];
                String deactivate_url = base_url+"update_parkingActiveState.php";
                //String deactivate_url = "https://duepark.000webhostapp.com/update_parkingActiveState.php";
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
                    Log.d("Result123", result.toString());
                    return result.toString();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Location... values) {
            locationList.add(values[0]);
            activationStateListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String result) {
            switch (result) {
                case "setParking":
                    DecimalFormat df = new DecimalFormat("000");
                    String id = acronym + df.format(Integer.parseInt(parking_id));
                    parkingName.setText(parking);
                    parkingId.setText(id);
                    if (activation_state.equals("yes")) {
                        relLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    } else if (activation_state.equals("no")) {
                        relLayout.setBackgroundColor(getResources().getColor(R.color.red));
                    }
                    break;
                case "deactivate":
                    Toast.makeText(ctx, "Deactivate successfully", Toast.LENGTH_SHORT).show();
                    getParkingData();
                    getLocationData();
                    break;
                case "activate":
                    Toast.makeText(ctx, "Activate successfully", Toast.LENGTH_SHORT).show();
                    getParkingData();
                    getLocationData();
                    break;
                case "notdeactivateparking":
                    Toast.makeText(ctx, "Partner can't be deactivated", Toast.LENGTH_SHORT).show();
                    break;
                case "notdeactivatelocation":
                    Toast.makeText(ctx, "Location can't be deactivated", Toast.LENGTH_SHORT).show();
                    break;
                case "notactivateparking":
                    Toast.makeText(ctx, "Partner can't be activated", Toast.LENGTH_SHORT).show();
                    break;
                case "notactivatelocation":
                    Toast.makeText(ctx, "Location can't be activated", Toast.LENGTH_SHORT).show();
                    break;
                case "0":
                    Toast.makeText(ctx, "No Location found..", Toast.LENGTH_SHORT).show();
                    break;
                /*case "setLocation":
                    Toast.makeText(ctx, "", Toast.LENGTH_SHORT).show();
                    break;*/
            }

            progressDialog.dismiss();
        }
    }
}
