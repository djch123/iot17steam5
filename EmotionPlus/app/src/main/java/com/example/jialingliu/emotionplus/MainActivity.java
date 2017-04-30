package com.example.jialingliu.emotionplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Intent intent;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_main:
//                    mTextMessage.setText(R.string.title_main);
                    intent.setClass(getApplicationContext(), HistoryActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_homekit:
//                    mTextMessage.setText(R.string.title_homekit);
                    intent.setClass(getApplicationContext(), HomekitActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_recommendation:
//                    mTextMessage.setText(R.string.title_recommendation);
                    intent.setClass(getApplicationContext(), RecommendationActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_game:
//                    mTextMessage.setText(R.string.title_game);
                    intent.setClass(getApplicationContext(), GameActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent();
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
