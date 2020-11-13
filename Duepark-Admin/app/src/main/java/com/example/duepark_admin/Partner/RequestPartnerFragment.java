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

import com.example.duepark_admin.Adapter.RequestPartnerListAdapter;
import com.example.duepark_admin.Model.Parking;
import com.example.duepark_admin.Model.RequestParking;
import com.example.duepark_admin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RequestPartnerFragment extends Fragment {

    private String base_url;
    private View view;
    private RecyclerView recyclerView;
    private ArrayList<RequestParking> requestParkingList;
    private RequestPartnerListAdapter requestPartnerListAdapter;

    public RequestPartnerFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_request_partner, container, false);

        base_url = view.getResources().getString(R.string.base_url);
        requestParkingList = new ArrayList<RequestParking>();
        requestPartnerListAdapter = new RequestPartnerListAdapter(requestParkingList, getContext());
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(requestPartnerListAdapter);

        BackgroundTask backgroundTask = new BackgroundTask(getContext());
                backgroundTask.execute("requests");

        return view;
    }

    class BackgroundTask extends AsyncTask<String, RequestParking, Void>{
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
            String requestParking_url = base_url+"get_requestParkingList.php";
            try{
                URL url = new URL(requestParking_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                InputStream ips = httpURLConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(ips,"iso-8859-1"));
                StringBuilder result= new StringBuilder();
                String line = "";
                while((line=br.readLine())!=null){
                    result.append(line+"\n");
                }
                String resultData  = result.toString().trim();

                JSONObject jsonObject = new JSONObject(resultData);
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                int count = 0;
                requestParkingList.clear();
                while(count<jsonArray.length()){
                    JSONObject jo =jsonArray.getJSONObject(count);
                    count++;
                    RequestParking requestParking = new RequestParking(jo.getString("id"), jo.getString("EmployeeName"),jo.getString("EmployeeMobileNumber"));
                    publishProgress(requestParking);
                }
                Log.d("JSON-STRING-REQUEST",resultData);
                httpURLConnection.disconnect();
                ips.close();
                br.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(RequestParking... values) {
            requestParkingList.add(values[0]);
            requestPartnerListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            progressDialog.dismiss();
        }
    }
}
