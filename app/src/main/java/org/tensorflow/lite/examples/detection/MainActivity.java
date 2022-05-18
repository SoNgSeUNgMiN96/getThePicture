package org.tensorflow.lite.examples.detection;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.speech.tts.TextToSpeech.ERROR;
import android.speech.tts.TextToSpeech;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private TextToSpeech tts;              // TTS 변수 선언


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button startbtn=(Button) findViewById(R.id.startbtn);
        Button signinbtn=(Button) findViewById(R.id.signinbtn);
        Button loginbtn=(Button) findViewById(R.id.loginbtn);


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });



        signinbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                tts.speak("회원가입 화면으로 이동합니다.",TextToSpeech.QUEUE_FLUSH, null);
                Intent intent=new Intent(MainActivity.this, SigninActivity.class);
                startActivity((intent));
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                tts.speak("로그인 화면으로 이동합니다.",TextToSpeech.QUEUE_FLUSH, null);
                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                startActivity((intent));
            }
        });
        startbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                tts.speak("물건 찾기 및 등록 메뉴로 이동합니다. 좌측 상단은 물건 등록, 우측상단은 물건찾기, 하단은 음성 인식 버튼입니다.",TextToSpeech.QUEUE_FLUSH, null);
                Intent intent=new Intent(MainActivity.this,DetectMenuActivity.class);
                startActivity((intent));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}