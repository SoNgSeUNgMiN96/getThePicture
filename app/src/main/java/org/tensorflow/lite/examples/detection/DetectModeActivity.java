package org.tensorflow.lite.examples.detection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DetectModeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detectmode);
        Button logupbtn=(Button) findViewById(R.id.btn_signup);
        Button detectbtn=(Button) findViewById(R.id.btn_detect);
        Button voicebtn=(Button) findViewById(R.id.btn_voice);
        Button myDetectBtn = (Button) findViewById(R.id.btn_my_detect);

        logupbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DetectModeActivity.this, MainActivity.class);
                startActivity((intent));
            }
        });


        voicebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Intent intent=new Intent(DetectModeActivity.this,DetectorActivity.class);
                //startActivity((intent));
            }
        });

        myDetectBtn.setOnClickListener(v-> startActivity(new Intent(DetectModeActivity.this, ImageActivity.class)));

        detectbtn.setOnClickListener(v -> startActivity(new Intent(DetectModeActivity.this, DetectorActivity.class)));
    }
}
