package com.example.duepark_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.duepark_admin.Service.MyFirebaseInstanceService;
import com.example.duepark_admin.Service.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

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

    private String base_url = null;
    private TextInputEditText inputemail, inputpassword;
    private TextInputLayout passwordLayout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        base_url = getResources().getString(R.string.base_url);
        sessionManager = new SessionManager(this);
        //sessionManager.checkLogin();

        inputemail = findViewById(R.id.email);
        inputpassword = findViewById(R.id.password);
        passwordLayout = findViewById(R.id.input_password);


        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }

    private void loginUser(){
        String email = inputemail.getText().toString().trim();
        String password = inputpassword.getText().toString().trim();

        String emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher emailMatches = emailPattern.matcher(email);

        if(email.isEmpty()){
            fieldEmpty(inputemail);
        }
        else if(!emailMatches.matches())
        {
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputemail.setError("Please check your email id.",myIcon);
            requestFocus(inputemail);
        }
        else if(password.isEmpty()){
            fieldEmpty(inputpassword);
            passwordLayout.setPasswordVisibilityToggleEnabled(false);
        }
        else if(password.length()<8){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputpassword.setError("Password should be of 8 digit",myIcon);
            requestFocus(inputpassword);
            passwordLayout.setPasswordVisibilityToggleEnabled(false);
        }else{
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute("login", email, password);
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
        sessionManager.checkLogin();
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{
        private static final String TAG = "LoginActivity";
        Context ctx;
        ProgressDialog progressDialog;
        String employeeId, employeeName, employeeProfilePic, role, isActivated, isNewPasswordCreated, generatedEmployeeId;

        BackgroundTask(Context ctx) {
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
            String keyword = params[0];
            if(keyword.equals("login")){
                String login_url = base_url+"login.php";
                //String login_url = "https://duepark.000webhostapp.com/consumer/login.php";
                try{
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("EmployeeEmailId","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                            +URLEncoder.encode("EmployeePassword","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"
                            +URLEncoder.encode("EmployeeEntity", "UTF-8")+"="+URLEncoder.encode("AdminApp", "UTF-8");
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
                    Log.d(TAG, "doInBackground: "+resultData);
                    if(!resultData.equals("0")) {
                        //String json_string = result.toString().trim();
                        JSONObject jsonObject = new JSONObject(resultData);
                        JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            employeeId = jo.getString("id");
                            employeeName = jo.getString("EmployeeName");
                            role = jo.getString("Role");
                            isActivated = jo.getString("IsActivated");
                            employeeProfilePic = jo.getString("EmployeeProfilePic");
                            isNewPasswordCreated = jo.getString("IsEmployeeNewPasswordCreated");
                            generatedEmployeeId = jo.getString("GeneratedEmployeeId");
                        }
                     //   resultData = userId;
                    }
                    br.close();
                    ips.close();

                    httpURLConnection.disconnect();
                    return resultData;
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(result.equals("0")){
                Toast.makeText(ctx, "Login Failed", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ctx, "Login Successful", Toast.LENGTH_SHORT).show();
                boolean isEmployeeNewPasswordCreated, _isActivated;
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

                sessionManager.createLoginSession(employeeId, employeeName,employeeProfilePic, role, isEmployeeNewPasswordCreated, _isActivated, generatedEmployeeId);
//                MyFirebaseInstanceService myFirebaseInstanceService = new MyFirebaseInstanceService(ctx);
//                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
//                    @Override
//                    public void onSuccess(InstanceIdResult instanceIdResult) {
//                        myFirebaseInstanceService.onNewToken(instanceIdResult.getToken());
//                    }
//                });
//                myFirebaseInstanceService.startBackgroundService();
                //Toast.makeText(ctx, userId+" "+userName+" "+userDesignation+" "+userProfile, Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(ctx, NewPasswordActivity.class));
                //finish();
            }
            progressDialog.dismiss();
        }
    }
}
