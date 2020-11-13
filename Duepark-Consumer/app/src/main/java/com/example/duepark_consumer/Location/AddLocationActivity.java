package com.example.duepark_consumer.Location;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.HomeActivity;
import com.example.duepark_consumer.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddLocationActivity extends AppCompatActivity {

    private String base_url;
    private static final String TAG = "AddLocationActivity";
    private SessionManagerHelper sessionManagerHelper;
    private EditText inputLocationName;
    private Spinner locationTypeSpinner;
    private TextView addressTV;
    private ArrayAdapter<String> dataAdapter;
    private String address, city, acronym;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final static String LocationName = "com.example.duepark_consumer.LocationName";
    private final static String LocationType = "com.example.duepark_consumer.LocationType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(this);
        sharedPreferences = getSharedPreferences("Location", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            address = bundle.getString("address");
            city = bundle.getString("city");
            acronym = bundle.getString("acronym");
        }

        inputLocationName = findViewById(R.id.inputLocationName);
        locationTypeSpinner = findViewById(R.id.locationTypeSpinner);
        addressTV = findViewById(R.id.addressTV);
        initSpinner();

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button addAddressBtn = findViewById(R.id.addAddressBtn);
        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAddress();
            }
        });

        Button saveLocationBtn = findViewById(R.id.saveLocationBtn);
        saveLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLocation();
            }
        });

        if(address!=null && !address.equals("null")){
            addressTV.setText(address);
        }

        getLocationData();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, LocationActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void addAddress(){
        Dexter.withActivity(AddLocationActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        storeLocationData();
                        Intent i = new Intent(AddLocationActivity.this, MapsActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(AddLocationActivity.this));
                            builder.setTitle("Permission Denied")
                                    .setMessage("Permission to access device location is permanently denied. you need to go to setting to allow the permission.")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", Objects.requireNonNull(getPackageName()), null));
                                        }
                                    })
                                    .show();
                        } else {
                            Toast.makeText(AddLocationActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void initSpinner(){
        List<String> type = new ArrayList<>();
        type.add(0, "-- Select Location Type --");
        type.add("Private");
        type.add("Public");

        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, type){
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        locationTypeSpinner.setAdapter(dataAdapter);
    }

    private void getLocationData(){
        if(sharedPreferences.contains(LocationName)){
            inputLocationName.setText(sharedPreferences.getString(LocationName,""));
        }
        if(sharedPreferences.contains(LocationType)){
            int spinnerPosition = dataAdapter.getPosition(sharedPreferences.getString(LocationType, ""));
            locationTypeSpinner.setSelection(spinnerPosition);
        }
    }

    private void storeLocationData(){
        if(inputLocationName.getText() != null){
            editor.remove(LocationName);
            //editor.apply();
            editor.putString(LocationName, inputLocationName.getText().toString().trim());
            //editor.apply();
            editor.commit();
        }
        if(locationTypeSpinner.getSelectedItemPosition() != 0){
            editor.remove(LocationType);
            //editor.apply();
            editor.putString(LocationType, locationTypeSpinner.getSelectedItem().toString());
            //editor.apply();
            editor.commit();
        }
    }

    private void addLocation(){
        String locationName = inputLocationName.getText().toString().trim();
        //String locationType = locationTypeSpinner.getSelectedItem().toString();
        String locationAddress = addressTV.getText().toString().trim();
        String locationDate = getCurrentDate();
        String locationTime = getCurrentTime();

        if(locationName.isEmpty()){
            fieldEmpty(inputLocationName);
        }
        else if(locationTypeSpinner.getSelectedItemId() == 0){
            Toast.makeText(AddLocationActivity.this,"Please select location type", Toast.LENGTH_SHORT).show();
        }
        else if(locationAddress.isEmpty()){
            Toast.makeText(AddLocationActivity.this,"Please provide the location address", Toast.LENGTH_SHORT).show();
        }
        else{
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute(locationName, locationAddress, locationTypeSpinner.getSelectedItem().toString(), locationDate, locationTime);
        }
    }

    private void fieldEmpty(EditText input){
        Drawable myIcon = getResources().getDrawable(R.drawable.error);
        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
        input.setError("Field can't be Empty",myIcon);
        requestFocus(input);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private String getCurrentDate(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(today);
    }

    private String getCurrentTime(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        return format.format(today);
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{
        private Context ctx;
        private ProgressDialog progressDialog;

        BackgroundTask(Context ctx){
            this.ctx = ctx;
            this.progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Creating Location...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String addLocationUrl = base_url+"add_location.php";
            //String addLocationUrl = "https://duepark.000webhostapp.com/consumer/add_location.php";
            try{
                HashMap<String, String> employee = sessionManagerHelper.getEmployeeDetails();
                URL url = new URL(addLocationUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream ops = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                String data = URLEncoder.encode("LocationName","UTF-8")+"="+URLEncoder.encode(params[0],"UTF-8")+"&"
                        +URLEncoder.encode("LocationAddress","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                        +URLEncoder.encode("LocationType", "UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"+
                        URLEncoder.encode("LocationDate", "UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8")+"&"+
                        URLEncoder.encode("LocationTime", "UTF-8")+"="+URLEncoder.encode(params[4],"UTF-8")+"&"+
                        URLEncoder.encode("LocationActiveState", "UTF-8")+"="+URLEncoder.encode("true","UTF-8") +"&"+
                        URLEncoder.encode("ParkingId", "UTF-8")+"="+URLEncoder.encode(employee.get(SessionManagerHelper.ParkingId), "UTF-8");
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
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("0")){
                Toast.makeText(ctx, "Something went wrong.. Location can't created.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(ctx, "Location created successfully", Toast.LENGTH_SHORT).show();
                editor.clear();
                editor.apply();
                onBackPressed();
            }
            progressDialog.dismiss();
        }
    }
}