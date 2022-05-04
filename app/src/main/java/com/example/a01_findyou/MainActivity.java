package com.example.a01_findyou;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnOpenSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpenSignUp = (Button)findViewById(R.id.btnOpenSignUp);

        btnOpenSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                //  SignUpActivity가 다크모드로 되는거 수정. 그리고 등록 에러 뜸
            }
        });
    }
}