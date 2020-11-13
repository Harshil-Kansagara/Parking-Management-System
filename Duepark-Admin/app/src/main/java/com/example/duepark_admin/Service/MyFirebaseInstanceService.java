package com.example.duepark_admin.Service;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.duepark_admin.R;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MyFirebaseInstanceService extends FirebaseMessagingService {

    private String base_url;
    public static final String TAG = "FirebaseInstanceService";
    private Context ctx;
    private String token;

    private SessionManager sessionManager;
    private HashMap<String, String> employee;

    public MyFirebaseInstanceService(Context ctx){
        this.ctx = ctx;
        sessionManager = new SessionManager(ctx);
        employee = sessionManager.getUserDetails();
        base_url = ctx.getResources().getString(R.string.base_url);
    }

    public MyFirebaseInstanceService(){}

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "onNewToken: "+token);
        this.token = token;
    }

    public void startBackgroundService(){
        BackgroundTask backgroundTask = new BackgroundTask(ctx);
        backgroundTask.execute(token);
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{
        Context ctx;

        BackgroundTask(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {

            try{
                String addToken_url = base_url+"add_adminAppEmployeeToken.php";
                //String addToken_url = "https://duepark.000webhostapp.com/consumer/add_adminAppEmployeeToken.php";
                URL url = new URL(addToken_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream ops = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                String data = URLEncoder.encode("EmployeeId","UTF-8")+"="+URLEncoder.encode(employee.get(SessionManager.EmployeeId),"UTF-8")+"&"
                        +URLEncoder.encode("EmployeeToken","UTF-8")+"="+URLEncoder.encode(params[0],"UTF-8");
                bw.write(data);
                bw.flush();
                bw.close();
                ops.close();

                InputStream ips = httpURLConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(ips,"iso-8859-1"));
                StringBuilder result= new StringBuilder();
                String line = "";
                while((line=br.readLine())!=null){
                    result.append(line+"\n");
                }

                String resultData  = result.toString().trim();
                br.close();
                ips.close();
                httpURLConnection.disconnect();
                Log.d(TAG, "doInBackground: "+resultData);
                return resultData;
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: "+result);
        }
    }
}
