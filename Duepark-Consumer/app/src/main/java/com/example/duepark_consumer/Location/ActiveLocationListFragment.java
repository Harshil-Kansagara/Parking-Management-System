package com.example.duepark_consumer.Location;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_consumer.Adapter.LocationListAdapter;
import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.Model.LocationList;
import com.example.duepark_consumer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ActiveLocationListFragment extends Fragment {

    private String base_url;
    private static final String TAG = "ActiveLocationFragment";
    private SessionManagerHelper sessionManagerHelper;
    private ArrayList<LocationList> locationLists;
    private LocationListAdapter locationListAdapter;

    public ActiveLocationListFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_location_list, container, false);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(getContext());

        locationLists = new ArrayList<>();
        locationListAdapter = new LocationListAdapter(locationLists, getContext(),getActivity());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(locationListAdapter);

        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        backgroundTask.execute();

        return view;
    }

    class BackgroundTask extends AsyncTask<String, LocationList, String>{
        private ProgressDialog progressDialog;

        BackgroundTask(Context ctx){
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Getting location list...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> employee = sessionManagerHelper.getEmployeeDetails();
            String activeLocationList_url = base_url+"get_activeLocationList.php?ParkingId="+employee.get(SessionManagerHelper.ParkingId)+"&EmployeeId="+employee.get(SessionManagerHelper.EmployeeId)+"&EmployeeRole="+employee.get(SessionManagerHelper.EmployeeRole);
            //String activeLocationList_url = "https://duepark.000webhostapp.com/consumer/get_activeLocationList.php?ParkingId="+employee.get(SessionManagerHelper.ParkingId)+"&EmployeeId="+employee.get(SessionManagerHelper.EmployeeId)+"&EmployeeRole="+employee.get(SessionManagerHelper.EmployeeRole);
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
                while(count<jsonArray.length()){
                    JSONObject jo =jsonArray.getJSONObject(count);
                    count++;
                    LocationList locationList = new LocationList(jo.getString("id"), jo.getString("LocationName"),jo.getString("GeneratedLocationId"),
                            jo.getString("LocationActiveState"), jo.getString("GeneratedParkingId"), jo.getString("ParkingAcronym"));
                    publishProgress(locationList);
                }
                Log.d(TAG, "doInBackground: "+result);
                inputStream.close();
                bufferedReader.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(LocationList... values) {
            locationLists.add(values[0]);
            locationListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
        }
    }
}
