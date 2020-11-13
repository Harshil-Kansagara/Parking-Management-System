package com.example.duepark_admin.Vehicle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duepark_admin.HomeActivity;
import com.example.duepark_admin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class DetailVehicleActivity extends AppCompatActivity {

    private static final String TAG = "DetailVehicle";
    private String parkedVehicleId, base_url;
    private TextView currentStatusTV, vehicleNumberTV, parkingLocationIdTV, passUserNameTV, passUserMobileNumberTV, paidAmountTV, pendingAmountTV, parkedDateTV, parkedTimeTV, releasedTimeTV, parkedByTV, releasedByTV, cashPaymentTV,
            onlinePaymentTV, totalPaymentTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_vehicle);

        base_url = getResources().getString(R.string.base_url);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            parkedVehicleId = bundle.getString("parkedVehicleId");
        }

        currentStatusTV = findViewById(R.id.currentStatusTV);
        vehicleNumberTV = findViewById(R.id.vehicleNumberTV);
        parkingLocationIdTV = findViewById(R.id.parkingLocationIdTV);
        passUserNameTV = findViewById(R.id.passUserNameTV);
        passUserMobileNumberTV = findViewById(R.id.passUserMobileNumberTV);
        paidAmountTV = findViewById(R.id.paidAmountTV);
        parkedDateTV = findViewById(R.id.parkedDateTV);
        parkedTimeTV = findViewById(R.id.parkedTimeTV);
        pendingAmountTV = findViewById(R.id.pendingAmountTV);
        releasedTimeTV = findViewById(R.id.releasedTimeTV);
        parkedByTV = findViewById(R.id.parkedByTV);
        releasedByTV = findViewById(R.id.releasedByTV);
        cashPaymentTV = findViewById(R.id.cashPaymentTV);
        onlinePaymentTV = findViewById(R.id.onlinePaymentTV);
        totalPaymentTV = findViewById(R.id.totalPaymentTV);


        /*FloatingActionButton verifyBtn = findViewById(R.id.verifyBtn);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });*/

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
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    class BackgroundTask extends AsyncTask<String, Void, String> {
        private Context context;
        private ProgressDialog progressDialog;
        private String monthlyPassId, fullName, mobileNumber, parkedTime, parkedDate, parkedBy, releasedTime, releasedBy, isMonthlyPass, generatedMonthlyPassId;
        private String locationId, paidAmount, parkedPaymentType, isParkingFree, releasedAmount, releasedPaymentType, isPayLater;
        private String generatedParkingId, parkingAcronym, generatedLocationId, currentStatus, vehicleNumber;

        public BackgroundTask(Context ctx) {
            context = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Fetching vehicle detail...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String vehicleDetail_url = base_url + "get_vehicleDetail_Admin.php?ParkedVehicleId="+parkedVehicleId;
            try{
                URL url = new URL(vehicleDetail_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine())!=null){
                    stringBuilder.append(line+"\n");
                }
                httpURLConnection.disconnect();

                String result = stringBuilder.toString().trim();
                if(result!=null){
                    JSONObject jo = new JSONObject(result);
                    vehicleNumber = jo.getString("VehicleNumber");
                    currentStatus = jo.getString("CurrentStatus");
                    parkingAcronym = jo.getString("ParkingAcronym");
                    generatedParkingId = jo.getString("GeneratedParkingId");
                    fullName = jo.getString("FullName");
                    mobileNumber = jo.getString("MobileNumber");
                    parkedDate = jo.getString("ParkedDate");
                    parkedTime = jo.getString("ParkedTime");
                    parkedBy = jo.getString("ParkedBy");
                    releasedTime = jo.getString("ReleasedTime");
                    releasedBy = jo.getString("ReleasedBy");
                    generatedLocationId = jo.getString("GeneratedLocationId");
                    isMonthlyPass = jo.getString("IsMonthlyPass");
                    if(isMonthlyPass.equals("true")){
                        monthlyPassId = jo.getString("MonthlyPassId");
                        generatedMonthlyPassId = jo.getString("GeneratedMonthlyPassId");
                    }
                    else{
                        locationId = jo.getString("LocationId");
                        paidAmount = jo.getString("PaidAmount");
                        parkedPaymentType = jo.getString("ParkedPaymentType");
                        isPayLater = jo.getString("IsPayLater");
                        isParkingFree = jo.getString("IsParkingFree");
                        releasedAmount = jo.getString("ReleasedAmount");
                        releasedPaymentType = jo.getString("ReleasedPaymentType");
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
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("get")){
                DecimalFormat df = new DecimalFormat("000");
                String parkingid = parkingAcronym + df.format(Integer.parseInt(generatedParkingId));
                char char_locationId = (char) (Integer.parseInt(generatedLocationId) + 'A' - 1);
                String location_id = parkingid + char_locationId;
                currentStatusTV.setText("CURRENT STATUS : "+currentStatus);
                vehicleNumberTV.setText(vehicleNumber);
                parkingLocationIdTV.setText(location_id);
                pendingAmountTV.setText("Rs. 00");
                passUserNameTV.setText(fullName);
                passUserMobileNumberTV.setText(mobileNumber);
                parkedDateTV.setText(parkedDate);
                parkedTimeTV.setText(parkedTime);
                parkedByTV.setText(parkedBy);
                if(currentStatus.equals("PARKED")){
                    releasedByTV.setText("N/A");
                    releasedTimeTV.setText("N/A");
                }else {
                    releasedByTV.setText(releasedBy);
                    releasedTimeTV.setText(releasedTime);
                }
                if(isMonthlyPass.equals("true")){
                    paidAmountTV.setText("Rs. 00");
                    if(parkedBy.equals(releasedBy)){
                        String payment = parkedBy + "<font color='#0e88d3'> Rs. 00";
                        cashPaymentTV.setText(Html.fromHtml(payment));
                        onlinePaymentTV.setText(Html.fromHtml(payment));
                        totalPaymentTV.setText(Html.fromHtml("<font color='#0e88d3'> Rs. 00"));
                    }
                    else{
                        String payment = parkedBy + "<font color='#0e88d3'> Rs. 00</font> <br>"+ releasedBy+ "<font color='#0e88d3'> Rs. 00</font>";
                        cashPaymentTV.setText(Html.fromHtml(payment));
                        onlinePaymentTV.setText(Html.fromHtml(payment));
                        totalPaymentTV.setText(Html.fromHtml("<font color='#0e88d3'> Rs. 00</font>"));
                    }
                }
                else{
                    // If Parking is free
                    if(isParkingFree.equals("1")){
                        paidAmountTV.setText("Rs. 00");
                        if(parkedBy.equals(releasedBy)){
                            String payment = parkedBy + "<font color='#0e88d3'> Rs. 00";
                            cashPaymentTV.setText(Html.fromHtml(payment));
                            onlinePaymentTV.setText(Html.fromHtml(payment));
                            totalPaymentTV.setText(Html.fromHtml("<font color='#0e88d3'> Rs. 00"));
                        }
                        else{
                            String payment = parkedBy + "<font color='#0e88d3'> Rs. 00</font> <br>"+ releasedBy+ "<font color='#0e88d3'> Rs. 00</font>";
                            cashPaymentTV.setText(Html.fromHtml(payment));
                            onlinePaymentTV.setText(Html.fromHtml(payment));
                            totalPaymentTV.setText(Html.fromHtml("<font color='#0e88d3'> Rs. 00</font>"));
                        }
                    }
                    // If parking is not free
                    else{
                        // If isPayLater chosen
                        if(isPayLater.equals("1")){
                            // If released by is not empty
                            if(!currentStatus.equals("RELEASED")) {
                                paidAmountTV.setText("Rs. "+releasedAmount);
                                if (parkedBy.equals(releasedBy)) {
                                    if (releasedPaymentType.equals("Online")) {
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + releasedAmount + "</font>"));
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00"));
                                    } else {
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + releasedAmount));
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00 </font>"));
                                    }
                                    //totalPaymentTV.setText(Html.fromHtml("<font color='#0e88d3'> Rs. "+ releasedAmount +"</font>"));
                                } else {
                                    if (releasedPaymentType.equals("Online")) {
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00</font> <br>" + releasedBy + "<font color='#0e88d3'> Rs. " + releasedAmount + "</font>"));
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00</font> <br>" + releasedBy + "<font color='#0e88d3'> Rs. 00</font>"));
                                    } else {
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00</font> <br>" + releasedBy + "<font color='#0e88d3'> Rs. " + releasedAmount + "</font>"));
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00</font> <br>" + releasedBy + "<font color='#0e88d3'> Rs. 00</font>"));
                                    }
                                }
                                totalPaymentTV.setText(Html.fromHtml("<font color='#0e88d3'> Rs. " + releasedAmount + "</font>"));
                            }
                            // if released by is empty
                            else{
                                paidAmountTV.setText("Rs. 00");
                                if (parkedPaymentType.equals("Online")) {
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + paidAmount + "</font>"));
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00"));
                                } else {
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + paidAmount));
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00 </font>"));
                                }
                                totalPaymentTV.setText(Html.fromHtml("<font color='#0e88d3'> Rs. " + paidAmount + "</font>"));
                            }
                        }
                        // If isPayLater is not chosen
                        else{
                            if(currentStatus.equals("RELEASED")) {
                                paidAmountTV.setText("Rs. " + (Integer.parseInt(paidAmount) + Integer.parseInt(releasedAmount)));
                                if (parkedBy.equals(releasedBy)) {
                                    if (releasedPaymentType.equals("Online") && parkedPaymentType.equals("Online")) {
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + (Integer.parseInt(paidAmount) + Integer.parseInt(releasedAmount)) + "</font>"));
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00"));
                                    } else if (releasedPaymentType.equals("Cash") && parkedPaymentType.equals("Cash")) {
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + (Integer.parseInt(paidAmount) + Integer.parseInt(releasedAmount)) + "</font>"));
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00"));
                                    } else if (releasedPaymentType.equals("Online") && parkedPaymentType.equals("Cash")) {
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + releasedAmount + " </font>"));
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + paidAmount + "</font>"));
                                    } else if (releasedPaymentType.equals("Cash") && parkedPaymentType.equals("Online")) {
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + releasedAmount + " </font>"));
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + paidAmount + "</font>"));
                                    } else if (releasedPaymentType.equals("null") && parkedPaymentType.equals("Online")) {
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00</font>"));
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + paidAmount + "</font>"));
                                    } else if (releasedPaymentType.equals("null") && parkedPaymentType.equals("Cash")) {
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + paidAmount + " </font>"));
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00</font>"));
                                    }
                                /*else{
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. "+releasedAmount));
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+ " <font color='#0e88d3'> Rs. 00 </font>"));
                                }*/
                                    //totalPaymentTV.setText(Html.fromHtml("<font color='#0e88d3'> Rs. "+()+"</font>"));
                                } else {
                                    if (releasedPaymentType.equals("Online") && parkedPaymentType.equals("Online")) {
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + paidAmount + "</font> <br>" + releasedBy + "<font color='#0e88d3'> Rs. " + releasedAmount + "</font>"));
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00</font> <br>" + releasedBy + "<font color='#0e88d3'> Rs. 00</font>"));
                                    } else if (releasedPaymentType.equals("Cash") && parkedPaymentType.equals("Cash")) {
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + paidAmount + "</font> <br>" + releasedBy + "<font color='#0e88d3'> Rs. " + releasedAmount + "</font>"));
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00</font> <br>" + releasedBy + "<font color='#0e88d3'> Rs. 00</font>"));
                                    } else if (releasedPaymentType.equals("Online") && parkedPaymentType.equals("Cash")) {
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00</font> <br>" + releasedBy + "<font color='#0e88d3'> Rs. " + releasedAmount + "</font>"));
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + paidAmount + "</font> <br>" + releasedBy + " <font color='#0e88d3'> Rs. 00</font>"));
                                    } else if (releasedPaymentType.equals("Cash") && parkedPaymentType.equals("Online")) {
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00</font> <br>" + releasedBy + "<font color='#0e88d3'> Rs. " + releasedAmount + "</font>"));
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + paidAmount + "</font> <br>" + releasedBy + " <font color='#0e88d3'> Rs. 00</font>"));
                                    } else if (releasedPaymentType.equals("null") && parkedPaymentType.equals("Online")) {
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00</font> <br>" + releasedBy + "<font color='#0e88d3'> Rs. 00</font>"));
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + paidAmount + "</font> <br>" + releasedBy + " <font color='#0e88d3'> Rs. 00</font>"));
                                    } else if (releasedPaymentType.equals("null") && parkedPaymentType.equals("Cash")) {
                                        onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00</font> <br>" + releasedBy + "<font color='#0e88d3'> Rs. 00</font>"));
                                        cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + paidAmount + "</font> <br>" + releasedBy + " <font color='#0e88d3'> Rs. 00</font>"));
                                    }
                                }
                                totalPaymentTV.setText(Html.fromHtml("<font color='#0e88d3'> Rs. " + (Integer.parseInt(paidAmount) + Integer.parseInt(releasedAmount)) + "</font>"));
                            }
                            else{
                                paidAmountTV.setText("Rs. "+paidAmount);
                                if (parkedPaymentType.equals("Online")) {
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + paidAmount + "</font>"));
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00"));
                                } else {
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. " + paidAmount));
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy + " <font color='#0e88d3'> Rs. 00 </font>"));
                                }
                                totalPaymentTV.setText(Html.fromHtml("<font color='#0e88d3'> Rs. " + paidAmount + "</font>"));
                            }
                        }
                    }
                }
            }
            progressDialog.dismiss();
        }
    }
}