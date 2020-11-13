package com.example.duepark_consumer.ParkedVehicle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.HomeActivity;
import com.example.duepark_consumer.ParkedVehicle.Add.AddVehicleUserActivity;
import com.example.duepark_consumer.R;
import com.example.duepark_consumer.Valet.ValetHomeActivity;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DetailParkedVehicleActivity extends AppCompatActivity implements View.OnClickListener {

    private String base_url;
    private static final String TAG = "DetailParkedVehicle";
    private String parkedVehicleId=null, vehicleType=null, vehicleNumber=null, paymentType=null;
    private TextView vehicleDataTV, inputPaidAmount, inputPendingAmount;
    private EditText inputPassUserName, inputPassUserMobileNumber, inputReleasedTime;
    private final int Cash_Btn_Id = 1;
    private final int Online_Btn_Id = 2;
    private int selectedPaymentBtnId = 0;
    private SessionManagerHelper sessionManagerHelper;
    private HashMap<String, String> employeeDetails;
    private Button cashBtn, onlineBtn, releasedVehicleBtn;
    private boolean isButtonSelectable = true, isValet;
    private LinearLayout paymentSelectionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_parked_vehicle);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(this);
        employeeDetails = sessionManagerHelper.getEmployeeDetails();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            parkedVehicleId = bundle.getString("parkedVehicleId");
            vehicleType = bundle.getString("vehicleType");
            vehicleNumber = bundle.getString("vehicleNumber");
            isValet = bundle.getBoolean("isValet");
            assert vehicleNumber != null;
            vehicleNumber = vehicleNumber.toUpperCase().replace("-", "");
        }

        vehicleDataTV = findViewById(R.id.vehicleDataTV);
        vehicleDataTV.setText(vehicleType+" - "+vehicleNumber);

        inputPassUserName = findViewById(R.id.inputPassUserName);
        inputPassUserMobileNumber = findViewById(R.id.inputPassUserMobileNumber);
        inputReleasedTime = findViewById(R.id.inputReleasedTime);
        inputReleasedTime.setText(getReleasedTime1());
        inputPaidAmount = findViewById(R.id.inputPaidAmount);
        inputPendingAmount = findViewById(R.id.inputPendingAmount);
        cashBtn = findViewById(R.id.cashBtn);
        cashBtn.setOnClickListener(this);
        onlineBtn = findViewById(R.id.onlineBtn);
        onlineBtn.setOnClickListener(this);
        /*paymentSelectionLayout = findViewById(R.id.paymentSelectionLayout);*/

        releasedVehicleBtn = findViewById(R.id.releasedVehicleBtn);
        releasedVehicleBtn.setOnClickListener(this);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("get", parkedVehicleId);
    }

    @Override
    public void onClick(View view) {
        if(view == cashBtn){
            cashBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_click));
            onlineBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_shape_light));
            selectedPaymentBtnId = 1;
            paymentType = "Cash";
        }
        else if(view == onlineBtn){
            cashBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_shape_light));
            onlineBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_click));
            selectedPaymentBtnId = 2;
            paymentType = "Online";
        }
        else{
            releasedVehicle();
        }
    }

    private void releasedVehicle(){
        String pending_amount = inputPendingAmount.getText().toString().trim();
        String releasedAmount = pending_amount.substring(20);
        BackgroundTask backgroundTask = new BackgroundTask(this);
        if(isButtonSelectable) {
            if (selectedPaymentBtnId == 0) {
                Toast.makeText(DetailParkedVehicleActivity.this, "Please select mode of payment..", Toast.LENGTH_SHORT).show();
            }
            else{
                if(selectedPaymentBtnId == 1){
                    backgroundTask.execute("released", parkedVehicleId, releasedAmount, getReleasedTime1(), employeeDetails.get(SessionManagerHelper.EmployeeName), "Cash",
                            employeeDetails.get(SessionManagerHelper.EmployeeId), getReleasedDate());
                }
                else{
                    backgroundTask.execute("released", parkedVehicleId, releasedAmount, getReleasedTime1(), employeeDetails.get(SessionManagerHelper.EmployeeName), "Online",
                            employeeDetails.get(SessionManagerHelper.EmployeeId), getReleasedDate());
                }
            }
        }
        else{
            backgroundTask.execute("released", parkedVehicleId, "00", getReleasedTime1(), employeeDetails.get(SessionManagerHelper.EmployeeName), "null", employeeDetails.get(SessionManagerHelper.EmployeeId), getReleasedDate());
        }
    }

    private String getReleasedDate(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(today);
    }

    private String getReleasedTime1(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        return format.format(today);
    }

    private String getReleasedTime(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(today);
    }

    @Override
    public void onBackPressed() {
        if(isValet){
            Intent i = new Intent(this, ValetHomeActivity.class);
            i.putExtra("IsValetReleasedVehicle", true);
            i.putExtra("IsValetMonthlyPass", false);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
        else {
            startActivity(new Intent(this, ParkedVehicleActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{

        private Context context;
        private ProgressDialog progressDialog;
        private String isMonthlyPass, monthlyPassId, id, fullName, mobileNumber, parkedTime, parkedDate, generatedLocationId, generatedMonthlyPassId, issuedDate, expiryDate, parkedPaymentType;
        private String locationId, paidAmount, paymentTimeRate, isPayLater, isParkingFree, carFixHour, carFixHourRate, carChargesOption, carChargesOptionRate, bikeFixHour, bikeFixHourRate, bikeChargesOption, bikeChargesOptionRate;

        public BackgroundTask(Context ctx){
            this.context = ctx;
            this.progressDialog = new ProgressDialog(this.context);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if(params[0].equals("get")){
                progressDialog.setMessage("Fetching parked vehicle data...");
                String getParkedVehicle_url = base_url+"get_parkedVehicleDetail.php?ParkedVehicleId="+params[1];
                try{
                    URL url = new URL(getParkedVehicle_url);
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
                    if(result!= null){
                        JSONObject jo = new JSONObject(result);
                        isMonthlyPass = jo.getString("IsMonthlyPass");
                        id = jo.getString("Id");
                        fullName = jo.getString("FullName");
                        mobileNumber = jo.getString("MobileNumber");
                        parkedDate = jo.getString("ParkedDate");
                        parkedTime = jo.getString("ParkedTime");
                        parkedPaymentType = jo.getString("ParkedPaymentType");
                        if(isMonthlyPass.equals("true")){
                            monthlyPassId = jo.getString("MonthlyPassId");
                            generatedLocationId = jo.getString("GeneratedLocationId");
                            generatedMonthlyPassId = jo.getString("GeneratedMonthlyPassId");
                            issuedDate = jo.getString("IssuedDate");
                            expiryDate = jo.getString("ExpiryDate");
                        }
                        else{
                            //locationCloseTime = jo.getString("LocationCloseTime");
                            paidAmount = jo.getString("PaidAmount");
                            paymentTimeRate = jo.getString("PaymentTimeRate");
                            isPayLater = jo.getString("IsPayLater");
                            isParkingFree = jo.getString("IsParkingFree");
                            if(isParkingFree.equals("0")){
                                if(vehicleType.equals("Car")){
                                    carFixHour = jo.getString("CarFixHour");
                                    carFixHourRate = jo.getString("CarFixHourRate");
                                    carChargesOption = jo.getString("CarChargesOption");
                                    carChargesOptionRate = jo.getString("CarChargesOptionRate");
                                }
                                else{
                                    bikeFixHour = jo.getString("BikeFixHour");
                                    bikeFixHourRate = jo.getString("BikeFixHourRate");
                                    bikeChargesOption = jo.getString("BikeChargesOption");
                                    bikeChargesOptionRate = jo.getString("BikeChargesOptionRate");
                                }
                            }
                        }
                    }
                    inputStream.close();
                    bufferedReader.close();
                    Log.d(TAG, "doInBackground: "+result);
                    return "get";
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                progressDialog.setMessage("Releasing parked vehicle...");
                String releasedVehicle_url = base_url+"released_parkedVehicle.php";
                try{
                    URL url = new URL(releasedVehicle_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("ParkedVehicleId","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"+
                            URLEncoder.encode("ReleasedAmount", "UTF-8")+"="+URLEncoder.encode(params[2], "UTF-8")+"&"+
                            URLEncoder.encode("ReleasedTime", "UTF-8")+"="+URLEncoder.encode(params[3], "UTF-8")+"&"+
                            URLEncoder.encode("ReleasedBy", "UTF-8")+"="+URLEncoder.encode(params[4], "UTF-8")+"&"+
                            URLEncoder.encode("ReleasedPaymentType", "UTF-8")+"="+URLEncoder.encode(params[5], "UTF-8")+"&"+
                            URLEncoder.encode("IsParkedVehicleReleased", "UTF-8")+"="+URLEncoder.encode("true", "UTF-8")+"&"+
                            URLEncoder.encode("ReleasedEmployeeId", "UTF-8")+"="+URLEncoder.encode(params[6], "UTF-8")+"&"+
                            URLEncoder.encode("ReleasedDate", "UTF-8")+"="+URLEncoder.encode(params[7], "UTF-8");
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
                    Log.d(TAG, "doInBackground: "+result.toString().trim());
                    return result.toString().trim();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("get")){
                inputPassUserName.setText(fullName);
                inputPassUserMobileNumber.setText(mobileNumber);
                if(!parkedPaymentType.equals("null")){
                    if(parkedPaymentType.equals("Cash")){
                        cashBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_click));
                        onlineBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light));
                        paymentType = "Cash";
                        selectedPaymentBtnId = 1;
                    }
                    else if(parkedPaymentType.equals("Online")){
                        onlineBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_click));
                        cashBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light));
                        paymentType = "Online";
                        selectedPaymentBtnId = 2;
                    }
                }
                if(isMonthlyPass.equals("true")){
                    inputPaidAmount.setText("Monthly Pass : "+generatedLocationId+""+generatedMonthlyPassId);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat month_date = new SimpleDateFormat("dd MMM", Locale.US);
                    try {
                        Date issued_date = sdf.parse(Objects.requireNonNull(issuedDate));
                        Date expiry_date = sdf.parse(Objects.requireNonNull(expiryDate));
                        assert issued_date != null;
                        String issued_Date = month_date.format(issued_date);
                        assert expiry_date != null;
                        String expiry_Date = month_date.format(expiry_date);
                        inputPendingAmount.setText("Pass Date : "+issued_Date+" - "+expiry_Date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    cashBtn.setEnabled(false);
                    onlineBtn.setEnabled(false);
                    isButtonSelectable = false;
                    onlineBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                    cashBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                }
                else{
                    if(isParkingFree.equals("1")){
                        inputPaidAmount.setText("Paid Amount - Rs 00");
                        inputPendingAmount.setText("Pending Amount - Rs 00");
                        cashBtn.setEnabled(false);
                        onlineBtn.setEnabled(false);
                        isButtonSelectable = false;
                        onlineBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                        cashBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                    }
                    else{
                        // Payment Time Rate = Full Day
                        if(paymentTimeRate.contains("Full Day")) {
                            inputPaidAmount.setText("Paid Amount - Rs " + paidAmount);
                            inputPendingAmount.setText("Pending Amount - Rs 00");
                            cashBtn.setEnabled(false);
                            onlineBtn.setEnabled(false);
                            isButtonSelectable = false;
                            onlineBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                            cashBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                        }
                        // Payment Time Rate = Half Day or Per Hour
                        else {
                            long difference = 0;
                            try {
                                String currentTime = getReleasedTime1();
                                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                Date date1 = format.parse(parkedTime.substring(0, parkedTime.length()-3)+":00");
                                Date date2 = format.parse(currentTime.substring(0, currentTime.length()-3)+":00");
                                difference = date2.getTime() - date1.getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            // If Pay Later is not selected
                            if (isPayLater.equals("0")) {
                                if (vehicleType.equals("Car")) {
                                    // If difference of time is more than car hour rate than need to pay full day charge
                                    if(paymentTimeRate.contains("Per hour rate")){
                                        long hour = TimeUnit.MILLISECONDS.toHours(difference);
                                        inputPaidAmount.setText("Paid Amount - Rs "+paidAmount);
                                        if(hour>1){
                                            inputPendingAmount.setText("Pending Amount - Rs "+ ((hour * Long.parseLong(carChargesOptionRate))-Long.parseLong(paidAmount)));
                                        }
                                        else{
                                            inputPendingAmount.setText("Pending Amount - Rs 00");
                                            cashBtn.setEnabled(false);
                                            onlineBtn.setEnabled(false);
                                            onlineBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                            cashBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                            isButtonSelectable = false;
                                        }
                                    }
                                    else if(paymentTimeRate.contains("hours per rate")){
                                        long carMillisecond = Integer.parseInt(carFixHour) * 60 * 60 * 1000;
                                        inputPaidAmount.setText("Paid Amount - Rs "+paidAmount);
                                        if(carMillisecond < difference){
                                            if(carChargesOption.equals("PerHour")){
                                                long hour = TimeUnit.MILLISECONDS.toHours(difference);
                                                long extraHourParked = hour - Long.parseLong(carFixHour);
                                                inputPendingAmount.setText("Pending Amount - Rs "+ (extraHourParked * Long.parseLong(carChargesOptionRate)));
                                            }
                                            else{
                                                inputPendingAmount.setText("Pending Amount - Rs "+(Integer.parseInt(carChargesOptionRate) - Integer.parseInt(paidAmount)));
                                            }
                                        }
                                        else{
                                            inputPaidAmount.setText("Paid Amount - Rs "+paidAmount);
                                            inputPendingAmount.setText("Pending Amount - Rs 00");
                                            cashBtn.setEnabled(false);
                                            onlineBtn.setEnabled(false);
                                            onlineBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                            cashBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                            isButtonSelectable = false;
                                        }
                                    }
                                    else if(paymentTimeRate.contains("Full day rate")){
                                        inputPaidAmount.setText("Paid Amount - Rs "+paidAmount);
                                        inputPendingAmount.setText("Pending Amount - Rs 00");
                                        cashBtn.setEnabled(false);
                                        onlineBtn.setEnabled(false);
                                        onlineBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                        cashBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                        isButtonSelectable = false;
                                    }
                                    /*long carMillisecond = Integer.parseInt(carHour) * 60 *60 * 1000;
                                    if (carMillisecond < difference) {
                                        inputPaidAmount.setText("Paid Amount - Rs "+paidAmount);
                                        inputPendingAmount.setText("Pending Amount - Rs "+(Integer.parseInt(carFullDayRate) - Integer.parseInt(paidAmount)));
                                    }
                                    else{
                                        inputPaidAmount.setText("Paid Amount - Rs "+paidAmount);
                                        inputPendingAmount.setText("Pending Amount - Rs 00");
                                        cashBtn.setEnabled(false);
                                        onlineBtn.setEnabled(false);
                                        isButtonSelectable = false;
                                        onlineBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                        cashBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                    }*/
                                } else {
                                    if(paymentTimeRate.contains("Per hour rate")){
                                        long hour = TimeUnit.MILLISECONDS.toHours(difference);
                                        inputPaidAmount.setText("Paid Amount - Rs "+paidAmount);
                                        if(hour>1){
                                            inputPendingAmount.setText("Pending Amount - Rs "+ ((hour * Long.parseLong(bikeChargesOptionRate))-Long.parseLong(paidAmount)));
                                        }
                                        else{
                                            inputPendingAmount.setText("Pending Amount - Rs 00");
                                            cashBtn.setEnabled(false);
                                            onlineBtn.setEnabled(false);
                                            onlineBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                            cashBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                            isButtonSelectable = false;
                                        }
                                    }
                                    else if(paymentTimeRate.contains("hours per rate")){
                                        long bikeMillisecond = Integer.parseInt(bikeFixHour) * 60 * 60 * 1000;
                                        inputPaidAmount.setText("Paid Amount - Rs "+paidAmount);
                                        if(bikeMillisecond < difference){
                                            if(bikeChargesOption.equals("PerHour")){
                                                long hour = TimeUnit.MILLISECONDS.toHours(difference);
                                                long extraHourParked = hour - Long.parseLong(bikeFixHour);
                                                inputPendingAmount.setText("Pending Amount - Rs "+ (extraHourParked * Long.parseLong(bikeChargesOptionRate)));
                                            }
                                            else{
                                                inputPendingAmount.setText("Pending Amount - Rs "+(Integer.parseInt(bikeChargesOptionRate) - Integer.parseInt(paidAmount)));
                                            }
                                        }
                                        else{
                                            inputPaidAmount.setText("Paid Amount - Rs "+paidAmount);
                                            inputPendingAmount.setText("Pending Amount - Rs 00");
                                            cashBtn.setEnabled(false);
                                            onlineBtn.setEnabled(false);
                                            onlineBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                            cashBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                            isButtonSelectable = false;
                                        }
                                    }
                                    else if(paymentTimeRate.contains("Full day rate")){
                                        inputPaidAmount.setText("Paid Amount - Rs "+paidAmount);
                                        inputPendingAmount.setText("Pending Amount - Rs 00");
                                        cashBtn.setEnabled(false);
                                        onlineBtn.setEnabled(false);
                                        onlineBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                        cashBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                        isButtonSelectable = false;
                                    }
                                    // If difference of time is more than bike hour rate than need to pay full day charge
                                    /*if (bikeMillisecond < difference) {
                                        inputPaidAmount.setText("Paid Amount - Rs "+paidAmount);
                                        inputPendingAmount.setText("Pending Amount - Rs "+(Integer.parseInt(bikeFullDayRate) - Integer.parseInt(paidAmount)));
                                    }
                                    else{
                                        inputPaidAmount.setText("Paid Amount - Rs "+paidAmount);
                                        inputPendingAmount.setText("Pending Amount - Rs 00");
                                        cashBtn.setEnabled(false);
                                        onlineBtn.setEnabled(false);
                                        onlineBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                        cashBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_shape_light_transparent));
                                        isButtonSelectable = false;
                                    }*/
                                }
                            }
                            // If pay later is selected
                            else {
                                if (vehicleType.equals("Car")) {
                                    if(carChargesOption.equals("PerHour")){
                                        long hours = TimeUnit.MILLISECONDS.toHours(difference);
                                        inputPaidAmount.setText("Paid Amount - Rs 00");
                                        if(hours==0) {
                                            inputPendingAmount.setText("Pending Amount - Rs "+carChargesOptionRate);
                                        }
                                        else{
                                            inputPendingAmount.setText("Pending Amount - Rs " + (hours * Long.parseLong(carChargesOptionRate)));
                                        }
                                    }
                                    else{
                                        long carMillisecond = Integer.parseInt(carFixHour) * 60 * 60 * 1000;
                                        inputPaidAmount.setText("Paid Amount - Rs 00");
                                        if(carMillisecond < difference){
                                            inputPendingAmount.setText("Pending Amount - Rs "+carChargesOptionRate);
                                        }
                                        else{
                                            inputPendingAmount.setText("Pending Amount - Rs "+carFixHourRate);
                                        }
                                    }
                                    /*long carMillisecond = Integer.parseInt(carHour) * 60 *60 * 1000;
                                    if (carMillisecond < difference) {
                                        inputPaidAmount.setText("Paid Amount - Rs 00");
                                        inputPendingAmount.setText("Pending Amount - Rs "+carFullDayRate);
                                    }
                                    // Need to pay hourly charge
                                    else{
                                        inputPaidAmount.setText("Paid Amount - Rs 00");
                                        inputPendingAmount.setText("Pending Amount - Rs "+carRate);
                                    }*/
                                } else {
                                    if(bikeChargesOption.equals("PerHour")){
                                        long hours = TimeUnit.MILLISECONDS.toHours(difference);
                                        inputPaidAmount.setText("Paid Amount - Rs 00");
                                        if(hours==0) {
                                            inputPendingAmount.setText("Pending Amount - Rs " + bikeChargesOptionRate);
                                        }
                                        else{
                                            inputPendingAmount.setText("Pending Amount - Rs " + (hours * Long.parseLong(bikeChargesOptionRate)));
                                        }
                                    }
                                    else{
                                        long bikeMillisecond = Integer.parseInt(bikeFixHour) * 60 * 60 * 1000;
                                        inputPaidAmount.setText("Paid Amount - Rs 00");
                                        if(bikeMillisecond < difference){
                                            inputPendingAmount.setText("Pending Amount - Rs "+bikeChargesOptionRate);
                                        }
                                        else{
                                            inputPendingAmount.setText("Pending Amount - Rs "+bikeFixHourRate);
                                        }
                                    }
                                    // If difference of time is more than bike hour rate than need to pay full day charge
                                    /*if (bikeMillisecond < difference) {
                                        inputPaidAmount.setText("Paid Amount - Rs 00");
                                        inputPendingAmount.setText("Pending Amount - Rs "+bikeFullDayRate);
                                    }
                                    // Need to pay hourly charge
                                    else{
                                        inputPaidAmount.setText("Paid Amount - Rs 00");
                                        inputPendingAmount.setText("Pending Amount - Rs "+bikeRate);
                                    }*/
                                }
                            }
                        }
                    }
                }
            }
            else if(result.equals("released")){
                Toast.makeText(context, "Vehicle successfully released...", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context, ReleasedParkedVehicleActivity.class);
                i.putExtra("parkedVehicleId", parkedVehicleId);
                i.putExtra("vehicleType", vehicleType);
                i.putExtra("vehicleNumber", vehicleNumber);
                i.putExtra("isValet", isValet);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
            else{
                Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }
}