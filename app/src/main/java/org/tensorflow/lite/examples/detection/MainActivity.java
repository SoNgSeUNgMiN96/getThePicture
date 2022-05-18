package org.tensorflow.lite.examples.detection;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button startbtn=(Button) findViewById(R.id.startbtn);
        Button signinbtn=(Button) findViewById(R.id.signinbtn);
        Button loginbtn=(Button) findViewById(R.id.loginbtn);
        signinbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, SigninActivity.class);
                startActivity((intent));
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                startActivity((intent));
            }
        });
        startbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,DetectMenuActivity.class);
                startActivity((intent));
            }
        });
    }
}