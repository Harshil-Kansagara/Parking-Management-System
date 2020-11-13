package com.example.duepark_consumer.Location;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Adapter.AssignEmployeeListAdapter;
import com.example.duepark_consumer.Adapter.EmployeeListAdapter;
import com.example.duepark_consumer.Helper.SessionManagerHelper;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AssignedEmployeeActivity extends AppCompatActivity {

    private String base_url;
    private static final String TAG = "AssignedEmployeeAct";
    private String locationId, locationActiveState, parkingAcronym, generatedParkingId, generatedLocationId;
    private TextView locationIdTV;
    private Spinner roleSpinner;
    private SessionManagerHelper sessionManagerHelper;
    private HashMap<String, String> employeeDetail;
    private  ArrayAdapter<String> dataAdapter;
    private ArrayList<EmployeeList> employeeLists;
    private AssignEmployeeListAdapter assignEmployeeListAdapter;
    private Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_employee);

        base_url = getResources().getString(R.string.base_url);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            locationId = bundle.getString("locationId");
            locationActiveState = bundle.getString("locationActiveState");
            parkingAcronym = bundle.getString("parkingAcronym");
            generatedParkingId = bundle.getString("generatedParkingId");
            generatedLocationId = bundle.getString("generatedLocationId");
        }

        sessionManagerHelper = new SessionManagerHelper(this);
        employeeDetail = sessionManagerHelper.getEmployeeDetails();

        DecimalFormat df = new DecimalFormat("000");
        String parkingid = parkingAcronym + df.format(Integer.parseInt(generatedParkingId));
        char char_locationId = (char) (Integer.parseInt(generatedLocationId) + 'A' - 1);
        String location_id = parkingid + char_locationId;
        locationIdTV = findViewById(R.id.locationIdTV);
        locationIdTV.setText("Location Id : "+location_id);

        roleSpinner = findViewById(R.id.roleSpinner);
        initRoleSpinner();
        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BackgroundTask backgroundTask = new BackgroundTask(view.getContext());
                backgroundTask.execute("get", adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        employeeLists = new ArrayList<>();
        assignEmployeeListAdapter = new AssignEmployeeListAdapter(this, employeeLists);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(assignEmployeeListAdapter);

        updateBtn = findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idData = "";
                StringBuilder data = new StringBuilder("");
                List<Integer> removeEmployeeIdList = assignEmployeeListAdapter.getRemoveEmployeeIdList();

                for(int i =0; i<removeEmployeeIdList.size();i++){
                    String id = removeEmployeeIdList.get(i).toString();
                    data.append(removeEmployeeIdList.get(i).toString()).append(",");
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

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(Objects.requireNonNull(employeeDetail.get(SessionManagerHelper.EmployeeRole)).equals("Manager")){
            roleSpinner.setVisibility(View.GONE);
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute("get", "Valet");
        }

    }

    private void initRoleSpinner(){
        String employeeRole = employeeDetail.get(SessionManagerHelper.EmployeeRole);
        List<String> type = new ArrayList<>();
        assert employeeRole != null;
        if(employeeRole.equals("Manager")){
            type.add("Valet");
        }
        else{
            type.add("Manager");
            type.add("Valet");
        }
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, type)/*{
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        }*/;
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        roleSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AssignedEmployeeActivity.this, DetailLocationActivity.class);
        intent.putExtra("locationId", locationId);
        intent.putExtra("locationActiveState", locationActiveState);
        intent.putExtra("parkingAcronym", parkingAcronym);
        intent.putExtra("generatedParkingId", generatedParkingId);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    class BackgroundTask extends AsyncTask<String, EmployeeList, String>{
        private ProgressDialog progressDialog;

        BackgroundTask(Context ctx){
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if(params[0].equals("get")) {
                progressDialog.setMessage("Getting employee list...");
                HashMap<String, String> employee = sessionManagerHelper.getEmployeeDetails();
                String getAssignEmployeeList_url = base_url+"get_assignEmployeeListPerLocation.php?LocationId=" + locationId + "&EmployeeRole=" + params[1];
                //String getAssignEmployeeList_url = "https://duepark.000webhostapp.com/consumer/get_assignEmployeeListPerLocation.php?LocationId=" + locationId + "&EmployeeRole=" + params[1];
                try {
                    URL url = new URL(getAssignEmployeeList_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }

                    httpURLConnection.disconnect();

                    String result = stringBuilder.toString().trim();

                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    employeeLists.clear();
                    while (count < jsonArray.length()) {
                        JSONObject jo = jsonArray.getJSONObject(count);
                        count++;
                        EmployeeList employeeList = new EmployeeList(jo.getString("id"), jo.getString("GeneratedEmployeeId"), jo.getString("EmployeeName"), jo.getString("Role"), jo.getString("EmployeeActiveState"));
                        publishProgress(employeeList);
                    }
                    Log.d(TAG, "doInBackground: " + result);
                    return "get";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(params[0].equals("update")){
                progressDialog.setMessage("Updating employee list...");
                HashMap<String, String> employee = sessionManagerHelper.getEmployeeDetails();
                String deleteAssignEmployees_url = base_url+"delete_assignEmployeesPerLocation.php";
                //String deleteAssignEmployees_url = "https://duepark.000webhostapp.com/consumer/delete_assignEmployeesPerLocation.php";
                try{
                    URL url = new URL(deleteAssignEmployees_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("LocationId","UTF-8")+"="+URLEncoder.encode(locationId,"UTF-8")+"&"
                            +URLEncoder.encode("EmployeeIds","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8");
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
        protected void onProgressUpdate(EmployeeList... values) {
            employeeLists.add(values[0]);
            assignEmployeeListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String result) {
            if(!result.equals("get")) {
                if (result.equals("updated")) {
                    Toast.makeText(AssignedEmployeeActivity.this, "Assign Employee List updated successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(AssignedEmployeeActivity.this, result, Toast.LENGTH_SHORT).show();
                }
            }
            progressDialog.dismiss();
        }

    }
}