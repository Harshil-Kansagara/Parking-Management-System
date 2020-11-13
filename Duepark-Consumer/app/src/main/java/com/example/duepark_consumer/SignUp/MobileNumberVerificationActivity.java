package com.example.duepark_consumer.SignUp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Employee.ActivatedEmployeeActivity;
import com.example.duepark_consumer.Helper.GetOtpInterface;
import com.example.duepark_consumer.R;
import com.example.duepark_consumer.Receiver.MySMSBroadCastReceiver;
import com.example.duepark_consumer.WelcomeActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OtpView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

public class MobileNumberVerificationActivity extends AppCompatActivity {//implements  GoogleApiClient.ConnectionCallbacks,GetOtpInterface, GoogleApiClient.OnConnectionFailedListener {

    private String base_url;
    private static final String TAG = "ProfileVerification";
    private TextView verificationSentenceTV, verifyCodeTV;
    private SharedPreferences sharedPreferences;
    private OtpView otpView;
    private GoogleApiClient mGoogleApiClient;
    private MySMSBroadCastReceiver mySMSBroadCastReceiver;
    private int RESOLVE_HINT = 2;
    private static SharedPreferences.Editor editor;
    private String EmployeeName, EmployeeState, EmployeeCity, EmployeeMobileNumber, EmployeePassword, mVerificationId, mobileNumberForOtp;
    private final static String EmployeeNameSharedPreference = "com.example.duepark_consumer.EmployeeName";
    private final static String EmployeeStateSharedPreference = "com.example.duepark_consumer.EmployeeState";
    private final static String EmployeeCitySharedPreference = "com.example.duepark_consumer.EmployeeCity";
    private final static String EmployeeMobileNumberSharedPreference = "com.example.duepark_consumer.EmployeeMobileNumber";
    private final static String EmployeePasswordSharedPreference = "com.example.duepark_consumer.EmployeePassword";
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Button resendBtn, verifyOtpBtn;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number_verification);

        base_url = getResources().getString(R.string.base_url);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getApplication().getSharedPreferences("NewEmployeeProfile", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        otpView = findViewById(R.id.otpView);
        verifyCodeTV = findViewById(R.id.verifyCodeTV);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView wrongNumberBtn = findViewById(R.id.wrongNumberBtn);
        wrongNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        verifyOtpBtn = findViewById(R.id.verifyOtpBtn);
        verifyOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verificationCode = otpView.getText().toString().trim();
                PhoneAuthCredential phoneAuthProvider = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                BackgroundTask backgroundTask = new BackgroundTask(view.getContext());
                backgroundTask.execute(EmployeeName, EmployeeState, EmployeeCity, EmployeeMobileNumber, EmployeePassword);
            }
        });

        resendBtn = findViewById(R.id.resendBtn);
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode("+91 "+EmployeeMobileNumber, mResendToken);
            }
        });
        verificationSentenceTV = findViewById(R.id.verificationSentenceTV);

        //final TextView timerTV = findViewById(R.id.timerTV);
        /*new CountDownTimer(60000, 1000){
            @Override
            public void onTick(long l) {
                Log.d("TAG", "onTick: "+String.valueOf(l));
                timerTV.setText("00:"+String.valueOf(l/1000));
            }

            @Override
            public void onFinish() {
                timerTV.setText("F");
            }
        };*/

        /*mySMSBroadCastReceiver = new MySMSBroadCastReceiver();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
        mySMSBroadCastReceiver.setOnOtpListeners(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getApplicationContext().registerReceiver(mySMSBroadCastReceiver, intentFilter);*/

        // get mobile number from phone
        //getHintPhoneNumber();
        //start SMS listner
        //smsListener();

        getEmployeeData();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: "+phoneAuthCredential);
                //signInWithPhoneAuthCredential(phoneAuthCredential);
                BackgroundTask backgroundTask = new BackgroundTask(MobileNumberVerificationActivity.this);
                backgroundTask.execute(EmployeeName, EmployeeState, EmployeeCity, EmployeeMobileNumber, EmployeePassword);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                Log.d(TAG, "onCodeSent: "+token);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                resendBtn.setEnabled(true);

            }
        };

        PhoneAuthProvider.getInstance(mAuth).verifyPhoneNumber(
                "+91 "+EmployeeMobileNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );
    }

    private void isMobileNumberVerified(boolean check){
        if(check){

        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MobileNumberVerificationActivity.this, SignUp3Activity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
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

    /*@Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onOtpReceived(String otp) {
        Toast.makeText(this, "OTP Received: "+otp, Toast.LENGTH_SHORT).show();
        otpView.setText(otp);
    }

    @Override
    public void onOtpTimeout() {
        Toast.makeText(this, "Time out, please resend", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void smsListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(this);
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(MobileNumberVerificationActivity.this, "SMS Retriever Started", Toast.LENGTH_LONG).show();
            }
        });
        mTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MobileNumberVerificationActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }*/

    /**
     * @desc This function is using hint picker to show user's phone number
     */
    /*public void getHintPhoneNumber() {
        HintRequest hintRequest =
                new HintRequest.Builder()
                        .setPhoneNumberIdentifierSupported(true)
                        .build();
        PendingIntent mIntent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
        try {
            startIntentSenderForResult(mIntent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result if we want hint number
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    credential.getId();  //<-- will need to process phone number string
                    //inputMobileNumber.setText(credential.getId());
                }
            }
        }
    }*/

    private void getEmployeeData(){
        if(sharedPreferences.contains(EmployeeNameSharedPreference)){
            EmployeeName = sharedPreferences.getString(EmployeeNameSharedPreference, "");
        }
        if(sharedPreferences.contains(EmployeeStateSharedPreference)){
            EmployeeState = sharedPreferences.getString(EmployeeStateSharedPreference, "");
        }
        if(sharedPreferences.contains(EmployeeCitySharedPreference)){
            EmployeeCity = sharedPreferences.getString(EmployeeCitySharedPreference, "");
        }
        if(sharedPreferences.contains(EmployeeMobileNumberSharedPreference)){
            EmployeeMobileNumber = sharedPreferences.getString(EmployeeMobileNumberSharedPreference, "");
            /*if(EmployeeMobileNumber.length() == 10){
                mobileNumberForOtp = "+91 "+EmployeeMobileNumber.substring(0,4)+" "+EmployeeMobileNumber.substring(4);
                Log.d(TAG, "getEmployeeData: "+mobileNumberForOtp);
            }*/
            verifyCodeTV.setText("Verify +91 "+EmployeeMobileNumber);
            String sourceString = "Waiting to automatically detect an SMS sent to "+"</br><b>+91" + EmployeeMobileNumber + "</b> ";
            verificationSentenceTV.setText(Html.fromHtml(sourceString));
        }
        if(sharedPreferences.contains(EmployeePasswordSharedPreference)){
            EmployeePassword = sharedPreferences.getString(EmployeePasswordSharedPreference, "");
        }
    }

    class BackgroundTask extends AsyncTask<String, Void, String> {
        Context ctx;
        ProgressDialog progressDialog;

        BackgroundTask(Context ctx){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Creating Profile...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String addEmployeeUrl = base_url+"add_employee.php";
            //String addEmployeeUrl = "https://duepark.000webhostapp.com/consumer/add_employee.php";
            try{
                URL url = new URL(addEmployeeUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream ops = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                String data = URLEncoder.encode("EmployeeName","UTF-8")+"="+URLEncoder.encode(params[0],"UTF-8")+"&"
                        +URLEncoder.encode("EmployeeState","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                        +URLEncoder.encode("EmployeeCity", "UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"+
                        URLEncoder.encode("EmployeeMobileNumber", "UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8")+"&"+
                        URLEncoder.encode("EmployeePassword", "UTF-8")+"="+URLEncoder.encode(params[4],"UTF-8")+"&"+
                        URLEncoder.encode("EmployeeActiveState", "UTF-8")+"="+URLEncoder.encode("true","UTF-8")+"&"+
                        URLEncoder.encode("EmployeeRole", "UTF-8")+"="+URLEncoder.encode("SuperAdmin","UTF-8") +"&"+
                        URLEncoder.encode("EmployeeEntity", "UTF-8")+"="+URLEncoder.encode("ConsumerApp", "UTF-8")+"&"+
                        URLEncoder.encode("LocationId", "UTF-8")+"="+URLEncoder.encode("0", "UTF-8");
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
            if(result.equals("exist")){
                Toast.makeText(ctx, "Parking with same mobile number already exists", Toast.LENGTH_SHORT).show();
                //onBackPressed();
            }
            else if(result.equals("NoEntityFound")){

            }
            else{
                try {
                    int employeeId = Integer.parseInt(result.trim());
                    Toast.makeText(ctx, "Parking request successfully created...", Toast.LENGTH_SHORT).show();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(ctx, WelcomeActivity.class);
                    //intent.putExtra("EmployeeId", employeeId);
                    ctx.startActivity(intent);
                    //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } catch (NumberFormatException ne) {
                    Toast.makeText(ctx, "New parking not created successfully.. Please try again later..", Toast.LENGTH_SHORT).show();
                }

                /*Toast.makeText(ctx, "New profile created successfully...", Toast.LENGTH_LONG).show();
                editor.clear();
                editor.apply();
                ctx.startActivity(new Intent(ctx, WelcomeActivity.class));*/
                //(MobileNumberVerificationActivity)overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            //else {
                //Toast.makeText(ctx, "New profile not created successfully.. Please try again later..", Toast.LENGTH_SHORT).show();
                //editor.clear();
                //editor.apply();
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //;
            //}
            progressDialog.dismiss();
        }
    }
}