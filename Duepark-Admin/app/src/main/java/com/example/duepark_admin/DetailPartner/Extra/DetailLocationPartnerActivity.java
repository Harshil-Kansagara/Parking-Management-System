package com.example.duepark_admin.DetailPartner.Extra;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_admin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailLocationPartnerActivity extends AppCompatActivity implements View.OnClickListener {

    private String base_url;
    private String id, locationname, common_address;
    private EditText location_Name;
    private Spinner spinner;
    private TextView locationAddress, locationId, address, commonAddressTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_location_partner);

        base_url = getResources().getString(R.string.base_url);

        /*Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            id = bundle.getString("id");
            locationname = bundle.getString("locationName");
        }

        TextView locationName = findViewById(R.id.location_name);
        locationName.setText(locationname);

        location_Name = findViewById(R.id.locationName);
        spinner = findViewById(R.id.type_spinner);
        locationAddress = findViewById(R.id.locationAddress);
        locationId = findViewById(R.id.locationId);
        address = findViewById(R.id.address);
        commonAddressTV = findViewById(R.id.commonAddress);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        Button deactivateBtn = findViewById(R.id.deactivateLocationBtn);
        deactivateBtn.setOnClickListener(this);

        Button changeAddressBtn = findViewById(R.id.changeAddressBtn);
        changeAddressBtn.setOnClickListener(this);

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(this);

        initSpinner();

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("get", id);*/
    }

    @Override
    public void onClick(View v) {
        /* switch (v.getId())
         {
            case R.id.deactivateLocationBtn:
                deactivateMethod();
                break;

            case R.id.changeAddressBtn:
                changeAddress();
                break;

            case R.id.saveBtn:
                saveChanges();
                break;

            case R.id.backBtn:
                onBackPressed();
                break;

            case R.id.cancelBtn:
                onBackPressed();
                break;
         }*/
    }

    private void saveChanges(){
        String location_name = location_Name.getText().toString().trim();
        String location_address = locationAddress.getText().toString().trim();
        if(location_name.isEmpty()){
            fieldEmpty(location_Name);
        }else if(spinner.getSelectedItemId() == 0) {
            Toast.makeText(this, "Please select location type", Toast.LENGTH_SHORT).show();
        }else{

            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute("save", id, location_name, location_address, spinner.getSelectedItem().toString());
        }
    }

    private void fieldEmpty(EditText input){
        Drawable myIcon = getResources().getDrawable(R.drawable.error);
        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
        input.setError("Field can't be Empty",myIcon);
        requestFocus(input);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void initSpinner(){
        List<String> type = new ArrayList<>();
        type.add(0, "-- Select Type --");
        type.add("Private");
        type.add("Public");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, type){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    return false;
                }
                else{
                    return true;
                }
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    private void changeAddress(){
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.nearby_common_location, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText commonAddress = promptsView.findViewById(R.id.commonAddress);
        commonAddress.setText(locationAddress.getText().toString().trim());

        alertDialogBuilder
                .setTitle("Enter New Address:")
                .setCancelable(false)
                .setView(promptsView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        common_address = commonAddress.getText().toString().trim();
                        if(!common_address.isEmpty()) {
                            locationAddress.setText(common_address);
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void deactivateMethod() {
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("deactivate", id);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        startActivity(new Intent(DetailLocationPartnerActivity.this, LocationListPartnerActivity.class));
        finish();
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{

        String acronym, parking_id, location_id, location_address, location_type, location_name;
        Context ctx;
        ProgressDialog progressDialog;

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
            if(keyword.equals("deactivate")){
                progressDialog.setMessage("Deactivating Location...");
                String id = params[1];
                String deactivate_url = base_url+"set_locationActivationState.php";
                //String deactivate_url = "https://duepark.000webhostapp.com/update_locationActiveState.php";
                try{
                    URL url = new URL(deactivate_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8");
                    bw.write(data);
                    bw.flush();
                    bw.close();
                    ops.close();

                    InputStream ips = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(ips,"iso-8859-1"));
                    StringBuilder result= new StringBuilder();
                    String line = "";
                    while((line=br.readLine())!=null){
                        result.append(line);
                    }
                    br.close();
                    ips.close();
                    httpURLConnection.disconnect();
                    Log.d("Result", result.toString());
                    return result.toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(keyword.equals("get")){
                String primaryId = params[1];
                String get_url = base_url+"get_location.php?LocationId="+primaryId;
                //String get_url = "https://duepark.000webhostapp.com/get_locationData.php?id="+primaryId;
                try{
                    URL url = new URL(get_url);
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
                        acronym = jo.getString("Acronym");
                        parking_id = jo.getString("ParkingId");
                        location_id = jo.getString("LocationId");
                        location_name = jo.getString("LocationName");
                        location_address = jo.getString("LocationAddress");
                        location_type = jo.getString("LocationType");
                        count++;
                        Log.d("Result", acronym +"\n"+ parking_id +"\n"+ location_id +"\n"+ location_name
                                +"\n"+ location_address +"\n"+ location_type);
                    }
                    return "get";
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            if(keyword.equals("save")){
                progressDialog.setMessage("Updating Data...");
                String primaryId = params[1];
                String name = params[2];
                String main_address = params[3];
                String location_type = params[4];
                String update_url = base_url+"update_location.php";
                //String update_url = "https://duepark.000webhostapp.com/update_locationData.php";
                try{
                    URL url = new URL(update_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(primaryId,"UTF-8")+"&"
                            +URLEncoder.encode("locationname","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                            +URLEncoder.encode("locationaddress","UTF-8")+"="+URLEncoder.encode(main_address,"UTF-8")+"&"
                            +URLEncoder.encode("locationtype","UTF-8")+"="+URLEncoder.encode(location_type,"UTF-8");
                    bw.write(data);
                    bw.flush();
                    bw.close();
                    ops.close();

                    InputStream ips = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(ips,"iso-8859-1"));
                    StringBuilder result= new StringBuilder();
                    String line = "";
                    while((line=br.readLine())!=null){
                        result.append(line);
                    }
                    br.close();
                    ips.close();
                    httpURLConnection.disconnect();
                    Log.d("Result123", result.toString());
                    return result.toString();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("get")) {
                DecimalFormat df = new DecimalFormat("000");
                String parkingid = acronym + df.format(Integer.parseInt(parking_id));
                char locationid = (char)(Integer.parseInt(location_id)+'A'-1);
                String location_id = parkingid + locationid;
                location_Name.setText(location_name);
                locationAddress.setText(location_address);
                spinner.setSelection(Integer.parseInt(location_type));
                locationId.setText(location_id);
            }
            else if(result.equals("deactivate")){
                Toast.makeText(ctx, "Location is deactivated successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }else if(result.equals("notdeactivate")){
                Toast.makeText(ctx, "Location cannot deactivated successfully.. Try again after sometime", Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("updatedLocation"))
            {
                Toast.makeText(ctx, "Location data is updated successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else if(result.equals("notupdatedLocation")){
                Toast.makeText(ctx, "Location data is not updated", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }
}
