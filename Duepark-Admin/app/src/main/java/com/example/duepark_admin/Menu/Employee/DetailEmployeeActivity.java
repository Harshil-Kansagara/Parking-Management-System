package com.example.duepark_admin.Menu.Employee;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_admin.HomeActivity;
import com.example.duepark_admin.R;
import com.example.duepark_admin.Service.SessionManager;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailEmployeeActivity extends AppCompatActivity {

    private String base_url;
    private String userid, username, back, generatedEmployeeId = null;
    private TextView employeeName, employeeId, employeeIdTV;
    private EditText inputmobilenumber, inputemailid, inputaadharnumber;
    private Spinner spinner;
    private HashMap<String, String> user;
    private SessionManager sessionManager;
    private Button getParkingBtn;
    private CircleImageView profilePic;
    private Uri pickImageUri = null;
    private static int RESULT_LOAD_IMAGE = 1;
    private Bitmap bitmap;
    String a;
    int keyDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_employee);

        base_url = getResources().getString(R.string.base_url);
        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            userid = bundle.getString("userid");
            username = bundle.getString("username");
            generatedEmployeeId = bundle.getString("generatedemployeeid");
            back = bundle.getString("back");
        }

        employeeName = findViewById(R.id.employeeName);
        employeeId = findViewById(R.id.employeeId);
        employeeIdTV = findViewById(R.id.employeeIdTV);
        //inputname = findViewById(R.id.name);
        inputmobilenumber = findViewById(R.id.mobileNumber);
        inputemailid = findViewById(R.id.emailId);
        inputaadharnumber = findViewById(R.id.aadharNumber);
        inputaadharnumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean flag = true;
                String eachBlock[] = inputaadharnumber.getText().toString().split(" ");
                for (int i = 0; i < eachBlock.length; i++) {
                    if (eachBlock[i].length() > 4) {
                        flag = false;
                    }
                }
                if (flag) {

                    inputaadharnumber.setOnKeyListener(new View.OnKeyListener() {

                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    if (keyDel == 0) {

                        if (((inputaadharnumber.getText().length() + 1) % 5) == 0) {

                            if (inputaadharnumber.getText().toString().split(" ").length <= 3) {
                                inputaadharnumber.setText(inputaadharnumber.getText() + " ");
                                inputaadharnumber.setSelection(inputaadharnumber.getText().length());
                            }
                        }
                        a = inputaadharnumber.getText().toString();
                    } else {
                        a = inputaadharnumber.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    inputaadharnumber.setText(a);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        spinner =findViewById(R.id.type_spinner);
        initSpinner();

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        Button deleteBtn = findViewById(R.id.deleteBtn);
        if(!Objects.equals(user.get(sessionManager.EmployeeRole), "SuperAdmin") && !Objects.equals(user.get(sessionManager.EmployeeRole),"Admin")){
            deleteBtn.setVisibility(View.GONE);
        }
        else{
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BackgroundTask backgroundTask = new BackgroundTask(DetailEmployeeActivity.this);
                    backgroundTask.execute("delete", userid);
                }
            });
        }
        getParkingBtn = findViewById(R.id.getParkingBtn);
        if(userid.equals(user.get(sessionManager.EmployeeId))){
            getParkingBtn.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
        }
        else {
            getParkingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent parkingListActivity = new Intent(DetailEmployeeActivity.this, ParkingListActiveEmployeeActivity.class);
                    parkingListActivity.putExtra("userid", userid);
                    parkingListActivity.putExtra("username", username);
                    parkingListActivity.putExtra("designation", spinner.getSelectedItem().toString());
                    startActivity(parkingListActivity);
                    finish();
                }
            });
        }

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

        setUserData();
    }

    public void pickImage()
    {
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

    private void checkAndRequestForPermission( )
    {
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
                    profilePic.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveData(){
        //String name  = inputname.getText().toString().trim();
        String mobilenumber = inputmobilenumber.getText().toString().trim();
        String emailid = inputemailid.getText().toString().trim();
        String aadharnumber = inputaadharnumber.getText().toString().trim();

        String emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
        String mobileRegex = "^[6-9][0-9]{9}$";

        Pattern emailPattern = Pattern.compile(emailRegex);
        Pattern mobilePattern = Pattern.compile(mobileRegex);

        Matcher numberMatches = mobilePattern.matcher(mobilenumber);
        Matcher emailMatches = emailPattern.matcher(emailid);

        /*if(name.isEmpty()){
            fieldEmpty(inputname);
        }else*/ if(!numberMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputmobilenumber.setError("Please enter valid mobile number.!",myIcon);
            requestFocus(inputmobilenumber);
        }else if(mobilenumber.isEmpty()){
            fieldEmpty(inputmobilenumber);
        }else if(!emailMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputemailid.setError("Please enter valid email address.!",myIcon);
            requestFocus(inputemailid);
        }else if(emailid.isEmpty()){
            fieldEmpty(inputemailid);
        }else if(aadharnumber.isEmpty()){
            fieldEmpty(inputaadharnumber);
        }else if(aadharnumber.length()<12){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            inputaadharnumber.setError("Please enter valid aadhar number.!",myIcon);
            requestFocus(inputaadharnumber);
        }else if(!userid.equals(user.get(sessionManager.EmployeeId))) {
            if (spinner.getSelectedItemId() == 0) {
                Toast.makeText(this, "Please select the designation..", Toast.LENGTH_SHORT).show();
            }
            else{
                BackgroundTask backgroundTask = new BackgroundTask(this);
                if(userid.equals(user.get(sessionManager.EmployeeId))){
                    if(pickImageUri!=null) {
                        backgroundTask.execute("save", userid, mobilenumber, emailid, user.get(sessionManager.EmployeeRole), aadharnumber, getStringImage(bitmap));
                    }else{
                        backgroundTask.execute("save", userid, mobilenumber, emailid, user.get(sessionManager.EmployeeRole), aadharnumber, "null");
                    }
                }
                else {
                    if (pickImageUri != null) {
                        backgroundTask.execute("save", userid, mobilenumber, emailid, spinner.getSelectedItem().toString(), aadharnumber, getStringImage(bitmap));
                    } else {
                        backgroundTask.execute("save", userid, mobilenumber, emailid, spinner.getSelectedItem().toString(), aadharnumber, "null");
                    }
                }
            }
        }

    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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

    @Override
    public void onBackPressed() {
        if(back.equals("back")){
            //sessionManager.logoutUser();
            Intent menuIntent = new Intent(DetailEmployeeActivity.this, HomeActivity.class);
            menuIntent.putExtra("menu","menu");
            startActivity(menuIntent);
            finish();
        }else {
            Intent employeeActivity = new Intent(this, EmployeeActivity.class);
            startActivity(employeeActivity);
            finish();
        }
    }

    private void setUserData(){
        DecimalFormat df = new DecimalFormat("000");
        String id = df.format(Integer.parseInt(generatedEmployeeId));
        employeeId.setText(id);
        employeeName.setText(username);

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("get", userid);
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{
        // Parking Count remaining
        private Context ctx;
        private ProgressDialog progressDialog;
        String name, mobileno, email, des, aadhar, parkingCount, profile;
        String baseUrl = "https://duepark.000webhostapp.com/profilePic/";

        BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String keyword = params[0];
            if(keyword.equals("get")){
                progressDialog.setMessage("Loading data...");
                String userid = params[1];
                String detail_url = base_url+"get_employee.php?EmployeeId="+userid+"&Entity=AdminApp";
                //String detail_url = "https://duepark.000webhostapp.com/get_employeeData.php?userid="+userid;
                try {
                    URL url = new URL(detail_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while((line = bufferedReader.readLine())!=null){
                        stringBuilder.append(line+"\n");
                    }
                    httpURLConnection.disconnect();

                    String jsonString = stringBuilder.toString().trim();

                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    while(count<jsonArray.length()){
                        JSONObject jo = jsonArray.getJSONObject(count);
                        name = jo.getString("EmployeeName");
                        mobileno = jo.getString("EmployeeMobileNumber");
                        email = jo.getString("EmployeeEmailId");
                        des = jo.getString("EmployeeRole");
                        aadhar = jo.getString("EmployeeAdharNumber");
                        parkingCount = jo.getString("ParkingCount");
                        profile = jo.getString("EmployeeProfilePic");
                        count++;
                    }
                    return "get";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(keyword.equals("save")){
                String update_url = base_url+"update_employee.php";
                //String update_url = "https://duepark.000webhostapp.com/update_employeeData.php?userid="+userid;
                try{
                    progressDialog.setMessage("Updating data...");
                    URL url = new URL(update_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("EmployeeId","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                            +URLEncoder.encode("EmployeeMobileNumber","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"
                            +URLEncoder.encode("EmployeeEmailId","UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8")+"&"
                            +URLEncoder.encode("EmployeePassword", "UTF-8")+"="+URLEncoder.encode(params[4],"UTF-8")+"&"+
                            URLEncoder.encode("EmployeeAdharNumber", "UTF-8")+"="+URLEncoder.encode(params[5],"UTF-8")+"&"+
                            URLEncoder.encode("EmployeeProfilePic", "UTF-8")+"="+URLEncoder.encode(params[6],"UTF-8");

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
                    return result.toString();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else if(keyword.equals("delete")){
                progressDialog.setMessage("Deleting data...");
                String userid = params[1];
                String delete_url = base_url+"delete_employee.php?EmployeeId="+userid+"&EmployeeRole="+spinner.getSelectedItem().toString();
                //String detail_url = "https://duepark.000webhostapp.com/delete_employeeData.php?userid="+userid;
                try {
                    URL url = new URL(delete_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while((line = bufferedReader.readLine())!=null){
                        stringBuilder.append(line+"\n");
                    }
                    httpURLConnection.disconnect();
                    Log.d("TAG", "doInBackground: "+stringBuilder.toString());
                    return stringBuilder.toString().trim();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            switch (result) {
                case "get":
                    //inputname.setText(name);
                    inputmobilenumber.setText(mobileno);
                    inputemailid.setText(email);
                    inputaadharnumber.setText(aadhar);
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
                    /*if(!profile.equals("null")){
                        Picasso.get().load(baseUrl+profile+".png").into(profilePic);
                    }
                    else{
                        Picasso.get().load(R.drawable.userphoto).into(profilePic);
                        //profilePic.setImageResource(R.drawable.userphoto);
                    }*/
                    getParkingBtn.setText("PARKING ADDED - " + parkingCount);
                    break;
                case "UpdateSuccessfully":
                    Toast.makeText(ctx, "Data updated successfully", Toast.LENGTH_SHORT).show();
                    if(userid.equals(user.get(sessionManager.EmployeeId))){
                        sessionManager.logoutUser();
                    }else {
                        Intent employeeActivity = new Intent(ctx, EmployeeActivity.class);
                        startActivity(employeeActivity);
                        finish();
                    }
                    break;
                case "delete":
                    Toast.makeText(ctx, "Employee data is delete successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    break;
                default:
                    Log.d("TAG", "onPostExecute: "+result);
                    break;

            }
            progressDialog.dismiss();
        }
    }
}
