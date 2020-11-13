package com.example.duepark_admin.DetailPartner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.duepark_admin.R;
import com.example.duepark_admin.Service.addListenerOnTextChange;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfilePartnerActivity extends AppCompatActivity implements View.OnClickListener {

    private String base_url;
    private String id, parking, parking_city, abbreviation, address;
    private EditText parkingName, ownerName, mobileNumber, phoneNumber, emailAddress;
    private TextView parkingAddress, parkingId;
    private Spinner spinner;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_partner);

        base_url = getResources().getString(R.string.base_url);
        sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.remove("Data");
        editor.apply();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            address = bundle.getString("Address");
            abbreviation = bundle.getString("Acronym");
            parking_city = bundle.getString("City");
            id = bundle.getString("Id");
            parking = bundle.getString("ParkingName");
        }

        getIdData();

        parkingId = findViewById(R.id.parkingId);
        parkingName = findViewById(R.id.parkingName);
        ownerName = findViewById(R.id.ownerName);
        mobileNumber = findViewById(R.id.mobileNumber);
        phoneNumber = findViewById(R.id.phoneNumber);
        emailAddress = findViewById(R.id.emailAddress);
        spinner = findViewById(R.id.type_spinner);
        parkingAddress = findViewById(R.id.parkingAddress);

        if(!address.isEmpty()){
            parkingAddress.setText(address);
        }

        TextView parking_name = findViewById(R.id.parking_name);
        parking_name.setText(parking);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(this);

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        Button changeAddressBtn = findViewById(R.id.changeAddressBtn);
        changeAddressBtn.setOnClickListener(this);

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("get",id);

        initSpinner();
        //editTextChangeMethod();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.backBtn:
                onBackPressed();
                break;

            case R.id.cancelBtn:
                onBackPressed();
                break;

            case R.id.saveBtn:
                saveData();
                break;

            case R.id.changeAddressBtn:
                openMap();
                break;
        }
    }

    private void openMap(){
        Dexter.withActivity(ProfilePartnerActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        startActivity(new Intent(ProfilePartnerActivity.this, ChangeAddressActivity.class));
                        storeIdData();
                        finish();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePartnerActivity.this);
                            builder.setTitle("Permission Denied")
                                    .setMessage("Permission to access device location is permanently denied. you need to go to setting to allow the permission.")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                        }
                                    })
                                    .show();
                        } else {
                            Toast.makeText(ProfilePartnerActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void getIdData(){
        if(sharedPreferences.contains("Id")){
            id = sharedPreferences.getString("Id","");
        }
        if(sharedPreferences.contains("ParkingName")){
            parking = sharedPreferences.getString("ParkingName", "");
        }
    }

    private void storeIdData(){
        if(id!=null){
            editor.remove("Id");
            editor.apply();
            editor.putString("Id", id);
            editor.apply();
        }
        if(parking!=null){
            editor.remove("ParkingName");
            editor.apply();
            editor.putString("ParkingName", parking);
            editor.apply();
        }
    }

    private void initSpinner(){
        List<String> type = new ArrayList<>();
        type.add(0, "-- Select Type --");
        type.add("Personal");
        type.add("Business");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, type){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    return false;
                }
                else{
                    return true;
                }
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    private void saveData() {
        String emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
        String mobileRegex = "^[6-9][0-9]{9}$";

        final String parking_name = parkingName.getText().toString().trim();
        final String owner_name = ownerName.getText().toString().trim();
        final String mobile_number  = mobileNumber.getText().toString().trim();
        final String phone_number = phoneNumber.getText().toString().trim();
        final String email_address = emailAddress.getText().toString().trim();
        final String parking_address = parkingAddress.getText().toString().trim();

        Pattern emailPattern = Pattern.compile(emailRegex);
        Pattern mobilePattern = Pattern.compile(mobileRegex);

        Matcher numberMatches = mobilePattern.matcher(mobile_number);
        Matcher emailMatches = emailPattern.matcher(email_address);

        if(parking_name.isEmpty()){
            fieldEmpty(parkingName);
        }
        else if(owner_name.isEmpty()){
            fieldEmpty(ownerName);
        }
        else if(mobile_number.isEmpty()){
            fieldEmpty(mobileNumber);
        }
        else if(!numberMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            mobileNumber.setError("Please enter valid mobile number.!",myIcon);
            requestFocus(mobileNumber);
        }
        else if(email_address.isEmpty()){
            fieldEmpty(emailAddress);
        }
        else if(!emailMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            emailAddress.setError("Please check your email id.",myIcon);
            requestFocus(emailAddress);
        }
        else if(parking_address.isEmpty()) {
            Toast.makeText(this, "Please add the parking address", Toast.LENGTH_SHORT).show();
        }
        else if(spinner.getSelectedItemId() == 0) {
            Toast.makeText(this,"Please select type", Toast.LENGTH_SHORT).show();
        }
        else{
            Log.d("Result", abbreviation+"\n"+parking_name+"\n"+owner_name+"\n"+mobile_number+"\n"+phone_number+"\n"+
                    spinner.getSelectedItemId()+"\n"+
                    email_address+"\n"+address+"\n"+parking_city);
            BackgroundTask backgroundTask =new BackgroundTask(this);
            backgroundTask.execute("save",id, abbreviation, parking_name, owner_name, mobile_number, phone_number, email_address,
                    address, parking_city, spinner.getSelectedItem().toString());
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

    @Override
    public void onBackPressed() {
        editor.remove("Data");
        editor.apply();
        Intent intent = new Intent(ProfilePartnerActivity.this, DetailPartnerActivity.class);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        /*intent.putExtra("Id", id);
        intent.putExtra("ParkingName", parking);*/
        startActivity(intent);
        finish();
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{

        private Context ctx;
        private String acronym, pi, pn, on, mn, phone, e, pt, pa, result;
        private ProgressDialog progressDialog;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading Data...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String keyword = params[0];
            if(keyword.equals("get")) {
                String id = params[1];
                String detail_url = base_url+"get_parking.php?ParkingId="+id;
                //String detail_url = "https://duepark.000webhostapp.com/get_parking.php?id="+id;
                try {
                    URL url = new URL(detail_url);
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
                        pi = jo.getString("ParkingId");
                        abbreviation = jo.getString("ParkingAcronym");
                        pn = jo.getString("ParkingName");
                        on = jo.getString("EmployeeName");
                        mn = jo.getString("EmployeeMobileNumber");
                        phone = jo.getString("EmployeePhoneNumber");
                        e = jo.getString("EmployeeEmailId");
                        pt = jo.getString("ParkingType");
                        pa = jo.getString("ParkingAddress");
                        parking_city = jo.getString("ParkingCity");
                        count++;
                    }
                    if(address.isEmpty()){
                        address = pa;
                    }
                    return "get";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(keyword.equals("save"))
            {
                String id = params[1];
                acronym = params[2];
                pn = params[3];
                on = params[4];
                mn = params[5];
                phone = params[6];
                e = params[7];
                pa = params[8];
                String city = params[9];
                pt = params[10];
                String update_url = base_url+"update_parking.php";
                //String update_url = "https://duepark.000webhostapp.com/update_parking.php";
                try{
                    URL url = new URL(update_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("ParkingId","UTF-8")+"="+URLEncoder.encode(id,"UTF-8")+"&"
                            +URLEncoder.encode("ParkingAbbreviation","UTF-8")+"="+URLEncoder.encode(acronym,"UTF-8")+"&"
                            +URLEncoder.encode("ParkingName","UTF-8")+"="+URLEncoder.encode(pn,"UTF-8")+"&"
                            +URLEncoder.encode("EmployeeName", "UTF-8")+"="+URLEncoder.encode(on,"UTF-8")+"&"+
                            URLEncoder.encode("EmployeeMobileNumber", "UTF-8")+"="+URLEncoder.encode(mn,"UTF-8")+"&"+
                            URLEncoder.encode("EmployeePhoneNumber", "UTF-8")+"="+URLEncoder.encode(phone,"UTF-8")+"&"+
                            URLEncoder.encode("EmployeeEmailId", "UTF-8")+"="+URLEncoder.encode(e,"UTF-8")+"&"+
                            URLEncoder.encode("ParkingAddress", "UTF-8")+"="+URLEncoder.encode(pa,"UTF-8")+"&"+
                            URLEncoder.encode("ParkingCity", "UTF-8")+"="+URLEncoder.encode(city,"UTF-8")+"&"+
                            URLEncoder.encode("ParkingType", "UTF-8")+"="+URLEncoder.encode(pt,"UTF-8");

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
        protected void onPostExecute(String result) {
            if(result.equals("get")) {
                DecimalFormat df = new DecimalFormat("000");
                String parking_id = abbreviation + df.format(Integer.parseInt(pi));

                parkingId.setText(parking_id);
                parkingName.setText(pn);
                ownerName.setText(on);
                mobileNumber.setText(mn);
                phoneNumber.setText(phone);
                emailAddress.setText(e);
                if(pt.equals("Personal")){
                    spinner.setSelection(1);
                }
                else{
                    spinner.setSelection(2);
                }
                parkingAddress.setText(pa);

            }
            else if(result.trim().equals("UpdateSuccessfully")){
                Toast.makeText(ProfilePartnerActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else{
                Log.d("ProfilePartner", "onPostExecute: "+result);
            }
            progressDialog.dismiss();
        }
    }

    private void editTextChangeMethod() {
        parkingName.addTextChangedListener(new addListenerOnTextChange(this, parkingName));
        ownerName.addTextChangedListener(new addListenerOnTextChange(this, ownerName));
        mobileNumber.addTextChangedListener(new addListenerOnTextChange(this, mobileNumber));
        phoneNumber.addTextChangedListener(new addListenerOnTextChange(this, phoneNumber));
        emailAddress.addTextChangedListener(new addListenerOnTextChange(this, emailAddress));
    }
}
