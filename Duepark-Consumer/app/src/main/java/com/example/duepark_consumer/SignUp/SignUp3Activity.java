package com.example.duepark_consumer.SignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.duepark_consumer.Helper.SmsHashCodeHelper;
import com.example.duepark_consumer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp3Activity extends AppCompatActivity {

    private EditText inputMobileNumber, inputPassword;
    private TextView privacyPolicyTV;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final static String EmployeeMobileNumberSharedPreference = "com.example.duepark_consumer.EmployeeMobileNumber";
    private final static String EmployeePasswordSharedPreference = "com.example.duepark_consumer.EmployeePassword";
    private final static String privacyPolicyString = "By continuing, you agree to the <font color='#0e88d3'>Privacy Policy</font> and <font color='#0e88d3'>Terms of service</font>.";

    // Same her
    private final static String EmployeeStateSharedPreference = "com.example.duepark_consumer.EmployeeState";
    private final static String EmployeeCitySharedPreference = "com.example.duepark_consumer.EmployeeCity";
    private final static String EmployeeNameSharedPreference = "com.example.duepark_consumer.EmployeeName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up3);

        sharedPreferences = getApplication().getSharedPreferences("NewEmployeeProfile", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        inputMobileNumber = findViewById(R.id.inputMobileNumber);
        inputPassword = findViewById(R.id.inputPassword);
        privacyPolicyTV = findViewById(R.id.privacyPolicyTV);
        privacyPolicyTV.setText(Html.fromHtml(privacyPolicyString));

        FloatingActionButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FloatingActionButton signup3Btn = findViewById(R.id.signup3Btn);
        signup3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            createProfile();
            }
        });

        /*SmsHashCodeHelper smsHashCodeHelper = new SmsHashCodeHelper(this);
        smsHashCodeHelper.getAppHashCode();*/

        getUserData();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUp3Activity.this, SignUp2Activity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void createProfile(){

        String mobileRegex = "^[6-9][0-9]{9}$";
        String employeeMobileNumber = inputMobileNumber.getText().toString().trim();
        String employeePassword = inputPassword.getText().toString().trim();

        Pattern mobilePattern = Pattern.compile(mobileRegex);
        Matcher mobileNumberMatches = mobilePattern.matcher(employeeMobileNumber);

        if(employeeMobileNumber.isEmpty()){
            fieldEmpty(inputMobileNumber);
        }
        else if(employeePassword.isEmpty()){
            fieldEmpty(inputPassword);
        }
        else if(employeePassword.length() < 8){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputPassword.setError("Please enter password having length greater than 8.!",myIcon);
            requestFocus(inputPassword);
        }
        else if(!mobileNumberMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputMobileNumber.setError("Please enter valid mobile number.!",myIcon);
            requestFocus(inputMobileNumber);
        }
        else{
            storeUserData();
            //String employeeState = null, employeeCity = null, employeeName= null;
            // Remove this after verification code done
            /*if(sharedPreferences.contains(EmployeeStateSharedPreference)){
                employeeState = sharedPreferences.getString(EmployeeStateSharedPreference, "");
            }
            if(sharedPreferences.contains(EmployeeCitySharedPreference)){
                employeeCity = sharedPreferences.getString(EmployeeCitySharedPreference,"");
            }
            if(sharedPreferences.contains(EmployeeNameSharedPreference)){
                employeeName = sharedPreferences.getString(EmployeeNameSharedPreference,"");
            }*/
            // Add Message deliver code here
           /* try{
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(userMobileNumber,null,"Your OTP for Duepark application is "+getRandomNumberString(),null,null);
            }
            catch (Exception e){
                e.printStackTrace();
            }*/
            startActivity(new Intent(SignUp3Activity.this, MobileNumberVerificationActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
            /*MobileNumberVerificationActivity.BackgroundTask backgroundTask = new MobileNumberVerificationActivity.BackgroundTask(this);
            backgroundTask.execute(employeeName, employeeState, employeeCity, employeeMobileNumber, employeePassword);*/

            /*startActivity(new Intent(this, WelcomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();*/
        }
    }

    private void storeUserData(){
        if(inputMobileNumber.getText() != null){
            editor.remove(EmployeeMobileNumberSharedPreference);
            //editor.apply();
            editor.putString(EmployeeMobileNumberSharedPreference, inputMobileNumber.getText().toString().trim());
            //editor.apply();
        }

        if(inputPassword.getText() != null){
            editor.remove(EmployeePasswordSharedPreference);
            //editor.apply();
            editor.putString(EmployeePasswordSharedPreference, inputPassword.getText().toString().trim());
            //editor.apply();
        }
        editor.commit();
    }

    private void getUserData(){
        if(sharedPreferences.contains(EmployeeMobileNumberSharedPreference)){
            inputMobileNumber.setText(sharedPreferences.getString(EmployeeMobileNumberSharedPreference, ""));
        }
        if(sharedPreferences.contains(EmployeePasswordSharedPreference)){
            inputPassword.setText(sharedPreferences.getString(EmployeePasswordSharedPreference,""));
        }

    }

    private static String getRandomNumberString(){
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
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

    // Add Mobile Number Exists in database or Not
}

