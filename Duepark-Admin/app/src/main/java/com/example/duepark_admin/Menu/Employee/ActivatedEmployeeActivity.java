package com.example.duepark_admin.Menu.Employee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duepark_admin.R;
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
    private String userid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activated_employee);

        base_url = getResources().getString(R.string.base_url);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            userid = bundle.getString("userid");
        }

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FloatingActionButton employeeListBtn = findViewById(R.id.employeeListBtn);
        employeeListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivatedEmployeeActivity.this, EmployeeActivity.class));
                finish();
            }
        });

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("get", userid);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivatedEmployeeActivity.this, AddEmployeeActivity.class));
        finish();
    }

    class BackgroundTask extends AsyncTask<String, Void, Void> {

        Context ctx;
        ProgressDialog progressDialog;
        TextView textid, textname, textmobilenumber, textemailid, textdesignation, textaadharnumber;
        String userId, name, mobilenumber, emailid, designation, aadharnumber, profile, generatedEmployeeId;
        CircleImageView profile_photo;
        //String baseUrl = "https://duepark.000webhostapp.com/profilePic/";

        BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            textid = findViewById(R.id.employeeId);
            textname = findViewById(R.id.name);
            textmobilenumber = findViewById(R.id.mobileNumber);
            textemailid = findViewById(R.id.emailId);
            textdesignation = findViewById(R.id.designationType);
            textaadharnumber = findViewById(R.id.aadharNumber);
            profile_photo = findViewById(R.id.profile_photo);

            progressDialog.setMessage("Loading Data....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String keyword = params[0];
            if(keyword.equals("get"))
            {
                String id = params[1];
                String detail_url = base_url+"get_employee.php?EmployeeId="+id+"&Entity=AdminApp";
                //String detail_url = "https://duepark.000webhostapp.com/get_employeeData.php?userid="+id;
                try
                {
                        URL url = new URL(detail_url);
                        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                        InputStream inputStream = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                        StringBuilder stringBuilder = new StringBuilder();
                        String line;

                        while((line = bufferedReader.readLine())!=null){
                            stringBuilder.append(line+"\n");
                        }
                        httpURLConnection.disconnect();

                        String jsonString = stringBuilder.toString().trim();
                        JSONObject jsonObject = new JSONObject(jsonString);
                        JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                        int count = 0;
                        while(count<jsonArray.length()) {
                            JSONObject jo = jsonArray.getJSONObject(count);
                            userId = jo.getString("id");
                            name = jo.getString("EmployeeName");
                            mobilenumber = jo.getString("EmployeeMobileNumber");
                            emailid = jo.getString("EmployeeEmailId");
                            designation = jo.getString("EmployeeRole");
                            aadharnumber = jo.getString("EmployeeAdharNumber");
                            profile = jo.getString("EmployeeProfilePic");
                            generatedEmployeeId = jo.getString("GeneratedEmployeeId");
                            count++;
                        }
                        Log.d("Result", jsonString);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            DecimalFormat df = new DecimalFormat("000");
            String generated_user_id = df.format(Integer.parseInt(generatedEmployeeId));
            textid.setText(generated_user_id);
            textname.setText(name);
            textmobilenumber.setText(mobilenumber);
            textemailid.setText(emailid);
            textdesignation.setText(designation);
            textaadharnumber.setText(aadharnumber);
            /*if(!profile.equals("null")){
                Picasso.get().load(baseUrl+profile+".png").into(profile_photo);
            }
            else{
                Picasso.get().load(R.drawable.userphoto).into(profile_photo);
                //profile_photo.setImageResource(R.drawable.userphoto);
            }*/
            progressDialog.dismiss();
        }
    }
}
