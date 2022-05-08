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
        Button logforgot= (Button) findViewById(R.id.login_forgot);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

      /*  logforgot.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,FindpwActivity.class);
                startActivity((intent));
            }
        });*/
    }

}
