package com.example.duepark_consumer.MonthlyPass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.Model.LocationListWithMonthlyPass;
import com.example.duepark_consumer.R;
import com.example.duepark_consumer.Valet.ValetHomeActivity;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddMonthlyPassActivity extends AppCompatActivity {

    private String base_url, selectedVehicle = "", selectedMonth = "", selectedLocationBikeCharge, selectedLocationCarCharge, generatedLocationId = null, payableAmount = null, locationId;
    private static final String TAG = "AddMonthlyPassActivity";
    private SessionManagerHelper sessionManagerHelper;
    private HashMap<String, String> employeeDetail;
    private EditText inputPassUserName, inputVehicleNumber1; //,inputPassUserMobileNumber;
    private com.bachors.prefixinput.EditText inputPassUserMobileNumber;
    private TextView payableAmountTV;
    private Spinner vehicleTypeSpinner, locationIdSpinner, monthSpinner;
    private Button createMonthlyPassBtn, cashBtn, onlineBtn;
    private ArrayAdapter<String> monthAdapter, vehicleTypeAdapter, locationIdsDataAdapter;
    private final int CASH_BTN_ID = 1;
    private final int ONLINE_BTN_ID = 2;
    private int selectedBtnId = 0;
    private ArrayList<LocationListWithMonthlyPass> locationListWithMonthlyPasses;
    private List<String> locationIdType;
    private boolean isValet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_monthly_pass);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(this);
        employeeDetail = sessionManagerHelper.getEmployeeDetails();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            isValet = bundle.getBoolean("IsValet");
        }

        locationIdType = new ArrayList<>();
        locationListWithMonthlyPasses = new ArrayList<>();

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("get");

        inputPassUserName = findViewById(R.id.inputPassUserName);
        inputPassUserMobileNumber = findViewById(R.id.inputPassUserMobileNumber);

        vehicleTypeSpinner = findViewById(R.id.vehicleTypeSpinner);
        initVehicleSpinner();
        vehicleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> vehicleAdapterView, View view, int position, long l) {
                if(vehicleAdapterView.getSelectedItemId() != 0) {
                    selectedVehicle = vehicleAdapterView.getSelectedItem().toString();
                    setPayableAmount();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        inputVehicleNumber1 = findViewById(R.id.inputVehicleNumber1);

        locationIdSpinner = findViewById(R.id.locationIdSpinner);

        monthSpinner = findViewById(R.id.monthSpinner);
        initMonthSpinner();
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> monthAdapterView, View view, int position, long l) {
                if(monthAdapterView.getSelectedItemId() != 0) {
                    String[] selected_month = monthAdapterView.getSelectedItem().toString().split("\\s");
                    selectedMonth = selected_month[0];
                    setPayableAmount();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        payableAmountTV = findViewById(R.id.payableAmountTV);

        cashBtn = findViewById(R.id.cashBtn);
        cashBtn.setId(CASH_BTN_ID);
        cashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cashBtn.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.btn_click));
                onlineBtn.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.btn_shape_light));
                selectedBtnId = view.getId();
            }
        });

        onlineBtn = findViewById(R.id.onlineBtn);
        onlineBtn.setId(ONLINE_BTN_ID);
        onlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onlineBtn.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.btn_click));
                cashBtn.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.btn_shape_light));
                selectedBtnId = view.getId();
            }
        });

        createMonthlyPassBtn = findViewById(R.id.createMonthlyPassBtn);
        createMonthlyPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMonthlyPass();
            }
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(Objects.equals(employeeDetail.get(SessionManagerHelper.EmployeeRole), "Manager") || Objects.equals(employeeDetail.get(SessionManagerHelper.EmployeeRole), "Valet")){
            locationIdSpinner.setVisibility(View.GONE);
        }
        else{
            locationIdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    generatedLocationId = locationIdType.get(position);
                    locationId = locationListWithMonthlyPasses.get(position).getLocationId();
                    selectedLocationBikeCharge = locationListWithMonthlyPasses.get(position).getBikeMonthlyPassRate();
                    selectedLocationCarCharge = locationListWithMonthlyPasses.get(position).getCarMonthlyPassRate();
                    setPayableAmount();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    private void setPayableAmount() {
        if(!selectedVehicle.isEmpty() && !selectedMonth.isEmpty()) {
            if (selectedVehicle.equals("Bike")) {
                if(employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("Manager") || employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("Valet")){
                    payableAmount = String.valueOf(Integer.parseInt(locationListWithMonthlyPasses.get(0).getBikeMonthlyPassRate()) * Integer.parseInt(selectedMonth));
                }
                else {
                    payableAmount = String.valueOf(Integer.parseInt(selectedLocationBikeCharge) * Integer.parseInt(selectedMonth));
                }
            } else {
                if(employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("Manager") || employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("Valet")){
                    payableAmount = String.valueOf(Integer.parseInt(locationListWithMonthlyPasses.get(0).getCarMonthlyPassRate()) * Integer.parseInt(selectedMonth));
                }
                else {
                    payableAmount = String.valueOf(Integer.parseInt(selectedLocationCarCharge) * Integer.parseInt(selectedMonth));
                }
            }
            payableAmountTV.setText("Payable Amount is Rs " + payableAmount);
        }
    }

    private void initMonthSpinner(){
        List<String> months = new ArrayList<>();
        months.add(0,"-- Select Month --");
        months.add("1 Month");
        months.add("2 Month");
        months.add("3 Month");
        months.add("4 Month");
        months.add("5 Month");
        months.add("6 Month");
        months.add("7 Month");
        months.add("8 Month");
        months.add("9 Month");
        months.add("10 Month");
        months.add("11 Month");
        months.add("12 Month");

        monthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, months){
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);
    }

    private void initVehicleSpinner(){
        List<String> vehicleType = new ArrayList<>();
        vehicleType.add(0,"-- Select Vehicle Type --");
        vehicleType.add("Car");
        vehicleType.add("Bike");

        vehicleTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, vehicleType){
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        vehicleTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(vehicleTypeAdapter);
    }

    private void createMonthlyPass(){
        String passUserName = inputPassUserName.getText().toString().trim();
        String passUserMobileNumber = inputPassUserMobileNumber.getText().toString().trim();
        String vehicleNumber1 = inputVehicleNumber1.getText().toString().trim();
        String paymentMode = null;
        if(employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("Manager") || employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("Valet")){
            locationId = locationListWithMonthlyPasses.get(0).getLocationId();
            generatedLocationId = locationListWithMonthlyPasses.get(0).getGeneratedLocationId();
        }
        //String mobileRegex = "^[6-9][0-9]{9}$";
        //Pattern mobilePattern = Pattern.compile(mobileRegex);
        //Matcher mobileNumberMatches = mobilePattern.matcher(passUserMobileNumber);

        if(passUserName.isEmpty()){
            fieldEmpty(inputPassUserName);
        }
        else if(passUserMobileNumber.isEmpty()){
            fieldEmpty(inputPassUserMobileNumber);
        }
        //else if(!mobileNumberMatches.matches()){
        else if(passUserMobileNumber.length()<13){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputPassUserMobileNumber.setError("Please enter valid mobile number.!",myIcon);
            requestFocus(inputPassUserMobileNumber);
        }
        else if(vehicleNumber1.isEmpty()){ //&& vehicleNumber2.isEmpty() && vehicleNumber3.isEmpty() && vehicleNumber4.isEmpty() && vehicleNumber5.isEmpty()){
            fieldEmpty(inputVehicleNumber1);
        }
        else if(selectedVehicle.isEmpty()){
            Toast.makeText(this, "Please select vehicle...", Toast.LENGTH_SHORT).show();
        }
        else if(selectedMonth.isEmpty()){
            Toast.makeText(this, "Please select pass duration..", Toast.LENGTH_SHORT).show();
        }
        else if(!payableAmount.isEmpty()) {
            if (selectedBtnId == 0 && !payableAmount.equals(0)) {
                Toast.makeText(this, "Please select payment mode..", Toast.LENGTH_SHORT).show();
            }
            else{
                if(payableAmount.equals(0)){
                    paymentMode = "null";
                }
                else {
                    if (selectedBtnId == CASH_BTN_ID) {
                        paymentMode = "Cash";
                    } else {
                        paymentMode = "Online";
                    }
                }
                String vehicleNumberData = null;
                StringBuilder vehicleNumberBuilder = new StringBuilder();
                if(!vehicleNumber1.isEmpty()){
                    vehicleNumberBuilder.append(vehicleNumber1).append(",");
                }
            /*if(!vehicleNumber2.isEmpty()){
                vehicleNumberBuilder.append(vehicleNumber2).append(",");
            }
            if(!vehicleNumber3.isEmpty()){
                vehicleNumberBuilder.append(vehicleNumber3).append(",");
            }
            if(!vehicleNumber4.isEmpty()){
                vehicleNumberBuilder.append(vehicleNumber4).append(",");
            }
            if(!vehicleNumber5.isEmpty()){
                vehicleNumberBuilder.append(vehicleNumber5).append(",");
            }*/
                if(!vehicleNumberBuilder.toString().isEmpty()){
                    vehicleNumberData = vehicleNumberBuilder.toString().substring(0, vehicleNumberBuilder.toString().length() - 1);
                }
                //Log.d(TAG, "createMonthlyPass: "+locationId+" "+passUserName+" "+passUserMobileNumber+" "+selectedVehicle+" "+vehicleNumberData+" "+selectedMonth+" "+payableAmount+" "+paymentMode+" "+generatedLocationId+" "+getExpiryDate());
                BackgroundTask backgroundTask = new BackgroundTask(this);
                backgroundTask.execute("add",locationId, passUserName, passUserMobileNumber, selectedVehicle, vehicleNumberData, selectedMonth, payableAmount, paymentMode, generatedLocationId);
            }
        }
    }

    private String getExpiryDate(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c1 = Calendar.getInstance();
        String currentDate = format.format(today); // current date here
        int daysToAdd = 30 * Integer.parseInt(selectedMonth);
        c1.add(Calendar.DAY_OF_YEAR, daysToAdd);
        Date resultDate = c1.getTime();
        String expiryDate = format.format(resultDate);
        return expiryDate;
    }

    private String getIssuedDate(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(today);
    }

    private String getIssuedTime(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        return format.format(today);
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
        if(isValet){
            Intent i = new Intent(this, ValetHomeActivity.class);
            i.putExtra("IsValetMonthlyPass", true);
            i.putExtra("IsValetReleasedVehicle", false);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
        else {
            startActivity(new Intent(this, MonthlyPassActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
    }

    class BackgroundTask extends AsyncTask<String, LocationListWithMonthlyPass, String>{

        private Context ctx;
        private ProgressDialog progressDialog;
        private String exists =  null, monthlyPassId = null;
        private boolean isLocationListExists = false;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(this.ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if(params[0].equals("get")){
                progressDialog.setMessage("Getting location list..");
                String activeLocationList_url = base_url+"get_activeLocationListWithMonthlyPass.php?ParkingId="+employeeDetail.get(SessionManagerHelper.ParkingId)+"&EmployeeId="+employeeDetail.get(SessionManagerHelper.EmployeeId)+"&EmployeeRole="+employeeDetail.get(SessionManagerHelper.EmployeeRole);
                //String activeLocationList_url = "https://duepark.000webhostapp.com/consumer/get_activeLocationListWithMonthlyPass.php?ParkingId="+employeeDetail.get(SessionManagerHelper.ParkingId)+"&EmployeeId="+employeeDetail.get(SessionManagerHelper.EmployeeId)+"&EmployeeRole="+employeeDetail.get(SessionManagerHelper.EmployeeRole);
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
                    if(jsonArray.length()>0) {
                        isLocationListExists = true;
                        while (count < jsonArray.length()) {
                            JSONObject jo = jsonArray.getJSONObject(count);
                            count++;
                            LocationListWithMonthlyPass locationListWithMonthlyPass = new LocationListWithMonthlyPass(jo.getString("id"), jo.getString("LocationName"), jo.getString("GeneratedLocationId"),
                                    jo.getString("LocationActiveState"), jo.getString("GeneratedParkingId"), jo.getString("ParkingAcronym"), jo.getString("CarMonthlyPassRate"), jo.getString("BikeMonthlyPassRate"));
                            publishProgress(locationListWithMonthlyPass);
                        }
                    }
                    Log.d(TAG, "doInBackground: "+result);
                    return "get";
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                progressDialog.setMessage("Creating monthly pass..");
                String addMonthlyPass_url = base_url+"add_monthlyPass.php";
                //String addMonthlyPass_url = "https://duepark.000webhostapp.com/consumer/add_monthlyPass.php";
                try{
                    URL url = new URL(addMonthlyPass_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("LocationId","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                            +URLEncoder.encode("PassUserName","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"
                            +URLEncoder.encode("PassUserMobileNumber", "UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8")+"&"+
                            URLEncoder.encode("VehicleType", "UTF-8")+"="+URLEncoder.encode(params[4],"UTF-8")+"&"+
                            URLEncoder.encode("VehicleNumbers", "UTF-8")+"="+URLEncoder.encode(params[5],"UTF-8")+"&"+
                            URLEncoder.encode("PassDuration", "UTF-8")+"="+URLEncoder.encode(params[6],"UTF-8") +"&"+
                            URLEncoder.encode("PayableAmount", "UTF-8")+"="+URLEncoder.encode(params[7],"UTF-8") +"&"+
                            URLEncoder.encode("PayableType", "UTF-8")+"="+URLEncoder.encode(params[8], "UTF-8")+"&"+
                            URLEncoder.encode("IssuedDate", "UTF-8")+"="+URLEncoder.encode(getIssuedDate(), "UTF-8")+"&"+
                            URLEncoder.encode("ExpiryDate", "UTF-8")+"="+URLEncoder.encode(getExpiryDate(), "UTF-8")+"&"+
                            URLEncoder.encode("IssuedTime", "UTF-8")+"="+URLEncoder.encode(getIssuedTime(), "UTF-8")+"&"+
                            URLEncoder.encode("IssuedBy", "UTF-8")+"="+URLEncoder.encode(employeeDetail.get(SessionManagerHelper.EmployeeName), "UTF-8")+"&"+
                            URLEncoder.encode("ActiveMonthlyPassState", "UTF-8")+"="+URLEncoder.encode("true", "UTF-8")+"&"+
                            URLEncoder.encode("GeneratedLocationId", "UTF-8")+"="+URLEncoder.encode(params[9], "UTF-8")+"&"+
                            URLEncoder.encode("ParkingId", "UTF-8")+"="+URLEncoder.encode(employeeDetail.get(SessionManagerHelper.ParkingId), "UTF-8")+"&"+
                            URLEncoder.encode("IssuedEmployeeId", "UTF-8")+"="+URLEncoder.encode(employeeDetail.get(SessionManagerHelper.EmployeeId), "UTF-8");
                    bw.write(data);
                    bw.flush();
                    bw.close();
                    ops.close();

                    InputStream ips = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(ips,"iso-8859-1"));
                    StringBuilder stringBuilder= new StringBuilder();
                    String line = "";
                    while((line=br.readLine())!=null){
                        stringBuilder.append(line);
                    }

                    JSONObject jsonObject = new JSONObject(stringBuilder.toString().trim());
                    exists = jsonObject.getString("exists");
                    monthlyPassId = jsonObject.getString("monthlyPassId");

                    br.close();
                    ips.close();
                    httpURLConnection.disconnect();
                    Log.d(TAG, "doInBackground: "+stringBuilder.toString().trim());
                    return "add";
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(LocationListWithMonthlyPass... values) {
            locationListWithMonthlyPasses.add(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("get")){
                if(isLocationListExists) {
                    if (!employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("Manager")) {
                        //locationIdType.add(0, "-- Select Location Id --");
                        for (int i = 0; i < locationListWithMonthlyPasses.size(); i++) {
                            DecimalFormat df = new DecimalFormat("000");
                            String parkingid = locationListWithMonthlyPasses.get(i).getParkingAcronym() + df.format(Integer.parseInt(locationListWithMonthlyPasses.get(i).getGeneratedParkingId()));
                            char char_locationId = (char) (Integer.parseInt(locationListWithMonthlyPasses.get(i).getGeneratedLocationId()) + 'A' - 1);
                            String location_id = parkingid + char_locationId;
                            locationIdType.add(location_id);
                        }
                        locationIdsDataAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, locationIdType);
                        locationIdsDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        locationIdSpinner.setAdapter(locationIdsDataAdapter);
                    } else {
                        DecimalFormat df = new DecimalFormat("000");
                        String parkingid = locationListWithMonthlyPasses.get(0).getParkingAcronym() + df.format(Integer.parseInt(locationListWithMonthlyPasses.get(0).getGeneratedParkingId()));
                        char char_locationId = (char) (Integer.parseInt(locationListWithMonthlyPasses.get(0).getGeneratedLocationId()) + 'A' - 1);
                        generatedLocationId = parkingid + char_locationId;
                        selectedLocationBikeCharge = locationListWithMonthlyPasses.get(0).getBikeMonthlyPassRate();
                        selectedLocationCarCharge = locationListWithMonthlyPasses.get(0).getCarMonthlyPassRate();
                        locationId = locationListWithMonthlyPasses.get(0).getLocationId();
                    }
                }
                else{
                    Toast.makeText(ctx, "No location found.. please add location first...", Toast.LENGTH_LONG).show();
                }
            }
            else {
                if(exists.equals("true")){
                    Toast.makeText(ctx, "Monthly pass already exists with vehicle number plate provided..", Toast.LENGTH_SHORT).show();
                    /*Intent i = new Intent(AddMonthlyPassActivity.this, AssignMonthlyPassVehicleNumberListActivity.class);
                    i.putExtra("monthlyPassId", monthlyPassId.trim());
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();*/
                }
                else {
                    try{
                        int newMonthlyPassId = Integer.parseInt(monthlyPassId.trim());
                        Toast.makeText(ctx, "Monthly pass create successfully..", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ctx, ActivatedMonthlyPassActivity.class);
                        intent.putExtra("monthlyPassId", String.valueOf(newMonthlyPassId));
                        intent.putExtra("IsValet", isValet);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                    catch (NumberFormatException ne){
                        Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            progressDialog.dismiss();
        }
    }
}