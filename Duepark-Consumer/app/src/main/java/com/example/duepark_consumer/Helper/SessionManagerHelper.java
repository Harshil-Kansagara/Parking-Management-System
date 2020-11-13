package com.example.duepark_consumer.Helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.duepark_consumer.HomeActivity;
import com.example.duepark_consumer.LoginActivity;
import com.example.duepark_consumer.NewPasswordActivity;
import com.example.duepark_consumer.R;
import com.example.duepark_consumer.Valet.ValetHomeActivity;
import com.example.duepark_consumer.WelcomeActivity;

import java.util.HashMap;

public class SessionManagerHelper {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "EmployeeProfile";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String ParkingId = "com.example.duepark_consumer.ParkingId";
    public static final String LocationId = "com.example.duepark_consumer.LocationId";
    public static final String ParkingName = "com.example.duepark_consumer.ParkingName";
    public static final String EmployeeId = "com.example.duepark_consumer.EmployeeId";
    public static final String EmployeeName = "com.example.duepark_consumer.EmployeeName";
    public static final String EmployeeProfilePic = "com.example.duepark_consumer.EmployeeProfilePic";
    public static final String EmployeeVehicleType = "com.example.duepark_consumer.EmployeeVehicleType";
    public static final String IsEmployeeNewPasswordCreated = "com.example.duepark_consumer.IsEmployeeNewPasswordCreated";
    public static final String GeneratedEmployeeId = "com.example.duepark_consumer.GeneratedEmployeeId";
    public static final String EmployeeRole = "com.example.duepark_consumer.EmployeeRole";
    public static final String IsActivated = "com.example.duepark_consumer.IsActivated";

    public SessionManagerHelper(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String parkingId, String locationId, String parkingName, String employeeId, String employeeName, String employeeProfilePic, String employeeRole,
                                   String vehicleType, boolean isEmployeeNewPasswordCreated, boolean isActivated, String generatedEmployeeId){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(ParkingId, parkingId);
        editor.putString(LocationId, locationId);
        editor.putString(ParkingName, parkingName);
        editor.putString(EmployeeId, employeeId);
        editor.putString(EmployeeName, employeeName);
        editor.putString(EmployeeProfilePic, employeeProfilePic);
        editor.putString(GeneratedEmployeeId, generatedEmployeeId);
        editor.putString(EmployeeRole, employeeRole);
        editor.putString(EmployeeVehicleType, vehicleType);
        editor.putBoolean(IsEmployeeNewPasswordCreated, isEmployeeNewPasswordCreated);
        editor.putBoolean(IsActivated, isActivated);
        editor.commit();
        checkLogin();
    }

    public void checkLogin() {
        //if (!this.isLoggedIn()) {
        if (this.isLoggedIn()) {
            Intent i = null;

            if(!pref.getBoolean(IsActivated, false)){
                i = new Intent(_context, WelcomeActivity.class);
            }
            else {
                if (!pref.getBoolean(IsEmployeeNewPasswordCreated, false)) {
                    i = new Intent(_context, NewPasswordActivity.class);
                    //Toast.makeText(_context, "New Password not created", Toast.LENGTH_SHORT).show();
                } else {
                    if(pref.getString(EmployeeRole,"").equals("Valet")){
                        i = new Intent(_context, ValetHomeActivity.class);
                    }
                    else {
                        i = new Intent(_context, HomeActivity.class);
                    }
                }
            }
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            //_context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public void updateEmployeeNewPassword(boolean IsEmployeeNewPasswordGenerated){
        if(pref.contains(IsEmployeeNewPasswordCreated)){
            editor.remove(IsEmployeeNewPasswordCreated);
            editor.apply();
            editor.putBoolean(IsEmployeeNewPasswordCreated, IsEmployeeNewPasswordGenerated);
            editor.apply();
        }
    }

    public void updateParkingName(String parkingName){
        if(pref.contains(ParkingName)){
            editor.remove(ParkingName);
            editor.apply();
            editor.putString(ParkingName, parkingName);
            editor.apply();
        }
    }

    public HashMap<String, String> getEmployeeDetails(){
        HashMap<String, String> employee = new HashMap<String, String>();

        employee.put(ParkingId, pref.getString(ParkingId, ""));
        employee.put(LocationId, pref.getString(LocationId, ""));
        employee.put(ParkingName, pref.getString(ParkingName, ""));
        employee.put(EmployeeId, pref.getString(EmployeeId, ""));
        employee.put(EmployeeName, pref.getString(EmployeeName, ""));
        employee.put(EmployeeRole, pref.getString(EmployeeRole, ""));
        employee.put(EmployeeVehicleType, pref.getString(EmployeeVehicleType, ""));
        employee.put(GeneratedEmployeeId, pref.getString(GeneratedEmployeeId, ""));
        employee.put(EmployeeProfilePic, pref.getString(EmployeeProfilePic, ""));

        return employee;
    }

    public void logoutEmployee(){

        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
        //_context.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
