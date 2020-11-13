package com.example.duepark_consumer.Employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bachors.prefixinput.EditText;
import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditEmployeeDetailActivity extends AppCompatActivity {

    private static final String TAG = "EditEmployeeDetail";
    private String base_url;
    private SessionManagerHelper sessionManagerHelper;
    private TextInputLayout inputLayout_newValue1, inputLayout_newValue2;
    private TextInputEditText input_newValue2;
    private EditText input_newValue1;
    private boolean isEmployeeName=false, isEmployeeEmail = false, isEmployeePassword = false, isEmployeeMobileNumber = false, isValet = false, isContinueBtnPress = false, isEmployeeVehicleType = false, isEmployeeAdharNumber=false;
    private TextView currentTV, newTV, changeTV;
    private HashMap<String, String> employeeDetail;
    private String currentValue, employeeId, newMobileNumber, mVerificationId, vehicleType, a;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private Button saveBtn, cancelBtn;
    private RadioGroup vehicleTypeRadioGrp;
    private RadioButton carRadioBtn, bikeRadioBtn, bothRadioBtn;
    private int keyDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee_detail);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(this);
        employeeDetail = sessionManagerHelper.getEmployeeDetails();

        mAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            currentValue = bundle.getString("CurrentValue");
            isEmployeeName = bundle.getBoolean("IsEmployeeName");
            isEmployeeEmail = bundle.getBoolean("IsEmployeeEmail");
            isEmployeePassword = bundle.getBoolean("IsEmployeePassword");
            isEmployeeMobileNumber = bundle.getBoolean("IsEmployeeMobileNumber");
            isEmployeeVehicleType = bundle.getBoolean("IsEmployeeVehicleType");
            isEmployeeAdharNumber = bundle.getBoolean("IsEmployeeAdharNumber");
            employeeId = bundle.getString("EmployeeId");
            isValet = bundle.getBoolean("IsValet");
        }

        changeTV = findViewById(R.id.changeTV);
        currentTV = findViewById(R.id.currentTV);
        newTV = findViewById(R.id.newTV);
        inputLayout_newValue1 = findViewById(R.id.inputLayout_newValue1);
        inputLayout_newValue2 = findViewById(R.id.inputLayout_newValue2);
        input_newValue1 = findViewById(R.id.input_newValue1);
        input_newValue2 = findViewById(R.id.input_newValue2);
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        vehicleTypeRadioGrp = findViewById(R.id.vehicleRadioGrp);
        carRadioBtn = findViewById(R.id.carRadioBtn);
        bikeRadioBtn = findViewById(R.id.bikeRadioBtn);
        bothRadioBtn = findViewById(R.id.bothRadioBtn);
        vehicleTypeRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked) {
                RadioButton checkedRadioBtn = radioGroup.findViewById(checked);
                if(checkedRadioBtn.getText().equals("Car")){
                    vehicleType = "Car";
                }
                else if(checkedRadioBtn.getText().equals("Bike")){
                    vehicleType = "Bike";
                }
                else{
                    vehicleType = "Both";
                }
            }
        });

        if(isEmployeeName){
            inputLayout_newValue2.setVisibility(View.GONE);
            changeTV.setText("Change employee name");
            currentTV.setText("Current employee Name \n"+currentValue);
            newTV.setText("New employee name");
            vehicleTypeRadioGrp.setVisibility(View.GONE);
        }

        if(isEmployeeEmail){
            inputLayout_newValue2.setVisibility(View.GONE);
            changeTV.setText("Change your email address");
            currentTV.setText("Current email address : \n"+currentValue);
            newTV.setText("New email address");
            vehicleTypeRadioGrp.setVisibility(View.GONE);
        }

        if(isEmployeeMobileNumber){
            inputLayout_newValue2.setVisibility(View.GONE);
            changeTV.setText("Change your mobile number");
            currentTV.setText("Old mobile number : \n+91 "+currentValue);
            newTV.setText("New mobile number");
            saveBtn.setText("Continue");
            cancelBtn.setVisibility(View.VISIBLE);
            vehicleTypeRadioGrp.setVisibility(View.GONE);
            input_newValue1.setInputType(InputType.TYPE_CLASS_NUMBER);
            input_newValue1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
            //input_newValue1.setPrefix("91 ");
        }

        if(isEmployeePassword){
            input_newValue1.setHint("Current password");
            input_newValue2.setHint("New password");
            input_newValue1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            input_newValue2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            changeTV.setText("Change password");
            currentTV.setText("Use the form below to change the password for your duepark account");
            vehicleTypeRadioGrp.setVisibility(View.GONE);
        }

        if(isEmployeeVehicleType){
            changeTV.setText("Change vehicle type");
            currentTV.setText("Current vehicle type assign : "+currentValue);
            newTV.setText("Select new vehicle type");
            inputLayout_newValue1.setVisibility(View.GONE);
            inputLayout_newValue2.setVisibility(View.GONE);
            vehicleType = currentValue;
            if(currentValue.equals("Bike")){
                bikeRadioBtn.setChecked(true);
            }
            else if(currentValue.equals("Car")){
                carRadioBtn.setChecked(true);
            }
            else if(currentValue.equals("Both")){
                bothRadioBtn.setChecked(true);
            }
        }

        if(isEmployeeAdharNumber){
            changeTV.setText("Change adhar number");
            currentTV.setText("Current adhar number :\n"+currentValue);
            newTV.setText("New adhar number");
            vehicleTypeRadioGrp.setVisibility(View.GONE);
            inputLayout_newValue2.setVisibility(View.GONE);
            input_newValue1.setInputType(InputType.TYPE_CLASS_NUMBER);
            input_newValue1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
            input_newValue1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    boolean flag = true;
                    String eachBlock[] = input_newValue1.getText().toString().split(" ");
                    for (int i = 0; i < eachBlock.length; i++) {
                        if (eachBlock[i].length() > 4) {
                            flag = false;
                        }
                    }
                    if (flag) {

                        input_newValue1.setOnKeyListener(new View.OnKeyListener() {

                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {

                                if (keyCode == KeyEvent.KEYCODE_DEL)
                                    keyDel = 1;
                                return false;
                            }
                        });

                        if (keyDel == 0) {

                            if (((input_newValue1.getText().length() + 1) % 5) == 0) {

                                if (input_newValue1.getText().toString().split(" ").length <= 3) {
                                    input_newValue1.setText(input_newValue1.getText() + " ");
                                    input_newValue1.setSelection(input_newValue1.getText().length());
                                }
                            }
                            a = input_newValue1.getText().toString().trim();
                        } else {
                            a = input_newValue1.getText().toString();
                            keyDel = 0;
                        }

                    } else {
                        input_newValue1.setText(a);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmployeeEmail){
                    saveEmployeeEmailAddress();
                }

                else if(isEmployeePassword){
                    saveEmployeePassword();
                }

                else if(isEmployeeMobileNumber){
                    if(isContinueBtnPress){
                        String verificationCode = input_newValue1.getText().toString().trim();
                        PhoneAuthCredential phoneAuthProvider = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                        BackgroundTask backgroundTask = new BackgroundTask(EditEmployeeDetailActivity.this);
                        backgroundTask.execute(employeeId, "null", "null", "null", newMobileNumber, "null", "null", "null");
                    }
                    else {
                        saveEmployeeMobileNumber();
                    }
                }

                else if(isEmployeeVehicleType){
                    saveEmployeeVehicleType();
                }

                else if(isEmployeeAdharNumber){
                    saveEmployeeAdharNumber();
                }

                else if(isEmployeeName){
                    saveEmployeeName();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isContinueBtnPress){
                    resendVerificationCode("+91 "+newMobileNumber, mResendToken);
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
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token){
        PhoneAuthProvider.getInstance(mAuth).verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token
        );
    }

    private void saveEmployeeName(){
        String newEmployeeName = input_newValue1.getText().toString().trim();
        if(newEmployeeName.equals(currentValue)){
            onBackPressed();
        }
        else{
            if(newEmployeeName.isEmpty()){
                Drawable myIcon = getResources().getDrawable(R.drawable.error);
                myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                input_newValue1.setError("Field can't be Empty", myIcon);
                requestFocus(input_newValue1);
            }
            else{
                BackgroundTask backgroundTask = new BackgroundTask(this);
                backgroundTask.execute(employeeId, "null", "null", "null", "null","null","null", newEmployeeName);
            }
        }
    }

    private void saveEmployeeAdharNumber(){
        String employeeAdharNumber = input_newValue1.getText().toString().trim();
        if(employeeAdharNumber.equals(currentValue)){
            onBackPressed();
        }
        else{
            if(employeeAdharNumber.isEmpty()){
                Drawable myIcon = getResources().getDrawable(R.drawable.error);
                myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                input_newValue1.setError("Field can't be Empty", myIcon);
                requestFocus(input_newValue1);
            }
            else if(employeeAdharNumber.length()<12){
                Drawable myIcon = getResources().getDrawable(R.drawable.error);
                myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                input_newValue1.setError("Please enter valid aadhar number.!",myIcon);
                requestFocus(input_newValue1);
            }
            else{
                BackgroundTask backgroundTask = new BackgroundTask(this);
                backgroundTask.execute(employeeId, "null", "null", "null", "null","null",employeeAdharNumber, "null");
            }
        }
    }

    private void saveEmployeeVehicleType(){
        if(vehicleType.equals(currentValue)){
            onBackPressed();
        }
        else{
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute(employeeId, "null", "null", "null", "null",vehicleType, "null", "null");
        }
    }

    private void saveEmployeeMobileNumber(){
        String employeeMobileNumber = input_newValue1.getText().toString().trim();
        if(currentValue.equals(employeeMobileNumber)){
            onBackPressed();
        }
        else {

            String mobileRegex = "^[6-9][0-9]{9}$";
            Pattern mobilePattern = Pattern.compile(mobileRegex);
            Matcher mobileNumberMatches = mobilePattern.matcher(employeeMobileNumber);

            if (employeeMobileNumber.isEmpty()) {
                Drawable myIcon = getResources().getDrawable(R.drawable.error);
                myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                input_newValue1.setError("Field can't be Empty", myIcon);
                requestFocus(input_newValue1);
            } else if (!mobileNumberMatches.matches()) {
                Drawable myIcon = getResources().getDrawable(R.drawable.error);
                myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                input_newValue1.setError("Please enter valid mobile number.", myIcon);
                requestFocus(input_newValue1);
            } else {
                newMobileNumber = input_newValue1.getText().toString().trim();
                changeTV.setText("Verify Mobile Number");
                currentTV.setText(Html.fromHtml("<b>+91 " + newMobileNumber + "</b> <font color='#0e88d3'> (Change)</font>"));
                newTV.setText("A text with a One Time Password (OTP) has been sent to the number above");
                newTV.setTypeface(null, Typeface.NORMAL);
                newTV.setTextSize(16);
                saveBtn.setText("Verify");
                cancelBtn.setText("Resend OTP");
                isContinueBtnPress = true;
                cancelBtn.setEnabled(false);

                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Log.d(TAG, "onVerificationCompleted: " + phoneAuthCredential);
                        //signInWithPhoneAuthCredential(phoneAuthCredential);
                        BackgroundTask backgroundTask = new BackgroundTask(EditEmployeeDetailActivity.this);
                        backgroundTask.execute(employeeId, "null", "null", "null", newMobileNumber, "null", "null", "null");
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.w(TAG, "onVerificationFailed", e);
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        Log.d(TAG, "onCodeSent:" + verificationId);
                        Log.d(TAG, "onCodeSent: " + token);
                        // Save verification ID and resending token so we can use them later
                        mVerificationId = verificationId;
                        mResendToken = token;
                        cancelBtn.setEnabled(true);
                    }
                };

                PhoneAuthProvider.getInstance(mAuth).verifyPhoneNumber(
                        "+91 " + newMobileNumber,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        mCallbacks
                );
            }
        }
    }

    private void saveEmployeePassword(){
        String oldPassword = input_newValue1.getText().toString().trim();
        String newPassword = input_newValue2.getText().toString().trim();
        if(oldPassword.equals(newPassword)){
            onBackPressed();
        }
        else {
            if (oldPassword.isEmpty()) {
                Drawable myIcon = getResources().getDrawable(R.drawable.error);
                myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                input_newValue1.setError("Field can't be Empty", myIcon);
                requestFocus(input_newValue1);
            } else if (newPassword.isEmpty()) {
                fieldEmpty(input_newValue2);
            } else if (oldPassword.length() < 8) {
                Drawable myIcon = getResources().getDrawable(R.drawable.error);
                myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                input_newValue1.setError("Password should be of 8 digit", myIcon);
                requestFocus(input_newValue1);
            } else if (newPassword.length() < 8) {
                Drawable myIcon = getResources().getDrawable(R.drawable.error);
                myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                input_newValue2.setError("Password should be of 8 digit", myIcon);
                requestFocus(input_newValue2);
            } else {
                BackgroundTask backgroundTask = new BackgroundTask(this);
                backgroundTask.execute(employeeId, "null", oldPassword, newPassword, "null", "null", "null", "null");
            }
        }
    }

    private void saveEmployeeEmailAddress(){
        String employeeEmailId = input_newValue1.getText().toString().trim();
        if(employeeEmailId.equals(currentValue)){
            onBackPressed();
        }
        else {
            String emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
            Pattern emailPattern = Pattern.compile(emailRegex);
            Matcher emailMatches = emailPattern.matcher(employeeEmailId);

            if (employeeEmailId.isEmpty()) {
                Drawable myIcon = getResources().getDrawable(R.drawable.error);
                myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                input_newValue1.setError("Field can't be Empty", myIcon);
                requestFocus(input_newValue1);
            } else if (!emailMatches.matches()) {
                Drawable myIcon = getResources().getDrawable(R.drawable.error);
                myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                input_newValue1.setError("Please enter valid email address.!", myIcon);
                requestFocus(input_newValue1);
            } else {
                BackgroundTask backgroundTask = new BackgroundTask(this);
                backgroundTask.execute(employeeId, employeeEmailId, "null", "null", "null", "null", "null", "null");
            }
        }
    }

    private void fieldEmpty(TextInputEditText input){
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
        Intent i = new Intent(this, DetailEmployeeActivity.class);
        i.putExtra("EmployeeId", employeeId);
        i.putExtra("IsValet", isValet);
        i.putExtra("IsEmployeeActive","1");
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
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
            if(isEmployeeEmail){
                progressDialog.setMessage("Updating email address..");
            }
            else if(isEmployeePassword){
                progressDialog.setMessage("Updating password...");
            }
            else if(isEmployeeMobileNumber){
                progressDialog.setMessage("Updating mobile number...");
            }
            else if(isEmployeeVehicleType){
                progressDialog.setMessage("Updating vehicle type...");
            }
            else if(isEmployeeAdharNumber){
                progressDialog.setMessage("Updating adhar number...");
            }
            else if(isEmployeeName){
                progressDialog.setMessage("Updating employee name...");
            }
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String updateEmployee_url = base_url+"update_employee1.php";
            try {
                URL url = new URL(updateEmployee_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream ops = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                String data = URLEncoder.encode("EmployeeId", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("EmployeeEmailId", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                        URLEncoder.encode("EmployeeOldPassword", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                        URLEncoder.encode("EmployeeNewPassword", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8")+"&"+
                        URLEncoder.encode("EmployeeMobileNumber", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8")+"&"+
                        URLEncoder.encode("EmployeeVehicleType", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8")+"&"+
                        URLEncoder.encode("EmployeeAdharNumber", "UTF-8") + "=" + URLEncoder.encode(params[6], "UTF-8")+"&"+
                        URLEncoder.encode("EmployeeName", "UTF-8") + "=" + URLEncoder.encode(params[7], "UTF-8")+"&"+
                        URLEncoder.encode("Entity", "UTF-8") + "=" + URLEncoder.encode("ConsumerApp", "UTF-8");
                bw.write(data);
                bw.flush();
                bw.close();
                ops.close();

                InputStream ips = httpURLConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(ips, "iso-8859-1"));
                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
                br.close();
                ips.close();
                httpURLConnection.disconnect();
                Log.d(TAG, "doInBackground: " + result.toString());
                return result.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("UpdateSuccessfully")){
                Toast.makeText(EditEmployeeDetailActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else{
                Toast.makeText(EditEmployeeDetailActivity.this, result, Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }
}