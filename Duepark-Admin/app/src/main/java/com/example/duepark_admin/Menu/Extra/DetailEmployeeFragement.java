package com.example.duepark_admin.Menu.Extra;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.duepark_admin.R;
import com.example.duepark_admin.Service.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailEmployeeFragement extends Fragment {
    private View view;
    private SessionManager sessionManager;
    private String userid, username;
    private TextView employeeName, employeeId, name, mobilenumber, emailid, aadharnumber;
    private Spinner spinner;
    private HashMap<String, String> user;

    public DetailEmployeeFragement() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_employee, container, false);

        sessionManager = new SessionManager(getContext());
        user = sessionManager.getUserDetails();

        Bundle bundle = getArguments();
        if(bundle!=null){
            userid = bundle.getString("userid");
            username = bundle.getString("username");
        }

        employeeName = view.findViewById(R.id.employeeName);
        employeeId = view.findViewById(R.id.employeeId);
        name = view.findViewById(R.id.name);
        mobilenumber = view.findViewById(R.id.mobileNumber);
        emailid = view.findViewById(R.id.emailId);
        aadharnumber = view.findViewById(R.id.aadharNumber);

        spinner = view.findViewById(R.id.type_spinner);
        initSpinner();

        ImageView backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment employeeFragment = new EmployeeFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, employeeFragment).addToBackStack(null).commit();
            }
        });

        setUserData();

        return view;
    }

    private void initSpinner(){
        String loginUserDesignation = user.get(sessionManager.EmployeeRole);

        List<String> type = new ArrayList<>();
        type.add(0, "Designation");
        switch (loginUserDesignation) {
            case "SuperAdmin":
                type.add(1,"Admin");
                type.add(2, "Manager");
                type.add(3, "Sale");
                break;
            case "Admin":
                type.add(2, "Manager");
                type.add(3, "Sale");
                break;
            case "Manager":
                type.add(3, "Sale");
                break;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, type){
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    private void setUserData(){
        DecimalFormat df = new DecimalFormat("000");
        String id = df.format(Integer.parseInt(userid));

        employeeName.setText(username);
        employeeId.setText(id);
    }

    class BackgroundTask extends AsyncTask<String, Void, String> {
        private Context ctx;
        private ProgressDialog progressDialog;
        String name, mobileno, email, des, aadhar;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String keyword = params[0];
            if(keyword.equals("get")){
                progressDialog.setMessage("Loading data...");
                String userid = params[1];
                String detail_url = "https://duepark.000webhostapp.com/get_parkingData.php?userid="+userid;
                try {
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
                        name = jo.getString("name");
                        mobileno = jo.getString("mobilenumber");
                        email = jo.getString("emailid");
                        des = jo.getString("designation");
                        aadhar = jo.getString("aadharnumber");
                        count++;
                    }

                    return "get";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(keyword.equals("save")){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(des.equals("Admin")){
                spinner.setSelection(1);
            }else if(des.equals("Manager")){
                spinner.setSelection(2);
            }else if(des.equals("Sale")){
                spinner.setSelection(3);
            }
        }
    }
}
