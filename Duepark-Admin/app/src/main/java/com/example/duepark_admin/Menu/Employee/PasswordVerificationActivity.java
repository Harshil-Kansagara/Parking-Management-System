package com.example.duepark_admin.Menu.Employee;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_admin.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class PasswordVerificationActivity extends AppCompatActivity {

    private String base_url;
    private TextInputEditText inputpassword;
    private String name, mobilenumber, emailid, designation, aadharnumber, password, profilePic, userid;
    private TextInputLayout passwordLayout;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_verification);

        base_url = getResources().getString(R.string.base_url);
        SharedPreferences sharedPreferences = this.getSharedPreferences("Employee", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            name = bundle.getString("name");
            mobilenumber = bundle.getString("mobilenumber");
            emailid = bundle.getString("emailid");
            designation = bundle.getString("designation");
            aadharnumber = bundle.getString("aadharnumber");
            password = bundle.getString("password");
            profilePic = bundle.getString("profilePic");
            userid = bundle.getString("userid");
        }

        TextView mobile = findViewById(R.id.mobilenum);
        mobile.setText(mobilenumber);
        inputpassword = findViewById(R.id.password);
        passwordLayout = findViewById(R.id.input_password);
        Button confirmBtn = findViewById(R.id.confirmBtn);
        Button resendBtn = findViewById(R.id.resendBtn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPassword();
            }
        });

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendPasswordSms();
            }
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, AddEmployeeActivity.class));
        finish();
    }

    private void resendPasswordSms(){
        /*try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mobilenumber,null,"Your can access Duepark application using "+emailid+" and "+password,null,null);
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }*/
        Uri uri = Uri.parse("smsto:"+mobilenumber);
        Intent sendSMS = new Intent(Intent.ACTION_VIEW, uri);
        sendSMS.putExtra("sms_body", "You can access Duepark application using "+emailid+" and "+password);
        startActivity(sendSMS);
    }

    private void verifyPassword(){
        String verifiedPassword = inputpassword.getText().toString().trim();
        if(verifiedPassword.isEmpty()){
            fieldEmpty(inputpassword);
            passwordLayout.setPasswordVisibilityToggleEnabled(false);
        }
        else if(verifiedPassword.length()<8){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputpassword.setError("Password should be of 8 digit",myIcon);
            requestFocus(inputpassword);
            passwordLayout.setPasswordVisibilityToggleEnabled(false);
        }
        else if(!verifiedPassword.equals(password)){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputpassword.setError("Please enter password provide in SMS",myIcon);
            requestFocus(inputpassword);
            passwordLayout.setPasswordVisibilityToggleEnabled(false);
        }
        else{
            BackgroundClass backgroundClass = new BackgroundClass(this);
            backgroundClass.execute(name, mobilenumber, emailid, aadharnumber, password, profilePic, designation,  userid);
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

    class BackgroundClass extends AsyncTask<String, Void, String> {

        Context ctx;
        ProgressDialog progressDialog;

        public BackgroundClass(Context ctx){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(this.ctx);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Saving data...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String add_url = base_url+"add_employee.php";
            //String add_url = "https://duepark.000webhostapp.com/add_employeeData.php";
            try{
                URL url = new URL(add_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream ops = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                String data = URLEncoder.encode("EmployeeName","UTF-8")+"="+URLEncoder.encode(params[0],"UTF-8")+"&"
                        +URLEncoder.encode("EmployeeMobileNumber","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                        +URLEncoder.encode("EmployeeEmailId", "UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"+
                        URLEncoder.encode("EmployeeAdharNumber", "UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8")+"&"+
                        URLEncoder.encode("EmployeePassword", "UTF-8")+"="+URLEncoder.encode(params[4],"UTF-8")+"&"+
                        URLEncoder.encode("EmployeeProfilePic", "UTF-8")+"="+URLEncoder.encode(params[5],"UTF-8")+"&"+
                        URLEncoder.encode("EmployeeRole", "UTF-8")+"="+URLEncoder.encode(params[6],"UTF-8")+"&"+
                        URLEncoder.encode("EmployeeEntity", "UTF-8")+"="+URLEncoder.encode("AdminApp","UTF-8")+"&"+
                        URLEncoder.encode("LocationId", "UTF-8")+"="+URLEncoder.encode("0","UTF-8")+"&"+
                        URLEncoder.encode("LoggedInEmployeeId", "UTF-8")+"="+URLEncoder.encode(params[7],"UTF-8");
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
                Log.d("PasswordVerify", "doInBackground: "+result);
                return result.toString();
            }
            catch (Exception ignored){
                ignored.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("exists")){
                Toast.makeText(ctx, "Employee Already Exists", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else if(result.equals("0")){
                Toast.makeText(ctx, "Employee not add... Please try again later", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(ctx, "Employee saved Successfully", Toast.LENGTH_SHORT).show();
                editor.clear();
                editor.apply();
                Intent activatedEmployeeActivity = new Intent(ctx, ActivatedEmployeeActivity.class);
                activatedEmployeeActivity.putExtra("userid", result.trim());
                startActivity(activatedEmployeeActivity);
                finish();
            }
            progressDialog.dismiss();
        }
    }
}
