package com.example.duepark_admin.Menu.Employee;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.duepark_admin.R;
import com.example.duepark_admin.Service.SessionManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddEmployeeActivity extends AppCompatActivity {

    private String base_url;
    private SessionManager sessionManager;
    private Spinner spinner;
    private EditText inputname, inputmobileNumber, inputemailId, inputaadharNumber;
    private HashMap<String, String> user;
    private String password=null, mobilenumber, emailid;
    private CircleImageView profilePic;
    private Uri pickImageUri = null;
    private static int RESULT_LOAD_IMAGE = 1;
    String a;
    int keyDel;
    private Bitmap bitmap;
    private final static String Name = "com.example.duepark.Name";
    private final static String MobileNumber = "com.example.duepark.MobileNumber";
    private final static String EmailId = "com.example.duepark.EmailId";
    private final static String Designation = "com.example.duepark.Designation";
    private final static String AadharNumber = "com.example.duepark.AadharNumber";
    private final static String Password = "com.example.duepark.Password";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        base_url = getResources().getString(R.string.base_url);
        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails();

        sharedPreferences = this.getSharedPreferences("Employee", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        inputname = findViewById(R.id.name);
        inputmobileNumber = findViewById(R.id.mobileNumber);
        inputemailId = findViewById(R.id.emailId);
        inputaadharNumber = findViewById(R.id.aadharNumber);
        inputaadharNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean flag = true;
                String eachBlock[] = inputaadharNumber.getText().toString().split(" ");
                for (int i = 0; i < eachBlock.length; i++) {
                    if (eachBlock[i].length() > 4) {
                        flag = false;
                    }
                }
                if (flag) {

                    inputaadharNumber.setOnKeyListener(new View.OnKeyListener() {

                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    if (keyDel == 0) {

                        if (((inputaadharNumber.getText().length() + 1) % 5) == 0) {

                            if (inputaadharNumber.getText().toString().split(" ").length <= 3) {
                                inputaadharNumber.setText(inputaadharNumber.getText() + " ");
                                inputaadharNumber.setSelection(inputaadharNumber.getText().length());
                            }
                        }
                        a = inputaadharNumber.getText().toString();
                    } else {
                        a = inputaadharNumber.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    inputaadharNumber.setText(a);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        profilePic = findViewById(R.id.profile_photo);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 23)
                {
                    checkAndRequestForPermission();
                }
                else
                {
                    pickImage();
                }
            }
        });

        Button hireBtn = findViewById(R.id.hireBtn);
        hireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmployee();
            }
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        spinner = findViewById(R.id.type_spinner);
        initSpinner();

        if(ActivityCompat.checkSelfPermission(AddEmployeeActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            Log.d("Duepark", "Permission is not granted, requesting");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 123);
        }else {
            Log.d("Duepark", "Permission is granted");
        }

        getEmployeeData();
    }

    public void pickImage() {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    private void cropRequest(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(Objects.requireNonNull(AddEmployeeActivity.this));
    }

    private void checkAndRequestForPermission( ) {
        if(ContextCompat.checkSelfPermission(Objects.requireNonNull(AddEmployeeActivity.this), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(AddEmployeeActivity.this), Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Toast.makeText(AddEmployeeActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }
            else
            {
                int PReqCode = 1;
                ActivityCompat.requestPermissions(AddEmployeeActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        }
        else
        {
            pickImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data){
            Uri selectedImage = data.getData();
            CropImage.activity(selectedImage)
                    .start(this);
        }
        else if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK && requestCode==RESULT_LOAD_IMAGE) {
            Uri imageUri = CropImage.getPickImageResultUri(AddEmployeeActivity.this, data);
            cropRequest(imageUri);
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                pickImageUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pickImageUri);
                    profilePic.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddEmployeeActivity.this, EmployeeActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Duepark", "Permission has been granted");

            } else {
                Log.d("Duepark", "Permission has been denied or request cancelled");
            }
        }
    }

    private void getEmployeeData(){
        if(sharedPreferences.contains(Name)){
            inputname.setText(sharedPreferences.getString(Name, ""));
        }
        if(sharedPreferences.contains(MobileNumber)){
            inputmobileNumber.setText(sharedPreferences.getString(MobileNumber, ""));
        }
        if(sharedPreferences.contains(EmailId)){
            inputemailId.setText(sharedPreferences.getString(EmailId, ""));
        }
        if(sharedPreferences.contains(AadharNumber)){
            inputaadharNumber.setText(sharedPreferences.getString(AadharNumber, ""));
        }
        if(sharedPreferences.contains(Designation)){
            String des = sharedPreferences.getString(Designation,"");
            switch (user.get(sessionManager.EmployeeRole)) {
                case "SuperAdmin":
                    if ("Admin".equals(des)) {
                        spinner.setSelection(1);
                    } else if ("Manager".equals(des)) {
                        spinner.setSelection(2);
                    } else if ("Sale".equals(des)) {
                        spinner.setSelection(3);
                    }
                    break;
                case "Admin":
                    if("Manager".equals(des)){
                        spinner.setSelection(1);
                    }else if ("Sale".equals(des)){
                        spinner.setSelection(2);
                    }
                    break;
                case "Manager":
                    if("Sale".equals(des)){
                        spinner.setSelection(1);
                    }
                    break;
            }
        }
        if(sharedPreferences.contains(Password)){
            password = sharedPreferences.getString(Password, "");
        }
        editor.clear();
        editor.apply();
    }

    private void storeEmployeeData(){
        if(inputname.getText().toString().trim()!=null){
            editor.remove(Name);
            editor.apply();
            editor.putString(Name, inputname.getText().toString().trim());
            editor.apply();
        }

        if(inputmobileNumber.getText().toString().trim()!=null){
            editor.remove(MobileNumber);
            editor.apply();
            editor.putString(MobileNumber, inputmobileNumber.getText().toString().trim());
            editor.apply();
        }

        if(inputemailId.getText().toString().trim()!=null){
            editor.remove(EmailId);
            editor.apply();
            editor.putString(EmailId, inputemailId.getText().toString().trim());
            editor.apply();
        }

        if(inputaadharNumber.getText().toString().trim()!=null){
                editor.remove(AadharNumber);
                editor.apply();
                editor.putString(AadharNumber, inputaadharNumber.getText().toString().trim());
                editor.apply();
        }
        if(spinner.getSelectedItemId() != 0){
            editor.remove(Designation);
            editor.apply();
            editor.putString(Designation, spinner.getSelectedItem().toString());
            editor.apply();
        }
        if(password!=null){
            editor.remove(Password);
            editor.apply();
            editor.putString(Password, password);
            editor.apply();
        }
    }

    private void addEmployee(){
        String name = inputname.getText().toString().trim();
        mobilenumber = inputmobileNumber.getText().toString().trim();
        emailid = inputemailId.getText().toString().trim();
        String aadharnumber = inputaadharNumber.getText().toString().trim();
        if(password==null) {
            password = generateRandomPassword();
        }

        String emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
        String mobileRegex = "^[6-9][0-9]{9}$";

        Pattern emailPattern = Pattern.compile(emailRegex);
        Pattern mobilePattern = Pattern.compile(mobileRegex);

        Matcher numberMatches = mobilePattern.matcher(mobilenumber);
        Matcher emailMatches = emailPattern.matcher(emailid);

        if(name.isEmpty()){
            fieldEmpty(inputname);
        }else if(!numberMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputmobileNumber.setError("Please enter valid mobile number.!",myIcon);
            requestFocus(inputmobileNumber);
        }else if(mobilenumber.isEmpty()){
            fieldEmpty(inputmobileNumber);
        }else if(!emailMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputemailId.setError("Please enter valid email address.!",myIcon);
            requestFocus(inputemailId);
        }else if(emailid.isEmpty()){
            fieldEmpty(inputemailId);
        }else if(aadharnumber.isEmpty()){
            fieldEmpty(inputaadharNumber);
        }else if(aadharnumber.length()<12){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputaadharNumber.setError("Please enter valid aadhar number.!",myIcon);
            requestFocus(inputaadharNumber);
        }else if(spinner.getSelectedItemId() == 0){
            Toast.makeText(AddEmployeeActivity.this, "Please select the designation..", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent passwordVerifyActivity = new Intent(this, PasswordVerificationActivity.class);
            /*Uri uri = Uri.parse("smsto:"+mobilenumber);
            Intent sendSMS = new Intent(Intent.ACTION_VIEW, uri);
            sendSMS.putExtra("sms_body", "Your can access Duepark application using "+emailid+" and "+password);
            startActivity(sendSMS);*/
            try{
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(mobilenumber,null,"Your can access Duepark application using "+emailid+" and "+password,null,null);
            }catch (Exception e){
                e.printStackTrace();
            }
            passwordVerifyActivity.putExtra("name", name);
            passwordVerifyActivity.putExtra("mobilenumber", mobilenumber);
            passwordVerifyActivity.putExtra("emailid", emailid);
            passwordVerifyActivity.putExtra("designation",spinner.getSelectedItem().toString());
            passwordVerifyActivity.putExtra("aadharnumber", aadharnumber);
            passwordVerifyActivity.putExtra("password", password);
            if(pickImageUri!=null){
                passwordVerifyActivity.putExtra("profilePic",getStringImage(bitmap));
            }else{
                passwordVerifyActivity.putExtra("profilePic", "null");
             }
            passwordVerifyActivity.putExtra("userid",user.get(sessionManager.EmployeeId));
            storeEmployeeData();
            startActivity(passwordVerifyActivity);
            finish();

            /*BackgroundClass backgroundClass = new BackgroundClass(this);
            if(pickImageUri!=null){
                //Log.d("ImageUri", pickImageUri.toString()) ;
                backgroundClass.execute("add", name, mobilenumber, emailid, spinner.getSelectedItem().toString(), aadharnumber, password, getStringImage(bitmap), user.get(sessionManager.KEY_ID));
            }else{
                //Log.d("ImageUri", "null");
                backgroundClass.execute("add", name, mobilenumber, emailid, spinner.getSelectedItem().toString(), aadharnumber, password, "null", user.get(sessionManager.KEY_ID));
            }*/
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

    private String generateRandomPassword(){
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    private void initSpinner(){
        String loginUserDesignation = user.get(sessionManager.EmployeeRole);

        List<String> type = new ArrayList<>();
        type.add(0, "Designation");
        switch (loginUserDesignation) {
            case "SuperAdmin":
                type.add("Admin");
                type.add("Manager");
                type.add("Sale");
                break;
            case "Admin":
                type.add("Manager");
                type.add("Sale");
                break;
            case "Manager":
                type.add("Sale");
                break;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, type){
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    /*class BackgroundClass extends AsyncTask<String, Void, String> {

        Context ctx;
        ProgressDialog progressDialog;

        public BackgroundClass(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(this.ctx);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Saving data...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String keyword = params[0];
            if(keyword.equals("add")){
                String add_url = "https://duepark.000webhostapp.com/add_employeeData.php";
                try{
                    URL url = new URL(add_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    Log.d("ProfilePic", params[7]);
                    String data = URLEncoder.encode("userName","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                            +URLEncoder.encode("userMobileNumber","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"
                            +URLEncoder.encode("userEmail", "UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8")+"&"+
                            URLEncoder.encode("userDesignation", "UTF-8")+"="+URLEncoder.encode(params[4],"UTF-8")+"&"+
                            URLEncoder.encode("userAdharNumber", "UTF-8")+"="+URLEncoder.encode(params[5],"UTF-8")+"&"+
                            URLEncoder.encode("userPassword", "UTF-8")+"="+URLEncoder.encode(params[6],"UTF-8")+"&"+
                            URLEncoder.encode("profilePicUri", "UTF-8")+"="+URLEncoder.encode(params[7],"UTF-8")+"&"+
                            URLEncoder.encode("userId", "UTF-8")+"="+URLEncoder.encode(params[8],"UTF-8");
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
                    Log.d("Result", result.toString());
                    return result.toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("exist")){
                Toast.makeText(ctx, "Employee Already Exists", Toast.LENGTH_SHORT).show();
                //onBackPressed();
            }
            else if(result.equals("0")){
                Toast.makeText(ctx, "Employee not add... Please try again later", Toast.LENGTH_LONG).show();
            }
            else {
                try{
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(mobilenumber,null,"Your can access Duepark application using "+emailid+" and "+password,null,null);
                }catch (Exception e){
                    Toast.makeText(ctx,"SMS failed please try again. Please provide the permission.",Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(ctx, "Employee saved Successfully", Toast.LENGTH_SHORT).show();
                Intent activatedEmployeeActivity = new Intent(ctx, ActivatedEmployeeActivity.class);
                activatedEmployeeActivity.putExtra("userid", result);
                startActivity(activatedEmployeeActivity);
                finish();
            }
            progressDialog.dismiss();
        }
    }*/
}
