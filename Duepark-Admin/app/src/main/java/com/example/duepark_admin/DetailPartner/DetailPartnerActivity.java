package com.example.duepark_admin.DetailPartner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_admin.DetailPartner.HireEmployee.HireManagerEmployeeListActivity;
import com.example.duepark_admin.DetailPartner.HireEmployee.HireSaleEmployeeListActivity;
import com.example.duepark_admin.HomeActivity;
import com.example.duepark_admin.R;
import com.example.duepark_admin.Service.SessionManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DetailPartnerActivity extends AppCompatActivity implements View.OnClickListener {

    private String base_url;
    private String id, parking_name, activation_state;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_partner);

        base_url = getResources().getString(R.string.base_url);
        sessionManager = new SessionManager(this);

        sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            id = bundle.getString("Id");
            parking_name = bundle.getString("ParkingName");
            activation_state = bundle.getString("ActivationState");
        }
        if(id == null) {
            getIdData();
        }

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        TextView parkingName = findViewById(R.id.parkingName);
        parkingName.setText(parking_name);

        CardView profileCard = findViewById(R.id.profileCard);
        profileCard.setOnClickListener(this);

        CardView locationCard = findViewById(R.id.parkingLocationCard);
        locationCard.setOnClickListener(this);

        CardView vehicleCard = findViewById(R.id.vehicleParkedCard);
        vehicleCard.setOnClickListener(this);

        CardView employeeCard = findViewById(R.id.employeeCard);
        employeeCard.setOnClickListener(this);

        CardView incomeCard = findViewById(R.id.incomeCard);
        incomeCard.setOnClickListener(this);

        CardView activeCard = findViewById(R.id.activeCard);
        activeCard.setOnClickListener(this);

        initHireBtn();
    }

    private void initHireBtn(){
        HashMap<String, String> user = sessionManager.getUserDetails();
        /*Button adminBtn = findViewById(R.id.adminBtn);*/
        Button managerBtn = findViewById(R.id.managerBtn);
        Button saleBtn = findViewById(R.id.saleBtn);

        if(Objects.equals(user.get(sessionManager.EmployeeRole), "SuperAdmin") ||
                Objects.equals(user.get(sessionManager.EmployeeRole), "Admin")){
            //adminBtn.setOnClickListener(this);
            managerBtn.setOnClickListener(this);
            saleBtn.setOnClickListener(this);
        }
        else if(Objects.equals(user.get(sessionManager.EmployeeRole), "Manager")){
            //adminBtn.setVisibility(View.GONE);
            managerBtn.setVisibility(View.GONE);
            saleBtn.setOnClickListener(this);
        }
        else {
            //adminBtn.setVisibility(View.GONE);
            managerBtn.setVisibility(View.GONE);
            saleBtn.setVisibility(View.GONE);
        }
    }

    private void getIdData(){
        if(sharedPreferences.contains("Id")){
            id = sharedPreferences.getString("Id","");
        }
        if(sharedPreferences.contains("ParkingName")){
            parking_name = sharedPreferences.getString("ParkingName", "");
        }
    }

    private void storeIdData(){
        if(id!=null){
            editor.remove("Id");
            editor.apply();
            editor.putString("Id", id);
            editor.apply();
        }
        if(parking_name!=null){
            editor.remove("ParkingName");
            editor.apply();
            editor.putString("ParkingName", parking_name);
            editor.apply();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.backBtn:
                editor.remove("Data");
                editor.apply();
                Intent homeIntent = new Intent(this, HomeActivity.class);
                startActivity(homeIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;

            case R.id.profileCard:
                storeIdData();
                Intent intent = new Intent(this, ProfilePartnerActivity.class);
                intent.putExtra("Id", id);
                intent.putExtra("ParkingName", parking_name);
                intent.putExtra("Address", "");
                intent.putExtra("Acronym", "");
                intent.putExtra("City", "");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;

            case R.id.parkingLocationCard:
                storeIdData();
                //Intent locationIntent = new Intent(this, LocationListPartnerActivity.class);
                Intent locationIntent = new Intent(this, LocationPartnerActivity.class);
                locationIntent.putExtra("Id", id);
                locationIntent.putExtra("ParkingName", parking_name);
                startActivity(locationIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;

            case R.id.vehicleParkedCard:
                storeIdData();
                Intent vehicleIntent = new Intent(this, VehicleParkedPartnerActivity.class);
                vehicleIntent.putExtra("Id", id);
                vehicleIntent.putExtra("ParkingName", parking_name);
                startActivity(vehicleIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;

            case R.id.employeeCard:
                Intent employeeIntent = new Intent(this, EmployeePartnerActivity.class);
                employeeIntent.putExtra("Id", id);
                employeeIntent.putExtra("ParkingName", parking_name);
                startActivity(employeeIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;

            case R.id.incomeCard:
                Intent incomeIntent = new Intent(this, IncomePartnerActivity.class);
                incomeIntent.putExtra("Id", id);
                incomeIntent.putExtra("ParkingName", parking_name);
                startActivity(incomeIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;

            case R.id.activeCard:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetailPartnerActivity.this);
                if(activation_state.equals("1")){
                    alertBuilder.setTitle("Deactivate Partner");
                    alertBuilder.setMessage("Are you sure you want to deactivate partner ?");
                }
                else if(activation_state.equals("0")){
                    alertBuilder.setTitle("Activate Partner");
                    alertBuilder.setMessage("Are you sure you want to activate partner ?");
                }
                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BackgroundTask backgroundTask = new BackgroundTask(DetailPartnerActivity.this);
                        if(activation_state.equals("1")){
                            backgroundTask.execute("deactivate", id, "0");
                        }
                        else{
                            backgroundTask.execute("deactivate", id, "1");
                        }
                        dialog.cancel();

                    }
                });

                alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
                /*storeIdData();
                Intent activationStateIntent = new Intent(this, ActivationStatePartnerActivity.class);
                activationStateIntent.putExtra("Id", id);
                activationStateIntent.putExtra("ParkingName", parking_name);
                startActivity(activationStateIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();*/
                break;

           /* case R.id.adminBtn:
                storeIdData();
                openHireAdminEmployeeList();
                break;*/

            case R.id.managerBtn:
                storeIdData();
                openHireManagerEmployeeList();
                break;

            case R.id.saleBtn:
                storeIdData();
                openHireSaleEmployeeList();
                break;

        }
    }

    /*private void openHireAdminEmployeeList() {
        Intent hireAdminEmployeeList = new Intent(this, HireAdminEmployeeListActivity.class);
        hireAdminEmployeeList.putExtra("parkingid", id);
        startActivity(hireAdminEmployeeList);
        finish();
    }
*/
    private void openHireManagerEmployeeList(){
        Intent hireManagerEmployeeList = new Intent(this, HireManagerEmployeeListActivity.class);
        hireManagerEmployeeList.putExtra("parkingid", id);
        startActivity(hireManagerEmployeeList);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void openHireSaleEmployeeList(){
        Intent hireSaleEmployeeList = new Intent(this, HireSaleEmployeeListActivity.class);
        hireSaleEmployeeList.putExtra("parkingid", id);
        startActivity(hireSaleEmployeeList);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{
        private Context ctx;
        private ProgressDialog progressDialog;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(this.ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading Data...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if(params[0].equals("deactivate")){
                progressDialog.setMessage("Updating Partner activation state...");
                String id = params[1];
                String deactivate_url = base_url+"set_parkingActiveState_Admin.php";
                //String deactivate_url = "https://duepark.000webhostapp.com/update_parkingActiveState.php";
                try{
                    URL url = new URL(deactivate_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("ParkingId","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"+
                                URLEncoder.encode("ActiveState","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8");
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
        protected void onPostExecute(String s) {
            if(s.equals("activate")){
                Toast.makeText(ctx, "Partner activate successfully...", Toast.LENGTH_SHORT).show();
            }
            else if(s.equals("deactivate")){
                Toast.makeText(ctx, "Partner deactivate successfully...", Toast.LENGTH_SHORT).show();
            }
            onBackPressed();
            progressDialog.dismiss();
        }
    }
}
