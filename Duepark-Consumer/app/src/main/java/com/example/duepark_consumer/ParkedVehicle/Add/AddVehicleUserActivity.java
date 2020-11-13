package com.example.duepark_consumer.ParkedVehicle.Add;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.ParkedVehicle.ActivatedParkedVehicleActivity;
import com.example.duepark_consumer.R;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddVehicleUserActivity extends AppCompatActivity implements View.OnClickListener {

    private String base_url;
    private static final String TAG = "AddVehicleUser";
    private EditText inputParkedUserName, inputPenaltyCharges;//, inputParkedUserMobileNumber;
    private TextView vehicleNumberTV, monthlyPassDateTV, monthlyPassIdTV, paymentRateTV;
    private com.bachors.prefixinput.EditText inputParkedUserMobileNumber;
    private Spinner payableAmountSpinner;
    private Button cashBtn, onlineBtn;
    private int selectedPaymentBtnId = 0;
    private HashMap<String, String> employeeDetails;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final static String VehicleNumberPlateImageSharedPreference = "com.example.duepark_consumer.VehicleNumberPlateImage";
    private final static String RtoNumberSharedPreference = "com.example.duepark_consumer.RtoNumber";
    private final static String VehicleCodeSharedPreference = "com.example.duepark_consumer.VehicleCode";
    private final static String UniqueNumberSharedPreference = "com.example.duepark_consumer.UniqueNumber";
    private final static String LocationIdSharedPreference = "com.example.duepark_consumer.LocationId";
    private final static String GeneratedParkedVehicleIdSharedPreference = "com.example.duepark_consumer.GeneratedParkedVehicleId";
    private final static String VehicleTypeSharedPreference = "com.example.duepark_consumer.VehicleType";
    private String monthlyPassId = "null", generatedMonthlyPassId = "null", generatedLocationId = "null", parkedVehicleId = "null", paymentType = "null";
    private String vehicleNumberPlateImage=null, rtoNumber=null, vehicleCode=null, uniqueNumber=null, locationId=null, vehicleType=null, generatedParkedVehicleId="null";
    private String carChargeType=null, carFixHour = null, carFixHourRate = null, carChargesOption = null, carChargesOptionRate=null, bikeChargeType=null, bikeFixHour = null, bikeFixHourRate = null, bikeChargesOption = null, bikeChargesOptionRate=null;
    private List<String> locationPayments;
    private ArrayAdapter<String> locationPaymentAdapter;
    private LinearLayout paymentSelectionLayout;
    private String penaltyCharges = "null";
    private String isParkedVehicleReleased = "1",prevParkedVehicleId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle_user);

        base_url = getResources().getString(R.string.base_url);

        locationPayments = new ArrayList<>();

        SessionManagerHelper sessionManagerHelper = new SessionManagerHelper(this);
        employeeDetails = sessionManagerHelper.getEmployeeDetails();

        sharedPreferences = getApplication().getSharedPreferences("NewParkedVehicle", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        getVehicleData();

        vehicleNumberTV = findViewById(R.id.vehicleNumberTV);
        inputParkedUserName = findViewById(R.id.inputParkedUserName);
        inputParkedUserMobileNumber = findViewById(R.id.inputParkedUserMobileNumber);
        inputPenaltyCharges = findViewById(R.id.inputPenaltyCharges);
        paymentRateTV = findViewById(R.id.paymentRateTV);
        monthlyPassIdTV = findViewById(R.id.monthlyPassIdTV);
        monthlyPassDateTV = findViewById(R.id.monthlyPassDateTV);
        paymentSelectionLayout = findViewById(R.id.paymentSelectionLayout);
        cashBtn = findViewById(R.id.cashBtn);
        cashBtn.setOnClickListener(this);
        onlineBtn = findViewById(R.id.onlineBtn);
        onlineBtn.setOnClickListener(this);
        payableAmountSpinner = findViewById(R.id.payableAmountSpinner);
        payableAmountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String amount = payableAmountSpinner.getSelectedItem().toString();
                if(amount.equals("Free") || amount.equals("Pay Later")){
                    cashBtn.setEnabled(false);
                    onlineBtn.setEnabled(false);
                    onlineBtn.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.btn_shape_light_transparent));
                    cashBtn.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.btn_shape_light_transparent));
                    paymentType = "null";
                }
                else{
                    cashBtn.setEnabled(true);
                    onlineBtn.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String isMonthlyPass = bundle.getString("IsMonthlyPass");
            isParkedVehicleReleased = bundle.getString("IsParkedVehicleReleased");
            prevParkedVehicleId = bundle.getString("ParkedVehicleId");
            assert isMonthlyPass != null;
            if(isMonthlyPass.equals("true")){
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat month_date = new SimpleDateFormat("dd MMM", Locale.US);
                try {
                    Date issued_date = sdf.parse(Objects.requireNonNull(bundle.getString("IssuedDate")));
                    Date expiry_date = sdf.parse(Objects.requireNonNull(bundle.getString("ExpiryDate")));
                    assert issued_date != null;
                    String issuedDate = month_date.format(issued_date);
                    assert expiry_date != null;
                    String expiryDate = month_date.format(expiry_date);
                    monthlyPassDateTV.setText("Validation Date : "+issuedDate+" - "+expiryDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                monthlyPassId = bundle.getString("MonthlyPassId");
                generatedMonthlyPassId = bundle.getString("GeneratedMonthlyPassId");
                generatedLocationId = bundle.getString("GeneratedLocationId");
                locationId = bundle.getString("LocationId");

                inputParkedUserName.setText(bundle.getString("PassUserName"));
                inputParkedUserMobileNumber.setText(bundle.getString("PassUserMobileNumber"));
                monthlyPassIdTV.setText("Monthly Pass Id : "+generatedLocationId+""+generatedMonthlyPassId);
                generatedParkedVehicleId = bundle.getString("GeneratedParkedVehicleId");

                monthlyPassIdTV.setVisibility(View.VISIBLE);
                monthlyPassDateTV.setVisibility(View.VISIBLE);
                payableAmountSpinner.setVisibility(View.GONE);
                //inputParkedUserName.setEnabled(false);
                //inputParkedUserMobileNumber.setEnabled(false);
                paymentRateTV.setVisibility(View.GONE);
                //paymentSelectionLayout.setVisibility(View.GONE);
                cashBtn.setEnabled(false);
                onlineBtn.setEnabled(false);
                onlineBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_shape_light_transparent));
                cashBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_shape_light_transparent));
                //paymentSelectionLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));
            }
            else{
                if(employeeDetails.get(SessionManagerHelper.EmployeeRole).equals("Manager")){
                    locationId = employeeDetails.get(SessionManagerHelper.LocationId);
                }
                //parkedVehicleId = bundle.getString("ParkedVehicleId");
                inputParkedUserName.setText(bundle.getString("FullName"));
                inputParkedUserMobileNumber.setText(bundle.getString("MobileNumber"));
                cashBtn.setEnabled(true);
                onlineBtn.setEnabled(true);
            }
        }

        vehicleNumberTV.setText(vehicleType+" - "+rtoNumber+""+vehicleCode+""+uniqueNumber);

        Button parkedVehicleBtn = findViewById(R.id.parkedVehicleBtn);
        parkedVehicleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVehicle();
            }
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(monthlyPassId.equals("null")){
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute("get");
        }
    }

    @Override
    public void onClick(View view) {
        if(view == cashBtn){
            cashBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_click));
            onlineBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_shape_light));
            selectedPaymentBtnId = view.getId();
            paymentType = "Cash";
        }
        else{
            cashBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_shape_light));
            onlineBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_click));
            selectedPaymentBtnId = view.getId();
            paymentType = "Online";
        }
    }

    private void addVehicle()
    {
        String fullName = inputParkedUserName.getText().toString().trim();
        String mobileNumber = inputParkedUserMobileNumber.getText().toString().trim();
        String paidAmount = "00";
        //String paymentTimeRate = "null";
        String isPayLater = "false";
        String isParkingFree = "false";
        String vehicleNumber = rtoNumber+""+vehicleCode+""+uniqueNumber;
        String selectedPaymentTimeRate = "null";

        if(fullName.isEmpty()){
            fieldEmpty(inputParkedUserName);
        }
        else if(mobileNumber.isEmpty()){
            fieldEmpty(inputParkedUserMobileNumber);
        }
        else if(mobileNumber.length()<13 && !mobileNumber.equals("+9100")){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputParkedUserMobileNumber.setError("Please enter valid mobile number.!",myIcon);
            requestFocus(inputParkedUserMobileNumber);
        }
        else if(monthlyPassId.equals("null")){
            if(isParkedVehicleReleased.equals("0")){
                if(penaltyCharges.isEmpty()) {
                    fieldEmpty(inputPenaltyCharges);
                }
            }

            if(!payableAmountSpinner.getSelectedItem().toString().equals("Free") && !payableAmountSpinner.getSelectedItem().toString().equals("Pay Later")){
                if(selectedPaymentBtnId == 0){
                    Toast.makeText(AddVehicleUserActivity.this, "Please select mode of payment..", Toast.LENGTH_SHORT).show();
                }
            }
                if (vehicleType.equals("Car")) {
                    if (!carChargeType.equals("Free")) {
                        selectedPaymentTimeRate = payableAmountSpinner.getSelectedItem().toString();
                        if (selectedPaymentTimeRate.contains("hours per rate")) {
                            //paymentTimeRate = "Hourly";
                            paidAmount = carFixHourRate;
                        } else if (selectedPaymentTimeRate.contains("Full day rate") || selectedPaymentTimeRate.contains("Per hour rate")) {
                            //paymentTimeRate = "Full";
                            paidAmount = carChargesOptionRate;
                        } else if (selectedPaymentTimeRate.equals("Pay Later")) {
                            isPayLater = "true";
                            //paidAmount = "00";
                        }
                    } else {
                        isParkingFree = "true";
                        //paidAmount = "00";
                    }
                    BackgroundTask backgroundTask = new BackgroundTask(this);
                    backgroundTask.execute("add",locationId, monthlyPassId, generatedParkedVehicleId, fullName, mobileNumber, vehicleType, vehicleNumber, paidAmount,
                            paymentType, selectedPaymentTimeRate, isPayLater, isParkingFree, employeeDetails.get(SessionManagerHelper.EmployeeName), getParkedTime(), getParkedDate(), employeeDetails.get(SessionManagerHelper.ParkingId),
                            employeeDetails.get(SessionManagerHelper.EmployeeId), prevParkedVehicleId, penaltyCharges);
                }
                // When vehicle type is bike
                else if (vehicleType.equals("Bike")) {
                    if (!bikeChargeType.equals("Free")) {
                        selectedPaymentTimeRate = payableAmountSpinner.getSelectedItem().toString();
                        if (selectedPaymentTimeRate.contains("hours per rate")) {
                            //paymentTimeRate = "Hourly";
                            paidAmount = bikeFixHourRate;
                        } else if (selectedPaymentTimeRate.contains("Full day rate")||selectedPaymentTimeRate.contains("Per hour rate")) {
                            //paymentTimeRate = "Full";
                            paidAmount = bikeChargesOptionRate;
                        } else if (selectedPaymentTimeRate.equals("Pay Later")) {
                            isPayLater = "true";
                            //paidAmount = "00";
                        }
                    } else {
                        isParkingFree = "true";
                        //paidAmount = "00";
                        //paidAmount = "00";
                    }
                    /*Log.d(TAG, "addVehicle: " + locationId + " " + monthlyPassId + " " + generatedParkedVehicleId + " " + fullName + " " + mobileNumber + " " + vehicleType + " " + vehicleNumber + " " + paidAmount
                            + " " + paymentType + " " + paymentTimeRate + " " + isPayLater + " " + isParkingFree);*/
                    BackgroundTask backgroundTask = new BackgroundTask(this);
                    backgroundTask.execute("add",locationId, monthlyPassId, generatedParkedVehicleId, fullName, mobileNumber, vehicleType, vehicleNumber, paidAmount,
                            paymentType, selectedPaymentTimeRate, isPayLater, isParkingFree, employeeDetails.get(SessionManagerHelper.EmployeeName), getParkedTime(), getParkedDate(), employeeDetails.get(SessionManagerHelper.ParkingId),
                            employeeDetails.get(SessionManagerHelper.EmployeeId), prevParkedVehicleId, penaltyCharges);
                }
        }
        else{
                BackgroundTask backgroundTask = new BackgroundTask(this);
                backgroundTask.execute("add",locationId, monthlyPassId, generatedParkedVehicleId, fullName, mobileNumber, vehicleType, vehicleNumber, paidAmount,
                        paymentType, selectedPaymentTimeRate, isPayLater, isParkingFree, employeeDetails.get(SessionManagerHelper.EmployeeName), getParkedTime(), getParkedDate(), employeeDetails.get(SessionManagerHelper.ParkingId),
                        employeeDetails.get(SessionManagerHelper.EmployeeId), prevParkedVehicleId, penaltyCharges);
        }
    }

    private String getParkedDate(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(today);
    }

    private String getParkedTime(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        return format.format(today);
    }

    private void getVehicleData()
    {
        if(sharedPreferences.contains(RtoNumberSharedPreference)){
            rtoNumber = sharedPreferences.getString(RtoNumberSharedPreference, "");
        }
        if(sharedPreferences.contains(VehicleCodeSharedPreference)){
            vehicleCode = sharedPreferences.getString(VehicleCodeSharedPreference, "");
        }
        if(sharedPreferences.contains(UniqueNumberSharedPreference)){
            uniqueNumber = sharedPreferences.getString(UniqueNumberSharedPreference, "");
        }
        if(sharedPreferences.contains(LocationIdSharedPreference))
        {
            locationId = sharedPreferences.getString(LocationIdSharedPreference,"");
        }
        if(sharedPreferences.contains(VehicleTypeSharedPreference))
        {
            vehicleType = sharedPreferences.getString(VehicleTypeSharedPreference, "");
        }
        if(sharedPreferences.contains(GeneratedParkedVehicleIdSharedPreference))
        {
            generatedParkedVehicleId = sharedPreferences.getString(GeneratedParkedVehicleIdSharedPreference, "");
        }
        if(sharedPreferences.contains(VehicleNumberPlateImageSharedPreference)){
            vehicleNumberPlateImage = sharedPreferences.getString(VehicleNumberPlateImageSharedPreference,"");
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, AddNumberPlateVehicleActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
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
        private Context ctx;
        private ProgressDialog progressDialog;
        private String res = "empty", newParkedVehicleId;

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
                progressDialog.setMessage("Getting location payment detail...");
                String locationPayment_url = base_url+"get_locationPaymentDetail.php?VehicleType="+vehicleType+"&LocationId="+locationId;
                //String locationPayment_url = "https://duepark.000webhostapp.com/consumer/get_locationPaymentDetail.php?VehicleType="+vehicleType+"&LocationId="+locationId;
                try{
                    URL url = new URL(locationPayment_url);
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
                        if(!jo.isNull("noData")) {
                            res = "notEmpty";
                            if (vehicleType.equals("Car")) {
                                carChargeType = jo.getString("CarChargeType");
                                carFixHour = jo.getString("CarFixHour");
                                carFixHourRate = jo.getString("CarFixHourRate");
                                carChargesOption = jo.getString("CarChargesOption");
                                carChargesOptionRate = jo.getString("CarChargesOptionRate");
                            } else {
                                bikeChargeType = jo.getString("BikeChargeType");
                                bikeFixHour = jo.getString("BikeFixHour");
                                bikeFixHourRate = jo.getString("BikeFixHourRate");
                                bikeChargesOption = jo.getString("BikeChargesOption");
                                bikeChargesOptionRate = jo.getString("BikeChargesOptionRate");
                            }
                        }
                        Log.d(TAG, "doInBackground: "+result);
                        inputStream.close();
                        bufferedReader.close();
                        return "get";
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
                progressDialog.setMessage("Creating parked vehicle detail...");
                String addParkedVehicle_url = base_url+"add_parkedVehicle.php";
                //String addParkedVehicle_url = "https://duepark.000webhostapp.com/consumer/add_parkedVehicle.php";
                try{
                    URL url = new URL(addParkedVehicle_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("LocationId","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                            +URLEncoder.encode("MonthlyPassId","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"
                            +URLEncoder.encode("GeneratedParkedVehicleId", "UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8")+"&"+
                            URLEncoder.encode("FullName", "UTF-8")+"="+URLEncoder.encode(params[4],"UTF-8")+"&"+
                            URLEncoder.encode("MobileNumber", "UTF-8")+"="+URLEncoder.encode(params[5],"UTF-8")+"&"+
                            URLEncoder.encode("VehicleType", "UTF-8")+"="+URLEncoder.encode(params[6],"UTF-8") +"&"+
                            URLEncoder.encode("VehicleNumber", "UTF-8")+"="+URLEncoder.encode(params[7], "UTF-8")+"&"+
                            URLEncoder.encode("PaidAmount", "UTF-8")+"="+URLEncoder.encode(params[8], "UTF-8")+"&"+
                            URLEncoder.encode("ParkedPaymentType", "UTF-8")+"="+URLEncoder.encode(params[9], "UTF-8")+"&"+
                            URLEncoder.encode("ParkedTimeRate", "UTF-8")+"="+URLEncoder.encode(params[10], "UTF-8")+"&"+
                            URLEncoder.encode("IsPayLater", "UTF-8")+"="+URLEncoder.encode(params[11], "UTF-8")+"&"+
                            URLEncoder.encode("IsParkingFree", "UTF-8")+"="+URLEncoder.encode(params[12], "UTF-8")+"&"+
                            URLEncoder.encode("ParkedBy", "UTF-8")+"="+URLEncoder.encode(params[13], "UTF-8")+"&"+
                            URLEncoder.encode("ParkedTime", "UTF-8")+"="+URLEncoder.encode(params[14], "UTF-8")+"&"+
                            URLEncoder.encode("ParkedDate", "UTF-8")+"="+URLEncoder.encode(params[15], "UTF-8")+"&"
                            +URLEncoder.encode("ParkingId", "UTF-8")+"="+URLEncoder.encode(params[16], "UTF-8")+"&"+
                            URLEncoder.encode("VehicleNumberPic", "UTF-8")+"="+URLEncoder.encode("null", "UTF-8")+"&"+
                            URLEncoder.encode("IsParkedVehicleReleased", "UTF-8")+"="+URLEncoder.encode("false", "UTF-8")+"&"+
                            URLEncoder.encode("ParkedEmployeeId", "UTF-8")+"="+URLEncoder.encode(params[17], "UTF-8")+"&"+
                            URLEncoder.encode("PrevParkedVehicleId", "UTF-8")+"="+URLEncoder.encode(params[18], "UTF-8")+"&"+
                            URLEncoder.encode("PenaltyCharges", "UTF-8")+"="+URLEncoder.encode(params[19], "UTF-8");
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

                    newParkedVehicleId = result.toString().trim();

                    br.close();
                    ips.close();
                    httpURLConnection.disconnect();
                    Log.d(TAG, "doInBackground: "+result.toString().trim());
                    return "add";
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
           if(result.equals("get")){
                if(res.equals("notEmpty")) {
                    if (vehicleType.equals("Car")) {
                        if (carChargeType.equals("Free")) {
                            locationPayments.add("Free");
                            //paymentSelectionLayout.setVisibility(View.GONE);
                        } else {
                            locationPayments.add(carFixHour + " hours per rate Rs " + carFixHourRate);
                            if(carChargesOption.equals("PerHour")){
                                locationPayments.add("Per hour rate Rs "+carChargesOptionRate);
                            }
                            else{
                                locationPayments.add("Full day rate Rs " + carChargesOptionRate);
                            }
                            locationPayments.add("Pay Later");
                        }
                    } else {
                        if (bikeChargeType.equals("Free")) {
                            locationPayments.add("Free");
                            //paymentSelectionLayout.setVisibility(View.GONE);
                        } else {
                            locationPayments.add(bikeFixHour + " hours per rate Rs " + bikeFixHourRate);
                            if(bikeChargesOption.equals("PerHour")){
                                locationPayments.add("Per hour rate Rs "+bikeChargesOptionRate);
                            }
                            else{
                                locationPayments.add("Full day rate Rs " + bikeChargesOptionRate);
                            }
                            locationPayments.add("Pay Later");
                        }
                    }
                    locationPaymentAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, locationPayments);
                    locationPaymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    payableAmountSpinner.setAdapter(locationPaymentAdapter);
                }
                else{
                    Toast.makeText(ctx, "Please add the payment for this location..", Toast.LENGTH_LONG).show();
                }
            }
            else{
                if(newParkedVehicleId.equals("0")){
                    Toast.makeText(ctx, "Vehicle is already parked...", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        int parkedVehicleId = Integer.parseInt(newParkedVehicleId);
                        editor.clear();
                        editor.apply();
                        Toast.makeText(ctx, "Vehicle Parked successfully...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ctx, ActivatedParkedVehicleActivity.class);
                        intent.putExtra("ParkedVehicleId", String.valueOf(parkedVehicleId));
                        intent.putExtra("IsValet", false);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } catch (Exception ex) {
                        //ex.printStackTrace();
                        Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            progressDialog.dismiss();
        }
    }
}