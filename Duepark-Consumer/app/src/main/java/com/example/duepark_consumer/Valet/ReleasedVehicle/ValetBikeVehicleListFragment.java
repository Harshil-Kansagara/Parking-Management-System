package com.example.duepark_consumer.Valet.ReleasedVehicle;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.duepark_consumer.Adapter.ParkedVehicleListAdapter;
import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.Model.ParkedVehicleList;
import com.example.duepark_consumer.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ValetBikeVehicleListFragment extends Fragment {

    private String base_url;
    private static final String TAG = "CarVehicleList" ;
    private SessionManagerHelper sessionManagerHelper;
    private HashMap<String, String> employeeDetail;
    private RecyclerView recyclerView;
    private List<ParkedVehicleList> parkedVehicleLists;
    private ParkedVehicleListAdapter parkedVehicleListAdapter;
    private TextInputEditText input_vehicleNumber;

    public ValetBikeVehicleListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_valet_bike_vehicle_list, container, false);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(getContext());
        employeeDetail = sessionManagerHelper.getEmployeeDetails();
        recyclerView = view.findViewById(R.id.recyclerView);

        parkedVehicleLists = new ArrayList<>();
        parkedVehicleListAdapter = new ParkedVehicleListAdapter(getContext(), parkedVehicleLists, getActivity(), true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(parkedVehicleListAdapter);

        input_vehicleNumber = view.findViewById(R.id.input_vehicleNumber);
        input_vehicleNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "onTextChanged: "+charSequence);
                getParkedVehicleSearchString(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                getParkedVehicleSearchString(editable.toString().trim());
            }
        });

        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        backgroundTask.execute();

        return view;
    }

    private void getParkedVehicleSearchString(String search){
        Log.d(TAG, "getParkedVehicleSearchString: "+search);
        ArrayList<ParkedVehicleList> filteredParkedVehicleList = new ArrayList<>();
        for(ParkedVehicleList item: parkedVehicleLists){
            if(item.getVehicleNumber().toLowerCase().contains(search.toLowerCase().trim()))
            {
                filteredParkedVehicleList.add(item);
            }
        }
        parkedVehicleListAdapter.filteredList(filteredParkedVehicleList);
    }

    class BackgroundTask extends AsyncTask<String, ParkedVehicleList, String> {
        private Context ctx;
        private ProgressDialog progressDialog;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Getting parked vehicle list...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String getCarParkedList_url = base_url+"get_parkedVehicleList.php?ParkingId="+employeeDetail.get(SessionManagerHelper.ParkingId)+"&LocationId="+employeeDetail.get(SessionManagerHelper.LocationId)+"&VehicleType=Bike";
            //String getCarParkedList_url = "https://duepark.000webhostapp.com/consumer/get_carParkedVehicleList.php?ParkingId="+params[0]+"&LocationId="+params[1];
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
                parkedVehicleLists.clear();
                while(count<jsonArray.length()){
                    JSONObject jo =jsonArray.getJSONObject(count);
                    count++;
                    ParkedVehicleList parkedVehicleList = new ParkedVehicleList(jo.getString("Id"), jo.getString("VehicleNumber"),
                            jo.getString("PaidAmount"), jo.getString("ParkedTime"), jo.getString("ParkedDate"), jo.getString("VehicleType"));
                    publishProgress(parkedVehicleList);
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
        protected void onProgressUpdate(ParkedVehicleList... values) {
            parkedVehicleLists.add(values[0]);
            parkedVehicleListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
        }
    }
}
