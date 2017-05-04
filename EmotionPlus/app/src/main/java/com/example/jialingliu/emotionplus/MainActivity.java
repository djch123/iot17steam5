package com.example.jialingliu.emotionplus;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.quickstart.PHHomeActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private ImageView emotionView;
    private ImageView tapView;
    private TextView tapTextView;
    private Intent intent;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_main:
                    intent.setClass(getApplicationContext(), HistoryActivity.class);
                    startActivity(intent);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            intent.setClass(getApplicationContext(), HistoryActivity.class);
                            startActivity(intent);
                        }
                    });
                    tapTextView.setText(R.string.tap_emotion);
                    return true;
                case R.id.navigation_homekit:

                   // if(hueOn==true) {
                     //   intent.setClass(getApplicationContext(), HomekitActivity.class);
                       // startActivity(intent);
                    //}
                    //else{
                        intent.setClass(getApplicationContext(), PHHomeActivity.class);
                        startActivity(intent);
                    //}

                    imageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                           // if(hueOn==true) {
                             //   intent.setClass(getApplicationContext(), HomekitActivity.class);
                               // startActivity(intent);
                            //}
                            //else{
                                intent.setClass(getApplicationContext(), PHHomeActivity.class);
                                startActivity(intent);
                            //}
                        }
                    });
                    tapTextView.setText(R.string.tap_homekit);
                    return true;
                case R.id.navigation_recommendation:
                    intent.setClass(getApplicationContext(), RecommendationActivity.class);
                    startActivity(intent);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            intent.setClass(getApplicationContext(), RecommendationActivity.class);
                            startActivity(intent);
                        }
                    });
                    tapTextView.setText(R.string.tap_recommendations);
                    return true;
//                case R.id.navigation_game:
////                    mTextMessage.setText(R.string.title_game);
//                    intent.setClass(getApplicationContext(), GameActivity.class);
//                    startActivity(intent);
//                    imageView.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            intent.setClass(getApplicationContext(), GameActivity.class);
//                            startActivity(intent);
//                        }
//                    });
//                    tapTextView.setText(R.string.tap_game);
//                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent();
        imageView = (ImageView) findViewById(R.id.mainimage);
        imageView.setImageResource(R.drawable.background);

        emotionView = (ImageView) findViewById(R.id.emotion);
        emotionView.setImageResource(R.drawable.emoji);

        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent.setClass(getApplicationContext(), HistoryActivity.class);
                startActivity(intent);
            }
        });

        tapView = (ImageView) findViewById(R.id.tap);
        tapView.setImageResource(R.drawable.tap);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(700);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(700);
        fadeOut.setDuration(700);
        fadeOut.setRepeatCount(Animation.INFINITE);

        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);

        tapView.setAnimation(animation);

        tapTextView = (TextView) findViewById(R.id.taptext);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/DancingScript-Regular.ttf");
        tapTextView.setTypeface(face);
        tapTextView.setText(R.string.tap_emotion);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
