package com.example.duepark_consumer.Location.Edit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.Location.DetailLocationActivity;
import com.example.duepark_consumer.Location.LocationActivity;
import com.example.duepark_consumer.R;

import java.util.HashMap;

public class EditCapacityLocationActivity extends AppCompatActivity {

    private String base_url;
    private SessionManagerHelper sessionManagerHelper;
    private HashMap<String, String> employeeDetails;
    private TextView editTV, currentTV;
    private EditText carCapacityET, bikeCapacityET;
    private boolean isAdd = false, isEdit=false;
    private String locationId, locationActiveState, parkingAcronym, generatedParkingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_capacity_location);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper = new SessionManagerHelper(this);
        employeeDetails = sessionManagerHelper.getEmployeeDetails();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            locationId = bundle.getString("LocationId");
            locationActiveState = bundle.getString("LocationActiveState");
            parkingAcronym = bundle.getString("ParkingAcronym");
            generatedParkingId = bundle.getString("GeneratedParkingId");
            isAdd = bundle.getBoolean("IsAdd");
            isEdit = bundle.getBoolean("IsEdit");
            if(isEdit){

            }
        }

        editTV = findViewById(R.id.editTV);
        currentTV = findViewById(R.id.currentTV);
        carCapacityET = findViewById(R.id.carCapacityET);
        bikeCapacityET = findViewById(R.id.bikeCapacityET);

    }

    @Override
    public void onBackPressed() {
        Intent i  = new Intent(this, DetailLocationActivity.class);
        i.putExtra("locationId", locationId);
        i.putExtra("locationActiveState", locationActiveState);
        i.putExtra("generatedParkingId", generatedParkingId);
        i.putExtra("parkingAcronym", parkingAcronym);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}