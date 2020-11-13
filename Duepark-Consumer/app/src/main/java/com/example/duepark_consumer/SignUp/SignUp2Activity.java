package com.example.duepark_consumer.SignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.duepark_consumer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SignUp2Activity extends AppCompatActivity {

    private static final String TAG = "SignUp2";
    //private EditText inputState, inputCity;
    private AutoCompleteTextView inputState, inputCity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final static String EmployeeStateSharedPreference = "com.example.duepark_consumer.EmployeeState";
    private final static String EmployeeCitySharedPreference = "com.example.duepark_consumer.EmployeeCity";
    private List<String> stateList;
    private ArrayAdapter<String> stateArrayAdapter;
    private List<String> citiesList;
    private ArrayAdapter<String> cityArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        sharedPreferences = getApplication().getSharedPreferences("NewEmployeeProfile", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        /*inputState = findViewById(R.id.inputState);
        inputCity = findViewById(R.id.inputCity);*/
        stateList = new ArrayList<>();
        citiesList = new ArrayList<>();

        inputState = findViewById(R.id.inputState);
        inputCity = findViewById(R.id.inputCity);
        initState();

        inputState.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                inputCity.setText("");
                citiesList.clear();
                String stateName = editable.toString();
                try{
                    JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
                    JSONArray statesArray = jsonObject.getJSONArray("states");
                    for (int i = 0; i < statesArray.length();i++){
                        JSONObject jo = statesArray.getJSONObject(i);
                        if(jo.getString("state").equals(stateName)){
                            JSONArray citiesArray = jo.getJSONArray("districts");
                            for (int j =0 ;j<citiesArray.length();j++) {
                                citiesList.add(citiesArray.getString(j));
                            }
                        }
                        cityArrayAdapter = new ArrayAdapter<String>(SignUp2Activity.this, android.R.layout.simple_list_item_1, citiesList);
                        inputCity.setAdapter(cityArrayAdapter);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FloatingActionButton signup2Btn = findViewById(R.id.signup2Btn);
        signup2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserLocation();
            }
        });

        getUserData();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUp2Activity.this, SignUp1Activity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private String loadJSONFromAsset(){
        String json = null;
        try{
            InputStream is = getAssets().open("state_district.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void initState(){
        try{
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
            JSONArray statesArray = jsonObject.getJSONArray("states");
            for (int i = 0; i < statesArray.length();i++){
                JSONObject jo = statesArray.getJSONObject(i);
                stateList.add(jo.getString("state"));
            }
            stateArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stateList);
            inputState.setAdapter(stateArrayAdapter);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void createUserLocation(){
        String userState = inputState.getText().toString();
        String userCity = inputCity.getText().toString();

        if(userState.isEmpty()){
            fieldEmpty(inputState);
        }
        else if(userCity.isEmpty()){
            fieldEmpty(inputCity);
        }
        else{
            storeUserData();
            startActivity(new Intent(SignUp2Activity.this, SignUp3Activity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }

    private void storeUserData(){
        /*if(spinnerState.getSelectedItemId() != 0){
            editor.remove(EmployeeStateSharedPreference);
            editor.putString(EmployeeStateSharedPreference, spinnerState.getSelectedItem().toString());
            editor.putInt(EmployeeStateSpinnerPositionSharedPreference,spinnerState.getSelectedItemPosition());
        }
        if(spinnerCity.getSelectedItemId() != 0){
            editor.remove(EmployeeCitySharedPreference);
            editor.putString(EmployeeCitySharedPreference, spinnerCity.getSelectedItem().toString());
            editor.putInt(EmployeeCitySharedPreference, spinnerCity.getSelectedItemPosition());
            Log.d(TAG, "storeUserData: "+spinnerCity.getSelectedItemPosition());
        }*/
        if(inputState.getText() != null){
            editor.remove(EmployeeStateSharedPreference);
            //editor.apply();
            editor.putString(EmployeeStateSharedPreference, inputState.getText().toString().trim());
            //editor.apply();
        }

        if(inputCity.getText() != null){
            editor.remove(EmployeeCitySharedPreference);
            //editor.apply();
            editor.putString(EmployeeCitySharedPreference, inputCity.getText().toString().trim());
            //editor.apply();
        }
        editor.commit();
    }

    private void getUserData(){
        /*if(sharedPreferences.contains(EmployeeStateSpinnerPositionSharedPreference)){
            spinnerState.setSelection(sharedPreferences.getInt(EmployeeStateSpinnerPositionSharedPreference, 0));
        }
        if(sharedPreferences.contains(EmployeeCitySpinnerPositionSharedPreference)){
            spinnerCity.setSelection(sharedPreferences.getInt(EmployeeCitySpinnerPositionSharedPreference, 0));
        }
        editor.clear();
        editor.apply();*/
        if(sharedPreferences.contains(EmployeeStateSharedPreference)){
            inputState.setText(sharedPreferences.getString(EmployeeStateSharedPreference, ""));
        }
        if(sharedPreferences.contains(EmployeeCitySharedPreference)){
            inputCity.setText(sharedPreferences.getString(EmployeeCitySharedPreference,""));
        }
        /*editor.clear();
        editor.apply();*/
    }

    private void fieldEmpty(AutoCompleteTextView input){
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
}
