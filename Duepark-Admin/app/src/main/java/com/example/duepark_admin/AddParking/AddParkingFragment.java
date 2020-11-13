package com.example.duepark_admin.AddParking;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.duepark_admin.HomeActivity;
import com.example.duepark_admin.R;
import com.example.duepark_admin.Service.SessionManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddParkingFragment extends Fragment {

    private String base_url;
    private static final String TAG = "AddParkingFragment";
    private Spinner spinner;
    private String address = null, abbrevation, parking_city, employeeId="null", employeeName=null, employeeMobileNumber=null;
    private EditText parkingName, ownerName, mobileNumber, phoneNumber, emailAddress;
    private TextView parkingAddress, addressAcronym;
    private final static String ParkingName = "com.example.duepark_admin.ParkingName";
    private final static String OwnerName = "com.example.duepark_admin.OwnerName";
    private final static String MobileNumber = "com.example.duepark_admin.MobileNumber";
    private final static String PhoneNumber = "com.example.duepark_admin.PhoneNumber";
    private final static String EmailAddress = "com.example.duepark_admin.EmailAddress";
    private final static String ParkingType = "com.example.duepark_admin.ParkingType";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ArrayAdapter<String> dataAdapter;

    public AddParkingFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_parking, container, false);

        base_url = getResources().getString(R.string.base_url);
        sharedPreferences = getContext().getSharedPreferences("Partner", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Bundle bundle = getArguments();
        if(bundle!=null){
            address = bundle.getString("Address");
            abbrevation = bundle.getString("Acronym");
            parking_city = bundle.getString("City");
            employeeId = bundle.getString("employeeId");
            employeeMobileNumber = bundle.getString("employeeMobileNumber");
            employeeName = bundle.getString("employeeName");
        }

        spinner = view.findViewById(R.id.type_spinner);
        initSpinner();

        /*ImageView backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });*/

        parkingName = view.findViewById(R.id.parkingName);
        ownerName = view.findViewById(R.id.ownerName);
        mobileNumber = view.findViewById(R.id.mobileNumber);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        emailAddress = view.findViewById(R.id.emailAddress);
        parkingAddress = view.findViewById(R.id.address);
        addressAcronym = view.findViewById(R.id.acronym);

        Button addParkingBtn = view.findViewById(R.id.addParkingBtn);
        addParkingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createParking();
            }
        });

        Button addAddressBtn = view.findViewById(R.id.addAddressBtn);
        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAddress();
            }
        });

        getFormData();

        if(employeeName != null && employeeMobileNumber != null){
            ownerName.setText(employeeName);
            ownerName.setEnabled(false);
            mobileNumber.setText(employeeMobileNumber);
            mobileNumber.setEnabled(false);
        }

        if(address!=null && abbrevation!=null){
            parkingAddress.setText(address);
            addressAcronym.setText("Acronym of State is "+abbrevation);
        }

        return view;
    }

    private void initSpinner(){
        List<String> type = new ArrayList<>();
        type.add(0, "-- Select Type --");
        type.add("Personal");
        type.add("Business");

        dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, type){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    return false;
                }
                else{
                    return true;
                }
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    /*private void backToHome() {
        startActivity(new Intent(getContext(), HomeActivity.class));
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        getActivity().finish();
    }*/

    private void addAddress() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        storeFormData();
                        Fragment addressFragment = new AddAddressFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        Bundle args = new Bundle();
                        args.putString("Add", "Parking");
                        args.putString("id", "null");
                        args.putString("employeeId", employeeId);
                        args.putString("employeeName", employeeName);
                        args.putString("employeeMobileNumber", employeeMobileNumber);
                        addressFragment.setArguments(args);
                        transaction.replace(R.id.fragment_container, addressFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                            builder.setTitle("Permission Denied")
                                    .setMessage("Permission to access device location is permanently denied. you need to go to setting to allow the permission.")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", Objects.requireNonNull(getContext()).getPackageName(), null));
                                        }
                                    })
                                    .show();
                        } else {
                            Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void storeFormData() {

        if(parkingName.getText() != null ) {
            editor.remove(ParkingName);
            editor.apply();
            editor.putString(ParkingName, parkingName.getText().toString().trim());
            editor.apply();
        }

        if(ownerName.getText() != null ) {
            editor.remove(OwnerName);
            editor.apply();
            editor.putString(OwnerName, ownerName.getText().toString().trim());
            editor.apply();
        }
        if(mobileNumber.getText() != null ) {
            editor.remove(MobileNumber);
            editor.apply();
            editor.putString(MobileNumber, mobileNumber.getText().toString().trim());
            editor.apply();
        }
        if(phoneNumber.getText() != null ) {
            editor.remove(PhoneNumber);
            editor.apply();
            editor.putString(PhoneNumber, phoneNumber.getText().toString().trim());
            editor.apply();
        }
        if(emailAddress.getText() != null ) {
            editor.remove(EmailAddress);
            editor.apply();
            editor.putString(EmailAddress, emailAddress.getText().toString().trim());
            editor.apply();
        }
        if(spinner.getSelectedItemPosition() != 0){
            editor.remove(ParkingType);
            editor.apply();
            editor.putString(ParkingType, spinner.getSelectedItem().toString());
            editor.apply();
        }

    }

    private void getFormData(){

        if(sharedPreferences.contains(ParkingName)){
            parkingName.setText(sharedPreferences.getString(ParkingName,""));
        }
        if(sharedPreferences.contains(OwnerName)){
            ownerName.setText(sharedPreferences.getString(OwnerName, ""));
        }
        if(sharedPreferences.contains(MobileNumber)){
            mobileNumber.setText(sharedPreferences.getString(MobileNumber, ""));
        }
        if(sharedPreferences.contains(PhoneNumber)){
            phoneNumber.setText(sharedPreferences.getString(PhoneNumber, ""));
        }
        if(sharedPreferences.contains(EmailAddress)){
            emailAddress.setText(sharedPreferences.getString(EmailAddress, ""));
        }
        if(sharedPreferences.contains(ParkingType)){
            int spinnerPosition = dataAdapter.getPosition(sharedPreferences.getString(ParkingType, ""));
            spinner.setSelection(spinnerPosition);
        }
        editor.clear();
        editor.apply();
    }

    private void createParking() {
        //String nameRegex = "^[A-Z][a-zA-Z_ ]*$";
        String emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
        String mobileRegex = "^[6-9][0-9]{9}$";

        final String parking_name = parkingName.getText().toString().trim();
        final String owner_name = ownerName.getText().toString().trim();
        final String mobile_number  = mobileNumber.getText().toString().trim();
        String phone_number = phoneNumber.getText().toString().trim();
        final String email_address = emailAddress.getText().toString().trim();
        final String parking_address = parkingAddress.getText().toString().trim();
        final String address_acronym = addressAcronym.getText().toString().trim();
        final String parking_date = getCurrentDate();
        final String parking_time = getCurrentTime();
        final String active_state = "true";

        //Pattern namePattern = Pattern.compile(nameRegex);
        Pattern emailPattern = Pattern.compile(emailRegex);
        Pattern mobilePattern = Pattern.compile(mobileRegex);

        //Matcher nameMatches = namePattern.matcher(owner_name);
        Matcher numberMatches = mobilePattern.matcher(mobile_number);
        Matcher emailMatches = emailPattern.matcher(email_address);

        if(parking_name.isEmpty()){
           fieldEmpty(parkingName);
        }
        else if(owner_name.isEmpty()){
            fieldEmpty(ownerName);
        }
        else if(mobile_number.isEmpty()){
            fieldEmpty(mobileNumber);
        }
        else if(!numberMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            mobileNumber.setError("Please enter valid mobile number.!",myIcon);
            requestFocus(mobileNumber);
        }
        else if(email_address.isEmpty()){
            fieldEmpty(emailAddress);
        }
        else if(!emailMatches.matches()){
            Drawable myIcon = getResources().getDrawable(R.drawable.error);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            emailAddress.setError("Please check your email id.",myIcon);
            requestFocus(emailAddress);
        }else if(parking_address.isEmpty()){
            Toast.makeText(getContext(), "Please add the parking address", Toast.LENGTH_SHORT).show();
        }else if(address_acronym.isEmpty()){
            Toast.makeText(getContext(), "Address Acronym is empty ", Toast.LENGTH_SHORT).show();
        }
        else if(spinner.getSelectedItemId() == 0){
            Toast.makeText(getContext(),"Please select type", Toast.LENGTH_SHORT).show();
        }
        else{
            if(phone_number.isEmpty()){
                phone_number = "null";
            }
            BackgroundTask backgroundTask =new BackgroundTask(getContext());
            backgroundTask.execute(employeeId, owner_name, mobile_number, phone_number, email_address ,abbrevation, parking_name, spinner.getSelectedItem().toString(),
                    parking_address,parking_city, parking_date, parking_time, active_state);
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
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private String getCurrentDate(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(today);
    }

    private String getCurrentTime(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        return format.format(today);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!isNetworkStatusAvailable(getContext())){
            Toast.makeText(getContext(), "Please make sure your internet connection is on.", Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isNetworkStatusAvailable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if(netInfo != null)
                return netInfo.isConnected();
        }
        return false;
    }

    class BackgroundTask extends AsyncTask<String, Void, String> {

        Context ctx;
        ProgressDialog progressDialog = new ProgressDialog(getContext());

        BackgroundTask(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Saving Data...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            //String add_url = "https://duepark.000webhostapp.com/consumer/add_parking.php";
            String add_url = base_url+"add_parking.php";
           /* String acronym = params[0];
            String parking = params[1];
            String owner = params[2];
            String mobile = params[3];
            String phone = params[4];
            String email = params[5];
            String address = params[6];
            String city = params[7];
            String type = params[8];
            String date = params[9];
            String time = params[10];
            String activation = params[11];
            String userId = params[12];*/

            try{
                URL url = new URL(add_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream ops = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                String data = URLEncoder.encode("employeeId","UTF-8")+"="+URLEncoder.encode(params[0],"UTF-8")+"&"
                        +URLEncoder.encode("employeeName","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"
                        +URLEncoder.encode("employeeMobileNumber","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"
                        +URLEncoder.encode("employeePhoneNumber","UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8")+"&"
                        +URLEncoder.encode("employeeEmailId","UTF-8")+"="+URLEncoder.encode(params[4],"UTF-8")+"&"
                        +URLEncoder.encode("parkingAcronym", "UTF-8")+"="+URLEncoder.encode(params[5],"UTF-8")+"&"+
                        URLEncoder.encode("parkingName", "UTF-8")+"="+URLEncoder.encode(params[6],"UTF-8")+"&"+
                        URLEncoder.encode("parkingType", "UTF-8")+"="+URLEncoder.encode(params[7],"UTF-8")+"&"+
                        URLEncoder.encode("parkingAddress", "UTF-8")+"="+URLEncoder.encode(params[8],"UTF-8")+"&"+
                        URLEncoder.encode("parkingCity", "UTF-8")+"="+URLEncoder.encode(params[9],"UTF-8")+"&"+
                        URLEncoder.encode("parkingDate", "UTF-8")+"="+URLEncoder.encode(params[10],"UTF-8")+"&"+
                        URLEncoder.encode("parkingTime", "UTF-8")+"="+URLEncoder.encode(params[11],"UTF-8")+"&"+
                        URLEncoder.encode("parkingActivationState", "UTF-8")+"="+URLEncoder.encode(params[12],"UTF-8");
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
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("0")){
                Toast.makeText(ctx, "Something went wrong..", Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("exist")){
                Toast.makeText(ctx, "Mobile number or Email Id already exists"+ result, Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(ctx, "Parking add Successfully "+ result, Toast.LENGTH_SHORT).show();
                editor.remove("Partner");
                editor.apply();
                Fragment activatedParkingFragment = new ActivatedParkingFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("id", result);
                activatedParkingFragment.setArguments(bundle);
                transaction.replace(R.id.fragment_container, activatedParkingFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            progressDialog.dismiss();
        }
    }
}
