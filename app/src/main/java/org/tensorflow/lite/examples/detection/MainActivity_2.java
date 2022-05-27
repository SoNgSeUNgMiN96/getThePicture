package org.tensorflow.lite.examples.detection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity_2 extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onStart() {
        super.onStart();
        // 활동을 초기화할 때 사용자가 현재 로그인되어 있는지 확인합니다.
        //FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Button startbtn = (Button) findViewById(R.id.startbtn);
        Button logoutbtn = (Button) findViewById(R.id.logoutbtn);


        logoutbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity_2.this,MainActivity.class);
                startActivity((intent));
            }
        });

        startbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity_2.this,DetectMenuActivity.class);
                startActivity((intent));
            }
        });
    }
}
