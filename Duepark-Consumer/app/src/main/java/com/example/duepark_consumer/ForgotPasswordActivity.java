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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.Valet.ValetHomeActivity;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    private String base_url;
    private TextInputEditText input_mobileNumber, input_password;
    private TextInputLayout inputLayout_password;
    private String TAG = "com.example.duepark_consumer.LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        base_url = getResources().getString(R.string.base_url);

        input_mobileNumber = findViewById(R.id.input_mobileNumber);
        input_password = findViewById(R.id.inputPassword);
        inputLayout_password = findViewById(R.id.inputLayout_password);

        Button confirmBtn = findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void updatePassword(){
        String mobileNumber = input_mobileNumber.getText().toString().trim();
        String newPassword = input_password.getText().toString().trim();

        String mobileRegex = "^[6-9][0-9]{9}$";
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        Matcher mobileNumberMatches = mobilePattern.matcher(mobileNumber);

        if(mobileNumber.isEmpty()){
            fieldEmpty(input_mobileNumber);
        }
        else if(!mobileNumberMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            input_mobileNumber.setError("Please enter valid mobile number.",myIcon);
            requestFocus(input_mobileNumber);
        }
        else if(newPassword.isEmpty()){
            fieldEmpty(input_password);
            inputLayout_password.setPasswordVisibilityToggleEnabled(false);
        }
        else if(newPassword.length()<8){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            input_password.setError("Password should be of 8 digit",myIcon);
            requestFocus(input_password);
            inputLayout_password.setPasswordVisibilityToggleEnabled(false);
        }
        else{
            BackgroundClass backgroundClass = new BackgroundClass(this);
            backgroundClass.execute(mobileNumber, newPassword);
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

        BackgroundClass(Context ctx){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(this.ctx);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Creating new password...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String update_url = base_url+"update_password.php";
            //String update_url = "https://duepark.000webhostapp.com/consumer/update_password.php";
            try{
                URL url = new URL(update_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream ops = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                String data = URLEncoder.encode("MobileNumber","UTF-8")+"="+URLEncoder.encode(params[0],"UTF-8")+"&"+
                        URLEncoder.encode("newPassword","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8");
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
                Log.d("Result", result.toString());
                return result.toString();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("1")){
                Toast.makeText(ctx, "New Password created successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else{
                Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }
}