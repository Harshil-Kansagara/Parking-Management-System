package com.example.duepark_consumer;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.SignUp.SignUp1Activity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private String base_url;
    private TextInputEditText input_mobileNumber, input_password;
    private TextInputLayout inputLayout_password;
    private String TAG = "com.example.duepark_consumer.LoginActivity";
    private SessionManagerHelper sessionManagerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(this);

        input_mobileNumber = findViewById(R.id.input_mobileNumber);
        input_password = findViewById(R.id.input_password);
        inputLayout_password = findViewById(R.id.inputLayout_password);

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEmployee();
            }
        });

        TextView forgotPsdBtn = findViewById(R.id.forgotPsdBtn);
        forgotPsdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), ForgotPasswordActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        TextView createBtn = findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUp1Activity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
    }

    private void loginEmployee(){

        String mobileRegex = "^[6-9][0-9]{9}$";
        String employeeMobileNumber = input_mobileNumber.getText().toString().trim();
        String employeePassword = input_password.getText().toString().trim();

        Pattern mobilePattern = Pattern.compile(mobileRegex);
        Matcher mobileNumberMatches = mobilePattern.matcher(employeeMobileNumber);

        if(employeeMobileNumber.isEmpty()){
            fieldEmpty(input_mobileNumber);
        }
        else if(!mobileNumberMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            input_mobileNumber.setError("Please enter valid mobile number.",myIcon);
            requestFocus(input_mobileNumber);
        }
        else if(employeePassword.isEmpty()){
            fieldEmpty(input_password);
            inputLayout_password.setPasswordVisibilityToggleEnabled(false);
        }
        else if(employeePassword.length()<8){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            input_password.setError("Password should be of 8 digit",myIcon);
            requestFocus(inputLayout_password);
            inputLayout_password.setPasswordVisibilityToggleEnabled(false);
        }
        else {
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute(employeeMobileNumber, employeePassword);
        }
    }

    private void fieldEmpty(TextInputEditText input){
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
    protected void onStart() {
        super.onStart();
        sessionManagerHelper.checkLogin();
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{
        Context ctx;
        ProgressDialog progressDialog;
        String parkingId, locationId, parkingName, employeeId, employeeName, employeeProfilePic, role, isActivated, isNewPasswordCreated, generatedEmployeeId, vehicleType;

        BackgroundTask(Context ctx){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Verifying...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            //String login_url = "https://duepark.000webhostapp.com/consumer/login.php";
            String login_url = base_url+"login.php";
            try{
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream ops = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                String data = URLEncoder.encode("EmployeeMobileNumber","UTF-8")+"="+URLEncoder.encode(params[0],"UTF-8")+"&"
                        +URLEncoder.encode("EmployeePassword","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                        +URLEncoder.encode("EmployeeEntity", "UTF-8")+"="+URLEncoder.encode("ConsumerApp", "UTF-8");
                bw.write(data);
                bw.flush();
                bw.close();
                ops.close();

                InputStream ips = httpURLConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(ips,"iso-8859-1"));
                StringBuilder result= new StringBuilder();
                String line = "";
                while((line=br.readLine())!=null){
                    result.append(line+"\n");
                }
                String resultData  = result.toString().trim();
                if(!resultData.equals("0")){
                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        parkingId = jo.getString("ParkingId");
                        locationId = jo.getString("LocationId");
                        parkingName = jo.getString("ParkingName");
                        employeeId = jo.getString("id");
                        employeeName = jo.getString("EmployeeName");
                        role = jo.getString("Role");
                        isActivated = jo.getString("IsActivated");
                        employeeProfilePic = jo.getString("EmployeeProfilePic");
                        isNewPasswordCreated = jo.getString("IsEmployeeNewPasswordCreated");
                        generatedEmployeeId = jo.getString("GeneratedEmployeeId");
                        vehicleType = jo.getString("VehicleType");
                    }
                }
                br.close();
                ips.close();
                httpURLConnection.disconnect();
                Log.d(TAG, "doInBackground: "+resultData);
                return resultData;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("0")){
                Toast.makeText(ctx, "Login Failed", Toast.LENGTH_SHORT).show();
            }else{
                boolean isEmployeeNewPasswordCreated, _isActivated;
                Toast.makeText(ctx, "Login Successful", Toast.LENGTH_SHORT).show();
                if(isNewPasswordCreated.equals("0")){
                    isEmployeeNewPasswordCreated = false;
                }
                else{
                    isEmployeeNewPasswordCreated = true;
                }

                if(isActivated.equals("0")){
                    _isActivated = false;
                }
                else{
                    _isActivated = true;
                }
                if(employeeProfilePic.equals("")){
                    employeeProfilePic = null;
                }
                sessionManagerHelper.createLoginSession(parkingId, locationId, parkingName, employeeId, employeeName,employeeProfilePic, role, vehicleType, isEmployeeNewPasswordCreated, _isActivated, generatedEmployeeId);
                //Toast.makeText(ctx, userId+" "+userName+" "+userDesignation+" "+userProfile, Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(ctx, NewPasswordActivity.class));
                //finish();
            }
            progressDialog.dismiss();
        }
    }
}
