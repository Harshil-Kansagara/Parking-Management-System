package com.example.duepark_consumer.ParkedVehicle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.BaseKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duepark_consumer.R;
import com.example.duepark_consumer.Valet.ValetHomeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReleasedParkedVehicleActivity extends AppCompatActivity {

    private String base_url;
    private static final String TAG = "ReleasedParkedVehicle";
    private TextView vehicleDataTV, passUserNameTV, passUserMobileNumberTV, paidAmountTV, pendingAmountTV, parkedDateTV, parkedTimeTV, releasedTimeTV, parkedByTV, releasedByTV, cashPaymentTV,
            onlinePaymentTV, totalPaymentTV;
    private boolean isBack = true, isValet;
    private String parkedVehicleId, vehicleType, vehicleNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_released_parked_vehicle);

        base_url = getResources().getString(R.string.base_url);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            parkedVehicleId = bundle.getString("parkedVehicleId");
            vehicleType = bundle.getString("vehicleType");
            vehicleNumber = bundle.getString("vehicleNumber");
            isValet = bundle.getBoolean("isValet");
        }

        vehicleDataTV = findViewById(R.id.vehicleDataTV);
        vehicleDataTV.setText(vehicleType+" - "+vehicleNumber.toUpperCase().replace("-", ""));
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

        FloatingActionButton verifyBtn = findViewById(R.id.verifyBtn);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBack = false;
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
        backgroundTask.execute();
    }

    @Override
    public void onBackPressed() {
        if(isValet){
            Intent i = new Intent(this, ValetHomeActivity.class);
            i.putExtra("IsValetReleasedVehicle", true);
            i.putExtra("IsValetMonthlyPass", false);
            startActivity(i);
            if(isBack) {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
            else{
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            finish();
        }else {
            startActivity(new Intent(this, ParkedVehicleActivity.class));
            if (isBack) {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            } else {
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            finish();
        }
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{

        private Context context;
        private ProgressDialog progressDialog;
        private String monthlyPassId, fullName, mobileNumber, parkedTime, parkedDate, parkedBy, releasedTime, releasedBy, isMonthlyPass, generatedMonthlyPassId, generatedLocationId;
        private String locationId, paidAmount, parkedPaymentType, isParkingFree, releasedAmount, releasedPaymentType, isPayLater, locationName;

        public BackgroundTask(Context ctx){
            context = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Fetching parked vehicle detail...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String releasedVehicle_url = base_url + "get_releasedVehicleDetail.php?ParkedVehicleId="+parkedVehicleId;
            try{
                URL url = new URL(releasedVehicle_url);
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
                    fullName = jo.getString("FullName");
                    mobileNumber = jo.getString("MobileNumber");
                    parkedDate = jo.getString("ParkedDate");
                    parkedTime = jo.getString("ParkedTime");
                    parkedBy = jo.getString("ParkedBy");
                    releasedTime = jo.getString("ReleasedTime");
                    releasedBy = jo.getString("ReleasedBy");
                    isMonthlyPass = jo.getString("IsMonthlyPass");
                    locationName = jo.getString("LocationName");
                    if(isMonthlyPass.equals("true")){
                        monthlyPassId = jo.getString("MonthlyPassId");
                        generatedLocationId = jo.getString("GeneratedLocationId");
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
                pendingAmountTV.setText("Rs. 00");
                passUserNameTV.setText(fullName);
                passUserMobileNumberTV.setText(mobileNumber);
                parkedDateTV.setText(parkedDate);
                parkedTimeTV.setText(parkedTime);
                parkedByTV.setText(parkedBy);
                releasedByTV.setText(releasedBy);
                releasedTimeTV.setText(releasedTime);
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
                            paidAmountTV.setText("Rs. "+releasedAmount);
                            if(parkedBy.equals(releasedBy)){
                                if(releasedPaymentType.equals("Online")){
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. "+releasedAmount+"</font>"));
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+ " <font color='#0e88d3'> Rs. 00"));
                                }
                                else{
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. "+releasedAmount));
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+ " <font color='#0e88d3'> Rs. 00 </font>"));
                                }
                                //totalPaymentTV.setText(Html.fromHtml("<font color='#0e88d3'> Rs. "+ releasedAmount +"</font>"));
                            }
                            else{
                                if(releasedPaymentType.equals("Online")){
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. 00</font> <br>"+releasedBy+"<font color='#0e88d3'> Rs. "+releasedAmount+"</font>"));
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. 00</font> <br>"+releasedBy+"<font color='#0e88d3'> Rs. 00</font>"));
                                }
                                else{
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. 00</font> <br>"+releasedBy+"<font color='#0e88d3'> Rs. "+releasedAmount+"</font>"));
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. 00</font> <br>"+releasedBy+"<font color='#0e88d3'> Rs. 00</font>"));
                                }
                            }
                            totalPaymentTV.setText(Html.fromHtml("<font color='#0e88d3'> Rs. "+ releasedAmount +"</font>"));
                        }
                        // If isPayLater is not chosen
                        else{
                            paidAmountTV.setText("Rs. "+(Integer.parseInt(paidAmount)+Integer.parseInt(releasedAmount)));
                            if(parkedBy.equals(releasedBy)){
                                if(releasedPaymentType.equals("Online") && parkedPaymentType.equals("Online")){
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. "+(Integer.parseInt(paidAmount)+Integer.parseInt(releasedAmount))+"</font>"));
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+ " <font color='#0e88d3'> Rs. 00"));
                                }
                                else if(releasedPaymentType.equals("Cash") && parkedPaymentType.equals("Cash")){
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. "+(Integer.parseInt(paidAmount)+Integer.parseInt(releasedAmount))+"</font>"));
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+ " <font color='#0e88d3'> Rs. 00"));
                                }
                                else if(releasedPaymentType.equals("Online") && parkedPaymentType.equals("Cash")){
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+ " <font color='#0e88d3'> Rs. "+releasedAmount+" </font>"));
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. "+paidAmount+"</font>"));
                                }
                                else if(releasedPaymentType.equals("Cash") && parkedPaymentType.equals("Online")){
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+ " <font color='#0e88d3'> Rs. "+releasedAmount+" </font>"));
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. "+paidAmount+"</font>"));
                                }
                                else if(releasedPaymentType.equals("null") && parkedPaymentType.equals("Online")){
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+ " <font color='#0e88d3'> Rs. 00</font>"));
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. "+paidAmount+"</font>"));
                                }
                                else if(releasedPaymentType.equals("null") && parkedPaymentType.equals("Cash")){
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+ " <font color='#0e88d3'> Rs. "+paidAmount+" </font>"));
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. 00</font>"));
                                }
                                /*else{
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. "+releasedAmount));
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+ " <font color='#0e88d3'> Rs. 00 </font>"));
                                }*/
                                //totalPaymentTV.setText(Html.fromHtml("<font color='#0e88d3'> Rs. "+()+"</font>"));
                            }
                            else{
                                if(releasedPaymentType.equals("Online") && parkedPaymentType.equals("Online")){
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. "+paidAmount+"</font> <br>"+releasedBy+"<font color='#0e88d3'> Rs. "+releasedAmount+"</font>"));
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. 00</font> <br>"+releasedBy+"<font color='#0e88d3'> Rs. 00</font>"));
                                }
                                else if(releasedPaymentType.equals("Cash") && parkedPaymentType.equals("Cash")){
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. "+paidAmount+"</font> <br>"+releasedBy+"<font color='#0e88d3'> Rs. "+releasedAmount+"</font>"));
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. 00</font> <br>"+releasedBy+"<font color='#0e88d3'> Rs. 00</font>"));
                                }
                                else if(releasedPaymentType.equals("Online") && parkedPaymentType.equals("Cash")){
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+ " <font color='#0e88d3'> Rs. 00</font> <br>"+releasedBy+"<font color='#0e88d3'> Rs. "+releasedAmount+"</font>"));
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. "+paidAmount+"</font> <br>"+releasedBy+" <font color='#0e88d3'> Rs. 00</font>"));
                                }
                                else if(releasedPaymentType.equals("Cash") && parkedPaymentType.equals("Online")){
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+ " <font color='#0e88d3'> Rs. 00</font> <br>"+releasedBy+"<font color='#0e88d3'> Rs. "+releasedAmount+"</font>"));
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. "+paidAmount+"</font> <br>"+releasedBy+" <font color='#0e88d3'> Rs. 00</font>"));
                                }
                                else if(releasedPaymentType.equals("null") && parkedPaymentType.equals("Online")){
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+ " <font color='#0e88d3'> Rs. 00</font> <br>"+releasedBy+"<font color='#0e88d3'> Rs. 00</font>"));
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. "+paidAmount+"</font> <br>"+releasedBy+" <font color='#0e88d3'> Rs. 00</font>"));
                                }
                                else if(releasedPaymentType.equals("null") && parkedPaymentType.equals("Cash")){
                                    onlinePaymentTV.setText(Html.fromHtml(parkedBy+ " <font color='#0e88d3'> Rs. 00</font> <br>"+releasedBy+"<font color='#0e88d3'> Rs. 00</font>"));
                                    cashPaymentTV.setText(Html.fromHtml(parkedBy+" <font color='#0e88d3'> Rs. "+paidAmount+"</font> <br>"+releasedBy+" <font color='#0e88d3'> Rs. 00</font>"));
                                }
                            }
                            totalPaymentTV.setText(Html.fromHtml("<font color='#0e88d3'> Rs. "+(Integer.parseInt(paidAmount)+Integer.parseInt(releasedAmount))+"</font>"));
                        }
                    }
                }
            }
            progressDialog.dismiss();
            if(!mobileNumber.equals("+9100")){
                releasedWhatsAppMsg();
            }
        }
        private void releasedWhatsAppMsg(){
            String number = mobileNumber.substring(1);
            String whatsAppMsg = "Hi,%0D%0AYour%20"+vehicleType+"%20"+vehicleNumber.toUpperCase()+"%20is%20successfully%20released%20at%20"+locationName+".%0D%0ADate%20-%20"+parkedDate+"%0D%0ATime%20-%20"+parkedTime+
                    "%0D%0AParked%20by%20-%20"+parkedBy+"%0D%0AAmount%20Paid%20-%20"+paidAmountTV.getText().toString().trim()+"%0D%0A%0D%0A%0D%0APowered%20By%20:%20Duepark%20Technologies%20Pvt%20Ltd";
            String whatsAppUrl = "https://wa.me/"+number+"?text="+whatsAppMsg;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(whatsAppUrl));
            startActivity(i);
        }
    }
}