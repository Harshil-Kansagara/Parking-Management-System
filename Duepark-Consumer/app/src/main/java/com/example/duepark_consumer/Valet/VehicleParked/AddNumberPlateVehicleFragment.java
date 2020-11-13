package com.example.duepark_consumer.Valet.VehicleParked;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.Model.LocationListWithGeneratedParkedVehicleId;
import com.example.duepark_consumer.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class AddNumberPlateVehicleFragment extends Fragment {

    private static final String TAG = "AddNumberPlateVehicle";
    private final static String VehicleNumberPlateImageSharedPreference = "com.example.duepark_consumer.VehicleNumberPlateImage";
    private final static String VehicleCodeSharedPreference = "com.example.duepark_consumer.VehicleCode";
    private final static String RtoNumberSharedPreference = "com.example.duepark_consumer.RtoNumber";
    private final static String UniqueNumberSharedPreference = "com.example.duepark_consumer.UniqueNumber";
    private final static String LocationIdPositionSharedPreference = "com.example.duepark_consumer.LocationIdPosition";
    private final static String LocationIdSharedPreference = "com.example.duepark_consumer.LocationId";
    private final static String GeneratedParkedVehicleIdSharedPreference = "com.example.duepark_consumer.GeneratedParkedVehicleId";
    private final static String VehicleTypeSharedPreference = "com.example.duepark_consumer.VehicleType";
    private String base_url;
    private HashMap<String, String> employeeDetail;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ArrayList<LocationListWithGeneratedParkedVehicleId> locationListWithGeneratedParkedVehicleIds;
    private List<String> locationIdType;
    private int selectedBtnId = 1;
    private EditText inputRTONumber, inputVehicleCode, inputUniqueNumber;
    private TextView vehicleIdTV, generatedLocationIdTV, monthlyPassGeneratedLocationIdTV, monthlyPassVehicleIdTV, passIdTV;
    private LinearLayout passIdLayout, monthlyPassIdLayout;
    private String vehicleType = "Car", rtoNumber = null, uniqueNumber= null, vehicleCode=null;
    private CircleImageView vehicleNumberPlateImage;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;
    private int recognizerCount = 0;
    private String monthlyPassId = null, passUserName = null, passUserMobileNumber = null, issuedDate = null, expiryDate = null, isMonthlyPass = "null", generatedMonthlyPassId = null;
    private String generatedLocationId = null, prevParkedVehicleId = "0", fullName = null, mobileNumber = null, locationId = null, generated_ParkedVehicleId= null;
    private String isParkedVehicleReleased = "1";
    private String r = "empty";
    private RelativeLayout orLayout;
    private TextView car, bike;
    private ImageButton carImg, bikImg;

    public AddNumberPlateVehicleFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_valet_add_number_plate_vehicle, container, false);

        base_url = getResources().getString(R.string.base_url);
        SessionManagerHelper sessionManagerHelper = new SessionManagerHelper(getContext());
        employeeDetail = sessionManagerHelper.getEmployeeDetails();

        sharedPreferences = getContext().getSharedPreferences("NewParkedVehicle", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        locationIdType = new ArrayList<>();
        locationListWithGeneratedParkedVehicleIds = new ArrayList<>();

        car = view.findViewById(R.id.car);
        bike = view. findViewById(R.id.bike);
        carImg = view.findViewById(R.id.carImg);
        bikImg = view.findViewById(R.id.bikImg);

        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                carImg.setVisibility(View.VISIBLE);
                bikImg.setVisibility(View.INVISIBLE);
                vehicleType = "Car";

            }
        });

        bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                carImg.setVisibility(View.INVISIBLE);
                bikImg.setVisibility(View.VISIBLE);
                vehicleType = "Bike";
            }
        });

        inputRTONumber = view.findViewById(R.id.inputRTONumber);
        inputVehicleCode = view.findViewById(R.id.inputVehicleCode);
        inputUniqueNumber = view.findViewById(R.id.inputUniqueNumber);

        Button checkNumberPlateBtn = view.findViewById(R.id.checkNumberPlateBtn);
        checkNumberPlateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNumberPlate();
            }
        });

        orLayout = view.findViewById(R.id.orLayout);
        orLayout.setVisibility(View.GONE);
        passIdTV = view.findViewById(R.id.passIdTV);
        passIdTV.setVisibility(View.GONE);
        passIdLayout = view.findViewById(R.id.passIdLayout);
        passIdLayout.setVisibility(View.GONE);
        generatedLocationIdTV = view.findViewById(R.id.generatedLocationIdTV);
        vehicleIdTV = view.findViewById(R.id.vehicleIdTV);
        /*if(Objects.equals(employeeDetail.get(SessionManagerHelper.EmployeeRole), "Valet")){
            locationIdSpinner.setVisibility(View.GONE);
            generatedLocationIdTV.setVisibility(View.VISIBLE);
        }
        else{
            locationIdSpinner.setVisibility(View.VISIBLE);
            generatedLocationIdTV.setVisibility(View.GONE);
        }*/

        monthlyPassIdLayout = view.findViewById(R.id.monthlyPassIdLayout);
        monthlyPassGeneratedLocationIdTV = view.findViewById(R.id.monthlyPassGeneratedLocationIdTV);
        monthlyPassVehicleIdTV = view.findViewById(R.id.monthlyPassVehicleIdTV);

        inputUniqueNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                uniqueNumber = editable.toString().trim();//.replace(" ","");
                vehicleCode = inputVehicleCode.getText().toString().trim();
                rtoNumber = inputRTONumber.getText().toString().trim();//.replace(" ","");
                //if(!rtoNumber.isEmpty() && !uniqueNumber.isEmpty() && !vehicleCode.isEmpty()){
                if(rtoNumber.length() == 4 && (vehicleCode.length()==1 || vehicleCode.length() == 2) && (uniqueNumber.length()==3||uniqueNumber.length()==4)){
                    verifyRTOCode(rtoNumber);
                    /*BackgroundTask backgroundTask = new BackgroundTask(AddNumberPlateVehicleActivity.this);
                    backgroundTask.execute("check",rtoNumber+""+uniqueNumber);*/
                }
            }
        });

        inputRTONumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                rtoNumber = editable.toString().trim();//.replace(" ","");
                vehicleCode = inputVehicleCode.getText().toString().trim();
                uniqueNumber = inputUniqueNumber.getText().toString().trim();//.replace(" ","");
                //if(!rtoNumber.isEmpty() && !uniqueNumber.isEmpty() && !vehicleCode.isEmpty()){
                if(rtoNumber.length() == 4 && (vehicleCode.length()==1 || vehicleCode.length() == 2) && (uniqueNumber.length()==3||uniqueNumber.length()==4)){
                    verifyRTOCode(rtoNumber);
                    /*BackgroundTask backgroundTask = new BackgroundTask(AddNumberPlateVehicleActivity.this);
                    backgroundTask.execute("check",rtoNumber+""+uniqueNumber);*/
                }
            }
        });

        inputVehicleCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                vehicleCode = editable.toString().trim();
                rtoNumber = inputRTONumber.getText().toString().trim();
                uniqueNumber = inputUniqueNumber.getText().toString().trim();
                //if(!rtoNumber.isEmpty() && !uniqueNumber.isEmpty() && !vehicleCode.isEmpty()){
                if(rtoNumber.length() == 4 && (vehicleCode.length()==1 || vehicleCode.length() == 2) && (uniqueNumber.length()==3||uniqueNumber.length()==4)){
                    verifyRTOCode(rtoNumber);
                    /*BackgroundTask backgroundTask = new BackgroundTask(AddNumberPlateVehicleActivity.this);
                    backgroundTask.execute("check",rtoNumber+""+uniqueNumber);*/
                }
            }
        });

        vehicleNumberPlateImage = view.findViewById(R.id.vehicleNumberPlateImage);
        vehicleNumberPlateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
            }
        });

        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        backgroundTask.execute("get");

        return view;
    }

    private void verifyRTOCode(String rtoNumber){
        boolean isRtoCodeFound = false;
        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
            JSONArray rtoNumberArray = jsonObject.getJSONArray("rto_numbers");
            for (int i=0;i<rtoNumberArray.length();i++){
                if(rtoNumberArray.get(i).toString().equals(rtoNumber.toUpperCase())){
                    BackgroundTask backgroundTask = new BackgroundTask(getContext());
                    backgroundTask.execute("check",rtoNumber+""+vehicleCode+""+uniqueNumber);
                    isRtoCodeFound = true;
                    break;
                }
            }
            if(!isRtoCodeFound){
                Toast.makeText(getContext(), "Please enter valid RTO Code", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJSONFromAsset(){
        String json = null;
        try{
            InputStream is = getContext().getAssets().open("rtonumbers.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    // #region start Camera and Number plate recognizer
    private void checkPermission(String permission, int requestCode){
        if (ContextCompat.checkSelfPermission(getContext(), permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] { permission },
                    requestCode);
        }
        else {
            Log.d(TAG, "checkPermission: Permission already granted");
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: Camera permission granted");
                dispatchTakePictureIntent();
            }
            else {
                Log.d(TAG, "onRequestPermissionsResult: Camera permission denied");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            assert extras != null;
            imageBitmap = (Bitmap) extras.get("data");
            vehicleNumberPlateImage.setImageBitmap(imageBitmap);
            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
            FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            firebaseVisionTextRecognizer.processImage(firebaseVisionImage)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            processText(firebaseVisionText);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    private void processText(FirebaseVisionText firebaseVisionText){
        recognizerCount = recognizerCount + 1;
        List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();
        if (blocks.size() > 9) {
            Toast.makeText(getContext(), "No text found", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "processText: No text found");
        }

        for(int i=0;i<blocks.size();i++){
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                String t_str = lines.get(j).getText().replace(" ","").trim();
                //vehicleNumberPlateImage.setImageBitmap(imageBitmap);
                Log.d(TAG, "processText: "+t_str);
                if(t_str.length() == 10){
                    inputRTONumber.setText(t_str.substring(0,t_str.length()-6));//.replace(" ","").trim());
                    // Here add this line
                    inputVehicleCode.setText(t_str.substring(t_str.length()-6, t_str.length()-4));
                    inputUniqueNumber.setText(t_str.substring(t_str.length()-4));
                }
                else if(t_str.length() == 9 || t_str.length() == 8){
                    String firstFourDigit = t_str.substring(0, 4);
                    boolean a = false;
                    if(firstFourDigit.substring(firstFourDigit.length()-2).matches("\\d+(?:\\.\\d+)?")){
                        inputRTONumber.setText(firstFourDigit);
                        a = true;
                    }
                    else{
                        inputRTONumber.setText(t_str.substring(0,2)+"0"+t_str.substring(2,3));
                    }
                    int uniqueNumberStartingPosition=0;
                    String uniqueNumberString = null;
                    String vehicleCodeTest = t_str;
                    if(a){
                        inputVehicleCode.setText(vehicleCodeTest.substring(4).replaceAll("[^A-Za-z]+",""));
                        uniqueNumberString = t_str.substring(4);
                    }
                    else{
                        inputVehicleCode.setText(vehicleCodeTest.substring(3).replaceAll("[^A-Za-z]+",""));
                        uniqueNumberString= t_str.substring(3);
                    }
                    for(int m=0;m<uniqueNumberString.length();m++){
                        if(uniqueNumberString.charAt(m) == '0' || uniqueNumberString.charAt(m) == '1' ||uniqueNumberString.charAt(m) == '2' ||uniqueNumberString.charAt(m) == '3' ||uniqueNumberString.charAt(m) == '4' ||uniqueNumberString.charAt(m) == '5' ||uniqueNumberString.charAt(m) == '6' ||uniqueNumberString.charAt(m) == '7' ||uniqueNumberString.charAt(m) == '8' ||uniqueNumberString.charAt(m) == '9'){
                            uniqueNumberStartingPosition = m;
                            break;
                        }
                    }
                    inputUniqueNumber.setText(uniqueNumberString.substring(uniqueNumberStartingPosition));
                }
                /*if(t_str.length() == 10){//if(numberPlatePattern.matcher(t_str).matches()){
                    Log.d(TAG, "processText: "+t_str);
                    inputRTONumber.setText(t_str.substring(0,t_str.length()-6));//.replace(" ","").trim());
                    inputUniqueNumber.setText(t_str.substring(t_str.length()-6));//.replace(" ","").trim());
                    recognizerCount = 0;
                }*/
            }
        }

        if(recognizerCount == 3){
            Toast.makeText(getContext(), "Number plate not recognize. Enter vehicle number manually...", Toast.LENGTH_SHORT).show();
        }
    }
    //# region end


    private void checkNumberPlate(){
        String rtoNumber = inputRTONumber.getText().toString().trim().replace(" ","");
        String uniqueNumber = inputUniqueNumber.getText().toString().trim().replace(" ","");
        //int locationPosition = locationIdSpinner.getSelectedItemPosition();
        String generatedVehicleParkedId = vehicleIdTV.getText().toString();

        if(rtoNumber.isEmpty()){
            fieldEmpty(inputRTONumber);
        }
        else if(vehicleCode.isEmpty()){
            fieldEmpty(inputVehicleCode);
        }
        else if(uniqueNumber.isEmpty()){
            fieldEmpty(inputUniqueNumber);
        }
        else if(selectedBtnId == 0){
            Toast.makeText(getContext(), "Please select vehicle type..", Toast.LENGTH_SHORT).show();
        }
        else {
            if(!isMonthlyPass.equals("true")) {
                if (generatedLocationIdTV.getVisibility() == View.VISIBLE) {
                    locationId = locationListWithGeneratedParkedVehicleIds.get(0).getLocationId();
                }
            }

            //String vehicleNumber = rtoNumber + uniqueNumber;

            storeVehicleData();
            Fragment addVehicleUserFragment = new AddVehicleUserFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            if(r.equals("notEmpty")) {
                Bundle i = new Bundle();
                i.putString("IsMonthlyPass", isMonthlyPass);
                i.putString("IsParkedVehicleReleased", isParkedVehicleReleased);
                i.putString("ParkedVehicleId", prevParkedVehicleId);
                if (isMonthlyPass.equals("true")) {
                    i.putString("MonthlyPassId", monthlyPassId);
                    i.putString("PassUserName", passUserName);
                    i.putString("PassUserMobileNumber", passUserMobileNumber);
                    i.putString("IssuedDate", issuedDate);
                    i.putString("ExpiryDate", expiryDate);
                    i.putString("GeneratedMonthlyPassId", generatedMonthlyPassId);
                    i.putString("GeneratedLocationId", generatedLocationId);
                    i.putString("LocationId", locationId);
                    i.putString("GeneratedParkedVehicleId", generated_ParkedVehicleId);
                } else {
                    i.putString("FullName", fullName);
                    i.putString("MobileNumber", mobileNumber);
                }
                addVehicleUserFragment.setArguments(i);
            }
            fragmentTransaction.replace(R.id.fragment_container, addVehicleUserFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }
    }

    private void getVehicleData(){
        if(sharedPreferences.contains(RtoNumberSharedPreference)){
            inputRTONumber.setText(sharedPreferences.getString(RtoNumberSharedPreference, ""));
        }
        if(sharedPreferences.contains(VehicleCodeSharedPreference)){
            inputVehicleCode.setText(sharedPreferences.getString(VehicleCodeSharedPreference,""));
        }
        if(sharedPreferences.contains(UniqueNumberSharedPreference)){
            inputUniqueNumber.setText(sharedPreferences.getString(UniqueNumberSharedPreference, ""));
        }
        /*if(sharedPreferences.contains(LocationIdPositionSharedPreference))
        {
            int spinnerPosition = sharedPreferences.getInt(LocationIdPositionSharedPreference, 0);
            locationIdSpinner.setSelection(spinnerPosition);
        }*/
        if(sharedPreferences.contains(VehicleTypeSharedPreference))
        {
            String vehicle = sharedPreferences.getString(VehicleTypeSharedPreference, "");
            if(vehicle.equals("Car")){
                carImg.setVisibility(View.VISIBLE);
                bikImg.setVisibility(View.INVISIBLE);

                vehicleType = "Car";
                selectedBtnId = 1;
            }
            else{
                bikImg.setVisibility(View.VISIBLE);
                carImg.setVisibility(View.INVISIBLE);

                vehicleType = "Bike";
                selectedBtnId = 2;
            }
        }
        // Add Vehicle Number Pic
        if(sharedPreferences.contains(VehicleNumberPlateImageSharedPreference)){
            vehicleNumberPlateImage.setImageBitmap(decodeBase64ToImage(sharedPreferences.getString(VehicleNumberPlateImageSharedPreference, "")));
        }
    }

    private void storeVehicleData(){
        if(inputRTONumber.getText() != null){
            editor.remove(RtoNumberSharedPreference);
            //editor.apply();
            editor.putString(RtoNumberSharedPreference, inputRTONumber.getText().toString().trim());
            //editor.apply();
            editor.commit();
        }
        if(inputVehicleCode.getText()!=null){
            editor.remove(VehicleCodeSharedPreference);
            editor.putString(VehicleCodeSharedPreference, inputVehicleCode.getText().toString().trim());
            editor.commit();
        }
        if(inputUniqueNumber.getText()!=null){
            editor.remove(UniqueNumberSharedPreference);
            //editor.apply();
            editor.putString(UniqueNumberSharedPreference, inputUniqueNumber.getText().toString().trim());
            //editor.apply();
            editor.commit();
        }
        if(vehicleType != null){
            editor.remove(VehicleTypeSharedPreference);
            //editor.apply();
            editor.putString(VehicleTypeSharedPreference, vehicleType);
            //editor.apply();
            editor.commit();
        }
        editor.remove(LocationIdSharedPreference);
        //editor.remove(LocationIdPositionSharedPreference);
        editor.remove(GeneratedParkedVehicleIdSharedPreference);
        editor.commit();
        if(passIdLayout.getVisibility() == View.GONE) {
            //editor.putInt(LocationIdPositionSharedPreference, locationIdSpinner.getSelectedItemPosition());//locationListWithGeneratedParkedVehicleIds.get(locationIdSpinner.getSelectedItemPosition()).getLocationId());
            editor.putString(LocationIdSharedPreference, locationId);
            editor.putString(GeneratedParkedVehicleIdSharedPreference, vehicleIdTV.getText().toString().trim());
            editor.commit();
        }
        // Add Vehicle Number Pic
        if(imageBitmap!=null){
            editor.remove(VehicleNumberPlateImageSharedPreference);
            editor.putString(VehicleNumberPlateImageSharedPreference, encodeImageToBase64(imageBitmap));
            editor.commit();
        }
    }

    public String encodeImageToBase64(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static Bitmap decodeBase64ToImage(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    private void fieldEmpty(EditText input){
        Drawable myIcon = getResources().getDrawable(R.drawable.error);
        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
        input.setError("Field can't be Empty",myIcon);
        requestFocus(input);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    class BackgroundTask extends AsyncTask<String, LocationListWithGeneratedParkedVehicleId, String> {

        private Context ctx;
        private ProgressDialog progressDialog;
        private boolean isLocationResponseNull = false;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(this.ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if(params[0].equals("get")){
                //progressDialog.setMessage("Getting location list..");
                String activeLocationList_url = base_url+"get_activeLocationListWithGeneratedVehicleParkedIds.php?ParkingId="
                        +employeeDetail.get(SessionManagerHelper.ParkingId)+"&EmployeeId="+employeeDetail.get(SessionManagerHelper.EmployeeId)+
                        "&EmployeeRole="+employeeDetail.get(SessionManagerHelper.EmployeeRole);
                /*String activeLocationList_url = "https://duepark.000webhostapp.com/consumer/get_activeLocationListWithGeneratedVehicleParkedIds.php?ParkingId="
                        +employeeDetail.get(SessionManagerHelper.ParkingId)+"&EmployeeId="+employeeDetail.get(SessionManagerHelper.EmployeeId)+
                        "&EmployeeRole="+employeeDetail.get(SessionManagerHelper.EmployeeRole);*/
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
                    if(jsonArray.length()>0) {
                        int count = 0;
                        while (count < jsonArray.length()) {
                            JSONObject jo = jsonArray.getJSONObject(count);
                            count++;
                            LocationListWithGeneratedParkedVehicleId locationListWithGeneratedParkedVehicleId = new LocationListWithGeneratedParkedVehicleId
                                    (jo.getString("GeneratedParkedVehicleId"), jo.getString("LocationId"), jo.getString("GeneratedLocationId"),
                                            jo.getString("GeneratedParkingId"), jo.getString("ParkingAcronym"));
                            publishProgress(locationListWithGeneratedParkedVehicleId);
                        }
                    }
                    else{
                        isLocationResponseNull = true;
                    }
                    Log.d(TAG, "doInBackground: "+result);
                    inputStream.close();
                    bufferedReader.close();
                    return "get";
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                progressDialog.setMessage("Checking vehicle number...");
                String activeLocationList_url = base_url+"check_vehicleNumberExists.php?VehicleNumber="+params[1]+"&ParkingId="+employeeDetail.get(SessionManagerHelper.ParkingId);
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

                    String jsonResult = stringBuilder.toString().trim();

                    JSONObject jsonObject = new JSONObject(jsonResult);
                    //JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    if(!jsonObject.isNull("server_response")) {
                        JSONObject jo = jsonObject.getJSONObject("server_response");
                        r = "notEmpty";
                        isMonthlyPass = jo.getString("IsMonthlyPass");
                        prevParkedVehicleId = jo.getString("ParkedVehicleId");
                        isParkedVehicleReleased = jo.getString("IsParkedVehicleReleased");
                        if (isMonthlyPass.equals("true")) {
                            monthlyPassId = jo.getString("MonthlyPassId");
                            passUserName = jo.getString("PassUserName");
                            passUserMobileNumber = jo.getString("PassUserMobileNumber");
                            issuedDate = jo.getString("IssuedDate");
                            expiryDate = jo.getString("ExpiryDate");
                            generatedMonthlyPassId = jo.getString("GeneratedMonthlyPassId");
                            generatedLocationId = jo.getString("GeneratedLocationId");
                            locationId = jo.getString("LocationId");
                            generated_ParkedVehicleId = jo.getString("GeneratedParkedVehicleId");
                        } else {
                            fullName = jo.getString("FullName");
                            mobileNumber = jo.getString("MobileNumber");
                        }
                    }
                    else{
                        r = "empty";
                    }
                    Log.d(TAG, "doInBackground: "+jsonResult);
                    inputStream.close();
                    bufferedReader.close();
                    return "check";
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(LocationListWithGeneratedParkedVehicleId... values) {
            locationListWithGeneratedParkedVehicleIds.add(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            DecimalFormat df = new DecimalFormat("000");
            if(result.equals("get")){
                if(isLocationResponseNull){
                    Toast.makeText(ctx, "Please add location...", Toast.LENGTH_SHORT).show();
                }
                else {
                    //locationIdType.add(0, "-- Select Location Id --");
                    if (employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("Valet")) {
                        String parkingid = locationListWithGeneratedParkedVehicleIds.get(0).getParkingAcronym() +
                                df.format(Integer.parseInt(locationListWithGeneratedParkedVehicleIds.get(0).getGeneratedParkingId()));
                        char char_locationId = (char) (Integer.parseInt(locationListWithGeneratedParkedVehicleIds.get(0).getGeneratedLocationId()) + 'A' - 1);
                        String location_id = parkingid + char_locationId;
                        generatedLocationIdTV.setText(location_id);
                        DecimalFormat vdf = new DecimalFormat("0000");
                        generated_ParkedVehicleId = vdf.format(Integer.parseInt(locationListWithGeneratedParkedVehicleIds.get(0).getGeneratedParkedVehicleId()) + 1);
                        vehicleIdTV.setText(generated_ParkedVehicleId);
                        getVehicleData();
                    } /*else {
                        locationIdType.clear();
                        for (int i = 0; i < locationListWithGeneratedParkedVehicleIds.size(); i++) {
                            String parkingid = locationListWithGeneratedParkedVehicleIds.get(i).getParkingAcronym() +
                                    df.format(Integer.parseInt(locationListWithGeneratedParkedVehicleIds.get(i).getGeneratedParkingId()));
                            char char_locationId = (char) (Integer.parseInt(locationListWithGeneratedParkedVehicleIds.get(i).getGeneratedLocationId()) + 'A' - 1);
                            String location_id = parkingid + char_locationId;
                            locationIdType.add(location_id);
                        }
                        ArrayAdapter<String> locationIdsDataAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, locationIdType);
                        locationIdsDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        locationIdSpinner.setAdapter(locationIdsDataAdapter);
                        getVehicleData();
                    }*/
                }
            }
            else{
                if(r.equals("notEmpty")) {
                    if(isMonthlyPass.equals("true")) {
                        passIdTV.setText("Monthly Pass Id");
                        orLayout.setVisibility(View.VISIBLE);
                        passIdTV.setVisibility(View.VISIBLE);
                        passIdLayout.setVisibility(View.GONE);
                        monthlyPassIdLayout.setVisibility(View.VISIBLE);
                        monthlyPassGeneratedLocationIdTV.setText(generatedLocationId);
                        monthlyPassVehicleIdTV.setText(df.format(Integer.parseInt(generatedMonthlyPassId)));
                        passIdTV.setTextColor(ContextCompat.getColor(ctx,R.color.colorPrimaryTransparent));
                        monthlyPassGeneratedLocationIdTV.setBackground(getResources().getDrawable(R.drawable.btn_shape_light_transparent));
                        monthlyPassVehicleIdTV.setBackground(getResources().getDrawable(R.drawable.btn_shape_light_transparent));
                    }
                    else{
                        passIdTV.setText("Pass Id");
                        monthlyPassIdLayout.setVisibility(View.GONE);
                        passIdLayout.setVisibility(View.VISIBLE);
                        passIdLayout.setVisibility(View.GONE);
                        passIdTV.setVisibility(View.GONE);
                        orLayout.setVisibility(View.GONE);
                        if(employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("Valet")){
                            String parkingid = locationListWithGeneratedParkedVehicleIds.get(0).getParkingAcronym() +
                                    df.format(Integer.parseInt(locationListWithGeneratedParkedVehicleIds.get(0).getGeneratedParkingId()));
                            char char_locationId = (char) (Integer.parseInt(locationListWithGeneratedParkedVehicleIds.get(0).getGeneratedLocationId()) + 'A' - 1);
                            String location_id = parkingid + char_locationId;
                            generatedLocationIdTV.setText(location_id);
                            DecimalFormat vdf = new DecimalFormat("0000");
                            String generated_VehicleParkedId = vdf.format(Integer.parseInt(locationListWithGeneratedParkedVehicleIds.get(0).getGeneratedParkedVehicleId())+1);
                            vehicleIdTV.setText(generated_VehicleParkedId);
                            //getVehicleData();
                        }
                    }
                }
                else{
                    monthlyPassIdLayout.setVisibility(View.GONE);
                    passIdLayout.setVisibility(View.GONE);
                    passIdTV.setVisibility(View.GONE);
                    orLayout.setVisibility(View.GONE);
                    passIdTV.setText("Pass Id");
                    if(employeeDetail.get(SessionManagerHelper.EmployeeRole).equals("Valet")){
                        String parkingid = locationListWithGeneratedParkedVehicleIds.get(0).getParkingAcronym() +
                                df.format(Integer.parseInt(locationListWithGeneratedParkedVehicleIds.get(0).getGeneratedParkingId()));
                        char char_locationId = (char) (Integer.parseInt(locationListWithGeneratedParkedVehicleIds.get(0).getGeneratedLocationId()) + 'A' - 1);
                        String location_id = parkingid + char_locationId;
                        generatedLocationIdTV.setText(location_id);
                        DecimalFormat vdf = new DecimalFormat("0000");
                        String generated_VehicleParkedId = vdf.format(Integer.parseInt(locationListWithGeneratedParkedVehicleIds.get(0).getGeneratedParkedVehicleId())+1);
                        vehicleIdTV.setText(generated_VehicleParkedId);
                    }
                   /* else {
                        locationIdType.clear();
                        for (int i = 0; i < locationListWithGeneratedParkedVehicleIds.size(); i++) {
                            String parkingid = locationListWithGeneratedParkedVehicleIds.get(i).getParkingAcronym() +
                                    df.format(Integer.parseInt(locationListWithGeneratedParkedVehicleIds.get(i).getGeneratedParkingId()));
                            char char_locationId = (char) (Integer.parseInt(locationListWithGeneratedParkedVehicleIds.get(i).getGeneratedLocationId()) + 'A' - 1);
                            String location_id = parkingid + char_locationId;
                            locationIdType.add(location_id);
                        }
                        ArrayAdapter<String> locationIdsDataAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, locationIdType);
                        locationIdsDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        locationIdSpinner.setAdapter(locationIdsDataAdapter);
                        //getVehicleData();
                    }*/
                }
            }
            progressDialog.dismiss();
        }
    }
}
