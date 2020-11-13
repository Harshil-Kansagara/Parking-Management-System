package com.example.duepark_consumer.Location.Edit;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.Location.DetailLocationActivity;
import com.example.duepark_consumer.R;
import com.google.android.gms.common.util.Strings;

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

public class EditVehicleChargesLocationActivity extends AppCompatActivity {

    private static final String TAG = "EditVehicleCharges";
    private String base_url;
    private SessionManagerHelper sessionManagerHelper;
    private HashMap<String, String> employeeDetails;
    private boolean isEdit = false, isAdd = false, isCar = false, isBike = false;
    private String currentFixHourValue, currentFixHourChargeValue, currentChargesOptionValue, currentChargesOptionRateValue, currentChargeType, locationId, locationActiveState, parkingAcronym, generatedParkingId;
    private Spinner chargeTypeSpinner, chargesOptionSpinner;
    private EditText fixHourET;
    private com.bachors.prefixinput.EditText fixHourChargesET, chargesOptionRateET;
    private TextView editTV, changeTV, currentTV, fixHourTV, fixHourChargesTV, chargesOptionTV, chargesTV;
    private ArrayAdapter<String> chargesDataAdapter, chargesOptionAdapter;
    private String chargeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vehicle_charges_location);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(this);
        employeeDetails = sessionManagerHelper.getEmployeeDetails();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            locationId = bundle.getString("LocationId");
            locationActiveState = bundle.getString("LocationActiveState");
            parkingAcronym = bundle.getString("ParkingAcronym");
            generatedParkingId = bundle.getString("GeneratedParkingId");
            isCar = bundle.getBoolean("IsCar");
            isBike = bundle.getBoolean("IsBike");
            isAdd = bundle.getBoolean("IsAdd");
            isEdit = bundle.getBoolean("IsEdit");
            if(isEdit){
                currentChargeType = bundle.getString("CurrentChargeType");
                if(currentChargeType.equals("Paid")) {
                    currentFixHourValue = bundle.getString("CurrentFixHourValue");
                    currentFixHourChargeValue = bundle.getString("CurrentFixHourChargeValue");
                    currentChargesOptionValue = bundle.getString("CurrentChargesOptionValue");
                    currentChargesOptionRateValue = bundle.getString("CurrentChargesOptionRateValue");
                }
            }
        }

        editTV = findViewById(R.id.editTV);
        changeTV = findViewById(R.id.changeTV);
        currentTV = findViewById(R.id.currentTV);
        chargeTypeSpinner = findViewById(R.id.chargeTypeSpinner);
        fixHourTV = findViewById(R.id.fixHourTV);
        fixHourChargesTV = findViewById(R.id.fixHourChargesTV);
        fixHourET = findViewById(R.id.fixHourET);
        fixHourChargesET = findViewById(R.id.fixHourChargesET);
        //fixHourChargesET.setPrefix("Rs. ");
        chargesOptionTV = findViewById(R.id.chargesOptionTV);
        chargesTV = findViewById(R.id.chargesTV);
        chargesOptionSpinner = findViewById(R.id.chargesOptionSpinner);
        chargesOptionRateET = findViewById(R.id.chargesOptionRateET);
        //chargesOptionRateET.setPrefix("Rs. ");

        initChargesTypeSpinner();
        initChargesOptionSpinner();

        if(isAdd){
            editTV.setText("Add location detail");
            if(isCar)
                changeTV.setText("Add car charges");
            else if(isBike)
                changeTV.setText("Add bike charges");
            fixHourChargesET.setPrefix("Rs. ");
            chargesOptionRateET.setPrefix("Rs. ");
        }
        else if(isEdit){
            editTV.setText("Edit location detail");
            String locationCharges = null;
            if(currentChargeType.equals("Free")){
                locationCharges = "Free";
                chargeTypeSpinner.setSelection(0);
                fixHourChargesET.setPrefix("Rs. ");
                chargesOptionRateET.setPrefix("Rs. ");
            }
            else{
                chargeTypeSpinner.setSelection(1);
                String chargesOption = null;
                if(currentChargesOptionValue.equals("PerHour")){
                    chargesOption = "Per Hour: Rs";
                    chargesOptionSpinner.setSelection(0);
                }
                else{
                    chargesOption = "Full Day: Rs";
                    chargesOptionSpinner.setSelection(1);
                }
                locationCharges = currentFixHourValue+" Hour: Rs"+currentFixHourChargeValue+" / "+chargesOption+""+currentChargesOptionRateValue;

                fixHourTV.setVisibility(View.VISIBLE);
                fixHourChargesTV.setVisibility(View.VISIBLE);
                fixHourET.setVisibility(View.VISIBLE);
                fixHourET.setText(currentFixHourValue);
                fixHourChargesET.setVisibility(View.VISIBLE);
                fixHourChargesET.setText("Rs. "+currentFixHourChargeValue);
                chargesOptionTV.setVisibility(View.VISIBLE);
                chargesTV.setVisibility(View.VISIBLE);
                chargesOptionSpinner.setVisibility(View.VISIBLE);
                chargesOptionRateET.setVisibility(View.VISIBLE);
                chargesOptionRateET.setText("Rs. "+currentChargesOptionRateValue);
            }

            if(isCar) {
                changeTV.setText("Change car charges");
                currentTV.setText("Current car location charges :\n"+locationCharges);
            }
            else if(isBike) {
                changeTV.setText("Change bike charges");
                currentTV.setText("Current bike location charges :\n"+locationCharges);
            }
        }

        chargeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> chargesAdapterView, View view, int position, long l) {
                chargeType = chargesAdapterView.getSelectedItem().toString();
                if(chargeType.equals("Paid")){
                    fixHourTV.setVisibility(View.VISIBLE);
                    fixHourChargesTV.setVisibility(View.VISIBLE);
                    fixHourET.setVisibility(View.VISIBLE);
                    fixHourChargesET.setVisibility(View.VISIBLE);
                    chargesOptionTV.setVisibility(View.VISIBLE);
                    chargesTV.setVisibility(View.VISIBLE);
                    chargesOptionSpinner.setVisibility(View.VISIBLE);
                    chargesOptionRateET.setVisibility(View.VISIBLE);
                }
                else{
                    fixHourTV.setVisibility(View.GONE);
                    fixHourET.setVisibility(View.GONE);
                    fixHourChargesTV.setVisibility(View.GONE);
                    fixHourChargesET.setVisibility(View.GONE);
                    chargesOptionTV.setVisibility(View.GONE);
                    chargesTV.setVisibility(View.GONE);
                    chargesOptionSpinner.setVisibility(View.GONE);
                    chargesOptionRateET.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLocationChargesDetail();
            }
        });
    }

    private void initChargesTypeSpinner(){
        List<String> chargesType = new ArrayList<>();
        chargesType.add("Free");
        chargesType.add("Paid");

        chargesDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, chargesType);
        chargesDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        chargeTypeSpinner.setAdapter(chargesDataAdapter);
    }

    private void initChargesOptionSpinner(){
        List<String> chargesOption = new ArrayList<>();
        chargesOption.add("Next Per Hour");
        chargesOption.add("Full Day");

        chargesOptionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, chargesOption);
        chargesOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        chargesOptionSpinner.setAdapter(chargesOptionAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent i  = new Intent(this, DetailLocationActivity.class);
        i.putExtra("locationId", locationId);
        i.putExtra("locationActiveState", locationActiveState);
        i.putExtra("generatedParkingId", generatedParkingId);
        i.putExtra("parkingAcronym", parkingAcronym);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void saveLocationChargesDetail(){

        String fixHour = "null";
        String fixHourCharges = "null";
        String chargesOption = "null";
        String chargesOptionRate = "null";
        String vehicleType = "null";
        if(isCar){
            vehicleType = "Car";
        }
        if(isBike){
            vehicleType = "Bike";
        }

        if(chargeType.equals("Paid")){
            fixHour = fixHourET.getText().toString().trim();
            fixHourCharges = fixHourChargesET.getText().toString().trim().replaceAll("Rs. ","");
            chargesOption = chargesOptionSpinner.getSelectedItem().toString();
            if(chargesOption.equals("Next Per Hour")){
                chargesOption = "PerHour";
            }
            else{
                chargesOption = "FullDay";
            }
            chargesOptionRate = chargesOptionRateET.getText().toString().trim().replaceAll("Rs. ","");

            if(fixHour.isEmpty()){
                fieldEmpty(fixHourET);
            }
            else if(fixHourCharges.isEmpty()){
                fieldEmpty(fixHourChargesET);
            }
            else if(chargesOptionRate.isEmpty()){
                fieldEmpty(chargesOptionRateET);
            }
        }

        // Add Background Task
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(vehicleType, fixHour, fixHourCharges, chargesOption, chargesOptionRate);
    }

    private void fieldEmpty(EditText input){
        Drawable myIcon = getResources().getDrawable(R.drawable.error);
        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
        input.setError("Field can not be Empty",myIcon);
        requestFocus(input);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{

        private Context ctx;
        private ProgressDialog progressDialog;

        BackgroundTask(Context ctx){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Adding location car charges...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String addLocationVehicleCharges_url = base_url+"add_locationVehicleCharges.php";
            try{
                URL url = new URL(addLocationVehicleCharges_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream ops = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                String data = URLEncoder.encode("LocationId","UTF-8")+"="+URLEncoder.encode(locationId,"UTF-8")+"&"
                        +URLEncoder.encode("ChargeType","UTF-8")+"="+URLEncoder.encode(chargeType,"UTF-8")+"&"
                        +URLEncoder.encode("VehicleType","UTF-8")+"="+URLEncoder.encode(params[0],"UTF-8")+"&"
                        +URLEncoder.encode("FixHour","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                        +URLEncoder.encode("FixHourCharges","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"
                        +URLEncoder.encode("ChargesOption","UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8")+"&"
                        +URLEncoder.encode("ChargesOptionRate","UTF-8")+"="+URLEncoder.encode(params[4],"UTF-8");
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
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("successful")){
                if(isAdd){
                    if(isCar){
                        Toast.makeText(ctx, "Car charges added successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else if(isBike){
                        Toast.makeText(ctx, "Bike charges added successfully...", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(isEdit){
                    if(isCar){
                        Toast.makeText(ctx, "Car charges updated successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else if(isBike){
                        Toast.makeText(ctx, "Bike charges updated successfully...", Toast.LENGTH_SHORT).show();
                    }
                }
                onBackPressed();
            }
            else{
                Log.d(TAG, "onPostExecute: "+result);
            }
            progressDialog.dismiss();
        }
    }
}