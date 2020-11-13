package com.example.duepark_consumer.MonthlyPass;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Model.MonthlyPass;
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
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailMonthlyPassActivity extends AppCompatActivity implements View.OnClickListener {

    private String base_url;
    private static final String TAG = "DetailMonthlyPassAct";
    private String monthlyPassId;
    private TextView monthlyPassIdTV, passUserNameTV, passUserMobileNumberTV, vehicleTypeTV, vehicleNumberTV, validityTV, vehicleNumber;
    private EditText passUserMobileNumberET, vehicleNumberET;
    private Button passUserMobileNumberEditBtn, vehicleNumberEditBtn, renewBtn;
    private ImageView backBtn;
    private MonthlyPass monthlyPass;
    private RelativeLayout vehicleNumberRL;
    //private int textViewId = 0, editTextId = 5;
    private String[] vehicleNumbers;
    private boolean isValet;
   /* private TextView textView;
    private EditText editText;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_monthly_pass);

        base_url = getResources().getString(R.string.base_url);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            monthlyPassId = bundle.getString("monthlyPassId");
            isValet = bundle.getBoolean("isValet");
        }

        /*textView = new TextView(this);
        editText = new EditText(this);*/
        monthlyPassIdTV = findViewById(R.id.monthlyPassIdTV);
        passUserNameTV = findViewById(R.id.passUserNameTV);
        passUserMobileNumberTV = findViewById(R.id.passUserMobileNumberTV);
        passUserMobileNumberET = findViewById(R.id.passUserMobileNumberET);
        vehicleTypeTV = findViewById(R.id.vehicleTypeTV);
        vehicleNumber = findViewById(R.id.vehicleNumber);
        vehicleNumberTV = findViewById(R.id.vehicleNumberTV);
        vehicleNumberET = findViewById(R.id.vehicleNumberET);
        validityTV = findViewById(R.id.validityTV);
        passUserMobileNumberEditBtn = findViewById(R.id.passUserMobileNumberEditBtn);
        passUserMobileNumberEditBtn.setOnClickListener(this);

        vehicleNumberEditBtn = findViewById(R.id.vehicleNumberEditBtn);
        vehicleNumberEditBtn.setOnClickListener(this);
        vehicleNumberRL = findViewById(R.id.vehicleNumberRL);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        renewBtn = findViewById(R.id.renewBtn);
        renewBtn.setOnClickListener(this);

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("get", monthlyPassId);
    }

    private void updateMonthlyPass(){
        String passUserMobileNumber = passUserMobileNumberET.getText().toString().trim();
        String vehicleNumber = vehicleNumberET.getText().toString().trim();

        String mobileRegex = "^[6-9][0-9]{9}$";
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        Matcher mobileNumberMatches = mobilePattern.matcher(passUserMobileNumber);

        if(passUserMobileNumber.isEmpty()){
            fieldEmpty(passUserMobileNumberET);
        }
        else if(!mobileNumberMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            passUserMobileNumberET.setError("Please enter valid mobile number.!",myIcon);
            requestFocus(passUserMobileNumberET);
        }
        else if(vehicleNumber.isEmpty()){
            fieldEmpty(vehicleNumberET);
        }
        else{
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute("update", monthlyPassId, passUserMobileNumber, vehicleNumber, "false");
        }

    }

    private void fieldEmpty(EditText input){
        Drawable myIcon = getResources().getDrawable(R.drawable.error);
        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
        input.setError("Field can't be Empty",myIcon);
        requestFocus(input);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if(isValet){
            Intent i = new Intent(this, ValetHomeActivity.class);
            i.putExtra("IsValetMonthlyPass", true);
            i.putExtra("IsValetReleasedVehicle", false);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }else {
            Intent i = new Intent(DetailMonthlyPassActivity.this, MonthlyPassActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        if(view == backBtn){
            onBackPressed();
        }
        else if(view == passUserMobileNumberEditBtn){
            if(passUserMobileNumberTV.getVisibility()==View.VISIBLE){
                passUserMobileNumberTV.setVisibility(View.INVISIBLE);
                passUserMobileNumberET.setVisibility(View.VISIBLE);
            }
            else{
                passUserMobileNumberTV.setVisibility(View.VISIBLE);
                passUserMobileNumberET.setVisibility(View.INVISIBLE);
            }
        }
        else if(view == vehicleNumberEditBtn){
            /*textViewId = 0;
            editTextId = 5;
            for(int i=0;i<vehicleNumbers.length;i++){
                if(textView.getId() == i+1){
                    if(textView.getVisibility() == View.VISIBLE){
                        textView.setVisibility(View.INVISIBLE);
                        editText.setVisibility(View.VISIBLE);
                    }
                    else{
                        textView.setVisibility(View.VISIBLE);
                        editText.setVisibility(View.INVISIBLE);
                    }
                }
            }*/
            if(vehicleNumberTV.getVisibility()==View.VISIBLE){
                vehicleNumberTV.setVisibility(View.INVISIBLE);
                vehicleNumberET.setVisibility(View.VISIBLE);
            }
            else{
                vehicleNumberTV.setVisibility(View.VISIBLE);
                vehicleNumberET.setVisibility(View.INVISIBLE);
            }
        }
        else if(view == renewBtn){
            updateMonthlyPass();
        }
    }

    class BackgroundTask extends AsyncTask<String, MonthlyPass, String>{
        private Context ctx;
        private ProgressDialog progressDialog;

        BackgroundTask(Context ctx){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if(params[0].equals("get")){
                progressDialog.setMessage("Getting monthly pass detail...");
                String getMonthlyPass_url = base_url+"get_monthlyPass.php?MonthlyPassId="+params[1];
                //String getMonthlyPass_url = "https://duepark.000webhostapp.com/consumer/get_monthlyPass.php?MonthlyPassId="+params[1];
                try{
                    URL url = new URL(getMonthlyPass_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }
                    httpURLConnection.disconnect();

                    String jsonString = stringBuilder.toString().trim();

                    JSONObject jo = new JSONObject(jsonString);
                    monthlyPass = new MonthlyPass(jo.getString("id"), jo.getString("PassUserName"), jo.getString("PassUserMobileNumber"),
                            jo.getString("PayableAmount"), jo.getString("PayableType"), jo.getString("IssuedDate"),
                            jo.getString("ExpiryDate"), jo.getString("VehicleNumber"), jo.getString("VehicleType"),
                            jo.getString("GeneratedLocationId"), jo.getString("GeneratedMonthlyPassId"), jo.getString("IssuedBy"),jo.getString("IssuedTime"));
                    return "get";
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(params[0].equals("update")){
                progressDialog.setMessage("Updating monthly pass...");
                String updateMonthlyPass_url = base_url+"update_monthlyPass.php";
                try{
                    URL url = new URL(updateMonthlyPass_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("MonthlyPassId","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                            +URLEncoder.encode("PassUserMobileNumber","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"
                            +URLEncoder.encode("VehicleNumber","UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8")+"&"
                            +URLEncoder.encode("IsRenewed","UTF-8")+"="+URLEncoder.encode(params[4],"UTF-8");
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
        protected void onPostExecute(String result) {
            if(result.equals("get")){
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat month_date = new SimpleDateFormat("dd MMM", Locale.US);
                try {
                    Date issued_date = sdf.parse(monthlyPass.getIssuedDate());
                    Date expiry_date = sdf.parse(monthlyPass.getExpiryDate());
                    String issuedDate = month_date.format(issued_date);
                    String expiryDate = month_date.format(expiry_date);
                    validityTV.setText(issuedDate+" - "+expiryDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                monthlyPassIdTV.setText("Monthly Pass Id : "+monthlyPass.getGeneratedLocationId()+""+monthlyPass.getGeneratedMonthlyPassId());
                passUserNameTV.setText(monthlyPass.getPassUserName());
                passUserMobileNumberTV.setText(monthlyPass.getPassUserMobileNumber());
                passUserMobileNumberET.setText(monthlyPass.getPassUserMobileNumber());
                vehicleTypeTV.setText(monthlyPass.getVehicleType());
                vehicleNumberTV.setText(monthlyPass.getVehicleNumber());
                vehicleNumberET.setText(monthlyPass.getVehicleNumber());
                /*if(monthlyPass.getVehicleNumber().contains(",")){
                    vehicleNumbers = monthlyPass.getVehicleNumber().split(",");
                    for(int i = 0;i<vehicleNumbers.length;i++) {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        if(i == 0){
                            params.addRule(RelativeLayout.BELOW, vehicleNumber.getId());
                        }
                        else{
                            params.addRule(RelativeLayout.BELOW, textViewId);
                        }
                        params.addRule(RelativeLayout.ALIGN_PARENT_START, 1);

                        // Generate TextView
                        textViewId = i+1;
                        textView.setText(vehicleNumbers[i]);
                        textView.setId(textViewId);
                        textView.setTextSize(18);
                        textView.setVisibility(View.VISIBLE);
                        textView.setTextColor(getResources().getColor(R.color.black));
                        textView.setPadding(2,2,2,2);
                        textView.setLayoutParams(params);

                        // Generate EditText
                        editTextId = i +1;
                        editText.setText(vehicleNumbers[i]);
                        editText.setId(editTextId);
                        editText.setVisibility(View.INVISIBLE);
                        editText.setLayoutParams(params);

                        vehicleNumberRL.addView(textView, params);
                        vehicleNumberRL.addView(editText, params);
                    }
                }
                else{
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, vehicleNumber.getId());
                    params.addRule(RelativeLayout.ALIGN_PARENT_START, 1);
                    textViewId = textViewId + 1;
                    editTextId = editTextId + 1;

                    // Generate TextView
                    textView.setText(monthlyPass.getVehicleNumber());
                    textView.setId(textViewId);
                    textView.setTextSize(18);
                    textView.setTextColor(getResources().getColor(R.color.black));
                    textView.setVisibility(View.VISIBLE);
                    textView.setPadding(2,2,2,2);
                    textView.setLayoutParams(params);

                    // Generate EditText
                    editText.setText(monthlyPass.getVehicleNumber());
                    editText.setId(editTextId);
                    editText.setVisibility(View.INVISIBLE);
                    editText.setLayoutParams(params);

                    vehicleNumberRL.addView(editText, params);
                    vehicleNumberRL.addView(textView, params);
                }*/
            }
            else if(result.equals("updated")){
                Toast.makeText(ctx, "Updated Successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else{
                Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }
}