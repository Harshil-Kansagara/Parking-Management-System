package com.example.duepark_admin.Menu.Employee;

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

import com.example.duepark_admin.Adapter.EmployeeListAdapter;
import com.example.duepark_admin.Model.Employee;
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
import java.util.Objects;

public class SaleEmployeeListFragment extends Fragment {

    private String base_url;
    private View view;
    private SessionManager sessionManager;

    public SaleEmployeeListFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sale_employee_list, container, false);

        base_url = getResources().getString(R.string.base_url);
        sessionManager = new SessionManager(getContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        BackgroundTask backgroundTask = new BackgroundTask(getContext());

        /*if(Objects.equals(user.get(sessionManager.EmployeeRole), "SuperAdmin")){
            backgroundTask.execute(user.get(sessionManager.EmployeeRole));
        }
        else{
            backgroundTask.execute(user.get(sessionManager.EmployeeRole), user.get(sessionManager.EmployeeId));
        }*/
        backgroundTask.execute("get", user.get(sessionManager.EmployeeId), user.get(sessionManager.EmployeeRole));
        return view;
    }

    class BackgroundTask extends AsyncTask<String, Employee, String>{

        private Context ctx;
        private EmployeeListAdapter employeeListAdapter;
        private ProgressDialog progressDialog;
        private ArrayList<Employee> employeeList;

        BackgroundTask(Context ctx) {
            this.ctx = ctx;
            employeeList = new ArrayList<>();
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(ctx,
                    LinearLayoutManager.VERTICAL, false);
            recyclerView.addItemDecoration(new DividerItemDecoration(ctx,
                    DividerItemDecoration.VERTICAL));
            recyclerView.setLayoutManager(mLinearLayoutManager);
            employeeListAdapter = new EmployeeListAdapter(employeeList, ctx);
            recyclerView.setAdapter(employeeListAdapter);

            progressDialog.setMessage("Loading Data...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String designation = params[0];
            /*if(Objects.equals(designation, "System Admin")){
                String recycler_url = "https://duepark.000webhostapp.com/get_saleList.php?userDesignation="+designation;
                setRecyclerView(recycler_url);
            }
            else{
                String userId = params[1];
                String recycler_url = "https://duepark.000webhostapp.com/get_saleList.php?userDesignation="+params[1]+"&userId="+userId;
                setRecyclerView(recycler_url);
            }*/
            String recycler_url = base_url+"get_employeeList.php?LoggedInEmployeeId="+params[1]+"&Role=Sale&LoggedInEmployeeRole="+params[2];
            setRecyclerView(recycler_url);
            return null;
        }

        @Override
        protected void onProgressUpdate(Employee... values) {
            employeeList.add(values[0]);
            employeeListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
        }

        private void setRecyclerView(String recyclerUrl){
            try {
                URL url = new URL(recyclerUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                httpURLConnection.disconnect();

                String json_string = stringBuilder.toString().trim();

                JSONObject jsonObject = new JSONObject(json_string);
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                int count = 0;
                while (count < jsonArray.length()) {
                    JSONObject jo = jsonArray.getJSONObject(count);
                    count++;
                    Employee employee = new Employee(jo.getString("id"), jo.getString("EmployeeName"), jo.getString("GeneratedEmployeeId"), jo.getString("Role"));
                    publishProgress(employee);
                }
                Log.d("JSON-STRING-ACTIVE", json_string);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
