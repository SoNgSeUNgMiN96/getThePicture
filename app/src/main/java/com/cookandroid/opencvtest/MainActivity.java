package com.cookandroid.opencvtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.cookandroid.opencvtest.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'opencvtest' library on application startup.
    static {
        System.loadLibrary("opencvtest");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());
        Intent ImageAct = new Intent(MainActivity.this, ImageActivity.class);
        startActivity(ImageAct);


    }

    /**
     * A native method that is implemented by the 'opencvtest' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}