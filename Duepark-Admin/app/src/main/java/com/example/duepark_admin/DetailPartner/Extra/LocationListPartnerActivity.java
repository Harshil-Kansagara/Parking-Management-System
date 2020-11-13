package com.example.duepark_admin.DetailPartner.Extra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duepark_admin.Adapter.Extra.LocationListAdapter;
import com.example.duepark_admin.DetailPartner.DetailPartnerActivity;
import com.example.duepark_admin.HomeActivity;
import com.example.duepark_admin.Model.Location;
import com.example.duepark_admin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LocationListPartnerActivity extends AppCompatActivity {

    private String base_url;
    private String parkingid, parkingName;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView parking_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list_partner);

        base_url = getResources().getString(R.string.base_url);
        sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.remove("Data");
        editor.apply();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            parkingid = bundle.getString("Id");
            parkingName = bundle.getString("ParkingName");
        }

        if(parkingName == null) {
            retrieveData();
        }
        if(parkingName!=null){
            storeData();
        }

        parking_name = findViewById(R.id.parking_name);
        parking_name.setText(parkingName);

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(parkingid);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button addLocationBtn = findViewById(R.id.addlocationBtn);
        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(LocationListPartnerActivity.this, HomeActivity.class);
                storeData();
                homeIntent.putExtra("id", parkingid);
                homeIntent.putExtra("add","add");
                startActivity(homeIntent);
                finish();
            }
        });
    }

    private void retrieveData(){
        if(sharedPreferences.contains("ParkingName")){
            parkingName = sharedPreferences.getString("ParkingName", "");
        }
        if(sharedPreferences.contains("ParkingId")){
            parkingid = sharedPreferences.getString("ParkingId","");
        }
    }

    private void storeData(){
        if(parkingName !=null){
            editor.remove("ParkingName");
            editor.apply();
            editor.putString("ParkingName", parkingName);
            editor.apply();
        }
        if(parkingid !=null){
            editor.remove("ParkingId");
            editor.apply();
            editor.putString("ParkingId", parkingid);
            editor.apply();
        }
    }

    @Override
    public void onBackPressed() {
        editor.remove("Data");
        editor.apply();
        Intent intent = new Intent(LocationListPartnerActivity.this, DetailPartnerActivity.class);
        /*intent.putExtra("Id", parkingid);
        intent.putExtra("ParkingName", parkingName);*/
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        startActivity(intent);
        finish();
    }

    class BackgroundTask extends AsyncTask<String, Location, Void>{

        private Context ctx;
        private LocationListAdapter locationListAdapter;
        private ArrayList<Location> locationList;
        private ProgressDialog progressDialog;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            locationList = new ArrayList<>();
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(ctx,
                    LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(mLinearLayoutManager);
            locationListAdapter = new LocationListAdapter(locationList, ctx);
            recyclerView.setAdapter(locationListAdapter);

            progressDialog.setMessage("Loading Data...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {
            String parkingId = params[0];
            String location_url = base_url+"get_activeLocationList_Admin.php?ParkingId="+parkingId;
            //String location_url = "https://duepark.000webhostapp.com/get_activeLocationListByParking.php?parkingId="+parkingId;
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

                JSONObject jsonObject = new JSONObject(json_string);
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                int count = 0;
                while(count<jsonArray.length()){
                    JSONObject jo =jsonArray.getJSONObject(count);
                    /*Location location = new Location(jo.getString("id"),jo.getString("GeneratedLocationId"), jo.getString("ParkingAcronym"),
                            jo.getString("GeneratedParkingId"), jo.getString("LocationName"));*/
                    //publishProgress(location);
                    count++;
                }
                Log.d("JSON-STRING",json_string);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Location... values) {
            super.onProgressUpdate(values);
            locationList.add(values[0]);
            locationListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }
}
