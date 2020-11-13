package com.example.duepark_admin.DetailPartner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duepark_admin.Adapter.LocationEmployeeCountAdapter;
import com.example.duepark_admin.Model.LocationEmployeeCount;
import com.example.duepark_admin.Model.ParkingEmployeeCount;
import com.example.duepark_admin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class EmployeePartnerActivity extends AppCompatActivity {

    private static final String TAG = "EmployeePartner";
    private String base_url;
    private String parkingId, parkingName;
    private TextView parking_name, parkingName_Id, employeesCount;
    private List<LocationEmployeeCount> locationEmployeeCountList;
    private LocationEmployeeCountAdapter locationEmployeeCountAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_partner);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            parkingId = bundle.getString("Id");
            parkingName = bundle.getString("ParkingName");
        }

        base_url = getResources().getString(R.string.base_url);
        locationEmployeeCountList = new ArrayList<>();
        locationEmployeeCountAdapter = new LocationEmployeeCountAdapter(this, locationEmployeeCountList);

        parking_name = findViewById(R.id.parking_name);
        parking_name.setText(parkingName);

        parkingName_Id = findViewById(R.id.parkingName_Id);
        employeesCount = findViewById(R.id.employeesCount);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(locationEmployeeCountAdapter);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DetailPartnerActivity.class);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        startActivity(intent);
        finish();
    }

    class BackgroundTask extends AsyncTask<String, LocationEmployeeCount, String>{
        private Context ctx;
        private ProgressDialog progressDialog;
        private ParkingEmployeeCount parkingEmployeeCount;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Loading employee count...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String parkingEmployeeCount_url = base_url+"get_parkingEmployeeCount.php?ParkingId="+parkingId;
            try{
                URL url = new URL(parkingEmployeeCount_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine())!=null){
                    stringBuilder.append(line+"\n");
                }

                httpURLConnection.disconnect();

                String result = stringBuilder.toString().trim();

                JSONObject jo = new JSONObject(result);
                parkingEmployeeCount = new ParkingEmployeeCount(jo.getString("ParkingId"), jo.getString("GeneratedParkingId"),
                        jo.getString("ParkingAcronym"), jo.getString("ParkingName"), jo.getString("TotalSuperAdminCount"),
                        jo.getString("TotalAdminCount"), jo.getString("TotalManagerCount"), jo.getString("TotalValetCount"));
                JSONArray jsonArray = jo.getJSONArray("0");
                int count = 0;
                locationEmployeeCountList.clear();
                while(count < jsonArray.length()){
                    JSONObject innerJO = jsonArray.getJSONObject(count);
                    LocationEmployeeCount locationEmployeeCount = new LocationEmployeeCount(innerJO.getString("LocationId"),
                                    innerJO.getString("GeneratedLocationId"), innerJO.getString("LocationName"),
                                    innerJO.getString("LocationManagerCount"), innerJO.getString("LocationValetCount"),
                                    parkingEmployeeCount.getGeneratedParkingId(), parkingEmployeeCount.getParkingAcronym());
                    count ++;
                    publishProgress(locationEmployeeCount);
                }
                Log.d(TAG, "doInBackground: "+result);
                inputStream.close();
                bufferedReader.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(LocationEmployeeCount... values) {
            locationEmployeeCountList.add(values[0]);
            locationEmployeeCountAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String s) {
            DecimalFormat df = new DecimalFormat("000");
            parkingName_Id.setText(parkingEmployeeCount.getParkingName()+" : "+parkingEmployeeCount.getParkingAcronym() + df.format(Integer.parseInt(parkingEmployeeCount.getGeneratedParkingId())));
            employeesCount.setText("Admin:"+parkingEmployeeCount.getTotalAdminCount()+" | Manager:"+parkingEmployeeCount.getTotalManagerCount()+" | Valet:"+parkingEmployeeCount.getTotalValetCount());
            progressDialog.dismiss();
        }
    }
}