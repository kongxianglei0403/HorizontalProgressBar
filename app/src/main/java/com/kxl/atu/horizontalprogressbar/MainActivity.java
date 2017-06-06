package com.kxl.atu.horizontalprogressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kxl.atu.horizontalprogressbar.Views.HorizontalProgressBar;

public class MainActivity extends AppCompatActivity {

    Button btn;
    HorizontalProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        progressBar = (HorizontalProgressBar) findViewById(R.id.progress);
        progressBar.setProgress(60).setProgressListener(new HorizontalProgressBar.ProgressListener() {
            @Override
            public void currentProgressListener(float currentProgress) {

            }
        });
        
        progressBar.startProgressAnimation();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setProgress(100);
            }
        });
    }
}
