package org.tensorflow.lite.examples.detection;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.speech.tts.TextToSpeech;
import static android.speech.tts.TextToSpeech.ERROR;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class DetectModeActivity extends AppCompatActivity {

    private TextToSpeech tts;              // TTS 변수 선언
    SpeechRecognizer mRecognizer;
    final int PERMISSION = 1;
    TextView voiceTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detectmode);
        Button detectbtn=(Button) findViewById(R.id.btn_detect);
        Button voicebtn=(Button) findViewById(R.id.btn_voice);
        Button myDetectBtn = (Button) findViewById(R.id.btn_my_detect);
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceTextView = (TextView)findViewById(R.id.voiceTextView);


        if ( Build.VERSION.SDK_INT >= 23 ){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });



        voicebtn.setOnClickListener(v ->{
            tts.speak("띠링.",TextToSpeech.QUEUE_FLUSH, null);
            mRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(intent);
        });

        myDetectBtn.setOnClickListener(v-> {
            tts.speak("내 물건 찾기 모드로 진입합니다. 중앙 상단 버튼을 눌러 카메라를 키고, 사진을 찍어 찾습니다.",TextToSpeech.QUEUE_FLUSH, null);
            startActivity(new Intent(DetectModeActivity.this, ImageActivity.class));
        });

        detectbtn.setOnClickListener(v -> {
            tts.speak("물건 찾기 모드로 진입합니다. 화면에 여러 물건들이 감지됩니다.",TextToSpeech.QUEUE_FLUSH, null);
            startActivity(new Intent(DetectModeActivity.this, DetectorActivity.class));
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



    private RecognitionListener listener = new RecognitionListener() {

        HashMap<String,String> labelDic = new HashMap<String,String>(){
            {
                put("컵","cup");     //mouse
                put("마우스","mouse");     //keyboard
                put("키보드","keyboard");
                put("휴대폰","cell phone");
                put("스마트폰","cell phone");
                put("폰","cell phone");
                put("시계","clock");
                put("책","book");
                put("키보드","keyboard");
                put("사람","person");     //mouse
                put("인간","person");     //mouse
                put("개","dog");
                put("강아지","dog");
                put("컴퓨터","laptop");
                put("노트북","laptop");
                put("마우스","mouse");
                put("가방","suitcase");
            }
        };

        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(),"음성인식을 시작합니다.",Toast.LENGTH_SHORT).show();
            //사용자가 말하기 시작할 준비가 되면 호출
        }
        @Override
        public void onBeginningOfSpeech() {
            //사용자가 말하기 시작했을 때 호출
        }
        @Override
        public void onRmsChanged(float rmsdB) {
            //입력받는 소리의 크기를 알려줌
        }
        @Override
        public void onBufferReceived(byte[] buffer) {
            //사용자가 말을 시작하고 인식이 된 단어를 buffer에 담음
        }
        @Override
        public void onEndOfSpeech() {
            //사용자가 말하기를 중지하면 호출
        }
        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;

                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            //인식 결과가 준비되면 호출
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for(int i = 0; i < matches.size() ; i++){
                voiceTextView.setText(matches.get(i));
            }

            String result = voiceTextView.getText().toString();

            if(result.contains("내")&&result.contains("물건")&&(result.contains("찾아")||result.contains("찾기"))){
                tts.speak("내 물건 찾기 화면으로 이동합니다.",TextToSpeech.QUEUE_FLUSH, null);
                startActivity(new Intent(DetectModeActivity.this, ImageActivity.class));
            }else if((result.contains("찾아")||result.contains("찾기"))){

                Intent detectIntent = new Intent(DetectModeActivity.this, DetectorActivity.class);
                try{
                    if(result.contains("찾아"))
                        result = result.split(" ")[0];
                    else{
                        result = result.split(" ")[0];
                    }
                    Toast.makeText(getApplicationContext(),labelDic.get(result),Toast.LENGTH_LONG).show();
                    if(labelDic.containsKey(result)){
                        detectIntent.putExtra("obj",labelDic.get(result));
                        Toast.makeText(getApplicationContext(),labelDic.get(result),Toast.LENGTH_LONG).show();
                        tts.speak(result+" 찾습니다.",TextToSpeech.QUEUE_FLUSH, null);
                    }else{
                        tts.speak("해당 물건은 목록에 없어요 물건 찾기 모드로 진입합니다.",TextToSpeech.QUEUE_FLUSH, null);
                    }

                    startActivity(detectIntent);

                }catch (Exception e){
                    tts.speak("물건 찾기 모드로 진입합니다.",TextToSpeech.QUEUE_FLUSH, null);
                    startActivity(new Intent(DetectModeActivity.this, DetectorActivity.class));
                }
            }else if(result.contains("메인 화면")){
                tts.speak("메인 화면으로 이동합니다.",TextToSpeech.QUEUE_FLUSH, null);
                startActivity(new Intent(DetectModeActivity.this, MainActivity.class));
            }else if(result.contains("물건")&&result.contains("등록")){
                tts.speak("물건 등록 화면으로 이동합니다.",TextToSpeech.QUEUE_FLUSH, null);
                startActivity(new Intent(DetectModeActivity.this, PopupActivity.class));
            }else if((result.contains("사진")||result.contains("이미지"))&&(result.contains("목록")||result.contains("리스트"))){
                tts.speak("이미지 목록 화면으로 이동합니다.",TextToSpeech.QUEUE_FLUSH, null);
                startActivity(new Intent(DetectModeActivity.this, ImgListActivity.class));
            }
            else{
                tts.speak("적당한 명령어를 찾지 못했어요. 다시 시도해주세요.",TextToSpeech.QUEUE_FLUSH, null);
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            //부분 인식 결과를 사용할 수 있을 때 호출
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            //향후 이벤트를 추가하기 위해 예약
        }
    };
}
