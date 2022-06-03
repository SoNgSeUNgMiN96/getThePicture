package org.tensorflow.lite.examples.detection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity_2 extends AppCompatActivity {
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Button startbtn = (Button) findViewById(R.id.startbtn);

        startbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity_2.this,DetectMenuActivity.class);
                startActivity((intent));
            }
        });
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(MainActivity_2.this,MainActivity.class);
        startActivity((intent));
        finish();
    }
}
