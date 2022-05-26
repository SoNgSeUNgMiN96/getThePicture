package org.tensorflow.lite.examples.detection;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.DIRECTORY_PICTURES;

import android.annotation.TargetApi;
import android.app.Activity;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static android.speech.tts.TextToSpeech.ERROR;
import android.speech.tts.TextToSpeech;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;


public class ImageActivity extends AppCompatActivity {

    private TextToSpeech tts;              // TTS 변수 선언


    static class MatchInfo{
        double avr;
        double minDist;
        int goodMatch;
        double score;
        String filePath;

        public MatchInfo(double avr, double minDist, String filePath,int goodMatch) {
            this.avr = avr;
            this.minDist = minDist;
            this.filePath = filePath;
            this.goodMatch = goodMatch;
        }
    }

    private static final String TAG = "AndroidOpenCv";
    private static final int REQ_CODE_SELECT_IMAGE = 100;
    private Bitmap mInputImage;
    private Bitmap mDesImage;
    private ImageView pickImageView;
    private TextView matchText;
    private Button btnCancel;

    private boolean mIsOpenCVReady = false;
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    private Uri photoUri;

    private MediaScanner mMediaScanner; // 사진 저장 시 갤러리 폴더에 바로 반영사항을 업데이트 시켜주려면 이 것이 필요하다(미디어 스캐닝)



    public native String orbFeatureJNI2(long inputImage, long outputImage);

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }



    public void orbFeatureUsingJNI(Bitmap mInputImage,Bitmap mDesImg) {
        if (!mIsOpenCVReady) {
            return;
        }
        Mat src = new Mat();
        Utils.bitmapToMat(mInputImage, src);
        Mat dst = new Mat();
        Utils.bitmapToMat(mDesImg, dst);

        String a = orbFeatureJNI2(src.getNativeObjAddr(), dst.getNativeObjAddr());
        matchText.setText(a);
        Utils.matToBitmap(dst, mDesImg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });




        setContentView(R.layout.activity_image);
        pickImageView = findViewById(R.id.pivk_iv);
        matchText = findViewById(R.id.matchText);
        btnCancel = findViewById(R.id.btn_cancel);
        mMediaScanner = MediaScanner.getInstance(getApplicationContext());

        btnCancel.setOnClickListener(v-> {
            tts.speak("취소합니다",TextToSpeech.QUEUE_FLUSH, null);
            startActivity(new Intent(ImageActivity.this, DetectMenuActivity.class));
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(PERMISSIONS)) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            mIsOpenCVReady = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SELECT_IMAGE) {

        }else if(requestCode ==0 && resultCode ==RESULT_OK){    //카메라로 찍어온것.
            Bundle extras = data.getExtras();
            mDesImage =  (Bitmap) extras.get("data");
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            pickImageView.setImageBitmap(mDesImage);

            //폴더 경로를 지정해줍니다.
            String path =Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES) + File.separator + "FINDU" + File.separator;
            
            File f = new File(path);
            File[] files = f.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().toLowerCase(Locale.US).endsWith(".jpg"); //확장자
                }
            });

            MatchInfo[] matchInfos = new MatchInfo[files.length];
            int idx=0;

            if(files.length==0){
                Toast.makeText(getApplicationContext(),"저장된 내 물건이 없습니다.",Toast.LENGTH_LONG);
                return;
            }

            for (File file : files) {       //파일들을 선형 탐색 한다.
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                orbFeatureUsingJNI(myBitmap,mDesImage);
                double minDist=0, avr=0, score=0;
                int goodMatch=0;
                String filePath="";
                //줄 바꿈을 기준으로 문자열을 찢어준다.  temp[0] = "minDist = 값", temp[1] = "avr = 값", temp[2] = "gootMatches.size() = 값" , temp[3] = "match여부", 파일경로 = file.getAbsolutePath()
                String[] temp = matchText.getText().toString().split("\\n");

                minDist = Double.parseDouble(temp[0].split(" = ")[1]);
                avr = Double.parseDouble(temp[1].split(" = ")[1]);
                filePath =file.getAbsolutePath();
                goodMatch = Integer.parseInt(temp[2].split(" = ")[1]);

                matchInfos[idx++] = new MatchInfo(avr,minDist,filePath,goodMatch);
                matchText.setText(matchText.getText()+"\n"+file.getAbsolutePath());
            }


            Arrays.sort(matchInfos, new Comparator<MatchInfo>() {
                @Override
                public int compare(MatchInfo matchInfo, MatchInfo t1) {

                    return (-matchInfo.avr-matchInfo.minDist<-t1.avr-t1.minDist)? 1:-1;
                }
            });
            String resultName =matchInfos[0].filePath.split("FINDU/")[1];

            if(matchInfos[0].minDist>90||matchInfos[0].goodMatch<4){

                matchText.setText("가장 유사한 물건은 "+(resultName.split("\\.")[0])+"입니다.\n 일치율이 적습니다. \ngood = "+matchInfos[0].goodMatch+"\n min = "+matchInfos[0].minDist+"\navr = "+matchInfos[0].avr);
                tts.speak(matchText.getText().toString(),TextToSpeech.QUEUE_FLUSH, null);
            }else{
                matchText.setText("가장 유사한 물건은 "+(resultName.split("\\.")[0])+"입니다.\n 일치율이 높습니다. \ngood = "+matchInfos[0].goodMatch+"\n min = "+matchInfos[0].minDist+"\navr = "+matchInfos[0].avr);
                tts.speak(matchText.getText().toString(),TextToSpeech.QUEUE_FLUSH, null);
            }


        }
    }

    public void onDestroy() {
        super.onDestroy();

        mInputImage.recycle();
        if (mInputImage != null) {
            mInputImage = null;
        }
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    public void onButtonClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //Toast myToast = Toast.makeText(this.getApplicationContext(), "여기가 언제 실행되는 onButtonClicked",Toast.LENGTH_LONG);
        //myToast.show();
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }



    public void onPickButtonClicked(View view){
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera,0);
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

    public String getImagePathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imgPath = cursor.getString(idx);
            cursor.close();
            return imgPath;
        }
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

        AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
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


    public static Uri getMediaUriFromPath(Context context, String filePath) {
        File imageFile = new File(filePath);
        Cursor cursor = context.getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ", new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor .getColumnIndex(MediaStore.MediaColumns._ID)); Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) { ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath); return context.getContentResolver().insert( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else { return null; }
        }
    }

}