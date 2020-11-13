package com.example.duepark_admin.AddParking;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.duepark_admin.Model.Parking;
import com.example.duepark_admin.R;
import com.example.duepark_admin.Service.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;

public class ActivatedParkingFragment extends Fragment {

    private String base_url;
    private View view;
    private FloatingActionButton locationBtn;
    private ImageView backBtn;
    private String id;
    private SessionManager sessionManager;
    private HashMap<String, String> user;

    public ActivatedParkingFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activated_parking, container, false);

        base_url = getResources().getString(R.string.base_url);
        sessionManager = new SessionManager(getContext());
        user = sessionManager.getUserDetails();

        Bundle bundle = getArguments();
        if(bundle!=null){
            id = bundle.getString("id");
        }

        locationBtn = view.findViewById(R.id.locationBtn);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addParkingLocation();
            }
        });

        backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });

        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        backgroundTask.execute(id);

        return view;
    }

    private void backToHome() {
        Fragment addParkingFragment = new AddParkingFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, addParkingFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    class BackgroundTask extends AsyncTask<String, Void, Void>{
        TextView pi, pn, on, mn, phone, e, a, pt, d, t, un, passwordInput;
        String parkingId, acronym, parkingName, ownerName,mobileNumber, phoneNumber, emailId, address, city,
                parkingType, date, time, password;
        ProgressDialog progressDialog;
        Context ctx;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(this.ctx);
        }

        @Override
        protected void onPreExecute() {
            pi = view.findViewById(R.id.parkingId);
            pn = view.findViewById(R.id.parkingName);
            on = view.findViewById(R.id.ownerName);
            mn = view.findViewById(R.id.mobileNumber);
            phone = view.findViewById(R.id.phoneNumber);
            e = view.findViewById(R.id.emailId);
            a = view.findViewById(R.id.address);
            pt = view.findViewById(R.id.parkingType);
            passwordInput = view.findViewById(R.id.passwordInput);
            d = view.findViewById(R.id.date);
            t = view.findViewById(R.id.time);
            un = view.findViewById(R.id.username);

            progressDialog.setMessage("Loading Data...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String primaryId = params[0];
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
                    parkingId = jo.getString("GeneratedParkingId");
                    acronym = jo.getString("ParkingAcronym");
                    parkingName = jo.getString("ParkingName");
                    ownerName = jo.getString("EmployeeName");
                    mobileNumber = jo.getString("EmployeeMobileNumber");
                    phoneNumber = jo.getString("EmployeePhoneNumber");
                    emailId = jo.getString("EmployeeEmailId");
                    parkingType = jo.getString("ParkingType");
                    address = jo.getString("ParkingAddress");
                    city = jo.getString("ParkingCity");
                    date = jo.getString("ParkingDate");
                    time = jo.getString("ParkingTime");
                    password = jo.getString("EmployeePassword");
                    count++;
                }
                Log.d("JSON-STRING", "doInBackground: "+jsonString);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            DecimalFormat df = new DecimalFormat("000");
            String parking_id = acronym + df.format(Integer.parseInt(parkingId));
            pi.setText(parking_id);
            pn.setText(parkingName);
            on.setText(ownerName);
            mn.setText(mobileNumber);
            if(phoneNumber.isEmpty()){
                phone.setText("-");
            }else{
                phone.setText(phoneNumber);
            }
            e.setText(emailId);
            a.setText(address);
            pt.setText(parkingType);
            /*if(parkingType.equals("1")) {
                pt.setText("Personal");
            }else if(parkingType.equals("2")){
                pt.setText("Business");
            }*/
            un.setText(user.get(sessionManager.EmployeeName));
            passwordInput.setText(password);
            d.setText(date);
            t.setText(time);
            progressDialog.dismiss();
        }
    }

    private void addParkingLocation() {
        Fragment addParkingLocationFragment = new AddParkingLocationFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("Address","null");
        args.putString("Acronym","null");
        args.putString("back","null");
        args.putString("City", "null");
        addParkingLocationFragment.setArguments(args);
        transaction.replace(R.id.fragment_container, addParkingLocationFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }
}
