package org.tensorflow.lite.examples.detection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;

    private EditText edtPhone;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtPassword2;

    //  입력받는 휴대폰 번호, 이메일, 비밀번호를 저장할 String 변수를 ""로 초기화
    private String phone = "";
    private String email = "";
    private String password = "";
    private String password2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();
        edtPhone = findViewById(R.id.signup_phone);
        edtEmail = findViewById(R.id.signup_email);
        edtPassword = findViewById(R.id.signup_pw);
        edtPassword2 =findViewById(R.id.signup_pw2);
    }

    public void signUp(View view) {
        phone = edtPhone.getText().toString();
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();
        password2 = edtPassword2.getText().toString();

        if(isValidEmail() && isValidPasswd()) {
            createUser(email, password);
        }
    }

    // 이메일 유효성 검사 (실존 이메일인지 검사하도록? / 이메일 중복 검사?)
    private boolean isValidEmail() {
        if (email.isEmpty()) {
            // 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (password.isEmpty()) {
            // 비밀번호 공백
            Log.v("test","email isEmpty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches() || !password.equals(password2)) {
            Log.v("test","pw not match or pw != pw2");
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 회원가입
    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println(task);
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            Toast.makeText(SignUpActivity.this, R.string.success_signup, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                        } else {
                            // 회원가입 실패
                            Toast.makeText(SignUpActivity.this, R.string.failed_signup, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
