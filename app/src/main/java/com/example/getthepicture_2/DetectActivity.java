package com.example.getthepicture_2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class DetectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        Button logupbtn=(Button) findViewById(R.id.btn_signup);
        Button registerbtn=(Button) findViewById(R.id.btn_register);
        Button detectbtn=(Button) findViewById(R.id.btn_detect);
        Button voicebtn=(Button) findViewById(R.id.btn_voice);

        logupbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DetectActivity.this,MainActivity.class);
                startActivity((intent));
            }
        });

        registerbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DetectActivity.this,PopupActivity.class);
                startActivity((intent));
            }
        });

    }
}
