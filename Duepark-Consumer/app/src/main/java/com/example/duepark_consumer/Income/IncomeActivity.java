package com.example.duepark_consumer.Income;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.duepark_consumer.Adapter.LocationIncomeListAdapter;
import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.History.HistoryActivity;
import com.example.duepark_consumer.HomeActivity;
import com.example.duepark_consumer.Model.EmployeeIncomeList;
import com.example.duepark_consumer.Model.IncomeList;
import com.example.duepark_consumer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class IncomeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private String TAG = "IncomeActivity";
    private String base_url;
    private SessionManagerHelper sessionManagerHelper;
    private HashMap<String, String> employeeDetails;
    private TextView totalEarningTV, dateTV;
    private List<EmployeeIncomeList> employeeIncomeLists;
    private List<IncomeList> incomeLists;
    private int totalPayment = 0, totalCashPayment = 0, totalOnlinePayment = 0;
    private Dialog dialog;
    private RelativeLayout totalEarningRL;
    private LocationIncomeListAdapter locationIncomeListAdapter;
    private RelativeLayout dateRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        incomeLists = new ArrayList<>();
        employeeIncomeLists = new ArrayList<>();
        dialog = new Dialog(this);
        locationIncomeListAdapter = new LocationIncomeListAdapter(this, incomeLists, employeeIncomeLists);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(this);
        employeeDetails = sessionManagerHelper.getEmployeeDetails();

        dateRL = findViewById(R.id.dateRL);
        dateTV = findViewById(R.id.dateTV);
        getCurrentDate();
        dateRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(locationIncomeListAdapter);

        totalEarningTV = findViewById(R.id.totalEarningTV);
        totalEarningRL = findViewById(R.id.totalEarningRL);
        totalEarningRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.total_income_popup);
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                TextView totalCashEarningTV = dialog.findViewById(R.id.totalCashEarningTV);
                TextView totalOnlineEarningTV = dialog.findViewById(R.id.totalOnlineEarningTV);
                TextView totalEarningTV = dialog.findViewById(R.id.totalEarningTV);
                totalCashEarningTV.setText("Rs "+decimalFormat.format(totalCashPayment));
                totalOnlineEarningTV.setText("Rs "+decimalFormat.format(totalOnlinePayment));
                totalEarningTV.setText("Rs "+decimalFormat.format(totalPayment));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button downloadHistoryBtn = findViewById(R.id.downloadHistoryBtn);
        downloadHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HistoryActivity.class);
                i.putExtra("IsIncomeHistory", true);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute();
    }

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        Date chosenDate = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        dateTV.setText(format.format(chosenDate));
        totalCashPayment = 0;
        totalOnlinePayment = 0;
        totalPayment = 0;
        BackgroundTask backgroundTask = new BackgroundTask(view.getContext());
        backgroundTask.execute();
    }

    private void getCurrentDate(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        dateTV.setText(format.format(today));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    class BackgroundTask extends AsyncTask<String, IncomeList, String>{

        private Context ctx;
        private ProgressDialog progressDialog;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Fetching total income...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String income_url = base_url+"get_incomeList.php?ParkingId="+employeeDetails.get(SessionManagerHelper.ParkingId)+"&Date="+dateTV.getText().toString().trim();
            try{
                URL url = new URL(income_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine())!=null){
                    stringBuilder.append(line+"\n");
                }

                httpURLConnection.disconnect();

                String result = stringBuilder.toString().trim();

                incomeLists.clear();
                employeeIncomeLists.clear();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                int count = 0;
                while(count<jsonArray.length()){
                    JSONObject jo = jsonArray.getJSONObject(count);
                    if(Objects.equals(employeeDetails.get(SessionManagerHelper.EmployeeRole), "Admin") || Objects.equals(employeeDetails.get(SessionManagerHelper.EmployeeRole), "SuperAdmin")) {
                        IncomeList incomeList = new IncomeList(jo.getString("LocationId"), jo.getString("LocationName"), jo.getString("GeneratedLocationId"),
                                jo.getString("GeneratedParkingId"), jo.getString("ParkingAcronym"));
                        int innerCount = 0;
                        JSONArray innerJSONArray = jo.getJSONArray("0");
                        while (innerCount < innerJSONArray.length()) {
                            JSONObject innerJO = innerJSONArray.getJSONObject(innerCount);
                            EmployeeIncomeList employeeIncomeList = new EmployeeIncomeList(incomeList.getLocationId(), innerJO.getString("EmployeeId"), innerJO.getString("EmployeeName"), innerJO.getString("CashPayment"),
                                    innerJO.getString("OnlinePayment"));
                            employeeIncomeLists.add(employeeIncomeList);
                            innerCount++;
                        }
                        publishProgress(incomeList);
                        count++;
                    }
                    else{
                        if(Objects.equals(employeeDetails.get(SessionManagerHelper.LocationId), jo.getString("LocationId"))){
                            IncomeList incomeList = new IncomeList(jo.getString("LocationId"), jo.getString("LocationName"), jo.getString("GeneratedLocationId"),
                                    jo.getString("GeneratedParkingId"), jo.getString("ParkingAcronym"));
                            int innerCount = 0;
                            JSONArray innerJSONArray = jo.getJSONArray("0");
                            while (innerCount < innerJSONArray.length()) {
                                JSONObject innerJO = innerJSONArray.getJSONObject(innerCount);
                                EmployeeIncomeList employeeIncomeList = new EmployeeIncomeList(incomeList.getLocationId(), innerJO.getString("EmployeeId"), innerJO.getString("EmployeeName"), innerJO.getString("CashPayment"),
                                        innerJO.getString("OnlinePayment"));
                                employeeIncomeLists.add(employeeIncomeList);
                                innerCount++;
                            }
                            publishProgress(incomeList);
                        }
                        count++;
                    }
                }
                Log.d(TAG, "doInBackground: "+result);
                inputStream.close();
                bufferedReader.close();
                return "get";
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(IncomeList... values) {
            incomeLists.add(values[0]);
            locationIncomeListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("get")) {
                DecimalFormat formatter = new DecimalFormat("#,###");
                for (int i = 0; i < employeeIncomeLists.size(); i++) {
                    totalCashPayment = totalCashPayment + Integer.parseInt(employeeIncomeLists.get(i).getCashPayment());
                    totalOnlinePayment = totalOnlinePayment + Integer.parseInt(employeeIncomeLists.get(i).getOnlinePayment());
                }
                totalPayment = totalCashPayment + totalOnlinePayment;
                totalEarningTV.setText("Rs "+ formatter.format(totalPayment));
            }
            progressDialog.dismiss();
        }
    }
}