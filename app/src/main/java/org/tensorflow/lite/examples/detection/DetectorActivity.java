/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.detection;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import static android.speech.tts.TextToSpeech.ERROR;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.time.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.tensorflow.lite.examples.detection.customview.OverlayView;
import org.tensorflow.lite.examples.detection.customview.OverlayView.DrawCallback;
import org.tensorflow.lite.examples.detection.env.BorderedText;
import org.tensorflow.lite.examples.detection.env.ImageUtils;
import org.tensorflow.lite.examples.detection.env.Logger;
import org.tensorflow.lite.examples.detection.tflite.Classifier;
import org.tensorflow.lite.examples.detection.tflite.YoloV4Classifier;
import org.tensorflow.lite.examples.detection.tracking.MultiBoxTracker;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();

    private static final int TF_OD_API_INPUT_SIZE = 416;
    private TextToSpeech tts;              // TTS 변수 선언
    private static final boolean TF_OD_API_IS_QUANTIZED = false;
    private static final String TF_OD_API_MODEL_FILE = "yolov4-416-fp32.tflite";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco.txt";
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    private static final boolean MAINTAIN_ASPECT = false;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;
    OverlayView trackingOverlay;
    private Integer sensorOrientation;

    LocalTime  before = LocalTime.now();


    private Classifier detector;

    private long lastProcessingTimeMs;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private boolean computingDetection = false;

    private long timestamp = 0;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private MultiBoxTracker tracker;
    private BorderedText borderedText;


    HashMap<String,String> labelDicReverse = new HashMap<String,String>(){
        {
            put("cup","컵");     //mouse
            put("mouse","마우스");     //keyboard
            put("keyboard","키보드");
            put("cell phone","휴대폰");
            put("clock","시계");
            put("book","책");
            put("person","사람");     //mouse
            put("dog","강아지");
            put("laptop","컴퓨터");
            put("suitcase","가방");
        }
    };



    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        tracker = new MultiBoxTracker(this);

        int cropSize = TF_OD_API_INPUT_SIZE;

        // TTS를 생성하고 OnInitListener로 초기화 한다.        textToSpeach 기능.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });


        try {

            detector =
                    YoloV4Classifier.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_IS_QUANTIZED);
