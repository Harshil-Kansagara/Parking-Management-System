package com.example.duepark_admin.Menu.Employee;

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
import android.widget.ImageView;

import com.example.duepark_admin.Adapter.PartnerListAdapter;
import com.example.duepark_admin.Model.Parking;
import com.example.duepark_admin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ParkingListActiveEmployeeActivity extends AppCompatActivity {

    private String userid, username, designation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_list_active_employee);

        Bundle bundle  = getIntent().getExtras();
        if(bundle!=null){
            userid = bundle.getString("userid");
            username = bundle.getString("username");
            designation = bundle.getString("designation");
        }

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(userid, designation);
    }

    @Override
    public void onBackPressed() {
        Intent detailEmployeeActivity = new Intent(this, DetailEmployeeActivity.class);
        detailEmployeeActivity.putExtra("userid",userid);
        detailEmployeeActivity.putExtra("username",username);
        startActivity(detailEmployeeActivity);
        finish();
    }

    class BackgroundTask extends AsyncTask<String, Parking, Void> {

        private Context ctx;
        private RecyclerView recyclerView;
        private PartnerListAdapter partnerListAdapter;
        private ArrayList<Parking> parkingList;
        private ProgressDialog progressDialog;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            parkingList = new ArrayList<>();
            progressDialog = new ProgressDialog(this.ctx);
        }

        @Override
        protected void onPreExecute() {
            recyclerView = findViewById(R.id.recyclerView);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(ctx,
                    LinearLayoutManager.VERTICAL, false);
            recyclerView.addItemDecoration(new DividerItemDecoration(ctx,
                    DividerItemDecoration.VERTICAL));
            recyclerView.setLayoutManager(mLinearLayoutManager);
            partnerListAdapter = new PartnerListAdapter(parkingList, ctx, ParkingListActiveEmployeeActivity.this);
            recyclerView.setAdapter(partnerListAdapter);

            progressDialog.setMessage("Loading Data...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String userId = params[0];
            String recycler_url = "https://duepark.000webhostapp.com/get_activePartner.php?userId="+userId+"&designation="+params[1];
            try
            {
                URL url = new URL(recycler_url);
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
                    count++;
                    Parking parking = new Parking(jo.getString("id"), jo.getString("ParkingId"),jo.getString("Acronym"),
                            jo.getString("ParkingName"), jo.getString("ParkingActiveState"));
                    publishProgress(parking);
                }
                Log.d("JSON-STRING-ACTIVE",json_string);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Parking... values) {
            super.onProgressUpdate(values);
            parkingList.add(values[0]);
            partnerListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            progressDialog.dismiss();
        }
    }
}
