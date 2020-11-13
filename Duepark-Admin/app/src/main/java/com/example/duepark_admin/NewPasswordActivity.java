package com.example.duepark_admin;

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
import android.widget.Toast;

import com.example.duepark_admin.Service.SessionManager;
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
import java.util.HashMap;

public class NewPasswordActivity extends AppCompatActivity {

    private String base_url;
    private TextInputEditText inputpassword;
    private TextInputLayout passwordLayout;
    private SessionManager sessionManager;
    private HashMap<String, String> user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        base_url = getResources().getString(R.string.base_url);
        sessionManager = new SessionManager(this);

        inputpassword = findViewById(R.id.password);
        passwordLayout = findViewById(R.id.input_password);
        Button confirmBtn = findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });
    }

    private void updatePassword(){
        user = sessionManager.getUserDetails();
        String newPassword = inputpassword.getText().toString().trim();
        if(newPassword.isEmpty()){
            fieldEmpty(inputpassword);
            passwordLayout.setPasswordVisibilityToggleEnabled(false);
        }
        else if(newPassword.length()<8){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputpassword.setError("Password should be of 8 digit",myIcon);
            requestFocus(inputpassword);
            passwordLayout.setPasswordVisibilityToggleEnabled(false);
        }
        else{
            BackgroundClass backgroundClass = new BackgroundClass(this);
            backgroundClass.execute(user.get(sessionManager.EmployeeId), newPassword);
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

    class BackgroundClass extends AsyncTask<String, Void, String>{

        Context ctx;
        ProgressDialog progressDialog;

        BackgroundClass(Context ctx){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(this.ctx);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Updating password...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String update_url = base_url+"update_password.php";
            //String update_url = "https://duepark.000webhostapp.com/update_password.php?userid="+params[0];
            try{
                URL url = new URL(update_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream ops = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                String data = URLEncoder.encode("newPassword","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"+
                            URLEncoder.encode("EmployeeId","UTF-8")+"="+URLEncoder.encode(params[0],"UTF-8");
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
                Toast.makeText(ctx, "New Password update successfully", Toast.LENGTH_SHORT).show();
                sessionManager.updateEmployeeNewPassword(true);
                startActivity(new Intent(ctx, HomeActivity.class));
                finish();
            }
            else{
                Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }
}
