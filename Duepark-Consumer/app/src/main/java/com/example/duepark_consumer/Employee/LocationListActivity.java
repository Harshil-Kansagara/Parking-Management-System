package com.example.duepark_consumer.Employee;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.duepark_consumer.Adapter.AssignLocationListAdapter;
import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.Model.LocationList;
import com.example.duepark_consumer.Model.SelectedLocation;
import com.example.duepark_consumer.R;

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
import java.util.ArrayList;
import java.util.HashMap;

public class LocationListActivity extends AppCompatActivity {

    private String base_url;
    private static final String TAG = "LocationListActivity";
    private String locationName, employeeId, isEmployeeActive;
    private ArrayList<LocationList> locationLists;
    private AssignLocationListAdapter assignLocationListAdapter;
    private SessionManagerHelper sessionManagerHelper;
    private HashMap<String, String> employeeDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        base_url = getResources().getString(R.string.base_url);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            employeeId = bundle.getString("EmployeeId");
            locationName = bundle.getString("LocationName");
            isEmployeeActive = bundle.getString("IsEmployeeActive");
        }

        sessionManagerHelper = new SessionManagerHelper(this);
        employeeDetail = sessionManagerHelper.getEmployeeDetails();

        SelectedLocation selectedLocation = new SelectedLocation();
        selectedLocation.setLocationName(locationName);
        locationLists = new ArrayList<>();
        assignLocationListAdapter = new AssignLocationListAdapter(locationLists, this, selectedLocation);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(assignLocationListAdapter);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button assignLocationBtn = findViewById(R.id.assignLocationBtn);
        assignLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LocationListActivity.this, DetailEmployeeActivity.class);
                i.putExtra("EmployeeId", employeeId);
                i.putExtra("LocationName", selectedLocation.getLocationName());
                i.putExtra("LocationId", selectedLocation.getId());
                i.putExtra("IsEmployeeActive", isEmployeeActive);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("get");
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(LocationListActivity.this, DetailEmployeeActivity.class);
        i.putExtra("EmployeeId", employeeId);
        i.putExtra("LocationName", locationName);
        i.putExtra("IsEmployeeActive", isEmployeeActive);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    class BackgroundTask extends AsyncTask<String, LocationList, String>{
        private Context ctx;
        private ProgressDialog progressDialog;

        BackgroundTask(Context ctx){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            if(params[0].equals("get")){
                progressDialog.setMessage("Getting location list...");
                String activeLocationList_url = base_url+"get_activeLocationList.php?ParkingId="+employeeDetail.get(SessionManagerHelper.ParkingId)+"&EmployeeId="+employeeDetail.get(SessionManagerHelper.EmployeeId)+"&EmployeeRole="+employeeDetail.get(SessionManagerHelper.EmployeeRole);
                //String activeLocationList_url = "https://duepark.000webhostapp.com/consumer/get_activeLocationList.php?ParkingId="+employeeDetail.get(SessionManagerHelper.ParkingId)+"&EmployeeId="+employeeDetail.get(SessionManagerHelper.EmployeeId)+"&EmployeeRole="+employeeDetail.get(SessionManagerHelper.EmployeeRole);
                try{
                    URL url = new URL(activeLocationList_url);
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

                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    locationLists.clear();
                    while(count<jsonArray.length()){
                        JSONObject jo =jsonArray.getJSONObject(count);
                        count++;
                        LocationList locationList = new LocationList(jo.getString("id"), jo.getString("LocationName"),jo.getString("GeneratedLocationId"),
                                jo.getString("LocationActiveState"), jo.getString("GeneratedParkingId"), jo.getString("ParkingAcronym"));
                        publishProgress(locationList);
                    }
                    Log.d(TAG, "doInBackground: "+result);
                    return "get";
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(LocationList... values) {
            locationLists.add(values[0]);
            assignLocationListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String result) {
            if(!result.equals("get")) {
                /*try {
                    int employeeId = Integer.parseInt(result);
                    Toast.makeText(ctx, "Employee create successfully..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ctx, ActivatedEmployeeActivity.class);
                    intent.putExtra("EmployeeId", employeeId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } catch (NumberFormatException ne) {
                    Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
                }*/
            }
            /*if(result.equals("EmployeeAdded")){
                Toast.makeText(ctx, "Employee create successfully..", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
            }*/
            progressDialog.dismiss();
        }
    }
}