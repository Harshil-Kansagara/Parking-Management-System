package com.example.duepark_consumer.Employee;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duepark_consumer.Model.Employee;
import com.example.duepark_consumer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivatedEmployeeActivity extends AppCompatActivity {

    private String base_url;
    private static final String TAG = "ActivatedEmployeeAct";
    private TextView employeeNameTV, employeeMobileNumberTV, employeeEmailIdTV, employeePasswordTV, generatedEmployeeIdTV, employeeDesignationTV, employeeAdharNumberTV, locationNameTV, locationName, vehicleType, vehicleTypeTV;
    private int employeeId;
    private Employee employee;
    private CircleImageView employeeProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activated_employee);

        base_url = getResources().getString(R.string.base_url);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            employeeId = bundle.getInt("EmployeeId");
        }

        employeeProfilePic = findViewById(R.id.employeeProfilePic);
        employeeNameTV = findViewById(R.id.employeeNameTV);
        employeeMobileNumberTV = findViewById(R.id.employeeMobileNumberTV);
        employeeEmailIdTV = findViewById(R.id.employeeEmailIdTV);
        employeePasswordTV = findViewById(R.id.employeePasswordTV);
        generatedEmployeeIdTV = findViewById(R.id.generatedEmployeeIdTV);
        employeeDesignationTV = findViewById(R.id.employeeDesignationTV);
        employeeAdharNumberTV = findViewById(R.id.employeeAdharNumberTV);
        locationName = findViewById(R.id.locationName);
        locationNameTV = findViewById(R.id.locationNameTV);
        vehicleType = findViewById(R.id.vehicleType);
        vehicleTypeTV = findViewById(R.id.vehicleTypeTV);

        FloatingActionButton verifyBtn = findViewById(R.id.verifyBtn);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivatedEmployeeActivity.this, EmployeeActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
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
        backgroundTask.execute(employeeId);

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ActivatedEmployeeActivity.this, AddEmployeeActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    class BackgroundTask extends AsyncTask<Integer, Void, Void>{
        private Context ctx;
        private ProgressDialog progressDialog;

        BackgroundTask(Context ctx){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Fetching...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            String getEmployeeDetail_url = base_url+"get_employee.php?EmployeeId="+params[0]+"&Entity=ConsumerApp";
            //String getEmployeeDetail_url = "https://duepark.000webhostapp.com/consumer/get_employee.php?EmployeeId="+params[0]+"&Entity=ConsumerApp";
            try{
                URL url = new URL(getEmployeeDetail_url);
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

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                int count = 0;
                while(count<jsonArray.length()){
                    JSONObject jo =jsonArray.getJSONObject(count);
                    employee = new Employee(jo.getString("id"), jo.getString("GeneratedEmployeeId"), jo.getString("EmployeeName"), jo.getString("EmployeeMobileNumber"),
                            jo.getString("EmployeeEmailId"), jo.getString("EmployeeAdharNumber"), jo.getString("EmployeePassword"), jo.getString("EmployeeProfilePic"),
                            jo.getString("EmployeeRole"), jo.getString("LocationName"), jo.getString("VehicleType"));
                    count++;
                }
                Log.d(TAG, "doInBackground: "+result);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //String baseUrl = "https://duepark.000webhostapp.com/consumer/consumer_profilePic/";
            DecimalFormat df = new DecimalFormat("000");
            String generatedEmployeeId = null;
            if(employee.getEmployeeRole().equals("Admin")){
                generatedEmployeeId = "A"+df.format(Integer.parseInt(employee.getGeneratedEmployeeId()));
            }
            if(employee.getEmployeeRole().equals("Manager")){
                generatedEmployeeId = "M"+df.format(Integer.parseInt(employee.getGeneratedEmployeeId()));
            }
            if(employee.getEmployeeRole().equals("Valet")){
                generatedEmployeeId = "V"+df.format(Integer.parseInt(employee.getGeneratedEmployeeId()));
            }
            generatedEmployeeIdTV.setText(generatedEmployeeId);
            employeeNameTV.setText(employee.getEmployeeName());
            employeeMobileNumberTV.setText(employee.getEmployeeMobileNumber());
            employeeEmailIdTV.setText(employee.getEmployeeEmailId());
            employeePasswordTV.setText(employee.getEmployeePassword());
            employeeDesignationTV.setText(employee.getEmployeeRole());
            employeeAdharNumberTV.setText(employee.getEmployeeAdharNumber());
            if(!employee.getEmployeeProfilePic().equals("null")){
                String profilePicUrl = base_url+"profilePic/"+employee.getEmployeeProfilePic()+".png";
                Picasso.get().load(profilePicUrl).into(employeeProfilePic);
            }
            if(employee.getLocationName().equals("null")){
                locationNameTV.setVisibility(View.GONE);
                locationName.setVisibility(View.GONE);
            }
            else{
                locationNameTV.setText(employee.getLocationName());
            }

            if(employee.getVehicleType().equals("null")){
                vehicleType.setVisibility(View.GONE);
                vehicleTypeTV.setVisibility(View.GONE);
            }
            else{
                vehicleTypeTV.setText(employee.getVehicleType());
            }

            progressDialog.dismiss();
/*            else{
                employeeProfilePic.setVisibility(View.INVISIBLE);
                //Picasso.get().load(R.drawable.userphoto).into(profile_photo);
                //profile_photo.setImageResource(R.drawable.userphoto);
            }*/
        }
    }
}