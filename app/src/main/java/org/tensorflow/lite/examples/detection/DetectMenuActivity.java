package org.tensorflow.lite.examples.detection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class DetectMenuActivity extends AppCompatActivity {
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
                Intent intent=new Intent(DetectMenuActivity.this, MainActivity.class);
                startActivity((intent));
            }
        });


        registerbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DetectMenuActivity.this,PopupActivity.class);
                startActivity((intent));
            }
        });

        detectbtn.setOnClickListener(v -> startActivity(new Intent(DetectMenuActivity.this, DetectModeActivity.class)));
    }
}
