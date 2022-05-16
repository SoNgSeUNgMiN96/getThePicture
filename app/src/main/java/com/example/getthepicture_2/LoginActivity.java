package com.example.getthepicture_2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button forgotid=(Button) findViewById(R.id.email_forgot);
        Button forgotpw=(Button) findViewById(R.id.pw_forgot);
        forgotpw.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,FindpwActivity.class);
                startActivity((intent));
            }
        });
        forgotid.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,FindmailActivity.class);
                startActivity((intent));
            }
        });
    }
}
