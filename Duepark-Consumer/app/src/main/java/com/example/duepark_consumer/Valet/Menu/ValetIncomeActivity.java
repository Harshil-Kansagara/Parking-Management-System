package com.example.duepark_consumer.Valet.Menu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.History.HistoryActivity;
import com.example.duepark_consumer.Model.EmployeeIncomeList;
import com.example.duepark_consumer.Model.IncomeList;
import com.example.duepark_consumer.R;
import com.example.duepark_consumer.Valet.ValetHomeActivity;

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

public class ValetIncomeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "ValetIncomeActivity";
    private String base_url;
    private SessionManagerHelper sessionManagerHelper;
    private HashMap<String, String> employeeDetail;
    private RelativeLayout dateRL;
    private TextView totalEarningTV, totalCashEarningTV, totalOnlineEarningTV, dateTV;
    private List<EmployeeIncomeList> employeeIncomeLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valet_income);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(this);
        employeeDetail = sessionManagerHelper.getEmployeeDetails();
        employeeIncomeLists = new ArrayList<>();

        dateRL = findViewById(R.id.dateRL);
        dateTV = findViewById(R.id.dateTV);

        dateRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        totalEarningTV = findViewById(R.id.totalEarningTV);
        totalCashEarningTV = findViewById(R.id.totalCashEarningTV);
        totalOnlineEarningTV = findViewById(R.id.totalOnlineEarningTV);

        Button downloadHistoryBtn = findViewById(R.id.downloadHistoryBtn);
        downloadHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HistoryActivity.class);
                i.putExtra("IsValetIncomeHistory", true);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getCurrentDate();

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("get", employeeDetail.get(SessionManagerHelper.LocationId), dateTV.getText().toString().trim());
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, ValetHomeActivity.class);
        i.putExtra("IsValetMenu", true);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
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
        BackgroundTask backgroundTask = new BackgroundTask(view.getContext());
        backgroundTask.execute("get", employeeDetail.get(SessionManagerHelper.LocationId), dateTV.getText().toString().trim());
    }

    private void getCurrentDate(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        dateTV.setText(format.format(today));
    }

    class BackgroundTask extends AsyncTask<String, IncomeList, String>{

        private Context ctx;
        private ProgressDialog progressDialog;
        private boolean isJSONEmpty = false;

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
        protected String doInBackground(String... params) {
            if(params[0].equals("get")){
                String income_url = base_url+"get_valetIncome.php?LocationId="+params[1]+"&Date="+params[2];
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

                    employeeIncomeLists.clear();
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    if(jsonArray.length() == 0){
                        isJSONEmpty = true;
                        Log.d(TAG, "doInBackground: "+result);
                        inputStream.close();
                        bufferedReader.close();
                        return "get";
                    }
                    int count = 0;
                    while(count < jsonArray.length()){
                        JSONObject jo = jsonArray.getJSONObject(count);
                        EmployeeIncomeList employeeIncomeList = new EmployeeIncomeList(jo.getString("EmployeeId"), jo.getString("CashPayment"),
                                jo.getString("OnlinePayment"));
                        employeeIncomeLists.add(employeeIncomeList);
                        count ++;
                    }
                    Log.d(TAG, "doInBackground: "+result);
                    inputStream.close();
                    bufferedReader.close();
                    return "get";
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("get")) {
                if(isJSONEmpty){
                    totalEarningTV.setText("Rs 00");
                    totalCashEarningTV.setText("Rs 00");
                    totalOnlineEarningTV.setText("Rs 00");
                }
                else{
                    int totalCashPayment = 0, totalOnlinePayment = 0, totalPayment = 0;
                    DecimalFormat formatter = new DecimalFormat("#,###");
                    for (int i = 0; i < employeeIncomeLists.size(); i++) {
                        if(employeeDetail.get(SessionManagerHelper.EmployeeId).equals(employeeIncomeLists.get(i).getEmployeeId())){
                            totalCashPayment = totalCashPayment + Integer.parseInt(employeeIncomeLists.get(i).getCashPayment());
                            totalOnlinePayment = totalOnlinePayment + Integer.parseInt(employeeIncomeLists.get(i).getOnlinePayment());
                        }
                    }
                    totalPayment = totalCashPayment + totalOnlinePayment;
                    totalEarningTV.setText("Rs "+ formatter.format(totalPayment));
                    totalCashEarningTV.setText("Rs "+formatter.format(totalCashPayment));
                    totalOnlineEarningTV.setText("Rs "+formatter.format(totalOnlinePayment));
                }
            }
            progressDialog.dismiss();
        }
    }
}