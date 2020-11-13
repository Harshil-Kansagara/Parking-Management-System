package com.example.duepark_admin.Partner;

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

import com.example.duepark_admin.Adapter.PartnerListAdapter;
import com.example.duepark_admin.Model.Parking;
import com.example.duepark_admin.R;
import com.example.duepark_admin.Service.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ActivePartnerFragment extends Fragment {
        //implements ConnectivityReceiver.ConnectivityReceiverListener {

    private String base_url;
    private View view;
    private RecyclerView recyclerView;
    private SessionManager sessionManager;
    private ArrayList<Parking> parkingList;
    private PartnerListAdapter partnerListAdapter;
    //private EditText searchinput;

    public ActivePartnerFragment(){ }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_active_partner, container, false);

        base_url = getResources().getString(R.string.base_url);
        sessionManager = new SessionManager(getContext());

        parkingList = new ArrayList<>();
        partnerListAdapter = new PartnerListAdapter(parkingList, getContext(), getActivity());
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(partnerListAdapter);

        /*searchinput = view.findViewById(R.id.searchText);
        search();*/

        HashMap<String, String> user = sessionManager.getUserDetails();
        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        backgroundTask.execute(user.get(sessionManager.EmployeeId), user.get(sessionManager.EmployeeRole));

        return view;
    }

    /*private void  search(){
        searchinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                partnerListAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }*/

/*    public void search_query(String searchQuery){
        //if(new PartnerFragment().searchQuery != null)
        Log.d("Search",searchQuery);
        partnerListAdapter.getFilter().filter(searchQuery);
    }*/

    /*@Override
    public void onStart() {
        super.onStart();
        checkConnection();
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }*/

    /*private void checkConnection() {
        ConnectivityManager.NetworkCallback connectivityCallback =
                new ConnectivityManager.NetworkCallback(){

                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        boolean isConnected = ConnectivityReceiver.isConnected();
                        showToast(isConnected);
                    }

                    @Override
                    public void onLost(@NonNull Network network) {
                        super.onLost(network);
                        boolean isConnected = false;
                        showToast(isConnected);
                    }
                };

        boolean isConnected = ConnectivityReceiver.isConnected();
        showToast(isConnected);
    }

    private void showToast(boolean isConnected) {
        if (isConnected) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            BackgroundTask backgroundTask = new BackgroundTask(getContext());
            backgroundTask.execute(user.get(sessionManager.KEY_ID), user.get(sessionManager.KEY_DESIGNATION));
        } else {
            Toast.makeText(getContext(), "Sorry! Not connected to internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showToast(isConnected);
    }*/

    public class BackgroundTask extends AsyncTask<String, Parking, Void>{

        private Context ctx;

        private ProgressDialog progressDialog;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(this.ctx);
        }

        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("Loading Data...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String userId = params[0];
            String recycler_url = base_url+"get_activeParkingList_Admin.php?EmployeeId="+userId+"&EmployeeRole="+params[1];
            //String recycler_url = "https://duepark.000webhostapp.com/get_activePartner.php?userId="+userId+"&designation="+params[1];
            try
            {
                URL url = new URL(recycler_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine())!=null){
                    stringBuilder.append(line+"\n");
                }

                httpURLConnection.disconnect();

                String json_string = stringBuilder.toString().trim();

                JSONObject jsonObject = new JSONObject(json_string);
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                int count = 0;
                parkingList.clear();
                while(count<jsonArray.length()){
                    JSONObject jo =jsonArray.getJSONObject(count);
                    count++;
                    Parking parking = new Parking(jo.getString("id"), jo.getString("ParkingId"),jo.getString("ParkingAcronym"),
                            jo.getString("ParkingName"), jo.getString("ParkingActiveState"));
                    publishProgress(parking);
                }
                Log.d("JSON-STRING-ACTIVE",json_string);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Parking... values) {
            parkingList.add(values[0]);
            partnerListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            progressDialog.dismiss();
        }
    }
}
