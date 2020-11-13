package com.example.duepark_consumer.MonthlyPass;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duepark_consumer.Model.MonthlyPass;
import com.example.duepark_consumer.R;
import com.example.duepark_consumer.Valet.ValetHomeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivatedMonthlyPassActivity extends AppCompatActivity {

    private String base_url;
    private FloatingActionButton checkBtn;
    private TextView monthlyPassIdTV, passUserNameTV, passUserMobileNumberTV, vehicleTypeTV, vehicleNumberTV, issuedDateTV, expiryDateTV, issuedTimeTV, issuedByTV, paidAmountTV;
    private String monthlyPassId;
    private MonthlyPass monthlyPass;
    private boolean isValet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activated_monthly_pass);

        base_url = getResources().getString(R.string.base_url);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            monthlyPassId = bundle.getString("monthlyPassId");
            isValet = bundle.getBoolean("IsValet");
        }

        monthlyPassIdTV = findViewById(R.id.monthlyPassIdTV);
        passUserNameTV = findViewById(R.id.passUserNameTV);
        passUserMobileNumberTV = findViewById(R.id.passUserMobileNumberTV);
        vehicleTypeTV = findViewById(R.id.vehicleTypeTV);
        vehicleNumberTV = findViewById(R.id.vehicleNumberTV);
        issuedDateTV = findViewById(R.id.issueDateTV);
        expiryDateTV = findViewById(R.id.expireDateTV);
        issuedTimeTV = findViewById(R.id.issuedTimeTV);
        issuedByTV = findViewById(R.id.issuedByTV);
        paidAmountTV = findViewById(R.id.paidAmountTV);

        checkBtn = findViewById(R.id.checkBtn);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValet){
                    Intent i = new Intent(view.getContext(), ValetHomeActivity.class);
                    i.putExtra("IsValetMonthlyPass", true);
                    i.putExtra("IsValetReleasedVehicle", false);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }else {
                    startActivity(new Intent(view.getContext(), MonthlyPassActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
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
        backgroundTask.execute("get", monthlyPassId);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, AddMonthlyPassActivity.class);
        i.putExtra("IsValet", isValet);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    class BackgroundTask extends AsyncTask<String, MonthlyPass, String> {
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
                progressDialog.setMessage("Getting monthly pass detail...");
                String getMonthlyPass_url = base_url+"get_monthlyPass.php?MonthlyPassId="+params[1];
                //String getMonthlyPass_url = "https://duepark.000webhostapp.com/consumer/get_monthlyPass.php?MonthlyPassId="+params[1];
                try{
                    URL url = new URL(getMonthlyPass_url);
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

                    JSONObject jo = new JSONObject(jsonString);
                    monthlyPass = new MonthlyPass(jo.getString("id"), jo.getString("PassUserName"), jo.getString("PassUserMobileNumber"),
                            jo.getString("PayableAmount"), jo.getString("PayableType"), jo.getString("IssuedDate"),
                            jo.getString("ExpiryDate"), jo.getString("VehicleNumber"), jo.getString("VehicleType"),
                            jo.getString("GeneratedLocationId"), jo.getString("GeneratedMonthlyPassId"), jo.getString("IssuedBy"), jo.getString("IssuedTime"));
                    return "get";
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("get")){
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat month_date = new SimpleDateFormat("dd MMMM, yyyy", Locale.US);
                try {
                    Date issued_date = sdf.parse(monthlyPass.getIssuedDate());
                    Date expiry_date = sdf.parse(monthlyPass.getExpiryDate());
                    String issuedDate = month_date.format(issued_date);
                    String expiryDate = month_date.format(expiry_date);
                    issuedDateTV.setText(issuedDate);
                    expiryDateTV.setText(expiryDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                monthlyPassIdTV.setText("Pass Id - "+monthlyPass.getGeneratedLocationId()+""+monthlyPass.getGeneratedMonthlyPassId());
                passUserNameTV.setText(monthlyPass.getPassUserName());
                passUserMobileNumberTV.setText(monthlyPass.getPassUserMobileNumber());
                vehicleTypeTV.setText(monthlyPass.getVehicleType());
                vehicleNumberTV.setText(monthlyPass.getVehicleNumber());
                issuedByTV.setText(monthlyPass.getIssuedBy());
                issuedTimeTV.setText(monthlyPass.getIssuedTime());
                paidAmountTV.setText("Rs. "+monthlyPass.getPayableAmount());
            }
            progressDialog.dismiss();
        }
    }
}