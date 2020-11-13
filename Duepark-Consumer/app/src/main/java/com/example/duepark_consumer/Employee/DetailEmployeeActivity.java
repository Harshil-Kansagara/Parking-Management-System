package com.example.duepark_consumer.Employee;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.Model.Employee;
import com.example.duepark_consumer.R;
import com.example.duepark_consumer.Valet.ValetHomeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailEmployeeActivity extends AppCompatActivity implements View.OnClickListener {

    private String base_url;
    private static final String TAG = "DetailEmployeeActivity";
    private SessionManagerHelper sessionManagerHelper;
    private TextView employeeIdTV, employeeNameTV, assignLocationTV, employeeEmailIdTV, employeeMobileNumberTV, employeePasswordTV, employeeAdharNumberTV, employeeRoleTV, vehicleTypeTV;
    private Button employeeNameEditBtn, assignLocationEditBtn, employeeEmailIdEditBtn, employeeMobileNumberEditBtn, /*employeeAdharNumberEditBtn,*/ employeePasswordEditBtn, vehicleTypeEditBtn, activationStateBtn;
    private CircleImageView employeeProfilePic;
    private FloatingActionButton saveBtn;
    private Employee employee;
    private ImageView backBtn;
    private String employeeId, locationName, locationId, vehicleType, isEmployeeActive;
    private Button updateBtn;
    private static int RESULT_LOAD_IMAGE = 1;
    private Uri pickImageUri = null;
    private Bitmap bitmap;
    private RelativeLayout assignLocationRL, vehicleTypeRL, employeeProfilePicRL, employeePasswordRL, employeeActivationStateRL;
    private View assignLocationV, passwordV, /*employeeActivationStateV,*/ vehicleTypeV;
    private boolean isValet = false;
    private EditText employeeEmailIdET, employeeMobileNumberET;
    private RadioGroup vehicleTypeRadioGrp;
    private RadioButton carRadioBtn, bikeRadioBtn, bothRadioBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_employee);

        base_url = getResources().getString(R.string.base_url);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            employeeId = bundle.getString("EmployeeId");
            locationName = bundle.getString("LocationName");
            locationId = bundle.getString("LocationId");
            isEmployeeActive = bundle.getString("IsEmployeeActive");
            isValet = bundle.getBoolean("IsValet");
        }

        sessionManagerHelper = new SessionManagerHelper(this);
        HashMap<String, String> employeeDetail = sessionManagerHelper.getEmployeeDetails();

        employeeProfilePicRL = findViewById(R.id.employeeProfilePicRL);
        assignLocationV = findViewById(R.id.assignLocationV);
        assignLocationRL = findViewById(R.id.assignLocationRL);
        employeeProfilePic = findViewById(R.id.employeeProfilePic);
        employeeIdTV = findViewById(R.id.employeeIdTV);
        employeeNameTV = findViewById(R.id.employeeNameTV);
        assignLocationTV = findViewById(R.id.assignLocationTV);
        employeeEmailIdTV = findViewById(R.id.employeeEmailIdTV);
        employeeMobileNumberTV = findViewById(R.id.employeeMobileNumberTV);
        employeePasswordRL = findViewById(R.id.employeePasswordRL);
        employeePasswordTV = findViewById(R.id.employeePasswordTV);
        passwordV = findViewById(R.id.passwordV);
        employeeAdharNumberTV = findViewById(R.id.employeeAdharNumberTV);
        employeeActivationStateRL = findViewById(R.id.employeeActivationStateRL);
        //employeeActivationStateV = findViewById(R.id.employeeActivationStateV);
        employeeRoleTV = findViewById(R.id.employeeRoleTV);
        vehicleTypeRL = findViewById(R.id.vehicleTypeRL);
        vehicleTypeTV = findViewById(R.id.vehicleTypeTV);
        vehicleTypeV = findViewById(R.id.vehicleTypeV);
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

        employeeEmailIdET = findViewById(R.id.employeeEmailIdET);
        employeeMobileNumberET = findViewById(R.id.employeeMobileNumberET);

        updateBtn = findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(this);

       /* employeeNameEditBtn = findViewById(R.id.employeeNameEditBtn);
        employeeNameEditBtn.setOnClickListener(this);*/

        assignLocationEditBtn = findViewById(R.id.assignLocationEditBtn);
        assignLocationEditBtn.setOnClickListener(this);

        employeeEmailIdEditBtn = findViewById(R.id.employeeEmailIdEditBtn);
        employeeEmailIdEditBtn.setOnClickListener(this);

        employeeMobileNumberEditBtn = findViewById(R.id.employeeMobileNumberEditBtn);
        employeeMobileNumberEditBtn.setOnClickListener(this);

       /* employeeAdharNumberEditBtn = findViewById(R.id.employeeAdharNumberEditBtn);
        employeeAdharNumberEditBtn.setOnClickListener(this);*/

        employeePasswordEditBtn = findViewById(R.id.employeePasswordEditBtn);
        employeePasswordEditBtn.setOnClickListener(this);

        activationStateBtn = findViewById(R.id.activationStateBtn);
        activationStateBtn.setOnClickListener(this);

        vehicleTypeEditBtn = findViewById(R.id.vehicleTypeEditBtn);
        vehicleTypeEditBtn.setOnClickListener(this);

        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        if(employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("Manager") || isValet){
            assignLocationEditBtn.setVisibility(View.GONE);
        }

        if(employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("Valet")){
            employeeProfilePicRL.setVisibility(View.GONE);
            vehicleTypeEditBtn.setVisibility(View.GONE);
            vehicleTypeV.setVisibility(View.GONE);
            employeePasswordRL.setVisibility(View.VISIBLE);
            employeePasswordEditBtn.setText("Reset");
            passwordV.setVisibility(View.VISIBLE);
            activationStateBtn.setVisibility(View.GONE);
            //employeeNameEditBtn.setVisibility(View.GONE);
        }

        if(employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("Admin") || employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("SuperAdmin")){
            employeePasswordRL.setVisibility(View.VISIBLE);
            passwordV.setVisibility(View.VISIBLE);
            employeeActivationStateRL.setVisibility(View.VISIBLE);
            //employeeActivationStateV.setVisibility(View.VISIBLE);
        }

        if(isEmployeeActive.equals("0")){
            activationStateBtn.setText("Activate Employee");
            employeeEmailIdEditBtn.setVisibility(View.GONE);
            assignLocationEditBtn.setVisibility(View.GONE);
            employeeEmailIdEditBtn.setVisibility(View.GONE);
            employeeMobileNumberEditBtn.setVisibility(View.GONE);
            employeePasswordEditBtn.setVisibility(View.GONE);
            vehicleTypeEditBtn.setVisibility(View.GONE);
            updateBtn.setVisibility(View.GONE);
        }
        else{
            activationStateBtn.setText("Deactivate Employee");
        }

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("get", employeeId);
    }

    @Override
    public void onBackPressed() {
        if(isValet){
            Intent i = new Intent(DetailEmployeeActivity.this, ValetHomeActivity.class);
            i.putExtra("IsValetMenu", true);
            startActivity(i);
        }else {
            startActivity(new Intent(DetailEmployeeActivity.this, EmployeeActivity.class));
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onClick(View view) {
        if(view == employeeEmailIdEditBtn){
           if(employeeEmailIdTV.getVisibility() == View.VISIBLE){
                employeeEmailIdTV.setVisibility(View.INVISIBLE);
                employeeEmailIdET.setVisibility(View.VISIBLE);
                employeeEmailIdET.setText(employee.getEmployeeEmailId());
           }
           else {//if(employeeEmailIdTV.getVisibility() == View.INVISIBLE){
                employeeEmailIdTV.setVisibility(View.VISIBLE);
                employeeEmailIdET.setVisibility(View.INVISIBLE);
                employeeEmailIdTV.setText(employeeEmailIdET.getText().toString().trim());
           }
        }
        else if(view == employeeMobileNumberEditBtn){
            if(employeeMobileNumberTV.getVisibility() == View.VISIBLE){
                employeeMobileNumberTV.setVisibility(View.INVISIBLE);
                employeeMobileNumberET.setVisibility(View.VISIBLE);
                employeeMobileNumberET.setText(employee.getEmployeeMobileNumber());
            }
            else {//if(employeeMobileNumberTV.getVisibility() == View.INVISIBLE){
                employeeMobileNumberTV.setVisibility(View.VISIBLE);
                employeeMobileNumberET.setVisibility(View.INVISIBLE);
                employeeMobileNumberTV.setText(employeeMobileNumberET.getText().toString().trim());
            }
        }
        else if(view == employeePasswordEditBtn){
            Intent i = new Intent(view.getContext(), EditEmployeeDetailActivity.class);
            i.putExtra("IsEmployeePassword", true);
            i.putExtra("EmployeeId", employeeId);
            i.putExtra("IsValet", isValet);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        else if(view == assignLocationEditBtn){
            Intent i = new Intent(this, LocationListActivity.class);
            i.putExtra("EmployeeId", employeeId);
            i.putExtra("LocationName", employee.getLocationName());
            i.putExtra("IsEmployeeActive", isEmployeeActive);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        else if(view == vehicleTypeEditBtn){
            if(vehicleTypeTV.getVisibility() == View.VISIBLE){
                vehicleTypeTV.setVisibility(View.GONE);
                vehicleTypeRadioGrp.setVisibility(View.VISIBLE);
                if(vehicleType.equals("Car")){
                    carRadioBtn.setChecked(true);
                }
                else if(vehicleType.equals("Bike")){
                    bikeRadioBtn.setChecked(true);
                }
                else{
                    bothRadioBtn.setChecked(true);
                }
            }
            else {
                vehicleTypeRadioGrp.setVisibility(View.GONE);
                vehicleTypeTV.setVisibility(View.GONE);
                if (carRadioBtn.isChecked()) {
                    vehicleTypeTV.setText("Car");
                } else if (bikeRadioBtn.isChecked()) {
                    vehicleTypeTV.setText("Bike");
                } else {
                    vehicleTypeTV.setText("Both");
                }
            }
        }
        else if(view == activationStateBtn){
            BackgroundTask backgroundTask = new BackgroundTask(this);
            if(isEmployeeActive.equals("0")){
                backgroundTask.execute("activation",employeeId, "true");
            }
            else{
                backgroundTask.execute("activation",employeeId, "false");
            }
        }
        else if(view == updateBtn){
            if(Build.VERSION.SDK_INT >= 23)
            {
                checkAndRequestForPermission();
            }
            else
            {
                pickImage();
            }
        }
        else if(view == saveBtn){
         /*   if(employeeEmailIdET.getText().toString().trim().equals(employee.getEmployeeEmailId()) &&
                    employeeMobileNumberET.getText().toString().trim().equals(employee.getEmployeeMobileNumber()) &&
                    vehicleType.equals(employee.getVehicleType()) &&
                    assignLocationTV.getText().toString().equals(employee.getLocationName())){
                onBackPressed();
            }
            else{
                updateEmployeeData();
            }*/
            updateEmployeeData();
            //onBackPressed();
        }
        else if(view == backBtn){
            onBackPressed();
        }
    }

    // For image
    public void pickImage() {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    private void cropRequest(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(Objects.requireNonNull(DetailEmployeeActivity.this));
    }

    private void checkAndRequestForPermission( ) {
        if(ContextCompat.checkSelfPermission(Objects.requireNonNull(DetailEmployeeActivity.this), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(DetailEmployeeActivity.this), Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Toast.makeText(DetailEmployeeActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }
            else
            {
                int PReqCode = 1;
                ActivityCompat.requestPermissions(DetailEmployeeActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        }
        else
        {
            pickImage();
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
            Uri imageUri = CropImage.getPickImageResultUri(DetailEmployeeActivity.this, data);
            cropRequest(imageUri);
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                pickImageUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pickImageUri);
                    employeeProfilePic.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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

    private void updateEmployeeData(){
        String employeeMobileNumber = employeeMobileNumberET.getText().toString().trim();
        String employeeEmailId = employeeEmailIdET.getText().toString().trim();
        String employeePassword = employee.getEmployeePassword();
        String employeeAdharNumber = employeeAdharNumberTV.getText().toString().trim();
        String profilePicUri = "null";
        String newLocationId = "null";

        String emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
        String mobileRegex = "^[6-9][0-9]{9}$";

        Pattern emailPattern = Pattern.compile(emailRegex);
        Pattern mobilePattern = Pattern.compile(mobileRegex);

        Matcher mobileNumberMatches = mobilePattern.matcher(employeeMobileNumber);
        Matcher emailMatches = emailPattern.matcher(employeeEmailId);

        if(employeeMobileNumber.isEmpty()){
            fieldEmpty(employeeMobileNumberET);
        }
        else if(employeeEmailId.isEmpty()){
            fieldEmpty(employeeEmailIdET);
        }
        else if(!mobileNumberMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            employeeMobileNumberET.setError("Please enter valid mobile number.!",myIcon);
            requestFocus(employeeMobileNumberET);
        }
        else if(!emailMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            employeeEmailIdET.setError("Please enter valid email address.!",myIcon);
            requestFocus(employeeEmailIdET);
        }
        else{
            if(pickImageUri != null) {
                profilePicUri = getStringImage(bitmap);
            }
            if(locationId != null){
                newLocationId = locationId;
            }
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute("update",employeeId, employeeMobileNumber, employeeEmailId, employeeAdharNumber, employeePassword, newLocationId, profilePicUri, vehicleType);
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

    class BackgroundTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        BackgroundTask(Context ctx){
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if(params[0].equals("get")) {
                progressDialog.setMessage("Getting employee detail...");
                String getEmployeeDetail_url = base_url+"get_employee.php?EmployeeId=" + params[1] + "&Entity=ConsumerApp";
                //String getEmployeeDetail_url = "https://duepark.000webhostapp.com/consumer/get_employee.php?EmployeeId=" + params[1] + "&Entity=ConsumerApp";
                try {
                    URL url = new URL(getEmployeeDetail_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }

                    httpURLConnection.disconnect();

                    String result = stringBuilder.toString().trim();
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    while (count < jsonArray.length()) {
                        JSONObject jo = jsonArray.getJSONObject(count);
                        employee = new Employee(jo.getString("id"), jo.getString("GeneratedEmployeeId"), jo.getString("EmployeeName"), jo.getString("EmployeeMobileNumber"),
                                jo.getString("EmployeeEmailId"), jo.getString("EmployeeAdharNumber"), jo.getString("EmployeePassword"), jo.getString("EmployeeProfilePic"),
                                jo.getString("EmployeeRole"), jo.getString("LocationName"), jo.getString("VehicleType"));
                        count++;
                    }
                    Log.d(TAG, "doInBackground: " + result);
                    return "get";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (params[0].equals("update")){
                progressDialog.setMessage("Updating employee detail...");
                String updateEmployee_url = base_url+"update_employee.php";
                ///String updateEmployee_url = "https://duepark.000webhostapp.com/consumer/update_employee.php";
                try {
                    URL url = new URL(updateEmployee_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                    String data = URLEncoder.encode("EmployeeId", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                            URLEncoder.encode("EmployeeMobileNumber", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                            URLEncoder.encode("EmployeeEmailId", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&" +
                            URLEncoder.encode("EmployeeAdharNumber", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8") + "&" +
                            URLEncoder.encode("EmployeePassword", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8") + "&" +
                            URLEncoder.encode("LocationId", "UTF-8") + "=" + URLEncoder.encode(params[6], "UTF-8") + "&" +
                            URLEncoder.encode("EmployeeProfilePic", "UTF-8") + "=" + URLEncoder.encode(params[7], "UTF-8")+"&"+
                            URLEncoder.encode("VehicleType", "UTF-8") + "=" + URLEncoder.encode(params[8], "UTF-8");
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
                    return result.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                if(isEmployeeActive.equals("0")){
                    progressDialog.setMessage("Activating employee...");
                }
                else{
                    progressDialog.setMessage("Deactivating employee...");
                }
                String setEmployeeActivationState_url = base_url+"set_employeeActivationState.php";
                try{
                    URL url = new URL(setEmployeeActivationState_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("EmployeeId","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                            +URLEncoder.encode("EmployeeActiveState","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8");
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
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Add profile pic update or add
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("get")) {
                if(!employee.getEmployeeRole().equals("Admin")){
                    assignLocationRL.setVisibility(View.VISIBLE);
                    assignLocationV.setVisibility(View.VISIBLE);
                }
                DecimalFormat df = new DecimalFormat("000");
                String generatedEmployeeId = null;
                if (employee.getEmployeeRole().equals("Admin")) {
                    generatedEmployeeId = "A" + df.format(Integer.parseInt(employee.getGeneratedEmployeeId()));
                }
                if (employee.getEmployeeRole().equals("Manager")) {
                    generatedEmployeeId = "M" + df.format(Integer.parseInt(employee.getGeneratedEmployeeId()));
                }
                if (employee.getEmployeeRole().equals("Valet")) {
                    generatedEmployeeId = "V" + df.format(Integer.parseInt(employee.getGeneratedEmployeeId()));
                }
                employeeIdTV.setText("Employee Id : "+generatedEmployeeId);
                employeeNameTV.setText(employee.getEmployeeName());
                employeeMobileNumberTV.setText(employee.getEmployeeMobileNumber());
                employeeEmailIdTV.setText(employee.getEmployeeEmailId());
                StringBuilder password = new StringBuilder();
                for(int i=0;i<employee.getEmployeePassword().length();i++){
                    password.append("*");
                }
                employeePasswordTV.setText(password.toString());
                employeeRoleTV.setText(employee.getEmployeeRole());
                employeeAdharNumberTV.setText(employee.getEmployeeAdharNumber());
                employeeMobileNumberET.setText(employee.getEmployeeMobileNumber());
                employeeEmailIdET.setText(employee.getEmployeeEmailId());
                if(locationName == null){
                    assignLocationTV.setText(employee.getLocationName());
                }
                else{
                    if(!locationName.equals(employee.getLocationName())){
                        assignLocationTV.setText(locationName);
                    }
                    else{
                        assignLocationTV.setText(employee.getLocationName());
                    }
                }
                if(employee.getVehicleType().equals("null")){
                    vehicleTypeRL.setVisibility(View.GONE);
                    vehicleTypeV.setVisibility(View.GONE);
                    vehicleType = employee.getVehicleType();
                }
                else{
                    vehicleTypeTV.setText(employee.getVehicleType());
                    vehicleType = employee.getVehicleType();
                }
                if (!employee.getEmployeeProfilePic().equals("null")) {
                    //String baseUrl = "https://duepark.000webhostapp.com/consumer/consumer_profilePic/";
                    String profilePicUrl = base_url +"profilePic/"+ employee.getEmployeeProfilePic() + ".png";
                    Picasso.get().load(profilePicUrl).into(employeeProfilePic);
                    updateBtn.setText("Change");
                }
                else{
                    updateBtn.setText("Add");
                }
            }
            else if(result.equals("UpdateSuccessfully")){
                Toast.makeText(DetailEmployeeActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else if(result.equals("Activate")){
                Toast.makeText(DetailEmployeeActivity.this, "Employee activate successfully...", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else if(result.equals("Deactivate")){
                Toast.makeText(DetailEmployeeActivity.this, "Employee deactivate successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else{
                Toast.makeText(DetailEmployeeActivity.this, result, Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }
}