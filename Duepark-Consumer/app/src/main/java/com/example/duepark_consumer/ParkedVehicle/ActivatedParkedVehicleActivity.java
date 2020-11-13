package com.example.duepark_consumer.ParkedVehicle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duepark_consumer.R;
import com.example.duepark_consumer.ParkedVehicle.Add.AddNumberPlateVehicleActivity;
import com.example.duepark_consumer.Valet.ValetHomeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActivatedParkedVehicleActivity extends AppCompatActivity {

    private String base_url;
    private String parkedVehicleId = null;
    private TextView vehicleDataTV, passUserNameTV, passUserMobileNumberTV, monthlyPass, monthlyPassIdTV, vehicleChargesTV, paidAmountText, paidAmountTV, pendingAmountText, pendingAmountTV, paymentTypeText, paymentTypeTV, payLater, payLaterTV, parkedDateTV, parkedTimeTV, parkedByTV;
    private boolean isValet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activated_parked_vehicle);

        base_url = getResources().getString(R.string.base_url);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            isValet = bundle.getBoolean("IsValet");
            parkedVehicleId = bundle.getString("ParkedVehicleId");
        }

        vehicleDataTV = findViewById(R.id.vehicleDataTV);
        passUserNameTV = findViewById(R.id.passUserNameTV);
        passUserMobileNumberTV = findViewById(R.id.passUserMobileNumberTV);
        monthlyPass = findViewById(R.id.monthlyPass);
        monthlyPassIdTV = findViewById(R.id.monthlyPassIdTV);
        //vehicleChargesTV = findViewById(R.id.vehicleChargesTV);
        paidAmountText = findViewById(R.id.paidAmount);
        paidAmountTV = findViewById(R.id.paidAmountTV);
        pendingAmountText = findViewById(R.id.pendingAmount);
        pendingAmountTV = findViewById(R.id.pendingAmountTV);
        paymentTypeText = findViewById(R.id.paymentType);
        paymentTypeTV = findViewById(R.id.paymentTypeTV);
        payLater = findViewById(R.id.payLater);
        payLaterTV = findViewById(R.id.payLaterTV);
        parkedDateTV = findViewById(R.id.parkedDateTV);
        parkedTimeTV = findViewById(R.id.parkedTimeTV);
        parkedByTV = findViewById(R.id.parkedByTV);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(parkedVehicleId != null){
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute();
        }

        FloatingActionButton verifyBtn = findViewById(R.id.verifyBtn);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValet){
                    startActivity(new Intent(view.getContext(), ValetHomeActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
                else {
                    startActivity(new Intent(view.getContext(), ParkedVehicleActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(isValet){
            startActivity(new Intent(this, ValetHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
        else {
            startActivity(new Intent(this, AddNumberPlateVehicleActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{

        private Context ctx;
        private ProgressDialog progressDialog;
        private String locationId, monthlyPassId, fullName, mobileNumber, vehicleType, vehicleNumber, isPayLater, paidAmount, parkedPaymentType, paymentTimeRate, isParkingFree, parkedBy, parkedTime, parkedDate;
        private String generatedLocationId, generatedMonthlyPassId, locationName;

        BackgroundTask(Context ctx){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(this.ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Getting Parked Vehicle detail...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String getParkedVehicle_url = base_url+"get_parkedVehicle.php?ParkedVehicleId="+parkedVehicleId;
            //String getParkedVehicle_url = "https://duepark.000webhostapp.com/consumer/get_parkedVehicle.php?ParkedVehicleId="+parkedVehicleId;
            try{
                URL url = new URL(getParkedVehicle_url);
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
                if(result!=null) {
                    JSONObject jo = new JSONObject(result);
                    locationId = jo.getString("LocationId");
                    monthlyPassId = jo.getString("MonthlyPassId");
                    fullName = jo.getString("FullName");
                    mobileNumber = jo.getString("MobileNumber");
                    vehicleType = jo.getString("VehicleType");
                    vehicleNumber = jo.getString("VehicleNumber");
                    parkedBy = jo.getString("ParkedBy");
                    parkedTime = jo.getString("ParkedTime");
                    parkedDate = jo.getString("ParkedDate");
                    locationName = jo.getString("LocationName");
                    if(monthlyPassId.equals("null")){
                        paidAmount = jo.getString("PaidAmount");
                        isPayLater = jo.getString("IsPayLater");
                        parkedPaymentType = jo.getString("ParkedPaymentType");
                        paymentTimeRate = jo.getString("PaymentTimeRate");
                        isParkingFree = jo.getString("IsParkingFree");
                    }
                    else{
                        generatedLocationId = jo.getString("GeneratedLocationId");
                        generatedMonthlyPassId = jo.getString("GeneratedMonthlyPassId");
                    }
                }
                inputStream.close();
                bufferedReader.close();
                return "get";
            }
            catch (Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("get")){
                vehicleDataTV.setText(vehicleType +" - "+vehicleNumber);
                passUserNameTV.setText(fullName);
                passUserMobileNumberTV.setText(mobileNumber);
                parkedByTV.setText(parkedBy);
                parkedDateTV.setText(parkedDate);
                parkedTimeTV.setText(parkedTime);
                if(monthlyPassId.equals("null")){
                    if(isParkingFree.equals("1")){
                        paymentTypeTV.setVisibility(View.GONE);
                        paymentTypeText.setVisibility(View.GONE);
                        paidAmountTV.setText("Rs. 00");
                        pendingAmountTV.setText("Rs. 00");
                        String url = "https://wa.me/"+mobileNumber+"?text=Hi,%0Your%20parked%20car%20"+vehicleNumber+"";
                    }
                    else{
                        if(isPayLater.equals("1")){
                            paidAmountTV.setText("Rs. 00");
                            pendingAmountTV.setText("Rs. 00");
                            paymentTypeTV.setVisibility(View.GONE);
                            paymentTypeText.setVisibility(View.GONE);
                            payLater.setVisibility(View.VISIBLE);
                            payLaterTV.setVisibility(View.VISIBLE);
                            payLaterTV.setText("Yes");
                        }
                        else{
                            paidAmountTV.setText("Rs. "+paidAmount);
                            pendingAmountTV.setText("Rs. 00");
                            paymentTypeTV.setText(parkedPaymentType);
                        }
                    }
                }
                else{
                    paidAmountText.setVisibility(View.GONE);
                    paidAmountTV.setVisibility(View.GONE);
                    pendingAmountText.setVisibility(View.GONE);
                    pendingAmountTV.setVisibility(View.GONE);
                    paymentTypeText.setVisibility(View.GONE);
                    paymentTypeTV.setVisibility(View.GONE);
                    monthlyPass.setVisibility(View.VISIBLE);
                    monthlyPassIdTV.setVisibility(View.VISIBLE);
                    monthlyPassIdTV.setText(generatedLocationId+""+generatedMonthlyPassId);
                }
            }
            else{
                //Toast.makeText(ctx, "", Toast.LENGTH_SHORT).show();
            }
            if(!mobileNumber.equals("+9100")) {
                parkedWhatsAppMsg();
            }
            progressDialog.dismiss();
        }

        private void parkedWhatsAppMsg(){
            String number = mobileNumber.substring(1);
            String whatsAppMsg = "Hi,%0D%0AYour%20"+vehicleType+"%20"+vehicleNumber.toUpperCase()+"%20is%20successfully%20parked%20at%20"+locationName+".%0D%0ADate%20-%20"+parkedDate+"%0D%0ATime%20-%20"+parkedTime+
                    "%0D%0AParked%20by%20-%20"+parkedBy+"%0D%0AAmount%20Paid%20-%20"+paidAmountTV.getText().toString().trim()+"%0D%0A%0D%0A%0D%0APowered%20By%20:%20Duepark%20Technologies%20Pvt%20Ltd";
            String whatsAppUrl = "https://wa.me/"+number+"?text="+whatsAppMsg;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(whatsAppUrl));
            startActivity(i);
        }
    }
}