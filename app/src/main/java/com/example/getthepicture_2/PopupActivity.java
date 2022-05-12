package com.example.getthepicture_2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        Button imagebtn= (Button) findViewById(R.id.btn_image);
        Button submitbtn= (Button) findViewById(R.id.btn_submit);
        Button cancelbtn= (Button) findViewById(R.id.btn_cancel);

        cancelbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PopupActivity.this,DetectActivity.class);
                startActivity((intent));
            }
        });
        imagebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PopupActivity.this,PhotoActivity.class);
                startActivity((intent));
            }
        });

    }
}
