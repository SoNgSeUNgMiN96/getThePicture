package org.tensorflow.lite.examples.detection;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import android.speech.tts.TextToSpeech;
import static android.speech.tts.TextToSpeech.ERROR;


import static android.os.Environment.DIRECTORY_PICTURES;



public class PopupActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    private Uri photoUri;
    private Button btnCancel;
    private Button imgList;
    private TextToSpeech tts;              // TTS 변수 선언

    private MediaScanner mMediaScanner; // 사진 저장 시 갤러리 폴더에 바로 반영사항을 업데이트 시켜주려면 이 것이 필요하다(미디어 스캐닝)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_save);

        // 사진 저장 후 미디어 스캐닝을 돌려줘야 갤러리에 반영됨.
        mMediaScanner = MediaScanner.getInstance(getApplicationContext());
        btnCancel = (Button)findViewById(R.id.btn_cancel);
        imgList = (Button)findViewById(R.id.img_list);

        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        // 권한 체크
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

        findViewById(R.id.iv_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {

                    }


                    if (photoFile != null) {
                        photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

        btnCancel.setOnClickListener(v-> {
            tts.speak("취소합니다. ",TextToSpeech.QUEUE_FLUSH, null);
            startActivity(new Intent(PopupActivity.this, DetectMenuActivity.class));
        });

        imgList.setOnClickListener(v-> {
            tts.speak("이미지 목록으로 이동합니다. ",TextToSpeech.QUEUE_FLUSH, null);
            startActivity(new Intent(PopupActivity.this, ImgListActivity.class));
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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;

            try {
                exif = new ExifInterface(imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegress(exifOrientation);
            } else {
                exifDegree = 0;
            }

            // 이미지 뷰에 비트맵을 set하여 이미지 표현
            ((ImageView) findViewById(R.id.iv_image)).setImageBitmap(rotate(bitmap, exifDegree));


            //저장 버튼
            findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText editText = findViewById(R.id.popup_name);

                    String result = "";
                    String filename = editText.getText().toString();

                    tts.speak(filename+"사진을 저장하였습니다.",TextToSpeech.QUEUE_FLUSH, null);

                    String strFolderName = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES) + File.separator + "FINDU" + File.separator;
                    File file = new File(strFolderName);
                    if (!file.exists())
                        file.mkdirs();

                    File f = new File(strFolderName + "/" + filename + ".jpg");
                    result = f.getPath();

                    FileOutputStream fOut = null;
                    try {
                        fOut = new FileOutputStream(f);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        result = "Save Error fOut";
                    }

                    // 비트맵 사진 폴더 경로에 저장
                    rotate(bitmap, exifDegree).compress(Bitmap.CompressFormat.JPEG, 70, fOut);

                    try {
                        fOut.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fOut.close();
                        // 방금 저장된 사진을 갤러리 폴더 반영 및 최신화
                        mMediaScanner.mediaScanning(strFolderName + "/" + filename + ".jpg");
                    } catch (IOException e) {
                        e.printStackTrace();
                        result = "File close Error";
                    }

                    Toast.makeText(getApplicationContext(), "등록되었습니다.", Toast.LENGTH_SHORT).show(); //등록 이후는 다시 메뉴로 돌아간다.
                    Intent intent=new Intent(PopupActivity.this, DetectModeActivity.class);
                    startActivity((intent));
                }
            });

        }
    }

    private int exifOrientationToDegress(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한이 허용됨", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한이 거부됨", Toast.LENGTH_SHORT).show();
        }
    };
}
