package com.example.duepark_consumer.MonthlyPass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Adapter.AssignVehicleNumberListForMonthlyPassAdapter;
import com.example.duepark_consumer.Location.AssignedEmployeeActivity;
import com.example.duepark_consumer.Model.AssignVehicleNumberListForMonthlyPass;
import com.example.duepark_consumer.Model.EmployeeList;
import com.example.duepark_consumer.R;

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

public class AssignMonthlyPassVehicleNumberListActivity extends AppCompatActivity {

    private String base_url;
    private static final String TAG = "AssignMonthlyPassAct" ;
    private TextView monthlyPassIdTV, passUserNameTV;
    private String monthlyPassId;
    private AssignVehicleNumberListForMonthlyPassAdapter assignVehicleNumberListForMonthlyPassAdapter;
    private List<AssignVehicleNumberListForMonthlyPass> assignVehicleNumberListForMonthlyPassList;
    private Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_monthly_pass_vehicle_number_list);

        base_url = getResources().getString(R.string.base_url);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            monthlyPassId = bundle.getString("monthlyPassId");
        }

        monthlyPassIdTV = findViewById(R.id.monthlyPassIdTV);
        passUserNameTV = findViewById(R.id.passUserNameTV);

        assignVehicleNumberListForMonthlyPassList = new ArrayList<>();
        assignVehicleNumberListForMonthlyPassAdapter = new AssignVehicleNumberListForMonthlyPassAdapter(assignVehicleNumberListForMonthlyPassList, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(assignVehicleNumberListForMonthlyPassAdapter);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        updateBtn = findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idData = "";
                StringBuilder data = new StringBuilder("");
                List<Integer> removeVehicleNumberIdList = assignVehicleNumberListForMonthlyPassAdapter.getRemoveVehicleNumberIdList();

                for(int i =0; i<removeVehicleNumberIdList.size();i++){
                    String id = removeVehicleNumberIdList.get(i).toString();
                    data.append(removeVehicleNumberIdList.get(i).toString()).append(",");
                }

                if(!data.toString().isEmpty()){
                    idData = data.toString().substring(0, data.toString().length() - 1);
                    BackgroundTask backgroundTask = new BackgroundTask(view.getContext());
                    backgroundTask.execute("update", idData);
                }
                else{
                    onBackPressed();
                }
            }
        });

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("get", monthlyPassId);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AddMonthlyPassActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    class BackgroundTask extends AsyncTask<String, AssignVehicleNumberListForMonthlyPass, String> {

        private Context ctx;
        private ProgressDialog progressDialog;
        private String passUserName, passUserMobileNumber, generatedMonthlyId, generatedLocationId;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            this.progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if(params[0].equals("get")){
                progressDialog.setMessage("Fetching all vehicle numbers..");
                String getVehicleNumberList_url = base_url+"get_assignVehicleNumberListPerMonthlyPass.php?MonthlyPassId=" + params[1];
                //String getVehicleNumberList_url = "https://duepark.000webhostapp.com/consumer/get_assignVehicleNumberListPerMonthlyPass.php?MonthlyPassId=" + params[1];
                try{
                    URL url = new URL(getVehicleNumberList_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }

                    httpURLConnection.disconnect();

                    String jsonString = stringBuilder.toString().trim();

                    JSONObject jsonObject = new JSONObject(jsonString);
                    passUserName = jsonObject.getString("PassUserName");
                    passUserMobileNumber = jsonObject.getString("PassUserMobileNumber");
                    generatedMonthlyId = jsonObject.getString("GeneratedMonthlyId");
                    generatedLocationId = jsonObject.getString("GeneratedLocationId");
                    JSONArray jsonArray = jsonObject.getJSONArray("vehicleNumberList");
                    int count = 0;
                    assignVehicleNumberListForMonthlyPassList.clear();
                    while (count < jsonArray.length()) {
                        JSONObject jo = jsonArray.getJSONObject(count);
                        count++;
                        AssignVehicleNumberListForMonthlyPass assignVehicleNumberListForMonthlyPass = new AssignVehicleNumberListForMonthlyPass(
                                jo.getString("VehicleNumberMonthlyPassId"), jo.getString("VehicleType"), jo.getString("VehicleNumber"));
                        publishProgress(assignVehicleNumberListForMonthlyPass);
                    }
                    Log.d(TAG, "doInBackground: " + jsonString);
                    return "get";
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                progressDialog.setMessage("Updating vehicle number list...");
                String deleteAssignEmployees_url = "https://duepark.000webhostapp.com/consumer/delete_assignVehicleNumberPerMonthlyPass.php";
                try{
                    URL url = new URL(deleteAssignEmployees_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("VehicleNumberIds","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8");
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
                    Log.d(TAG, "doInBackground: "+result.toString());
                    return result.toString();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(AssignVehicleNumberListForMonthlyPass... values) {
            assignVehicleNumberListForMonthlyPassList.add(values[0]);
            assignVehicleNumberListForMonthlyPassAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("get")){
                monthlyPassIdTV.setText("Monthly Pass Id : "+generatedLocationId+""+generatedMonthlyId);
                passUserNameTV.setText(passUserName);
            }
            else{
                if (result.equals("updated")) {
                    Toast.makeText(AssignMonthlyPassVehicleNumberListActivity.this, "Vehicle number updated for monthly pass id successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(AssignMonthlyPassVehicleNumberListActivity.this, result, Toast.LENGTH_SHORT).show();
                }
            }
            progressDialog.dismiss();
        }
    }
}