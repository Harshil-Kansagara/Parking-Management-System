package com.example.duepark_consumer.Help;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.duepark_consumer.Employee.DetailEmployeeActivity;
import com.example.duepark_consumer.HomeActivity;
import com.example.duepark_consumer.R;
import com.example.duepark_consumer.Valet.ValetHomeActivity;

public class HelpActivity extends AppCompatActivity {

    private static final String TAG = "HelpActivity";
    private boolean isValet = false;
    private EditText input_writeUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            isValet = bundle.getBoolean("IsValet");
        }

        input_writeUs = findViewById(R.id.input_writeUs);
        Button sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:info@duepark.com?subject=Help Support" + "&body="+ input_writeUs.getText().toString().trim()));
                startActivity(emailIntent);
            }
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(isValet){
            Intent i = new Intent(HelpActivity.this, ValetHomeActivity.class);
            i.putExtra("IsValetMenu", true);
            startActivity(i);
        }
        else{
            startActivity(new Intent(this, HomeActivity.class));
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}