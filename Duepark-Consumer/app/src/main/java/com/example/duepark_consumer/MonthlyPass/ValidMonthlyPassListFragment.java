package com.example.duepark_consumer.MonthlyPass;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duepark_consumer.Adapter.MonthlyPassListAdapter;
import com.example.duepark_consumer.Employee.AdminEmployeeListFragment;
import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.Model.MonthlyPassList;
import com.example.duepark_consumer.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

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

public class ValidMonthlyPassListFragment extends Fragment {

    private String base_url;
    private static final String TAG = "ValidMonthlyPassList";
    private HashMap<String, String> employeeDetail;
    private List<MonthlyPassList> monthlyPassLists;
    private MonthlyPassListAdapter monthlyPassListAdapter;
    private boolean isValet = false;

    public ValidMonthlyPassListFragment() { }

    public ValidMonthlyPassListFragment(boolean isValet) {
        this.isValet = isValet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_valid_monthly_pass_list, container, false);

        base_url = getResources().getString(R.string.base_url);

        SessionManagerHelper sessionManagerHelper = new SessionManagerHelper(getContext());
        employeeDetail = sessionManagerHelper.getEmployeeDetails();

        monthlyPassLists = new ArrayList<>();
        monthlyPassListAdapter = new MonthlyPassListAdapter(getContext(), monthlyPassLists, getActivity(), isValet);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(monthlyPassListAdapter);

        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        backgroundTask.execute();

        return view;
    }

    public void getMonthlyPassSearchString(String search){
        Log.d(TAG, "getMonthlyPassSearchString: "+search);
        ArrayList<MonthlyPassList> filteredMonthlyPassList = new ArrayList<>();
        for(MonthlyPassList item: monthlyPassLists){
            if(item.getPassUserName().toLowerCase().contains(search.toLowerCase().trim()))
            {
                filteredMonthlyPassList.add(item);
            }
        }
        monthlyPassListAdapter.filteredList(filteredMonthlyPassList);
    }

    class BackgroundTask extends AsyncTask<String, MonthlyPassList, String>{
        private Context ctx;
        private ProgressDialog progressDialog;

        public BackgroundTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Fetching monthly pass...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String fetchValidMonthlyPass_url = base_url+"get_validMonthlyPassList.php?ParkingId="+employeeDetail.get(SessionManagerHelper.ParkingId)+"&LocationId="+employeeDetail.get(SessionManagerHelper.LocationId);
            //String fetchValidMonthlyPass_url = "https://duepark.000webhostapp.com/consumer/get_validMonthlyPassList.php?ParkingId="+employeeDetail.get(SessionManagerHelper.ParkingId);
            try{
                URL url = new URL(fetchValidMonthlyPass_url);
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
                monthlyPassLists.clear();
                while(count<jsonArray.length()){
                    JSONObject jo =jsonArray.getJSONObject(count);
                    count++;
                    MonthlyPassList monthlyPassList = new MonthlyPassList(jo.getString("MonthlyPassId"), jo.getString("GeneratedLocationId"),
                            jo.getString("GeneratedMonthlyPassId"), jo.getString("PassUserName"));
                    publishProgress(monthlyPassList);
                }
                inputStream.close();
                bufferedReader.close();
                Log.d(TAG, "doInBackground: "+result);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(MonthlyPassList... values) {
            monthlyPassLists.add(values[0]);
            monthlyPassListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
        }
    }
}
