package com.example.duepark_consumer.Location;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.Location.Edit.EditVehicleChargesLocationActivity;
import com.example.duepark_consumer.Model.Location;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DetailLocationActivity extends AppCompatActivity implements View.OnClickListener {

    private String base_url;
    private static final String TAG = "LocationDetailActivity";
    private TextView locationIdTV, locationNameTV, locationTypeTV, locationAddressTV, openTimeTV, closeTimeTV, carChargesTV, bikeChargesTV, carCapacityTV, bikeCapacityTV, passChargesTV;
    private EditText locationNameET, locationAddressET, carCapacityET, bikeCapacityET, carPassChargesET, bikePassChargesET;
    private Spinner locationTypeSpinner;
    private Button locationNameEditBtn, locationTypeEditBtn, locationAddressEditBtn, saveBtn, assignEmployeeBtn, deactivateBtn, activateBtn, setOpenTimeEditBtn, setCloseTimeEditBtn, carChargesEditBtn, bikeChargesEditBtn, passChargesEditBtn, carCapacityEditBtn, bikeCapacityEditBtn;
    private ArrayAdapter<String> dataAdapter;
    private String locationId, locationActiveState, parkingAcronym, generatedParkingId;
    private Location location;
    private SessionManagerHelper sessionManagerHelper;
    private LinearLayout  carPassChargesLayout, bikePassChargesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_location);

        base_url = getResources().getString(R.string.base_url);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            locationId = bundle.getString("locationId");
            locationActiveState = bundle.getString("locationActiveState");
            parkingAcronym = bundle.getString("parkingAcronym");
            generatedParkingId = bundle.getString("generatedParkingId");
        }

        sessionManagerHelper = new SessionManagerHelper(this);
        HashMap<String, String> employeeData = sessionManagerHelper.getEmployeeDetails();


        locationIdTV = findViewById(R.id.locationIdTV);
        locationNameTV = findViewById(R.id.locationNameTV);
        locationTypeTV = findViewById(R.id.locationTypeTV);
        locationAddressTV = findViewById(R.id.locationAddressTV);
        openTimeTV = findViewById(R.id.openTimeTV);
        closeTimeTV = findViewById(R.id.closeTimeTV);
        carChargesTV = findViewById(R.id.carChargesTV);
        carCapacityTV = findViewById(R.id.carCapacityTV);
        bikeChargesTV = findViewById(R.id.bikeChargesTV);
        bikeCapacityTV = findViewById(R.id.bikeCapacityTV);
        passChargesTV = findViewById(R.id.passChargesTV);
        locationNameET = findViewById(R.id.locationNameET);
        locationAddressET = findViewById(R.id.locationAddressET);
        locationTypeSpinner = findViewById(R.id.locationTypeSpinner);
        initSpinner();

        carCapacityET = findViewById(R.id.carCapacityET);
        bikeCapacityET = findViewById(R.id.bikeCapacityET);
        carPassChargesET = findViewById(R.id.carPassChargesET);
        bikePassChargesET = findViewById(R.id.bikePassChargesET);

        carPassChargesLayout = findViewById(R.id.carPassChargesLayout);
        bikePassChargesLayout = findViewById(R.id.bikePassChargesLayout);

        locationNameEditBtn = findViewById(R.id.locationNameEditBtn);
        locationNameEditBtn.setOnClickListener(this);

        locationTypeEditBtn = findViewById(R.id.locationTypeEditBtn);
        locationTypeEditBtn.setOnClickListener(this);

        locationAddressEditBtn = findViewById(R.id.locationAddressEditBtn);
        locationAddressEditBtn.setOnClickListener(this);

        setOpenTimeEditBtn = findViewById(R.id.setOpenTimeEditBtn);
        setOpenTimeEditBtn.setOnClickListener(this);

        setCloseTimeEditBtn = findViewById(R.id.setCloseTimeEditBtn);
        setCloseTimeEditBtn.setOnClickListener(this);

        carChargesEditBtn = findViewById(R.id.carChargesEditBtn);
        carChargesEditBtn.setOnClickListener(this);

        bikeChargesEditBtn = findViewById(R.id.bikeChargesEditBtn);
        bikeChargesEditBtn.setOnClickListener(this);

        carCapacityEditBtn = findViewById(R.id.carCapacityEditBtn);
        carCapacityEditBtn.setOnClickListener(this);

        bikeCapacityEditBtn = findViewById(R.id.bikeCapacityEditBtn);
        bikeCapacityEditBtn.setOnClickListener(this);

        passChargesEditBtn = findViewById(R.id.passChargesEditBtn);
        passChargesEditBtn.setOnClickListener(this);

        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        assignEmployeeBtn = findViewById(R.id.assignEmployeeBtn);
        assignEmployeeBtn.setOnClickListener(this);

        deactivateBtn = findViewById(R.id.deactivateBtn);
        deactivateBtn.setOnClickListener(this);

        activateBtn = findViewById(R.id.activateBtn);
        activateBtn.setOnClickListener(this);

        if(employeeData.get(SessionManagerHelper.EmployeeRole).equals("Manager")){
            deactivateBtn.setVisibility(View.GONE);
            activateBtn.setVisibility(View.GONE);
        }

        if(locationActiveState.equals("0")){
            saveBtn.setVisibility(View.INVISIBLE);
            assignEmployeeBtn.setVisibility(View.INVISIBLE);
            deactivateBtn.setVisibility(View.INVISIBLE);
/*            locationNameEditBtn.setVisibility(View.INVISIBLE);
            locationTypeEditBtn.setVisibility(View.INVISIBLE);
            locationAddressEditBtn.setVisibility(View.INVISIBLE);*/
            activateBtn.setVisibility(View.VISIBLE);
        }

        final ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("get", locationId);
    }



    @Override
    public void onClick(View view) {
        if(view == locationNameEditBtn){
            if(locationActiveState.equals("0")){
                Toast.makeText(this, "Please activate the location for updating data..", Toast.LENGTH_SHORT).show();
            }
            else {
                if(locationNameTV.getVisibility() == View.VISIBLE){
                    locationNameTV.setVisibility(View.INVISIBLE);
                    locationNameET.setVisibility(View.VISIBLE);
                }
                else{
                    locationNameTV.setVisibility(View.VISIBLE);
                    locationNameET.setVisibility(View.INVISIBLE);
                    locationNameTV.setText(locationNameET.getText().toString().trim());
                }
            }
        }
        else if(view == locationTypeEditBtn){
            if(locationActiveState.equals("0")){
                Toast.makeText(this, "Please activate the location for updating data..", Toast.LENGTH_SHORT).show();
            }
            else{
                if(locationTypeTV.getVisibility() == View.VISIBLE){
                    locationTypeTV.setVisibility(View.INVISIBLE);
                    locationTypeSpinner.setVisibility(View.VISIBLE);
                }
                else{
                    locationTypeTV.setVisibility(View.VISIBLE);
                    locationTypeSpinner.setVisibility(View.INVISIBLE);
                    locationTypeTV.setText(locationTypeSpinner.getSelectedItem().toString());
                }
            }
        }
        else if(view == locationAddressEditBtn) {
            if (locationActiveState.equals("0")) {
                Toast.makeText(this, "Please activate the location for updating data..", Toast.LENGTH_SHORT).show();
            } else {
                if (locationAddressTV.getVisibility() == View.VISIBLE) {
                    locationAddressTV.setVisibility(View.INVISIBLE);
                    locationAddressET.setVisibility(View.VISIBLE);
                } else {
                    locationAddressTV.setVisibility(View.VISIBLE);
                    locationAddressET.setVisibility(View.INVISIBLE);
                    locationAddressTV.setText(locationAddressET.getText().toString().trim());
                }
            }
        }
        else if(view == setOpenTimeEditBtn){
            if(locationActiveState.equals("0")){
                Toast.makeText(this, "Please activate the location for updating data..", Toast.LENGTH_SHORT).show();
            }
            else {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(DetailLocationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int selectedMinute) {
                        String time = null;
                        String minuteString = selectedMinute < 10 ? "0" + selectedMinute : "" + selectedMinute;
                        if (hourOfDay >= 0 && hourOfDay < 12) {
                            time = hourOfDay + ":" + minuteString + " AM";
                        } else {
                            if (hourOfDay == 12) {
                                time = hourOfDay + ":" + minuteString + " PM";
                            } else {
                                hourOfDay = hourOfDay - 12;
                                time = hourOfDay + ":" + minuteString + " PM";
                            }
                        }
                        openTimeTV.setText(time);
                    }
                }, hour, minute, false); // if true than 24 hour format else false than 12 hour format
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        }
        else if(view == setCloseTimeEditBtn){
            if(locationActiveState.equals("0")){
                Toast.makeText(this, "Please activate the location for updating data..", Toast.LENGTH_SHORT).show();
            }
            else {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(DetailLocationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int selectedMinute) {
                        String time = null;
                        String minuteString = selectedMinute < 10 ? "0" + selectedMinute : "" + selectedMinute;
                        if (hourOfDay >= 0 && hourOfDay < 12) {
                            time = hourOfDay + ":" + minuteString + " AM";
                        } else {
                            if (hourOfDay == 12) {
                                time = hourOfDay + ":" + minuteString + " PM";
                            } else {
                                hourOfDay = hourOfDay - 12;
                                time = hourOfDay + ":" + minuteString + " PM";
                            }
                        }
                        closeTimeTV.setText(time);
                    }
                }, hour, minute, false);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        }
        else if(view == carChargesEditBtn){
            if(locationActiveState.equals("0")){
                Toast.makeText(this, "Please activate the location for updating data..", Toast.LENGTH_SHORT).show();
            }
            else {
                /*if (carChargesTV.getVisibility() == View.VISIBLE) {
                    carChargesTV.setVisibility(View.GONE);
                    carChargesRadioGrp.setVisibility(View.VISIBLE);
                    if (paidCarRadioBtn.isChecked()) {
                        carHourlyRateLayout.setVisibility(View.VISIBLE);
                        carFullDayRateLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    carChargesTV.setVisibility(View.VISIBLE);
                    carChargesRadioGrp.setVisibility(View.GONE);
                    carHourlyRateLayout.setVisibility(View.GONE);
                    carFullDayRateLayout.setVisibility(View.GONE);
                    if (!carHourET.getText().toString().isEmpty() && !carRateET.getText().toString().isEmpty() && !carFullDayRateET.getText().toString().isEmpty()) {
                        carChargesTV.setText(carHourET.getText().toString().trim() + " Hour: Rs" + carRateET.getText().toString().trim() + " / Full Day: Rs" + carFullDayRateET.getText().toString().trim());
                        paidCarRadioBtn.setChecked(true);
                    }
                }*/
                Intent i = new Intent(this, EditVehicleChargesLocationActivity.class);
                i.putExtra("IsCar",true);
                i.putExtra("LocationId",locationId);
                i.putExtra("LocationActiveState", locationActiveState);
                i.putExtra("ParkingAcronym", parkingAcronym);
                i.putExtra("GeneratedParkingId", generatedParkingId);
                if(location.getCarChargeType().equals("null")){
                    i.putExtra("IsAdd",true);
                }
                else{
                    i.putExtra("IsEdit", true);
                    i.putExtra("CurrentChargeType", location.getCarChargeType());
                    if(location.getCarChargeType().equals("Paid")){
                        i.putExtra("CurrentFixHourValue", location.getCarFixHour());
                        i.putExtra("CurrentFixHourChargeValue", location.getCarFixHourRate());
                        i.putExtra("CurrentChargesOptionValue", location.getCarChargesOption());
                        i.putExtra("CurrentChargesOptionRateValue", location.getCarChargesOptionRate());
                    }
                }
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        }
        else if(view == carCapacityEditBtn){
            if(locationActiveState.equals("0")){
                Toast.makeText(this, "Please activate the location for updating data..", Toast.LENGTH_SHORT).show();
            }
            else {
                if (carCapacityTV.getVisibility() == View.VISIBLE) {
                    carCapacityTV.setVisibility(View.INVISIBLE);
                    carCapacityET.setVisibility(View.VISIBLE);
                } else {
                    carCapacityTV.setVisibility(View.VISIBLE);
                    carCapacityET.setVisibility(View.INVISIBLE);
                    carCapacityTV.setText(carCapacityET.getText().toString().trim());
                }
            }
        }
        else if(view == bikeChargesEditBtn){
            if(locationActiveState.equals("0")){
                Toast.makeText(this, "Please activate the location for updating data..", Toast.LENGTH_SHORT).show();
            }
            else {
               /* if (bikeChargesTV.getVisibility() == View.VISIBLE) {
                    bikeChargesTV.setVisibility(View.INVISIBLE);
                    bikeChargesRadioGrp.setVisibility(View.VISIBLE);
                    if (paidBikeRadioBtn.isChecked()) {
                        bikeHourlyRateLayout.setVisibility(View.VISIBLE);
                        bikeFullDayRateLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    bikeChargesTV.setVisibility(View.VISIBLE);
                    bikeChargesRadioGrp.setVisibility(View.GONE);
                    bikeHourlyRateLayout.setVisibility(View.GONE);
                    bikeFullDayRateLayout.setVisibility(View.GONE);
                    if (!bikeHourET.getText().toString().isEmpty() && !bikeRateET.getText().toString().isEmpty() && !bikeFullDayRateET.getText().toString().isEmpty()) {
                        bikeChargesTV.setText(bikeHourET.getText().toString().trim() + " Hour: Rs" + bikeRateET.getText().toString().trim() + " / Full Day: Rs" + bikeFullDayRateET.getText().toString().trim());
                        paidBikeRadioBtn.setChecked(true);
                    }
                }*/
                Intent i = new Intent(this, EditVehicleChargesLocationActivity.class);
                i.putExtra("IsBike",true);
                i.putExtra("LocationId",locationId);
                i.putExtra("LocationActiveState", locationActiveState);
                i.putExtra("ParkingAcronym", parkingAcronym);
                i.putExtra("GeneratedParkingId", generatedParkingId);
                if(location.getBikeChargeType().equals("null")){
                    i.putExtra("IsAdd",true);
                }
                else{
                    i.putExtra("IsEdit", true);
                    i.putExtra("CurrentChargeType", location.getBikeChargeType());
                    if(location.getBikeChargeType().equals("Paid")){
                        i.putExtra("CurrentFixHourValue", location.getBikeFixHour());
                        i.putExtra("CurrentFixHourChargeValue", location.getBikeFixHourRate());
                        i.putExtra("CurrentChargesOptionValue", location.getBikeChargesOption());
                        i.putExtra("CurrentChargesOptionRateValue", location.getBikeChargesOptionRate());
                    }
                }
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        }
        else if(view == bikeCapacityEditBtn){
            if(locationActiveState.equals("0")){
                Toast.makeText(this, "Please activate the location for updating data..", Toast.LENGTH_SHORT).show();
            }
            else {
                if (bikeCapacityTV.getVisibility() == View.VISIBLE) {
                    bikeCapacityTV.setVisibility(View.INVISIBLE);
                    bikeCapacityET.setVisibility(View.VISIBLE);
                } else {
                    bikeCapacityTV.setVisibility(View.VISIBLE);
                    bikeCapacityET.setVisibility(View.INVISIBLE);
                    bikeCapacityTV.setText(bikeCapacityET.getText().toString().trim());
                }
            }
        }
        else if(view == passChargesEditBtn){
            if(locationActiveState.equals("0")){
                Toast.makeText(this, "Please activate the location for updating data..", Toast.LENGTH_SHORT).show();
            }
            else {
                if (passChargesTV.getVisibility() == View.VISIBLE) {
                    passChargesTV.setVisibility(View.INVISIBLE);
                    carPassChargesLayout.setVisibility(View.VISIBLE);
                    bikePassChargesLayout.setVisibility(View.VISIBLE);
                } else {
                    passChargesTV.setVisibility(View.VISIBLE);
                    carPassChargesLayout.setVisibility(View.GONE);
                    bikePassChargesLayout.setVisibility(View.GONE);
                    if (!carPassChargesET.getText().toString().isEmpty() && !bikePassChargesET.getText().toString().isEmpty()) {
                        passChargesTV.setText("Car: Rs" + carPassChargesET.getText().toString() + " / Bike: Rs" + bikePassChargesET.getText().toString());
                    }
                }
            }
        }
        else if(view == saveBtn){
            updateLocationData();
        }
        else if(view == assignEmployeeBtn){
            Intent intent = new Intent(DetailLocationActivity.this, AssignedEmployeeActivity.class);
            intent.putExtra("locationId", locationId);
            intent.putExtra("locationActiveState", locationActiveState);
            intent.putExtra("parkingAcronym", parkingAcronym);
            intent.putExtra("generatedParkingId", generatedParkingId);
            intent.putExtra("generatedLocationId", location.getGeneratedLocationId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        else if(view == deactivateBtn){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetailLocationActivity.this);
            if(locationActiveState.equals("1")){
                alertBuilder.setTitle("Deactivate Location");
                alertBuilder.setMessage("Are you sure you want to deactivate location ?");
            }
            alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BackgroundTask backgroundTask = new BackgroundTask(DetailLocationActivity.this);
                    backgroundTask.execute("deactivate", locationId);
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
        else if(view == activateBtn){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetailLocationActivity.this);
            if(locationActiveState.equals("0")){
                alertBuilder.setTitle("Activate Location");
                alertBuilder.setMessage("Are you sure you want to activate location ?");
            }
            alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BackgroundTask backgroundTask = new BackgroundTask(DetailLocationActivity.this);
                    backgroundTask.execute("activate", locationId);
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
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(DetailLocationActivity.this, LocationActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void updateLocationData(){
        String locationName = locationNameET.getText().toString().trim();
        String locationType = locationTypeSpinner.getSelectedItem().toString();
        String locationAddress = locationAddressET.getText().toString().trim();
        String openTime = openTimeTV.getText().toString().trim();
        String closeTime = closeTimeTV.getText().toString().trim();
        String carCapacity = carCapacityET.getText().toString().trim();
        String bikeCapacity = bikeCapacityET.getText().toString().trim();
        String carPassCharges = carPassChargesET.getText().toString();
        String bikePassCharges = bikePassChargesET.getText().toString();

        if(locationName.isEmpty()){
            fieldEmpty(locationNameET);
        }
        else if(locationAddress.isEmpty()){
            fieldEmpty(locationAddressET);
        }
        else if(openTime.isEmpty()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            openTimeTV.setError("Field can not be empty",myIcon);
            Toast.makeText(this, "Set opening time of location.", Toast.LENGTH_SHORT).show();
        }
        else if(closeTime.isEmpty()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            openTimeTV.setError("Field can not be empty",myIcon);
            Toast.makeText(this, "Set closing time of location.", Toast.LENGTH_SHORT).show();
        }
        else if(carCapacity.isEmpty()){
            fieldEmpty(carCapacityET);
        }
        else if(bikeCapacity.isEmpty()){
            fieldEmpty(bikeCapacityET);
        }
        else if(carPassCharges.isEmpty()){
            fieldEmpty(carPassChargesET);
        }
        else if(bikePassCharges.isEmpty()){
            fieldEmpty(bikePassChargesET);
        }
        else{
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute("update", locationId, location.getLocationDetailId(), locationName, locationType, locationAddress, openTime, closeTime, location.getCarChargeType(),
                    location.getCarFixHour(), location.getCarFixHourRate(), location.getCarChargesOption(), location.getCarChargesOptionRate(), carCapacity,
                    location.getBikeChargeType(), location.getBikeFixHour(), location.getBikeFixHourRate(), location.getBikeChargesOption(), location.getBikeChargesOptionRate(),
                    bikeCapacity, carPassCharges, bikePassCharges);
        }
    }

    private void fieldEmpty(EditText input){
        Drawable myIcon = getResources().getDrawable(R.drawable.error);
        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
        input.setError("Field can not be Empty",myIcon);
        requestFocus(input);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void initSpinner(){
        List<String> type = new ArrayList<>();
        type.add("Private");
        type.add("Public");

        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, type);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        locationTypeSpinner.setAdapter(dataAdapter);
    }

    class BackgroundTask extends AsyncTask<String, Location, String>{
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
                progressDialog.setMessage("Fetching location data...");
                String getLocation_url = base_url+"get_location.php?LocationId="+params[1];
                //String getLocation_url = "https://duepark.000webhostapp.com/consumer/get_location.php?LocationId="+params[1];
                try{
                    URL url = new URL(getLocation_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }
                    httpURLConnection.disconnect();

                    String jsonString = stringBuilder.toString().trim();

                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    while(count<jsonArray.length()) {
                        JSONObject jo = jsonArray.getJSONObject(count);
                        location = new Location(jo.getString("id"), jo.getString("GeneratedLocationId"), jo.getString("LocationName"),
                                jo.getString("LocationType"), jo.getString("LocationAddress"), jo.getString("LocationDate"),
                                jo.getString("LocationTime"), jo.getString("LocationActiveState"), jo.getString("LocationDetailId"), jo.getString("LocationOpenTime"),
                                jo.getString("LocationCloseTime"), jo.getString("CarChargeType"), jo.getString("CarFixHour"), jo.getString("CarFixHourRate"),
                                jo.getString("CarChargesOption"), jo.getString("CarChargesOptionRate"), jo.getString("CarCapacity"), jo.getString("BikeChargeType"),
                                jo.getString("BikeFixHour"), jo.getString("BikeFixHourRate"), jo.getString("BikeChargesOption"), jo.getString("BikeChargesOptionRate"),
                                jo.getString("BikeCapacity"),jo.getString("CarMonthlyPassRate"), jo.getString("BikeMonthlyPassRate"));
                        count++;
                    }
                    inputStream.close();
                    bufferedReader.close();
                    Log.d(TAG, "doInBackground: "+jsonString);
                    return "get";
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(params[0].equals("update")){
                progressDialog.setMessage("Updating Data...");
                String updateLocation_url = base_url+"update_location.php";
                //String updateLocation_url = "https://duepark.000webhostapp.com/consumer/update_location.php";
                try{
                    URL url = new URL(updateLocation_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("LocationId","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                            +URLEncoder.encode("LocationDetailId","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"
                            +URLEncoder.encode("LocationName","UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8")+"&"
                            +URLEncoder.encode("LocationType","UTF-8")+"="+URLEncoder.encode(params[4],"UTF-8")+"&"
                            +URLEncoder.encode("LocationAddress","UTF-8")+"="+URLEncoder.encode(params[5],"UTF-8")+"&"
                            +URLEncoder.encode("LocationOpenTime","UTF-8")+"="+URLEncoder.encode(params[6],"UTF-8")+"&"
                            +URLEncoder.encode("LocationCloseTime","UTF-8")+"="+URLEncoder.encode(params[7],"UTF-8")+"&"
                            +URLEncoder.encode("CarChargeType","UTF-8")+"="+URLEncoder.encode(params[8],"UTF-8")+"&"
                            +URLEncoder.encode("CarFixHour","UTF-8")+"="+URLEncoder.encode(params[9],"UTF-8")+"&"
                            +URLEncoder.encode("CarFixHourRate","UTF-8")+"="+URLEncoder.encode(params[10],"UTF-8")+"&"
                            +URLEncoder.encode("CarChargesOption","UTF-8")+"="+URLEncoder.encode(params[11],"UTF-8")+"&"
                            +URLEncoder.encode("CarChargesOptionRate","UTF-8")+"="+URLEncoder.encode(params[12],"UTF-8")+"&"
                            +URLEncoder.encode("CarCapacity","UTF-8")+"="+URLEncoder.encode(params[13],"UTF-8")+"&"
                            +URLEncoder.encode("BikeChargeType","UTF-8")+"="+URLEncoder.encode(params[14],"UTF-8")+"&"
                            +URLEncoder.encode("BikeFixHour","UTF-8")+"="+URLEncoder.encode(params[15],"UTF-8")+"&"
                            +URLEncoder.encode("BikeFixHourRate","UTF-8")+"="+URLEncoder.encode(params[16],"UTF-8")+"&"
                            +URLEncoder.encode("BikeChargesOption","UTF-8")+"="+URLEncoder.encode(params[17],"UTF-8")+"&"
                            +URLEncoder.encode("BikeChargesOptionRate","UTF-8")+"="+URLEncoder.encode(params[18],"UTF-8")+"&"
                            +URLEncoder.encode("BikeCapacity","UTF-8")+"="+URLEncoder.encode(params[19],"UTF-8")+"&"
                            +URLEncoder.encode("CarMonthlyPassRate","UTF-8")+"="+URLEncoder.encode(params[20],"UTF-8")+"&"
                            +URLEncoder.encode("BikeMonthlyPassRate","UTF-8")+"="+URLEncoder.encode(params[21],"UTF-8");
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
                    Log.d(TAG, "doInBackground: Update Call "+result.toString());
                    return result.toString();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(params[0].equals("deactivate")){
                progressDialog.setMessage("Deactivating location...");
                String updateLocationActivationState_url = base_url+"set_locationActivationState.php";
                //String updateLocationActivationState_url = "https://duepark.000webhostapp.com/consumer/set_locationActivationState.php";
                try{
                    URL url = new URL(updateLocationActivationState_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("LocationId","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                            +URLEncoder.encode("LocationActiveState","UTF-8")+"="+URLEncoder.encode("false","UTF-8");
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
                    Log.d(TAG, "doInBackground: "+result.toString());
                    return result.toString();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(params[0].equals("activate")){
                progressDialog.setMessage("Activating location...");
                String updateLocationActivationState_url = base_url+"set_locationActivationState.php";
                //String updateLocationActivationState_url = "https://duepark.000webhostapp.com/consumer/set_locationActivationState.php";
                try{
                    URL url = new URL(updateLocationActivationState_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("LocationId","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                            +URLEncoder.encode("LocationActiveState","UTF-8")+"="+URLEncoder.encode("true","UTF-8");
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
                    Log.d(TAG, "doInBackground: "+result.toString());
                    return result.toString();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("get")){
                DecimalFormat df = new DecimalFormat("000");
                String parkingid = parkingAcronym + df.format(Integer.parseInt(generatedParkingId));
                char char_locationId = (char) (Integer.parseInt(location.getGeneratedLocationId()) + 'A' - 1);
                String location_id = parkingid + char_locationId;
                locationIdTV.setText("Location Id : "+location_id);
                locationNameTV.setText(location.getLocationName());
                locationTypeTV.setText(location.getLocationType());
                locationAddressTV.setText(location.getLocationAddress());
                locationNameET.setText(location.getLocationName());
                int spinnerPostion = dataAdapter.getPosition(location.getLocationType());
                locationTypeSpinner.setSelection(spinnerPostion);
                locationAddressET.setText(location.getLocationAddress());
                if(location.getOpenTime().equals("null")){
                    setOpenTimeEditBtn.setText("Add");
                }
                else{
                    openTimeTV.setText(location.getOpenTime());
                }
                if(location.getCloseTime().equals("null")){
                    setCloseTimeEditBtn.setText("Add");
                }
                else{
                    closeTimeTV.setText(location.getCloseTime());
                }
                if(location.getCarChargeType().equals("null")){
                    //carChargesRadioGrp.setVisibility(View.INVISIBLE);
                    //carChargesTV.setVisibility(View.INVISIBLE);
                    //carChargesEditBtn.setVisibility(View.INVISIBLE);
                    carChargesEditBtn.setText("Add");
                }
                else{
                    carChargesEditBtn.setText("Edit");
                    //carChargesTV.setText(location.getCarChargeType());

                    if(location.getCarChargeType().equals("Paid")){
                        //paidCarRadioBtn.setChecked(true);
                        String chargesOption = null;
                        if(location.getCarChargesOption().equals("PerHour")){
                            chargesOption = "Per Hour: Rs";
                        }
                        else{
                            chargesOption = "Full Day: Rs";
                        }
                        carChargesTV.setText(location.getCarFixHour()+" Hour: Rs"+location.getCarFixHourRate()+" / "+chargesOption+""+location.getCarChargesOptionRate());
                    }
                    else{
                        carChargesTV.setText("Free");
                        //freeCarRadioBtn.setChecked(true);
                    }
                }
                if(location.getBikeChargeType().equals("null")){
                    /*bikeChargesRadioGrp.setVisibility(View.VISIBLE);
                    bikeChargesTV.setVisibility(View.INVISIBLE);
                    bikeChargesEditBtn.setVisibility(View.INVISIBLE);*/
                    bikeChargesEditBtn.setText("Add");
                }
                else{
                    bikeChargesEditBtn.setText("Edit");
                    if(location.getBikeChargeType().equals("Paid")){
                        String chargesOption = null;
                        if(location.getBikeChargesOption().equals("PerHour")){
                            chargesOption = "Per Hour: Rs";
                        }
                        else{
                            chargesOption = "Full Day: Rs";
                        }
                        bikeChargesTV.setText(location.getBikeFixHour()+" Hour: Rs"+location.getBikeFixHourRate()+" / "+chargesOption+""+location.getBikeChargesOptionRate());
                    }
                    else{
                        bikeChargesTV.setText("Free");
                    }
                }
                if(location.getCarCapacity().equals("null")){
                    carCapacityTV.setVisibility(View.INVISIBLE);
                    carCapacityET.setVisibility(View.VISIBLE);
                    carCapacityEditBtn.setVisibility(View.INVISIBLE);
                }
                else{
                    carCapacityTV.setText(location.getCarCapacity());
                    carCapacityET.setText(location.getCarCapacity());
                }
                if(location.getBikeCapacity().equals("null")){
                    bikeCapacityET.setVisibility(View.VISIBLE);
                    bikeCapacityTV.setVisibility(View.INVISIBLE);
                    bikeCapacityEditBtn.setVisibility(View.INVISIBLE);
                }
                else{
                    bikeCapacityTV.setText(location.getBikeCapacity());
                    bikeCapacityET.setText(location.getBikeCapacity());
                }
                if(location.getBikeMonthlyPass().equals("null") && location.getCarMonthlyPass().equals("null")){
                    carPassChargesLayout.setVisibility(View.VISIBLE);
                    bikePassChargesLayout.setVisibility(View.VISIBLE);
                    passChargesEditBtn.setVisibility(View.GONE);
                    passChargesTV.setVisibility(View.GONE);
                }
                else{
                    passChargesTV.setText("Car: Rs"+location.getCarMonthlyPass()+" / Bike: Rs"+location.getBikeMonthlyPass());
                    carPassChargesET.setText(location.getCarMonthlyPass());
                    bikePassChargesET.setText(location.getBikeMonthlyPass());
                }
            }
            else if(result.equals("UpdatedLocation")){
                Toast.makeText(ctx, "Updated location data successfully..", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else if(result.equals("NotupdatedLocation")){
                Toast.makeText(ctx, "Something went wrong.. Cannot update location data..", Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("Deactivate")){
                Toast.makeText(ctx, "Deactivate location successfully..", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else if(result.equals("Activate")){
                Toast.makeText(ctx, "Activate location successfully..", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            progressDialog.dismiss();
        }
    }
}