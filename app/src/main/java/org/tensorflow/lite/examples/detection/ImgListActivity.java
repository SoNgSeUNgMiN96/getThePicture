package org.tensorflow.lite.examples.detection;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.speech.tts.TextToSpeech.ERROR;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileFilter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;



public class ImgListActivity extends AppCompatActivity {

    private TextToSpeech tts;              // TTS 변수 선언
    private Button btn_voice, btn_read;
    SpeechRecognizer mRecognizer;
    TextView voiceTextView;
    ListView listView;
    MyAdapter myAdapter;
    Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

    ArrayList<SampleData> imgDataList;



    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_img_list);
        this.InitializeMovieData();

        listView = (ListView)findViewById(R.id.listView);
        myAdapter = new MyAdapter(this,imgDataList);
        listView.setAdapter(myAdapter);

        btn_voice = (Button)findViewById(R.id.btn_voice);
        btn_read = (Button)findViewById(R.id.btn_read);
        voiceTextView = (TextView)findViewById(R.id.voiceTextView);


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        btn_voice.setOnClickListener(v ->{
            mRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(intent);
        });

        btn_read.setOnClickListener( v->readImgList());






        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(PERMISSIONS)) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }






    }

    public void readImgList(){
        String imgNameList = "이미지 목록에 ";
        tts.speak("이미지 목록에",TextToSpeech.QUEUE_FLUSH, null);
        for(int i=0;i<imgDataList.size();i++){
            if(i!=imgDataList.size()-1)
                imgNameList += imgDataList.get(i).getImgName() + " 그리고 ";
            else imgNameList += imgDataList.get(i).getImgName() +" ";
        }
        imgNameList +="가 있습니다.";
        tts.speak(imgNameList,TextToSpeech.QUEUE_FLUSH, null);
    }



    public void InitializeMovieData()
    {
        imgDataList = new ArrayList<SampleData>();

        String path = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES) + File.separator + "FINDU" + File.separator;

        File f = new File(path);
        File[] files = f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase(Locale.US).endsWith(".jpg"); //확장자
            }
        });


        if(files.length==0){
            Toast.makeText(getApplicationContext(),"저장된 내 물건이 없습니다.",Toast.LENGTH_LONG);
            return;
        }


        for (File file : files) {       //파일들을 선형 탐색 한다.

            String imgPath = file.getAbsolutePath();

            String filePath="";
            filePath =file.getAbsolutePath();
            String imgName = filePath.split("FINDU/")[1].split("\\.")[0];

            imgDataList.add(new SampleData(imgPath,imgName));
        }
    }


    public void onDestroy() {
        super.onDestroy();


        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    private RecognitionListener listener = new RecognitionListener() {

        HashMap<String, String> labelDic = new HashMap<String, String>() {
            {
                put("컵", "cup");
                put("마우스", "mouse");
                put("키보드", "keyboard");
                put("휴대폰", "cell phone");
                put("스마트폰", "cell phone");
                put("폰", "cell phone");
                put("시계", "clock");
                put("책", "book");
                put("키보드", "keyboard");
                put("사람", "person");
                put("인간", "person");
                put("개", "dog");
                put("강아지", "dog");
                put("컴퓨터", "laptop");
                put("노트북", "laptop");
                put("마우스", "mouse");
                put("가방", "suitcase");
            }
        };


        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            //부분 인식 결과를 사용할 수 있을 때 호출
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            //향후 이벤트를 추가하기 위해 예약
        }

        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            //인과가 준비되면 호출식 결
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (int i = 0; i < matches.size(); i++) {
                voiceTextView.setText(matches.get(i));
            }

            String result = voiceTextView.getText().toString();

            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

            if (result.contains("내") && result.contains("물건") && (result.contains("찾아") || result.contains("찾기"))) {
                tts.speak("내 물건 찾기 화면으로 이동합니다.", TextToSpeech.QUEUE_FLUSH, null);
                startActivity(new Intent(ImgListActivity.this, ImageActivity.class));
            } else if ((result.contains("찾아") || result.contains("찾기"))) {

                Intent detectIntent = new Intent(ImgListActivity.this, DetectorActivity.class);
                try {
                    if (result.contains("찾아"))
                        result = result.split(" 찾아")[0];
                    else {
                        result = result.split(" 찾기")[0];
                    }
                    Toast.makeText(getApplicationContext(), labelDic.get(result), Toast.LENGTH_LONG).show();
                    if (labelDic.containsKey(result)) {
                        detectIntent.putExtra("obj", labelDic.get(result));
                        Toast.makeText(getApplicationContext(), labelDic.get(result), Toast.LENGTH_LONG).show();
                        tts.speak(result + " 찾습니다.", TextToSpeech.QUEUE_FLUSH, null);
                    } else {
                        tts.speak("해당 물건은 목록에 없어요 물건 찾기 모드로 진입합니다.", TextToSpeech.QUEUE_FLUSH, null);
                    }

                    startActivity(detectIntent);

                } catch (Exception e) {
                    tts.speak("물건 찾기 모드로 진입합니다.", TextToSpeech.QUEUE_FLUSH, null);
                    startActivity(new Intent(ImgListActivity.this, DetectorActivity.class));
                }
            } else if (result.contains("메인 화면")) {
                tts.speak("메인 화면으로 이동합니다.", TextToSpeech.QUEUE_FLUSH, null);
                startActivity(new Intent(ImgListActivity.this, MainActivity.class));
            } else if (result.contains("물건") && result.contains("등록")) {
                tts.speak("물건 등록 화면으로 이동합니다.", TextToSpeech.QUEUE_FLUSH, null);
                startActivity(new Intent(ImgListActivity.this, PopupActivity.class));
            } else if (result.contains("지워")||result.contains("삭제")) {
                if(result.contains("지워")){
                    result = result.split("지워")[0];
                }else{
                    result = result.split("삭제")[0];
                }
                double max=0, temp;
                int position = -1, cursor=0;
                String simiarImg="";
                SampleData simiarData = new SampleData("","");

                for (SampleData sampleData : imgDataList) {
                    temp = similarity(sampleData.getImgName(),result);
                    if(max<temp){
                        max=temp;
                        simiarImg = sampleData.getImgName();
                        simiarData = sampleData;
                        position = cursor;
                    }
                    cursor++;
                }

                tts.speak("가장 유사한 이미지는 "+simiarImg+"이고 유사도는"+max, TextToSpeech.QUEUE_FLUSH, null);
                if(max>0.4){
                    simiarData.getImg();
                    try {
                        File file = new File(simiarData.getImg());        //sampe에 넣어둔 이미지의 path를 기준으로 파일을 읽어들인다.
                        imgDataList.remove(position);        //해당 요소를 리스트에서 삭제한다.
                         //리스트뷰 갱신
                        listView.setAdapter(myAdapter);

                        if(file.exists()){      //파일이 존재하면
                            file.delete();      //파일을 삭제한다.
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            else {
                tts.speak("적당한 명령어를 찾지 못했어요. 다시 시도해주세요.", TextToSpeech.QUEUE_FLUSH, null);
            }

        }
    };

    private double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;

        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }

        int longerLength = longer.length();
        if (longerLength == 0) return 1.0;
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }
    private int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        int[] costs = new int[s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];

                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }

                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }

            if (i > 0) costs[s2.length()] = lastValue;
        }

        return costs[s2.length()];
    }




    // permission
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE"};


    private boolean hasPermissions(String[] permissions) {
        int result;
        for (String perms : permissions) {
            result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    // permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("실행을 위해 권한 허가가 필요합니다.");
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ImgListActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }



}
