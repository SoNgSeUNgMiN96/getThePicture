package com.example.getthepicture_2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity{

    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;

    // 이메일과 비밀번호 입력받는 edittext
    private EditText edtEmail;
    private EditText edtPassword;

    //  입력받는 이메일과 비밀번호를 저장할 String 변수를 ""로 초기화
    private String email = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.signup_email);
        edtPassword = findViewById(R.id.signup_pw);

        Button forgotemail=(Button) findViewById(R.id.email_forgot);
        Button forgotpw=(Button) findViewById(R.id.pw_forgot);

        forgotpw.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, FindPwActivity.class);
                startActivity((intent));
            }
        });

        forgotemail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, FindEmailActivity.class);
                startActivity((intent));
            }
        });
    }
}
