package com.example.duepark_consumer.Parking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bachors.prefixinput.EditText;
import com.example.duepark_consumer.Employee.DetailEmployeeActivity;
import com.example.duepark_consumer.Employee.EditEmployeeDetailActivity;
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

public class EditParkingDetailActivity extends AppCompatActivity {

    private static final String TAG = "EditParkingDetail";
    private String base_url;
    private SessionManagerHelper sessionManagerHelper;
    private HashMap<String, String> employeeDetails;
    private boolean isEmployeeName = false, isParkingName = false, isParkingType = false, isEmployeeEmail= false, isEmployeeMobileNumber = false, isContinueBtnPress = false, isEmployeePassword = false;
    private String currentValue, parkingType, mVerificationId, newMobileNumber;
    private TextView currentTV, newTV, changeTV;
    private TextInputLayout inputLayout_newValue1, inputLayout_newValue2;
    private TextInputEditText input_newValue2;
    private EditText input_newValue1;
    private Button saveBtn, cancelBtn;
    private RadioGroup parkingTypeRadioGrp;
    private RadioButton personalRadioBtn, businessRadioBtn;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_parking);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(this);
        employeeDetails = sessionManagerHelper.getEmployeeDetails();

        mAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            currentValue = bundle.getString("CurrentValue");
            isEmployeeName = bundle.getBoolean("IsEmployeeName");
            isParkingName = bundle.getBoolean("IsParkingName");
            isParkingType = bundle.getBoolean("IsParkingType");
            isEmployeeEmail = bundle.getBoolean("IsEmployeeEmail");
            isEmployeeMobileNumber = bundle.getBoolean("IsEmployeeMobileNumber");
            isEmployeePassword = bundle.getBoolean("IsEmployeePassword");
        }

        changeTV = findViewById(R.id.changeTV);
        currentTV = findViewById(R.id.currentTV);
        newTV = findViewById(R.id.newTV);
        inputLayout_newValue1 = findViewById(R.id.inputLayout_newValue1);
        inputLayout_newValue2 = findViewById(R.id.inputLayout_newValue2);
        input_newValue1 = findViewById(R.id.input_newValue1);
        input_newValue2 = findViewById(R.id.input_newValue2);
        parkingTypeRadioGrp = findViewById(R.id.parkingTypeRadioGrp);
        personalRadioBtn = findViewById(R.id.personalRadioBtn);
        businessRadioBtn = findViewById(R.id.businessRadioBtn);
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        parkingTypeRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked) {
                RadioButton checkedRadioBtn = radioGroup.findViewById(checked);
                if(checkedRadioBtn.getText().equals("Personal")){
                    parkingType = "Personal";
                }
                else {
                    parkingType = "Business";
                }
            }
        });

        if(isEmployeeName){
            inputLayout_newValue2.setVisibility(View.GONE);
            changeTV.setText("Change employee name");
            currentTV.setText("Current employee name \n"+currentValue);
            newTV.setText("New employee name");
            parkingTypeRadioGrp.setVisibility(View.GONE);
        }

        if(isParkingName){
            inputLayout_newValue2.setVisibility(View.GONE);
            changeTV.setText("Change parking name");
            currentTV.setText("Current parking name \n"+currentValue);
            newTV.setText("New parking name");
            parkingTypeRadioGrp.setVisibility(View.GONE);
        }

        if(isParkingType){
            inputLayout_newValue2.setVisibility(View.GONE);
            inputLayout_newValue1.setVisibility(View.GONE);
            changeTV.setText("Change parking type");
            currentTV.setText("Current parking type : "+currentValue);
            newTV.setText("Select new parking type");
            parkingType = currentValue;
            if(parkingType.equals("Personal")){
                personalRadioBtn.setChecked(true);
            }
            else{
                businessRadioBtn.setChecked(true);
            }
        }

        if(isEmployeeEmail){
            inputLayout_newValue2.setVisibility(View.GONE);
            parkingTypeRadioGrp.setVisibility(View.GONE);
            changeTV.setText("Change your email address");
            currentTV.setText("Current email address : \n"+currentValue);
            newTV.setText("New Email Address");
        }

        if(isEmployeeMobileNumber){
            inputLayout_newValue2.setVisibility(View.GONE);
            changeTV.setText("Change your mobile number");
            currentTV.setText("Old mobile number : \n+91 "+currentValue);
            newTV.setText("New mobile number");
            saveBtn.setText("Continue");
            cancelBtn.setVisibility(View.VISIBLE);
            parkingTypeRadioGrp.setVisibility(View.GONE);
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
            parkingTypeRadioGrp.setVisibility(View.GONE);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmployeeName){
                    saveEmployeeName();
                }
                else if(isParkingName){
                    saveParkingName();
                }
                else if(isParkingType){
                    saveParkingType();
                }
                else if(isEmployeeEmail){
                    saveEmployeeEmailAddress();
                }
                else if(isEmployeeMobileNumber){
                    if(isContinueBtnPress){
                        String verificationCode = input_newValue1.getText().toString().trim();
                        PhoneAuthCredential phoneAuthProvider = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);

                        BackgroundTask backgroundTask = new BackgroundTask(EditParkingDetailActivity.this);
                        backgroundTask.execute("null", "null", "null", "null", newMobileNumber, "null", "null");
                    }
                    else {
                        saveEmployeeMobileNumber();
                    }
                }
                else if(isEmployeePassword){
                    saveEmployeePassword();
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

    private void saveEmployeeName(){
        String employeeName = input_newValue1.getText().toString().trim();
        if(employeeName.equals(currentValue)){
            onBackPressed();
        }
        else{
            if(employeeName.isEmpty()){
                fieldEmpty(input_newValue1);
            }
            else{
                BackgroundTask backgroundTask = new BackgroundTask(this);
                backgroundTask.execute(employeeName, "null", "null", "null", "null", "null", "null");
            }
        }
    }

    private void saveParkingName(){
        String parkingName = input_newValue1.getText().toString().trim();
        if(parkingName.equals(currentValue)){
            onBackPressed();
        }
        else{
            if(parkingName.isEmpty()){
                fieldEmpty(input_newValue1);
            }
            else{
                BackgroundTask backgroundTask = new BackgroundTask(this);
                backgroundTask.execute("null", parkingName, "null", "null", "null", "null", "null");
            }
        }
    }

    private void saveParkingType(){
        if(parkingType.equals(currentValue)){
            onBackPressed();
        }
        else{
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute("null", "null", parkingType, "null", "null", "null", "null");
        }
    }

    private void saveEmployeeEmailAddress() {
        String employeeEmailId = input_newValue1.getText().toString().trim();
        if (employeeEmailId.equals(currentValue)) {
            onBackPressed();
        } else {
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
                backgroundTask.execute("null", "null", "null", employeeEmailId, "null", "null", "null");
            }
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
                fieldEmpty(input_newValue1);
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

                        BackgroundTask backgroundTask = new BackgroundTask(EditParkingDetailActivity.this);
                        backgroundTask.execute("null", "null", "null", "null", employeeMobileNumber, "null", "null");
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
                fieldEmpty(input_newValue1);
            } else if (newPassword.isEmpty()) {
                Drawable myIcon = getResources().getDrawable(R.drawable.error);
                myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                input_newValue2.setError("Field can't be Empty", myIcon);
                requestFocus(input_newValue2);
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
                backgroundTask.execute("null", "null", "null", "null", "null", oldPassword, newPassword);
            }
        }
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
        Intent i = new Intent(this, ParkingActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{

        private Context context;
        private ProgressDialog progressDialog;

        BackgroundTask(Context context){
            this.context = context;
            progressDialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            if(isEmployeeName){
                progressDialog.setMessage("Updating employee name...");
            }
            else if(isParkingName){
                progressDialog.setMessage("Updating parking name...");
            }
            else if(isParkingType){
                progressDialog.setMessage("Updating parking type...");
            }
            else if(isEmployeeEmail){
                progressDialog.setMessage("Updating email address..");
            }
            else if(isEmployeeMobileNumber){
                progressDialog.setMessage("Updating mobile number...");
            }
            else if(isEmployeePassword){
                progressDialog.setMessage("Updating password...");
            }
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String updateParking_url = base_url+"update_parking1.php";
            try{
                URL url = new URL(updateParking_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream ops = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                String data = URLEncoder.encode("EmployeeName","UTF-8")+"="+URLEncoder.encode(params[0],"UTF-8")+"&"+
                        URLEncoder.encode("ParkingName","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"+
                        URLEncoder.encode("ParkingType","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"+
                        URLEncoder.encode("EmployeeEmailId", "UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8")+"&"+
                        URLEncoder.encode("EmployeeMobileNumber", "UTF-8")+"="+URLEncoder.encode(params[4],"UTF-8")+"&"+
                        URLEncoder.encode("EmployeeOldPassword", "UTF-8")+"="+URLEncoder.encode(params[5],"UTF-8")+"&"+
                        URLEncoder.encode("EmployeeNewPassword","UTF-8")+"="+URLEncoder.encode(params[6],"UTF-8")+"&"+
                        URLEncoder.encode("ParkingId","UTF-8")+"="+URLEncoder.encode(employeeDetails.get(SessionManagerHelper.ParkingId),"UTF-8")+"&"+
                        URLEncoder.encode("EmployeeId","UTF-8")+"="+URLEncoder.encode(employeeDetails.get(SessionManagerHelper.EmployeeId),"UTF-8")+"&"+
                        URLEncoder.encode("Entity", "UTF-8") + "=" + URLEncoder.encode("ConsumerApp", "UTF-8");
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
            if(result.equals("UpdateSuccessfully")){
                Toast.makeText(EditParkingDetailActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else{
                Toast.makeText(EditParkingDetailActivity.this, result, Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }
}