package com.example.duepark_consumer.Capacity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.HomeActivity;
import com.example.duepark_consumer.Model.LocationCapacityList;
import com.example.duepark_consumer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParkingCapacityActivity extends AppCompatActivity {

    private static final String TAG = "ParkingCapacity";
    private String base_url;
    private boolean isCheckClicked = false;
    private Spinner vehicleTypeSpinner, locationSpinner;
    private TextView totalSpaceTV, totalOccupiedSpaceTV, totalVacantSpaceTV;
    private List<LocationCapacityList> locationCapacityLists;
    private List<String> locationLists;
    private String vehicleType, locationName;
    List<String> vehicleTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_capacity);

        base_url = getResources().getString(R.string.base_url);
        SessionManagerHelper sessionManagerHelper = new SessionManagerHelper(this);
        HashMap<String, String> employeeDetail = sessionManagerHelper.getEmployeeDetails();
        locationCapacityLists = new ArrayList<>();
        locationLists = new ArrayList<>();
        vehicleTypes = new ArrayList<>();

        vehicleTypeSpinner = findViewById(R.id.vehicleTypeSpinner);
        vehicleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                vehicleType = vehicleTypes.get(position);
                getSpaceCount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        initVehicleSpinner();
        locationSpinner = findViewById(R.id.locationSpinner);
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    locationName = locationLists.get(position);
                    getSpaceCount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        totalSpaceTV = findViewById(R.id.totalSpaceTV);
        totalOccupiedSpaceTV = findViewById(R.id.totalOccupiedSpaceTV);
        totalVacantSpaceTV = findViewById(R.id.totalVacantSpaceTV);

        FloatingActionButton checkBtn = findViewById(R.id.checkBtn);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCheckClicked = true;
                onBackPressed();
            }
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("get", employeeDetail.get(SessionManagerHelper.ParkingId));
    }

    private void getSpaceCount(){
        int totalSpaces = 0, totalOccupiedSpace = 0, totalVacantSpace = 0;
        if(locationCapacityLists.size() !=0) {
            for (int i = 0; i < locationCapacityLists.size(); i++) {
                if(!locationName.equals("All Locations")) {
                    if (locationName.equals(locationCapacityLists.get(i).getLocationName())) {
                        if (vehicleType.equals("Car")) {
                            totalOccupiedSpace = Integer.parseInt(locationCapacityLists.get(i).getOccupiedCarCapacity());
                            totalSpaces = Integer.parseInt(locationCapacityLists.get(i).getCarCapacity());
                            totalVacantSpace = totalSpaces - totalOccupiedSpace;
                        } else {
                            totalOccupiedSpace = Integer.parseInt(locationCapacityLists.get(i).getOccupiedBikeCapacity());
                            totalSpaces = Integer.parseInt(locationCapacityLists.get(i).getBikeCapacity());
                            totalVacantSpace = totalSpaces - totalOccupiedSpace;
                        }
                        break;
                    }
                }
                else{
                    if(vehicleType.equals("Car")) {
                        totalOccupiedSpace = totalOccupiedSpace + Integer.parseInt(locationCapacityLists.get(i).getOccupiedCarCapacity());
                        totalSpaces = totalSpaces + Integer.parseInt(locationCapacityLists.get(i).getCarCapacity());
                    }
                    else{
                        totalOccupiedSpace = totalOccupiedSpace + Integer.parseInt(locationCapacityLists.get(i).getOccupiedBikeCapacity());
                        totalSpaces = totalSpaces + Integer.parseInt(locationCapacityLists.get(i).getBikeCapacity());
                    }
                }
            }
            if(locationName.equals("All Locations")){
                totalVacantSpace = totalSpaces - totalOccupiedSpace;
            }
            totalSpaceTV.setText(String.valueOf(totalSpaces));
            totalOccupiedSpaceTV.setText(String.valueOf(totalOccupiedSpace));
            totalVacantSpaceTV.setText(String.valueOf(totalVacantSpace));
        }
    }

    private void initVehicleSpinner(){
        vehicleTypes.add("Car");
        vehicleTypes.add("Bike");

        ArrayAdapter<String> vehicleTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, vehicleTypes);
        vehicleTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(vehicleTypeAdapter);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        if(isCheckClicked){
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else{
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        finish();
    }

    class BackgroundTask extends AsyncTask<String, LocationCapacityList,String>{
        private Context ctx;
        private ProgressDialog progressDialog;
        private boolean isLocationListExists = false;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Fetching locations space...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if(params[0].equals("get")){
                String locationCapacity_url = base_url+"get_locationCapacityList.php?ParkingId="+params[1];
                try{
                    URL url = new URL(locationCapacity_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setDoOutput(true);
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while((line = bufferedReader.readLine())!=null){
                        stringBuilder.append(line).append("\n");
                    }

                    httpURLConnection.disconnect();

                    String result = stringBuilder.toString().trim();

                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    locationCapacityLists.clear();
                    if(jsonArray.length()>0) {
                        isLocationListExists = true;
                        while (count < jsonArray.length()) {
                            JSONObject jo = jsonArray.getJSONObject(count);
                            count++;
                            LocationCapacityList locationCapacityList = new LocationCapacityList(jo.getString("id"), jo.getString("LocationName"),
                                    jo.getString("CarCapacity"), jo.getString("BikeCapacity"),
                                    jo.getString("OccupiedCarCapacity"), jo.getString("OccupiedBikeCapacity"));
                            publishProgress(locationCapacityList);
                        }
                    }
                    inputStream.close();
                    bufferedReader.close();
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
        protected void onProgressUpdate(LocationCapacityList... values) {
            locationCapacityLists.add(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("get")){
                if(isLocationListExists) {
                    locationLists.add("All locations");
                    for (int i = 0; i < locationCapacityLists.size(); i++) {
                        locationLists.add(locationCapacityLists.get(i).getLocationName());
                    }
                }
                else{
                    locationLists.add("No locations found");
                    Toast.makeText(ctx, "No location found. Please add location first...", Toast.LENGTH_SHORT).show();
                    totalSpaceTV.setText("00");
                    totalOccupiedSpaceTV.setText("00");
                    totalVacantSpaceTV.setText("00");
                }
                ArrayAdapter<String> locationsAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, locationLists);
                locationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                locationSpinner.setAdapter(locationsAdapter);
            }
            progressDialog.dismiss();
        }
    }
}