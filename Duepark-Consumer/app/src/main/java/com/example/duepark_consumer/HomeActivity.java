package com.example.duepark_consumer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.duepark_consumer.Capacity.ParkingCapacityActivity;
import com.example.duepark_consumer.Employee.EmployeeActivity;
import com.example.duepark_consumer.Help.HelpActivity;
import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.Income.IncomeActivity;
import com.example.duepark_consumer.Location.LocationActivity;
import com.example.duepark_consumer.Model.ParkedVehicleList;
import com.example.duepark_consumer.MonthlyPass.MonthlyPassActivity;
import com.example.duepark_consumer.ParkedVehicle.ParkedVehicleActivity;
import com.example.duepark_consumer.Parking.ParkingActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private String base_url;
    private SessionManagerHelper sessionManagerHelper;
    private HashMap<String, String> employee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(this);
        employee = sessionManagerHelper.getEmployeeDetails();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        TextView parkingNameTV = findViewById(R.id.parkingNameTV);
        parkingNameTV.setText(employee.get(SessionManagerHelper.ParkingName));

        CardView profileCard = findViewById(R.id.profileCard);
        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ParkingActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        CardView locationCard = findViewById(R.id.locationCard);
        locationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, LocationActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        CardView employeeCard = findViewById(R.id.employeeCard);
        employeeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, EmployeeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        CardView vehicleParkedCard = findViewById(R.id.vehicleParkedCard);
        vehicleParkedCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ParkedVehicleActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        CardView incomeCard = findViewById(R.id.incomeCard);
        incomeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), IncomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        CardView monthlyPassCard = findViewById(R.id.monthPassCard);
        monthlyPassCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MonthlyPassActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        CardView capacityCard = findViewById(R.id.capacityCard);
        capacityCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), ParkingCapacityActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        CardView helpCard = findViewById(R.id.helpCard);
        helpCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, HelpActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.navigation_logOut){
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{

        private Context ctx;
        private ProgressDialog progressDialog;

        BackgroundTask(Context ctx){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Logging out...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String logout_url = base_url+"logout.php?EmployeeId="+employee.get(SessionManagerHelper.EmployeeId);
            try{
                URL url = new URL(logout_url);
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
                Log.d(TAG, "doInBackground: "+result);
                inputStream.close();
                bufferedReader.close();
                return result;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("LogoutSuccessfully")){
                sessionManagerHelper.logoutEmployee();
            }
            progressDialog.dismiss();
        }
    }
}
