package com.example.duepark_admin.AddParking;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import com.example.duepark_admin.DetailPartner.Extra.LocationListPartnerActivity;
import com.example.duepark_admin.DetailPartner.LocationPartnerActivity;
import com.example.duepark_admin.HomeActivity;
import com.example.duepark_admin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Objects;

public class ActivatedLocationFragment extends Fragment {

    private String base_url;
    private View view;
    private FloatingActionButton homeBtn;
    private String id, back;

    public ActivatedLocationFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activated_location, container, false);

        base_url = getResources().getString(R.string.base_url);

        Bundle bundle = getArguments();
        if(bundle!=null){
            id = bundle.getString("id");
            back = bundle.getString("back");
        }

        ImageView backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });

        homeBtn = view.findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
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

        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        backgroundTask.execute(id);

        return view;
    }
    private void backToHome() {
        Fragment addParkingLocationFragment = new AddParkingLocationFragment();
        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, addParkingLocationFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    class BackgroundTask extends AsyncTask<String, Void, Void>{

        Context ctx;
        TextView locationId, parkingName, locationName, locationAddress, locationType, locationDate, locationTime;
        String location_id, parking_id, acronym, location_name, location_address, location_type, location_date, location_time, parking_name;
        ProgressDialog progressDialog;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {

            locationId = view.findViewById(R.id.locationId);
            parkingName = view.findViewById(R.id.parkingName);
            locationName = view.findViewById(R.id.locationName);
            locationAddress = view.findViewById(R.id.locationAddress);
            locationType = view.findViewById(R.id.locationType);
            locationDate = view.findViewById(R.id.locationDate);
            locationTime = view.findViewById(R.id.locationTime);

            progressDialog.setMessage("Loading Data...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String primaryId = params[0];
            String detail_url = base_url+"get_location.php?LocationId="+primaryId;
            //String detail_url = "https://duepark.000webhostapp.com/get_location.php?LocationId="+primaryId;
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
                    acronym = jo.getString("ParkingAcronym");
                    parking_id = jo.getString("GeneratedParkingId");
                    parking_name = jo.getString("ParkingName");
                    location_id = jo.getString("GeneratedLocationId");
                    location_name = jo.getString("LocationName");
                    location_address = jo.getString("LocationAddress");
                    location_type = jo.getString("LocationType");
                    location_date = jo.getString("LocationDate");
                    location_time = jo.getString("LocationTime");
                    count++;
                }
                Log.d("ActivatedLocation", "doInBackground: "+jsonString);
            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            //converting to location id format
            DecimalFormat df = new DecimalFormat("000");
            String parkingid = acronym + df.format(Integer.parseInt(parking_id));
            char locationid = (char)(Integer.parseInt(location_id)+'A'-1);
            location_id = parkingid + locationid;

            locationId.setText(location_id);
            parkingName.setText(parking_name);
            locationName.setText(location_name);
            locationAddress.setText(location_address);
            locationType.setText(location_type);
            locationTime.setText(location_time);

            progressDialog.dismiss();
        }
    }
}
