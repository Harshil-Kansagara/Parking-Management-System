package com.example.duepark_consumer.History;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.Income.IncomeActivity;
import com.example.duepark_consumer.ParkedVehicle.ParkedVehicleActivity;
import com.example.duepark_consumer.R;
import com.example.duepark_consumer.Valet.Menu.ValetIncomeActivity;
import com.example.duepark_consumer.Valet.ValetHomeActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private String base_url;
    private RelativeLayout fromDateRL, endDateRL;
    private TextView fromDateTV, endDateTV, historyTV;
    private boolean isFromDateSet;
    private SessionManagerHelper sessionManagerHelper;
    private HashMap<String, String> employeeDetail;
    private boolean isParkedVehicleHistory = false, isIncomeHistory = false, isValetIncomeHistory = false, isValetParkedVehicleHistory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(this);
        employeeDetail = sessionManagerHelper.getEmployeeDetails();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            isParkedVehicleHistory = bundle.getBoolean("IsParkedVehicleHistory");
            isIncomeHistory = bundle.getBoolean("IsIncomeHistory");
            isValetIncomeHistory = bundle.getBoolean("IsValetIncomeHistory");
            isValetParkedVehicleHistory = bundle.getBoolean("IsValetParkedVehicleHistory");
        }

        historyTV = findViewById(R.id.historyTV);
        fromDateRL = findViewById(R.id.fromDateRL);
        endDateRL = findViewById(R.id.endDateRL);
        fromDateTV = findViewById(R.id.fromDateTV);
        endDateTV = findViewById(R.id.endDateTV);
        fromDateRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromDateSet = true;
                showDatePickerDialog();
            }
        });

        endDateRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromDateSet = false;
                showDatePickerDialog();
            }
        });

        if(isParkedVehicleHistory){
            historyTV.setText("Parked Vehicle History");
        }

        if(isIncomeHistory){
            historyTV.setText("Income History");
        }

        Button submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fromDate = fromDateTV.getText().toString().trim();
                String endDate = endDateTV.getText().toString().trim();
                if(fromDate.isEmpty()){
                    Toast.makeText(HistoryActivity.this, "From date is not set...", Toast.LENGTH_SHORT).show();
                }
                else if(endDate.isEmpty()){
                    Toast.makeText(HistoryActivity.this, "End date is not set...", Toast.LENGTH_SHORT).show();
                }
                else{
                    BackgroundTask backgroundTask = new BackgroundTask(view.getContext());
                    backgroundTask.execute(fromDate, endDate, employeeDetail.get(SessionManagerHelper.ParkingId), employeeDetail.get(SessionManagerHelper.EmployeeId));
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
    }

    @Override
    public void onBackPressed() {
        Intent i= null;
        if(isIncomeHistory){
            i = new Intent(this, IncomeActivity.class);
        }
        if(isParkedVehicleHistory){
            i = new Intent(this, ParkedVehicleActivity.class);
        }
        if(isValetIncomeHistory){
            i = new Intent(this, ValetIncomeActivity.class);
        }
        if(isValetParkedVehicleHistory){
            i = new Intent(this, ValetHomeActivity.class);
            i.putExtra("IsValetReleasedVehicle", true);
        }
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
        if(isFromDateSet){
            fromDateTV.setText(format.format(chosenDate));
        }
        else{
            endDateTV.setText(format.format(chosenDate));
        }
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{
        private Context ctx;
        private ProgressDialog progressDialog;
        private String generatingHistory_url = null;
        BackgroundTask(Context ctx){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(this.ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Generating parked vehicle history pdf...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String... params) {
            if(isParkedVehicleHistory){
                generatingHistory_url = base_url+"generate_parkedVehicleHistory.php?FromDate="+params[0]+"&EndDate="+params[1]+"&ParkingId="+params[2]+"&HistoryIssuerEmployeeId="+params[3];
            }
            if(isIncomeHistory){
                generatingHistory_url = base_url+"generate_incomeHistory.php?FromDate="+params[0]+"&EndDate="+params[1]+"&ParkingId="+params[2]+"&HistoryIssuerEmployeeId="+params[3];
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(generatingHistory_url));
            startActivity(browserIntent);
            progressDialog.dismiss();
        }
    }
}