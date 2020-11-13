package com.example.duepark_admin.AddParking;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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

import com.example.duepark_admin.DetailPartner.Extra.LocationListPartnerActivity;
import com.example.duepark_admin.DetailPartner.LocationPartnerActivity;
import com.example.duepark_admin.HomeActivity;
import com.example.duepark_admin.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONObject;

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
import java.util.List;
import java.util.Objects;

public class AddParkingLocationFragment extends Fragment {

    private String base_url;
    private View view;
    private String address, abbreviation,city, location_type, id, back;
    private TextView locationAddress;
    private EditText locationName;
    private Spinner spinner;
    //private BackgroundTask backgroundTask;
    private final static String LocationName = "com.example.duepark.LocationName";
    private final static String LocationType = "com.example.duepark.LocationType";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public AddParkingLocationFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_parking_location, container, false);

        base_url = getResources().getString(R.string.base_url);

        sharedPreferences = getContext().getSharedPreferences("LocationData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Bundle bundle = getArguments();
        if(bundle!=null) {
            address = bundle.getString("Address");
            abbreviation = bundle.getString("Acronym");
            id = bundle.getString("id");
            city = bundle.getString("City");
            back = bundle.getString("back");
        }

        ImageView backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });

        spinner = view.findViewById(R.id.type_spinner);
        initSpinner();

        locationName = view.findViewById(R.id.locationName);
        locationAddress = view.findViewById(R.id.locationAddress);

        if(address!=null && !address.equals("null")){
            locationAddress.setText(address);
        }

        Button skipBtn = view.findViewById(R.id.skipBtn);
        if(back.equals("back")) {
            skipBtn.setText("Cancel");
        }
        else {
            skipBtn.setText("Skip");
        }

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(back.equals("back")){
                    startActivity(new Intent(getContext(), LocationPartnerActivity.class));
                    Objects.requireNonNull(getActivity()).finish();
                }else {
                    startActivity(new Intent(getContext(), HomeActivity.class));
                    Objects.requireNonNull(getActivity()).finish();
                }
            }
        });

        Button addressBtn = view.findViewById(R.id.addAddressBtn);
        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddress();
            }
        });

        Button locationBtn = view.findViewById(R.id.addlocationBtn);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               addLocation();
            }
        });

        getFormData();

        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        backgroundTask.execute("get", id);
        return view;
    }

    private void initSpinner(){
        List<String> type = new ArrayList<>();
        type.add(0, "-- Select Type --");
        type.add("Private");
        type.add("Public");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, type){
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

    private void backToHome() {
        if(back.equals("back")){
            Intent parkingLocationPartnerIntent = new Intent(getContext(), LocationPartnerActivity.class);
            parkingLocationPartnerIntent.putExtra("Id", id);
            parkingLocationPartnerIntent.putExtra("ParkingName", "");
            startActivity(parkingLocationPartnerIntent);
            getActivity().finish();
        }
        else {
            Fragment activatedParkingFragment = new ActivatedParkingFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            args.putString("id", id);
            activatedParkingFragment.setArguments(args);
            transaction.replace(R.id.fragment_container, activatedParkingFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void addLocation() {

        final String location_name = locationName.getText().toString().trim();
        final String location_address = locationAddress.getText().toString().trim();
        final String location_date = getCurrentDate();
        final String location_time = getCurrentTime();

        if(location_name.isEmpty()){
            fieldEmpty(locationName);
        }else if(location_address.isEmpty()){
            Toast.makeText(getContext(), "Please add the parking address", Toast.LENGTH_SHORT).show();
        } else if(spinner.getSelectedItemId() == 0){
            Toast.makeText(getContext(),"Please select location type", Toast.LENGTH_SHORT).show();
        }else{

            editor.remove("LocationData");
            editor.apply();

            BackgroundTask backgroundTask = new BackgroundTask(getContext());
            backgroundTask.execute("add",location_name, location_address,spinner.getSelectedItem().toString(), location_date, location_time, id);
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
            Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
                        args.putString("Add", "Location");
                        args.putString("id", id);
                        args.putString("back", back);
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

    private void storeFormData(){
        if(locationName.getText() != null){
            editor.remove(LocationName);
            editor.apply();
            editor.putString(LocationName, locationName.getText().toString().trim());
            editor.apply();
        }
        if(spinner.getSelectedItemPosition() != 0){
            editor.remove(LocationType);
            editor.apply();
            editor.putInt(LocationType, spinner.getSelectedItemPosition());
            editor.apply();
        }
    }

    private void getFormData(){
        if(sharedPreferences.contains(LocationName)){
            locationName.setText(sharedPreferences.getString(LocationName,""));
        }
        if(sharedPreferences.contains(LocationType)){
            spinner.setSelection(sharedPreferences.getInt(LocationType, 0));
        }
        editor.clear();
        editor.apply();
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{

        TextView parking_name, parking_city;
        String parkingName, city;

        Context ctx;
        ProgressDialog progressDialog;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(this.ctx);
        }

        @Override
        protected void onPreExecute() {
            parking_name = view.findViewById(R.id.parkingName);
            parking_city = view.findViewById(R.id.parkingCity);

            progressDialog.setMessage("Loading Data...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String keyword = params[0];
            if(keyword.equals("get")){
                String primaryId = params[1];
                String detail_url = base_url+"get_parking.php?ParkingId="+primaryId;
                //String detail_url = "https://duepark.000webhostapp.com/consumer/get_parking.php?ParkingId="+primaryId;
                try{
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
                        parkingName = jo.getString("ParkingName");
                        city = jo.getString("ParkingCity");
                        count++;
                    }
                    return "get";
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(keyword.equals("add")){
                String add_url = base_url+"add_location.php";
                //String add_url = "https://duepark.000webhostapp.com/add_locationData.php";
                String locationname = params[1];
                String locationaddress = params[2];
                String locationtype = params[3];
                String locationdate = params[4];
                String locationtime = params[5];
                String parkingid = params[6];
                try{
                    URL url = new URL(add_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("LocationName","UTF-8")+"="+URLEncoder.encode(locationname,"UTF-8")+"&"
                            +URLEncoder.encode("LocationAddress","UTF-8")+"="+URLEncoder.encode(locationaddress,"UTF-8")+"&"
                            +URLEncoder.encode("LocationType","UTF-8")+"="+URLEncoder.encode(locationtype,"UTF-8")+"&"
                            +URLEncoder.encode("LocationDate", "UTF-8")+"="+URLEncoder.encode(locationdate,"UTF-8")+"&"+
                            URLEncoder.encode("LocationTime", "UTF-8")+"="+URLEncoder.encode(locationtime,"UTF-8")+"&"+
                            URLEncoder.encode("LocationActiveState", "UTF-8")+"="+URLEncoder.encode("true","UTF-8")+"&"+
                            URLEncoder.encode("ParkingId", "UTF-8")+"="+URLEncoder.encode(parkingid,"UTF-8");
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
                    Log.d("Result123", result.toString());
                    return result.toString();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("get")) {
                parking_name.setText(parkingName);
                parking_city.setText(city);

                progressDialog.dismiss();
            }else {
                if (result.equals("0")) {
                    Toast.makeText(ctx, "Data not saved successfully", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else{
                    Toast.makeText(ctx, "Data saved successfully", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", result);
                    bundle.putString("back", back);
                    progressDialog.dismiss();
                    Fragment activatedLocationFragment = new ActivatedLocationFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    activatedLocationFragment.setArguments(bundle);
                    transaction.replace(R.id.fragment_container, activatedLocationFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        }
    }
}
