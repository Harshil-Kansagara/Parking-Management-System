package com.example.duepark_admin.Service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.duepark_admin.HomeActivity;
import com.example.duepark_admin.LoginActivity;
import com.example.duepark_admin.NewPasswordActivity;

import java.util.HashMap;

public class SessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Login";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String EmployeeId = "com.example.duepark_admin.EmployeeId";
    public static final String EmployeeName = "com.example.duepark_admin.EmployeeName";
    public static final String EmployeeProfilePic = "com.example.duepark_admin.EmployeeProfilePic";
    public static final String IsEmployeeNewPasswordCreated = "com.example.duepark_admin.IsEmployeeNewPasswordCreated";
    public static final String GeneratedEmployeeId = "com.example.duepark_admin.GeneratedEmployeeId";
    public static final String EmployeeRole = "com.example.duepark_admin.EmployeeRole";
    public static final String IsActivated = "com.example.duepark_admin.IsActivated";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String employeeId, String employeeName, String employeeProfilePic, String employeeRole,
                                   boolean isEmployeeNewPasswordCreated, boolean isActivated, String generatedEmployeeId){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(EmployeeId, employeeId);
        editor.putString(EmployeeName, employeeName);
        editor.putString(EmployeeProfilePic, employeeProfilePic);
        editor.putString(GeneratedEmployeeId, generatedEmployeeId);
        editor.putString(EmployeeRole, employeeRole);
        editor.putBoolean(IsEmployeeNewPasswordCreated, isEmployeeNewPasswordCreated);
        editor.putBoolean(IsActivated, isActivated);
        editor.commit();
        checkLogin();
    }

    public void checkLogin() {
        //if (!this.isLoggedIn()) {
        if (this.isLoggedIn()) {
            Intent i = null;

            if (!pref.getBoolean(IsEmployeeNewPasswordCreated, false)) {
                i = new Intent(_context, NewPasswordActivity.class);
                Toast.makeText(_context, "New Password not created", Toast.LENGTH_SHORT).show();
            } else {
                i = new Intent(_context, HomeActivity.class);
            }
            //Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            _context.startActivity(i);
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

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> employee = new HashMap<String, String>();

        employee.put(EmployeeId, pref.getString(EmployeeId, ""));
        employee.put(EmployeeName, pref.getString(EmployeeName, ""));
        employee.put(EmployeeRole, pref.getString(EmployeeRole, ""));
        employee.put(GeneratedEmployeeId, pref.getString(GeneratedEmployeeId, ""));
        employee.put(EmployeeProfilePic, pref.getString(EmployeeProfilePic, ""));

        return employee;
    }

    public void logoutUser(){

        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);

    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    //public boolean isNewPasswordCreated() { return pref.getBoolean(KEY_NEW_PASSWORD, false); }
}
