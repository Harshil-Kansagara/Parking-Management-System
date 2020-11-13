package com.example.duepark_consumer.SignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.duepark_consumer.LoginActivity;
import com.example.duepark_consumer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SignUp1Activity extends AppCompatActivity {

    private FloatingActionButton signup1Btn, backBtn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText inputName;
    private final static String EmployeeNameSharedPreference = "com.example.duepark_consumer.EmployeeName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);

        sharedPreferences = getApplication().getSharedPreferences("NewEmployeeProfile", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        inputName = findViewById(R.id.inputName);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        signup1Btn = findViewById(R.id.signup1Btn);
        signup1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserName();
            }
        });

        getUserData();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUp1Activity.this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void createUserName(){
        String username = inputName.getText().toString().trim();

        if(username.isEmpty()){
            fieldEmpty(inputName);
        }
        else{
            storeUserData();
            startActivity(new Intent(SignUp1Activity.this, SignUp2Activity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }

    private void storeUserData(){
        if(inputName.getText() != null){
            editor.remove(EmployeeNameSharedPreference);
            //editor.apply();
            editor.putString(EmployeeNameSharedPreference, inputName.getText().toString().trim());
            //editor.apply();
            editor.commit();
        }
    }

    private void getUserData(){
        if(sharedPreferences.contains(EmployeeNameSharedPreference)){
            inputName.setText(sharedPreferences.getString(EmployeeNameSharedPreference, ""));
        }
       /* editor.clear();
        editor.apply();*/
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
}
