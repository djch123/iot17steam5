package com.example.jialingliu.emotionplus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * @author jialingliu
 */

public class HistoryActivity extends AppCompatActivity {
    TextView textView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        textView = (TextView) findViewById(R.id.helloworld);
        textView.setText("Here is the history");
    }
}