//            detector = TFLiteObjectDetectionAPIModel.create(
//                    getAssets(),
//                    TF_OD_API_MODEL_FILE,
//                    TF_OD_API_LABELS_FILE,
//                    TF_OD_API_INPUT_SIZE,
//                    TF_OD_API_IS_QUANTIZED);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing classifier!");
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(
                new DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {

                        String text = "", objName, findObj,decodeObj;
                        Double x,y;
                        String[] objectInfo, temp;

                        text += tracker.draw(canvas,text,getApplicationContext());

                        if (isDebug()) {
                            tracker.drawDebug(canvas, getApplicationContext());
                        }

                        LocalTime now = LocalTime.now();        //현재시각
                        Duration d1 = Duration.between(before,now); //마지막 동기화 시각과의 시간차이

                        if(text.length()>0&&(d1.getSeconds()>3)){       //이게 콜백함수가 연속되어서 3초에 한번만 읽도록 설정.
                            before = now;
                            objectInfo = text.split(" ");
                            findObj = getIntent().getStringExtra("obj");
                            Boolean success= false;
                            decodeObj = labelDicReverse.get(findObj);

                            for (int i = 0; i < objectInfo.length; i++) {
                                temp = objectInfo[i].split("x=");
                                objName = temp[0];

                                //띄어쓰기된 물건을 찾을 때 문제
                                Toast.makeText(getApplicationContext(),"찾을 물건 = "+findObj+"지금 물건 = "+objName,Toast.LENGTH_LONG).show();

                                if(objName.equals(findObj)){
                                    success = true;
                                    temp = temp[1].split("y=");
                                                                        x = Double.parseDouble(temp[0]);
                                    y = Double.parseDouble(temp[1]);
                                    if(x<canvas.getWidth()/3){      //좌측 영역
                                        if(y<canvas.getHeight()/3){     //좌상단 영역
                                            tts.speak("좌측 상단에 "+decodeObj+" 있습니다.",TextToSpeech.QUEUE_FLUSH, null);
                                        }else if(y<canvas.getHeight()*2/3){ //좌중앙
                                            tts.speak("좌측에 "+decodeObj+" 있습니다.",TextToSpeech.QUEUE_FLUSH, null);
                                        }else{      //좌하단
                                            tts.speak("좌측 하단에 "+decodeObj+" 있습니다.",TextToSpeech.QUEUE_FLUSH, null);
                                        }
                                    }else if(x<canvas.getWidth()*2/3){      //중앙 영역
                                        if(y<canvas.getHeight()/3){     //좌상단 영역
                                            tts.speak("상단에 "+decodeObj+" 있습니다.",TextToSpeech.QUEUE_FLUSH, null);
                                        }else if(y<canvas.getHeight()*2/3){ //좌중앙
                                            tts.speak("현재 중앙에 "+decodeObj+" 있습니다.",TextToSpeech.QUEUE_FLUSH, null);
                                        }else{      //좌하단
                                            tts.speak("하단에 "+decodeObj+" 있습니다.",TextToSpeech.QUEUE_FLUSH, null);
                                        }
                                    }else{
                                        if(y<canvas.getHeight()/3){     //좌상단 영역
                                            tts.speak("우측 상단에 "+decodeObj+" 있습니다.",TextToSpeech.QUEUE_FLUSH, null);
                                        }else if(y<canvas.getHeight()*2/3){ //좌중앙
                                            tts.speak("우측에 "+decodeObj+" 있습니다.",TextToSpeech.QUEUE_FLUSH, null);
                                        }else{      //좌하단
                                            tts.speak("우측 하단에 "+decodeObj+" 있습니다.",TextToSpeech.QUEUE_FLUSH, null);
                                        }
                                    }
                                }
                            }
                            if(!success) tts.speak("화면에 "+decodeObj+" 없습니다.",TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                });

        tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
    }

    @Override
    protected void processImage() {
        // TTS를 생성하고 OnInitListener로 초기화 한다.



        Toast.makeText(getApplicationContext(),"나오나",Toast.LENGTH_SHORT);



        ++timestamp;
        final long currTimestamp = timestamp;
        trackingOverlay.postInvalidate();

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;
        LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap);
        }

        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.i("Running detection on image " + currTimestamp);
                        final long startTime = SystemClock.uptimeMillis();
                        final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                        Log.e("CHECK", "run: " + results.size());

                        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                        final Canvas canvas = new Canvas(cropCopyBitmap);
                        final Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setStyle(Style.STROKE);
                        paint.setStrokeWidth(2.0f);

                        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                        switch (MODE) {
                            case TF_OD_API:
                                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                                break;
                        }

                        final List<Classifier.Recognition> mappedRecognitions =
                                new LinkedList<Classifier.Recognition>();

                        for (final Classifier.Recognition result : results) {
                            final RectF location = result.getLocation();
                            if (location != null && result.getConfidence() >= minimumConfidence) {
                                canvas.drawRect(location, paint);

                                cropToFrameTransform.mapRect(location);

                                result.setLocation(location);
                                mappedRecognitions.add(result);
                            }
                        }

                        tracker.trackResults(mappedRecognitions, currTimestamp);
                        trackingOverlay.postInvalidate();

                        computingDetection = false;

                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showFrameInfo(previewWidth + "x" + previewHeight);
                                        showCropInfo(cropCopyBitmap.getWidth() + "x" + cropCopyBitmap.getHeight());
                                        showInference(lastProcessingTimeMs + "ms");
                                    }
                                });
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.tfe_od_camera_connection_fragment_tracking;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    // Which detection model to use: by default uses Tensorflow Object Detection API frozen
    // checkpoints.
    private enum DetectorMode {
        TF_OD_API;
    }

    @Override
    protected void setUseNNAPI(final boolean isChecked) {
        runInBackground(() -> detector.setUseNNAPI(isChecked));
    }

    @Override
    protected void setNumThreads(final int numThreads) {
        runInBackground(() -> detector.setNumThreads(numThreads));
    }
}
