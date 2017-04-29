package com.example.jialingliu.emotionplus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * @author jialingliu
 */

public class RecommendationActivity extends AppCompatActivity {
    TextView textView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        textView = (TextView) findViewById(R.id.helloworld2);
        textView.setText("Here is the recommendations");
    }
}
