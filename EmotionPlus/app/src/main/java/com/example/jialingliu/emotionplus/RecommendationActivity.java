package com.example.jialingliu.emotionplus;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * @author jialingliu
 */

public class RecommendationActivity extends AppCompatActivity {
    private static final int cnt = 6;
    private TextView restaurantText;
    private TextView sportsText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        restaurantText = (TextView) findViewById(R.id.restauranttext);
        sportsText = (TextView) findViewById(R.id.sportstext);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/DancingScript-Regular.ttf");
        restaurantText.setTypeface(face);
        sportsText.setTypeface(face);
    }
}
