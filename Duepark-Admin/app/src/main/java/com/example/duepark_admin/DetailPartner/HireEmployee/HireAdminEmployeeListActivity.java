package com.example.duepark_admin.DetailPartner.HireEmployee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.duepark_admin.Adapter.HireEmployeeListAdapter;
import com.example.duepark_admin.DetailPartner.DetailPartnerActivity;
import com.example.duepark_admin.Model.HireEmployee;
import com.example.duepark_admin.R;
import com.example.duepark_admin.Service.SessionManager;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HireAdminEmployeeListActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private String parkingid;
    private HireEmployeeListAdapter hireEmployeeListAdapter;
    private ArrayList<HireEmployee> hireEmployeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire_admin_employee_list);

        sessionManager = new SessionManager(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            parkingid = bundle.getString("parkingid");
        }
        hireEmployeeList = new ArrayList<>();
        hireEmployeeListAdapter = new HireEmployeeListAdapter(this, hireEmployeeList);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initHireAdminEmployeeList();

        Button hireAdminEmployeeBtn = findViewById(R.id.hireAdminEmployeeBtn);
        hireAdminEmployeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idData = null;
                StringBuilder data = new StringBuilder("");
                List<HireEmployee> hireEmployeeL = hireEmployeeListAdapter.getHireEmployeeList();

                for (int i=0;i<hireEmployeeL.size();i++){
                    HireEmployee hireEmployee = hireEmployeeL.get(i);
                    if (hireEmployee.isChecked()){
                        data.append(hireEmployee.getEmployeeId()).append(",");
                    }
                }
                if(!data.toString().equals("")) {
                    idData = data.toString().substring(0, data.toString().length() - 1);
                }
                BackgroundTask backgroundTask = new BackgroundTask(HireAdminEmployeeListActivity.this);
                backgroundTask.execute("save", parkingid, idData);
            }
        });

    }

    private void initHireAdminEmployeeList(){
        HashMap<String, String> user = sessionManager.getUserDetails();
        BackgroundTask backgroundTask = new BackgroundTask(this);
        if(Objects.equals(user.get(sessionManager.EmployeeRole), "SuperAdmin")) {
            backgroundTask.execute("get", user.get(sessionManager.EmployeeRole), parkingid);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DetailPartnerActivity.class));
        finish();
    }

    class BackgroundTask extends AsyncTask<String, HireEmployee, String> {

        private Context ctx;
        private ProgressDialog progressDialog;

        BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(ctx,
                    LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(mLinearLayoutManager);
            recyclerView.setAdapter(hireEmployeeListAdapter);

            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String keyword = params[0];
            if (keyword.equals("get"))
            {
                if(params[1].equals("System Admin"))
                {
                    progressDialog.setMessage("Loading data....");
                    String get_url = "https://duepark.000webhostapp.com/get_parkingAdminHireData.php?designation="+params[1]+"&parkingid="+params[2];
                    try{
                        URL url = new URL(get_url);
                        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                        InputStream inputStream = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                        StringBuilder stringBuilder = new StringBuilder();
                        String line;

                        while((line = bufferedReader.readLine())!=null){
                            stringBuilder.append(line+"\n");
                        }

                        httpURLConnection.disconnect();

                        String json_string = stringBuilder.toString().trim();

                        JSONObject jsonObject = new JSONObject(json_string);
                        JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                        int count = 0;
                        while(count<jsonArray.length()){
                            JSONObject jo =jsonArray.getJSONObject(count);
                            HireEmployee hireEmployee = new HireEmployee(jo.getString("id"),jo.getString("name"), jo.getBoolean("isCheck"));
                            publishProgress(hireEmployee);
                            count++;
                        }
                        Log.d("JSON-STRING",json_string);
                        return "get";
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
            else if(keyword.equals("save")){
                progressDialog.setMessage("Saving data....");
                String save_url = "https://duepark.000webhostapp.com/add_hireAdminPartnerData.php?parkingid="+params[1];
                try{
                    URL url = new URL(save_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("adminIds","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8");
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
                    return result.toString();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(HireEmployee... values) {
            hireEmployeeList.add(values[0]);
            hireEmployeeListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("0")){
                Toast.makeText(ctx, "Data not saved successfully", Toast.LENGTH_SHORT).show();
            }
            else if (s.equals("1")){
                Toast.makeText(ctx, "Data updated successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ctx, DetailPartnerActivity.class));
                finish();
            }
            progressDialog.dismiss();
        }
    }
}
