package com.example.duepark_consumer.Employee;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Adapter.AssignLocationListAdapter;
import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.Model.LocationList;
import com.example.duepark_consumer.Model.SelectedLocation;
import com.example.duepark_consumer.R;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddEmployeeActivity extends AppCompatActivity {

    private String base_url;
    private static final String TAG = "AddEmployeeActivity";
    private TextView assignLocation, selectVehicleType;
    private EditText inputEmployeeName, inputEmployeeMobileNumber, inputEmployeeEmailId, inputEmployeeAdharNumber;
    private SessionManagerHelper sessionManagerHelper;
    private Spinner employeeDesignationSpinner;
    private HashMap<String, String> employeeDetail;
    private String a, vehicleType;
    private int keyDel;
    private CircleImageView profilePic;
    private static int RESULT_LOAD_IMAGE = 1;
    private Uri pickImageUri = null;
    private Bitmap bitmap;
    private RecyclerView recyclerView;
    private ArrayList<LocationList> locationLists;
    private AssignLocationListAdapter assignLocationListAdapter;
    private SelectedLocation selectedLocation;
    private String selected_location = "";
    private RadioGroup vehicleRadioGrp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        base_url = getResources().getString(R.string.base_url);
        selectedLocation = new SelectedLocation();
        sessionManagerHelper = new SessionManagerHelper(this);
        employeeDetail = sessionManagerHelper.getEmployeeDetails();

        locationLists = new ArrayList<>();
        assignLocationListAdapter = new AssignLocationListAdapter(locationLists, this, selectedLocation);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(assignLocationListAdapter);

        assignLocation = findViewById(R.id.assignLocation);
        inputEmployeeName = findViewById(R.id.inputEmployeeName);
        inputEmployeeMobileNumber = findViewById(R.id.inputEmployeeMobileNumber);
        inputEmployeeEmailId = findViewById(R.id.inputEmployeeEmailId);
        inputEmployeeAdharNumber = findViewById(R.id.inputEmployeeAdharNumber);
        inputEmployeeAdharNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                boolean flag = true;
                String eachBlock[] = inputEmployeeAdharNumber.getText().toString().split(" ");
                for (int i = 0; i < eachBlock.length; i++) {
                    if (eachBlock[i].length() > 4) {
                        flag = false;
                    }
                }
                if (flag) {

                    inputEmployeeAdharNumber.setOnKeyListener(new View.OnKeyListener() {

                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    if (keyDel == 0) {

                        if (((inputEmployeeAdharNumber.getText().length() + 1) % 5) == 0) {

                            if (inputEmployeeAdharNumber.getText().toString().split(" ").length <= 3) {
                                inputEmployeeAdharNumber.setText(inputEmployeeAdharNumber.getText() + " ");
                                inputEmployeeAdharNumber.setSelection(inputEmployeeAdharNumber.getText().length());
                            }
                        }
                        a = inputEmployeeAdharNumber.getText().toString();
                    } else {
                        a = inputEmployeeAdharNumber.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    inputEmployeeAdharNumber.setText(a);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        selectVehicleType = findViewById(R.id.selectVehicleType);
        vehicleRadioGrp = findViewById(R.id.vehicleRadioGrp);

        vehicleRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
        employeeDesignationSpinner = findViewById(R.id.employeeDesignationSpinner);
        initSpinner();
        employeeDesignationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getSelectedItem().toString().equals("Admin")){
                    recyclerView.setVisibility(View.GONE);
                    assignLocation.setVisibility(View.GONE);
                    vehicleType = "null";
                }
                else if(adapterView.getSelectedItem().toString().equals("Manager")){
                    recyclerView.setVisibility(View.VISIBLE);
                    assignLocation.setVisibility(View.VISIBLE);
                    vehicleRadioGrp.setVisibility(View.GONE);
                    selectVehicleType.setVisibility(View.GONE);
                    vehicleType = "null";
                }
                else{
                    recyclerView.setVisibility(View.VISIBLE);
                    assignLocation.setVisibility(View.VISIBLE);
                    vehicleRadioGrp.setVisibility(View.VISIBLE);
                    selectVehicleType.setVisibility(View.VISIBLE);
                    vehicleType = "Car";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        profilePic = findViewById(R.id.profilePic);
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

        Button hireEmployeeBtn = findViewById(R.id.hireEmployeeBtn);
        hireEmployeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEmployee();
            }
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("get");
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, EmployeeActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Duepark", "Permission has been granted");

            } else {
                Log.d("Duepark", "Permission has been denied or request cancelled");
            }
        }
    }

    private void initSpinner(){
        String employeeRole = employeeDetail.get(SessionManagerHelper.EmployeeRole);

        List<String> type = new ArrayList<>();
        //type.add(0, "-- Select Designation --");
        assert employeeRole != null;
        switch (employeeRole){
            case "SuperAdmin":
                type.add("Admin");
                type.add("Manager");
                type.add("Valet");
                break;
            case "Admin":
                type.add("Manager");
                type.add("Valet");
                break;
            case "Manager":
                type.add("Valet");
                break;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, type); /*{
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };*/
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        employeeDesignationSpinner.setAdapter(dataAdapter);
    }

    private void createEmployee(){
        String employeeName = inputEmployeeName.getText().toString().trim();
        String employeeMobileNumber = inputEmployeeMobileNumber.getText().toString().trim();
        String employeeEmailId = inputEmployeeEmailId.getText().toString().trim();
        String employeeAdharNumber = inputEmployeeAdharNumber.getText().toString().trim();
        String profilePicUri = "null";
        String employeeDesignation = employeeDesignationSpinner.getSelectedItem().toString();

        String emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
        String mobileRegex = "^[6-9][0-9]{9}$";

        Pattern emailPattern = Pattern.compile(emailRegex);
        Pattern mobilePattern = Pattern.compile(mobileRegex);

        Matcher mobileNumberMatches = mobilePattern.matcher(employeeMobileNumber);
        Matcher emailMatches = emailPattern.matcher(employeeEmailId);

        if(employeeName.isEmpty()){
            fieldEmpty(inputEmployeeName);
        }
        else if(employeeMobileNumber.isEmpty()){
            fieldEmpty(inputEmployeeMobileNumber);
        }
        else if(employeeEmailId.isEmpty()){
            fieldEmpty(inputEmployeeEmailId);
        }
        else if(employeeAdharNumber.isEmpty()){
            fieldEmpty(inputEmployeeAdharNumber);
        }
        else if(employeeAdharNumber.length()<12){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputEmployeeAdharNumber.setError("Please enter valid aadhar number.!",myIcon);
            requestFocus(inputEmployeeAdharNumber);
        }
        else if(!mobileNumberMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputEmployeeMobileNumber.setError("Please enter valid mobile number.!",myIcon);
            requestFocus(inputEmployeeMobileNumber);
        }
        else if(!emailMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputEmployeeEmailId.setError("Please enter valid email address.!",myIcon);
            requestFocus(inputEmployeeEmailId);
        }
        /*else if(employeeDesignationSpinner.getSelectedItemId() == 0){
            Toast.makeText(AddEmployeeActivity.this, "Please select the designation..", Toast.LENGTH_SHORT).show();
        }*/
        else{
            if(pickImageUri != null) {
                profilePicUri = getStringImage(bitmap);
            }
            String password = indianDuepark();
            BackgroundTask backgroundTask = new BackgroundTask(this);
            if(employeeDesignation.equals("Manager") || employeeDesignation.equals("Valet")) {
                // Handle when no radio button is selected
                if(!selected_location.equals("0")) {
                    selected_location = selectedLocation.getId();
                }
                if (selected_location.isEmpty()) {
                    Toast.makeText(AddEmployeeActivity.this, "Please assign location to the employee..", Toast.LENGTH_SHORT).show();
                }
                else{
                    backgroundTask.execute("add", employeeName, employeeMobileNumber, employeeEmailId, employeeAdharNumber, password, profilePicUri, employeeDesignationSpinner.getSelectedItem().toString(), vehicleType, selected_location);
                }
            }
            else {
                backgroundTask.execute("add", employeeName, employeeMobileNumber, employeeEmailId, employeeAdharNumber, password, profilePicUri, employeeDesignationSpinner.getSelectedItem().toString(), vehicleType, "0");
            }
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

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private String indianDuepark(){//generateRandomPassword(){
        /*String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }*/
        return "indianDuepark";//sb.toString();
    }

    class BackgroundTask extends AsyncTask<String, LocationList, String>{
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
                progressDialog.setMessage("Getting location list...");
                String activeLocationList_url = base_url+"get_activeLocationList.php?ParkingId="+employeeDetail.get(SessionManagerHelper.ParkingId)+"&EmployeeId="+employeeDetail.get(SessionManagerHelper.EmployeeId)+"&EmployeeRole="+employeeDetail.get(SessionManagerHelper.EmployeeRole);
                //String activeLocationList_url = "https://duepark.000webhostapp.com/consumer/get_activeLocationList.php?ParkingId="+employeeDetail.get(SessionManagerHelper.ParkingId)+"&EmployeeId="+employeeDetail.get(SessionManagerHelper.EmployeeId)+"&EmployeeRole="+employeeDetail.get(SessionManagerHelper.EmployeeRole);
                try{
                    URL url = new URL(activeLocationList_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while((line = bufferedReader.readLine())!=null){
                        stringBuilder.append(line+"\n");
                    }

                    httpURLConnection.disconnect();

                    String result = stringBuilder.toString().trim();

                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    locationLists.clear();
                    if(jsonArray.length()>0) {
                        while (count < jsonArray.length()) {
                            JSONObject jo = jsonArray.getJSONObject(count);
                            count++;
                            LocationList locationList = new LocationList(jo.getString("id"), jo.getString("LocationName"), jo.getString("GeneratedLocationId"),
                                    jo.getString("LocationActiveState"), jo.getString("GeneratedParkingId"), jo.getString("ParkingAcronym"));
                            publishProgress(locationList);
                        }
                    }
                    else{
                        selected_location = "0";
                    }
                    Log.d(TAG, "doInBackground: "+result);
                    return "get";
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(params[0].equals("add")) {
                progressDialog.setMessage("Creating Employee...");
                String addEmployee_url = base_url+"add_employee.php";
                //String addEmployee_url = "https://duepark.000webhostapp.com/consumer/add_employee.php";
                try {
                    URL url = new URL(addEmployee_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                    String data = URLEncoder.encode("EmployeeName", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                            URLEncoder.encode("EmployeeMobileNumber", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                            URLEncoder.encode("EmployeeEmailId", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&" +
                            URLEncoder.encode("EmployeeAdharNumber", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8") + "&" +
                            URLEncoder.encode("EmployeePassword", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8") + "&" +
                            URLEncoder.encode("EmployeeProfilePic", "UTF-8") + "=" + URLEncoder.encode(params[6], "UTF-8") + "&" +
                            URLEncoder.encode("EmployeeRole", "UTF-8") + "=" + URLEncoder.encode(params[7], "UTF-8") + "&" +
                            URLEncoder.encode("VehicleType", "UTF-8") + "=" + URLEncoder.encode(params[8], "UTF-8") + "&" +
                            URLEncoder.encode("LocationId", "UTF-8") + "=" + URLEncoder.encode(params[9], "UTF-8") + "&" +
                            URLEncoder.encode("EmployeeEntity", "UTF-8") + "=" + URLEncoder.encode("ConsumerApp", "UTF-8") + "&" +
                            URLEncoder.encode("EmployeeActiveState", "UTF-8") + "=" + URLEncoder.encode("true", "UTF-8") + "&" +
                            URLEncoder.encode("ParkingId", "UTF-8") + "=" + URLEncoder.encode(employeeDetail.get(SessionManagerHelper.ParkingId)) + "&" +
                            URLEncoder.encode("LoggedInEmployeeId", "UTF-8") + "=" + URLEncoder.encode(employeeDetail.get(SessionManagerHelper.EmployeeId));
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
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(LocationList... values) {
            locationLists.add(values[0]);
            assignLocationListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String result) {
            if(selected_location.equals("0")){
                Toast.makeText(ctx, "No location found...", Toast.LENGTH_SHORT).show();
            }
            if(!result.equals("get")) {
                try {
                    int employeeId = Integer.parseInt(result);
                    Toast.makeText(ctx, "Employee create successfully..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ctx, ActivatedEmployeeActivity.class);
                    intent.putExtra("EmployeeId", employeeId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } catch (NumberFormatException ne) {
                    Toast.makeText(ctx, "Employee already exists...", Toast.LENGTH_SHORT).show();
                }
            }
            /*if(result.equals("EmployeeAdded")){
                Toast.makeText(ctx, "Employee create successfully..", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
            }*/
            progressDialog.dismiss();
        }
    }
}