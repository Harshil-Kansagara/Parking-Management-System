package com.example.duepark_admin.Vehicle;

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

import com.example.duepark_admin.Adapter.VehicleListAdapter;
import com.example.duepark_admin.Model.VehicleList;
import com.example.duepark_admin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BikeVehicleFragment extends Fragment {

    private static final String TAG = "BikeVehicleList";
    private String base_url;
    private View view;
    private RecyclerView recyclerView;
    private List<VehicleList> vehicleLists;
    private VehicleListAdapter vehicleListAdapter;
    public BikeVehicleFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bike_vehicle, container, false);

        base_url = getResources().getString(R.string.base_url);
        vehicleLists = new ArrayList<>();
        vehicleListAdapter = new VehicleListAdapter(getContext(), vehicleLists, getActivity());

        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(vehicleListAdapter);

        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        backgroundTask.execute();

        return view;
    }

    public void getVehicleSearchString(String search){
        Log.d(TAG, "getParkedVehicleSearchString: "+search);
        ArrayList<VehicleList> filteredVehicleList = new ArrayList<>();
        for(VehicleList item: vehicleLists){
            if(item.getVehicleNumber().toLowerCase().contains(search.toLowerCase().trim()))
            {
                filteredVehicleList.add(item);
            }
        }
        vehicleListAdapter.filteredList(filteredVehicleList);
    }

    class BackgroundTask extends AsyncTask<String, VehicleList, String> {
        private Context ctx;
        private ProgressDialog progressDialog;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Getting vehicle list...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String getCarParkedList_url = base_url+"get_vehicleList_Admin.php?VehicleType=Bike";
            try{
                URL url = new URL(getCarParkedList_url);
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
                vehicleLists.clear();
                while(count<jsonArray.length()){
                    JSONObject jo =jsonArray.getJSONObject(count);
                    count++;
                    VehicleList vehicleList = new VehicleList(jo.getString("ParkedVehicleId"), jo.getString("VehicleNumber"),
                            jo.getString("VehicleType"), jo.getString("TotalAmount"), jo.getString("TotalTime"), jo.getString("Date"));
                    publishProgress(vehicleList);
                }
                Log.d(TAG, "doInBackground: "+result);
                inputStream.close();
                bufferedReader.close();
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(VehicleList... values) {
            vehicleLists.add(values[0]);
            vehicleListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
        }
    }
}
