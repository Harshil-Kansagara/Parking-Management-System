package com.example.duepark_consumer.Parking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.HomeActivity;
import com.example.duepark_consumer.Model.Parking;
import com.example.duepark_consumer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParkingActivity extends AppCompatActivity implements View.OnClickListener {

    private String base_url, parkingName;
    private static final String TAG = "ParkingActivity";
    private TextView parkingIdTV, parkingNameTV, parkingTypeTV, employeeNameTV, employeeEmailIdTV, employeeMobileNumberTV, employeePasswordTV, parkingAddressTV, employeeRoleTV;
    private EditText parkingNameET, employeeEmailIdET, employeeMobileNumberET, employeePasswordET;
    private Button employeeNameEditBtn, parkingNameEditBtn, parkingTypeEditBtn, employeeEmailIdEditBtn, employeeMobileNumberEditBtn, employeePasswordEditBtn;
    private SessionManagerHelper sessionManagerHelper;
    private FloatingActionButton saveBtn;
    private ImageView backBtn;
    private Parking parking;
    private Spinner parkingTypeSpinner;
    private ArrayAdapter<String> dataAdapter;
    private HashMap<String, String> employeeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(this);
        employeeData = sessionManagerHelper.getEmployeeDetails();

        parkingIdTV = findViewById(R.id.parkingIdTV);
        parkingNameTV = findViewById(R.id.parkingNameTV);
        parkingTypeTV = findViewById(R.id.parkingTypeTV);
        employeeNameTV = findViewById(R.id.employeeNameTV);
        employeeEmailIdTV = findViewById(R.id.employeeEmailIdTV);
        employeeMobileNumberTV = findViewById(R.id.employeeMobileNumberTV);
        employeePasswordTV = findViewById(R.id.employeePasswordTV);
        parkingAddressTV = findViewById(R.id.parkingAddressTV);
        employeeRoleTV = findViewById(R.id.employeeRoleTV);

        parkingNameET = findViewById(R.id.parkingNameET);
        employeeEmailIdET = findViewById(R.id.employeeEmailIdET);
        employeeMobileNumberET = findViewById(R.id.employeeMobileNumberET);
        employeePasswordET = findViewById(R.id.employeePasswordET);
        parkingTypeSpinner = findViewById(R.id.parkingTypeSpinner);
        initSpinner();

        /*employeeNameEditBtn = findViewById(R.id.employeeNameEditBtn);
        employeeNameEditBtn.setOnClickListener(this);*/

        parkingNameEditBtn = findViewById(R.id.parkingNameEditBtn);
        parkingNameEditBtn.setOnClickListener(this);

        parkingTypeEditBtn = findViewById(R.id.parkingTypeEditBtn);
        parkingTypeEditBtn.setOnClickListener(this);

        employeeEmailIdEditBtn = findViewById(R.id.employeeEmailIdEditBtn);
        employeeEmailIdEditBtn.setOnClickListener(this);

        employeeMobileNumberEditBtn = findViewById(R.id.employeeMobileNumberEditBtn);
        employeeMobileNumberEditBtn.setOnClickListener(this);

        employeePasswordEditBtn = findViewById(R.id.employeePasswordEditBtn);
        employeePasswordEditBtn.setOnClickListener(this);

        if(employeeData.get(SessionManagerHelper.EmployeeRole).equals("Manager")){
            parkingNameEditBtn.setVisibility(View.GONE);
            parkingTypeEditBtn.setVisibility(View.GONE);
        }

        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("get");
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(this, EditParkingDetailActivity.class);
       if(view == parkingNameEditBtn){
            if(parkingNameTV.getVisibility() == View.VISIBLE){
                parkingNameTV.setVisibility(View.INVISIBLE);
                parkingNameET.setVisibility(View.VISIBLE);
                parkingNameET.setText(parking.getParkingName());
            }
            else {//if (parkingNameTV.getVisibility() == View.INVISIBLE){
                parkingNameTV.setVisibility(View.VISIBLE);
                parkingNameET.setVisibility(View.INVISIBLE);
                parkingNameTV.setText(parkingNameET.getText().toString().trim());
            }
        }
        else if(view == parkingTypeEditBtn){
            if(parkingTypeTV.getVisibility() == View.VISIBLE){
                parkingTypeTV.setVisibility(View.INVISIBLE);
                parkingTypeSpinner.setVisibility(View.VISIBLE);
                int spinnerPosition = dataAdapter.getPosition(parking.getParkingType());
                parkingTypeSpinner.setSelection(spinnerPosition);
            }
            else {
                parkingTypeTV.setVisibility(View.VISIBLE);
                parkingTypeSpinner.setSelection(View.INVISIBLE);
                parkingTypeTV.setText(parkingTypeSpinner.getSelectedItem().toString());
            }
        }
        else if(view == employeeEmailIdEditBtn){
            if(employeeEmailIdTV.getVisibility() == View.VISIBLE){
                employeeEmailIdTV.setVisibility(View.INVISIBLE);
                employeeEmailIdET.setVisibility(View.VISIBLE);
                employeeEmailIdET.setText(parking.getEmployeeEmailId());
            }
            else {//if(employeeEmailIdTV.getVisibility() == View.INVISIBLE){
                employeeEmailIdTV.setVisibility(View.VISIBLE);
                employeeEmailIdET.setVisibility(View.INVISIBLE);
                employeeEmailIdTV.setText(employeeEmailIdET.getText().toString().trim());
            }
        }
        else if(view == employeeMobileNumberEditBtn){
            if(employeeMobileNumberTV.getVisibility() == View.VISIBLE){
                employeeMobileNumberTV.setVisibility(View.INVISIBLE);
                employeeMobileNumberET.setVisibility(View.VISIBLE);
                employeeMobileNumberET.setText(parking.getEmployeeMobileNumber());
            }
            else {//if(employeeMobileNumberTV.getVisibility() == View.INVISIBLE){
                employeeMobileNumberTV.setVisibility(View.VISIBLE);
                employeeMobileNumberET.setVisibility(View.INVISIBLE);
                employeeMobileNumberTV.setText(employeeMobileNumberET.getText().toString().trim());
            }
        }
        else if(view == employeePasswordEditBtn){
            i.putExtra("IsEmployeePassword", true);
            i.putExtra("CurrentValue", parking.getEmployeePassword());
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();

        }
        else if(view == saveBtn){
            if(parkingNameET.getText().toString().trim().equals(parking.getParkingName()) &&
                    employeeEmailIdET.getText().toString().trim().equals(parking.getEmployeeEmailId()) &&
                    employeeMobileNumberET.getText().toString().trim().equals(parking.getEmployeeMobileNumber()) &&
                    employeePasswordET.getText().toString().trim().equals(parking.getEmployeePassword()) &&
                    parkingTypeSpinner.getSelectedItem().toString().equals(parking.getParkingType())){
                onBackPressed();
            }
            else{
                updateProfileData();
            }
            //onBackPressed();
        }
        else if(view == backBtn){
            onBackPressed();
        }
    }

    private void initSpinner(){
        List<String> type = new ArrayList<>();
        //type.add(0, "-- Select Type --");
        type.add("Personal");
        type.add("Business");

        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, type);/*{
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    return false;
                }
                else{
                    return true;
                }
            }
        };*/
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        parkingTypeSpinner.setAdapter(dataAdapter);
    }

    private void updateProfileData(){
        String emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
        String mobileRegex = "^[6-9][0-9]{9}$";

        parkingName = parkingNameET.getText().toString().trim();
        String parkingType = parkingTypeSpinner.getSelectedItem().toString();
        String employeeEmailId = employeeEmailIdET.getText().toString().trim();
        String employeeMobileNumber = employeeMobileNumberET.getText().toString().trim();
        String employeePassword = employeePasswordET.getText().toString().trim();

        Pattern emailPattern = Pattern.compile(emailRegex);
        Pattern mobilePattern = Pattern.compile(mobileRegex);

        Matcher mobileNumberMatches = mobilePattern.matcher(employeeMobileNumber);
        Matcher emailMatches = emailPattern.matcher(employeeEmailId);

        if(parkingName.isEmpty()){
            fieldEmpty(parkingNameET);
        }
        else if(employeeEmailId.isEmpty()) {
            fieldEmpty(employeeEmailIdET);
        }
        else if(!emailMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            employeeEmailIdET.setError("Please check your email id.",myIcon);
            requestFocus(employeeEmailIdET);
        }
        else if(employeeMobileNumber.isEmpty()){
            fieldEmpty(employeeMobileNumberET);
        }
        else if(!mobileNumberMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            employeeMobileNumberET.setError("Please enter valid mobile number.!",myIcon);
            requestFocus(employeeMobileNumberET);
        }
        else if(employeePassword.isEmpty()){
            fieldEmpty(employeePasswordET);
        }
        else if(employeePassword.length()<8){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            employeePasswordET.setError("Please enter password having length greater than 8.!",myIcon);
            requestFocus(employeePasswordET);
        }
        else{
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute("update",parkingName, parkingType, employeeEmailId, employeeMobileNumber, employeePassword);
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

    class BackgroundTask extends AsyncTask<String, Void, String>{
        private ProgressDialog progressDialog;

        BackgroundTask(Context ctx){
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if(params[0].equals("get")) {
                progressDialog.setMessage("Loading Data...");
                String parkingData_url = base_url+"get_parking.php?EmployeeId=" + employeeData.get(SessionManagerHelper.EmployeeId);
                //String parkingData_url = "https://duepark.000webhostapp.com/consumer/get_parking.php?EmployeeId=" + employeeData.get(SessionManagerHelper.EmployeeId);
                try {
                    URL url = new URL(parkingData_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

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
                        parking = new Parking(jo.getString("EmployeeId"), jo.getString("EmployeeName"), jo.getString("EmployeeMobileNumber"), jo.getString("EmployeePhoneNumber"),
                                jo.getString("EmployeeEmailId"), jo.getString("EmployeePassword"), jo.getString("EmployeeRole"), jo.getString("ParkingId"),
                                jo.getString("GeneratedParkingId"), jo.getString("ParkingAcronym"), jo.getString("ParkingName"), jo.getString("ParkingType"),
                                jo.getString("ParkingAddress"), jo.getString("ParkingCity"), jo.getString("ParkingDate"), jo.getString("ParkingTime"),
                                jo.getString("ParkingActiveState"));
                        count++;
                    }
                    return "get";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                progressDialog.setMessage("Saving Data...");
                String updateParking_url = base_url+"update_parking.php";
                //String updateParking_url = "https://duepark.000webhostapp.com/consumer/update_parking.php";
                try {
                    URL url = new URL(updateParking_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("ParkingId","UTF-8")+"="+URLEncoder.encode(parking.getParkingId(),"UTF-8")+"&"
                            +URLEncoder.encode("ParkingName","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                            +URLEncoder.encode("ParkingType","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"
                            +URLEncoder.encode("EmployeeId","UTF-8")+"="+URLEncoder.encode(parking.getEmployeeId(),"UTF-8")+"&"
                            +URLEncoder.encode("EmployeeEmailId", "UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8")+"&"+
                            URLEncoder.encode("EmployeeMobileNumber", "UTF-8")+"="+URLEncoder.encode(params[4],"UTF-8")+"&"+
                            URLEncoder.encode("EmployeePassword", "UTF-8")+"="+URLEncoder.encode(params[5],"UTF-8");
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
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("get")){
                DecimalFormat df = new DecimalFormat("000");
                parkingIdTV.setText("Parking Id : "+parking.getParkingAcronym() + df.format(Integer.parseInt(parking.getGeneratedParkingId())));
                employeeRoleTV.setText(parking.getEmployeeRole());
                employeeNameTV.setText(parking.getEmployeeName());
                parkingNameTV.setText(parking.getParkingName());
                parkingTypeTV.setText(parking.getParkingType());
                employeeEmailIdTV.setText(parking.getEmployeeEmailId());
                employeeMobileNumberTV.setText(parking.getEmployeeMobileNumber());
                StringBuilder password = new StringBuilder();
                for(int i=0;i<parking.getEmployeePassword().length();i++){
                    password.append("*");
                }
                employeePasswordTV.setText(password);
                parkingAddressTV.setText(parking.getParkingAddress());
                parkingNameET.setText(parking.getParkingName());
                employeeMobileNumberET.setText(parking.getEmployeeMobileNumber());
                employeeEmailIdET.setText(parking.getEmployeeEmailId());
                employeePasswordET.setText(parking.getEmployeePassword());
                int spinnerPosition = dataAdapter.getPosition(parking.getParkingType());
                parkingTypeSpinner.setSelection(spinnerPosition);
            }
            else if(result.trim().equals("UpdateSuccessfully")){
                sessionManagerHelper.updateParkingName(parkingName);
                Toast.makeText(ParkingActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else{
                Log.d(TAG, "onPostExecute: "+result);
            }
            progressDialog.dismiss();
        }
    }
}