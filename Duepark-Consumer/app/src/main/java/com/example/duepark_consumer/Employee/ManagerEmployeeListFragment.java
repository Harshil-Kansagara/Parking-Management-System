package com.example.duepark_consumer.Employee;

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

import com.example.duepark_consumer.Adapter.EmployeeListAdapter;
import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.Model.EmployeeList;
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

public class ManagerEmployeeListFragment extends Fragment {

    private String base_url;
    private static final String TAG = "ManagerEmployeeListFrag";
    private SessionManagerHelper sessionManagerHelper;
    private ArrayList<EmployeeList> employeeLists;
    private EmployeeListAdapter employeeListAdapter;

    public ManagerEmployeeListFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_employee_list, container, false);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(getContext());

        employeeLists = new ArrayList<>();
        employeeListAdapter = new EmployeeListAdapter(getContext(), employeeLists, getActivity());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(employeeListAdapter);

        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        backgroundTask.execute();

        return view;
    }

    public void getEmployeeSearchString(String search){
        Log.d(TAG, "getEmployeeSearchString: "+search);

       ArrayList<EmployeeList> filteredEmployeeList = new ArrayList<>();
        for(EmployeeList item: employeeLists){
            if(item.getEmployeeName().toLowerCase().contains(search.toLowerCase().trim()))
            {
                filteredEmployeeList.add(item);
            }
        }
        employeeListAdapter.filteredList(filteredEmployeeList);

    }

    class BackgroundTask extends AsyncTask<String, EmployeeList, String> {
        private Context ctx;
        private ProgressDialog progressDialog;

        BackgroundTask(Context ctx){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Getting employee list...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> employee = sessionManagerHelper.getEmployeeDetails();
            String employeeList_url = base_url+"get_employeeList.php?ParkingId="+employee.get(SessionManagerHelper.ParkingId)+"&LoggedInEmployeeId="+employee.get(SessionManagerHelper.EmployeeId)+"&Role=Manager&LoggedInEmployeeRole="+employee.get(SessionManagerHelper.EmployeeRole)+"&EmployeeActiveState=true";
            //String adminEmployeeList_url = "https://duepark.000webhostapp.com/consumer/get_employeeList.php?ParkingId="+employee.get(SessionManagerHelper.ParkingId)+"&LoggedInEmployeeId="+employee.get(SessionManagerHelper.EmployeeId)+"&Role=Manager&LoggedInEmployeeRole="+employee.get(SessionManagerHelper.EmployeeRole);
            try{
                URL url = new URL(employeeList_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);
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
                employeeLists.clear();
                while(count<jsonArray.length()){
                    JSONObject jo =jsonArray.getJSONObject(count);
                    EmployeeList employeeList = new EmployeeList(jo.getString("id"), jo.getString("GeneratedEmployeeId"), jo.getString("EmployeeName"), jo.getString("Role"), jo.getString("EmployeeActiveState"));
                    publishProgress(employeeList);
                    count++;
                }
                Log.d(TAG, "doInBackground: "+result);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(EmployeeList... values) {
            employeeLists.add(values[0]);
            employeeListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
        }
    }
}
