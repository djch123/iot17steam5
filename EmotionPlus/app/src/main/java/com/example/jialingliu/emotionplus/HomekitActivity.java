package com.example.jialingliu.emotionplus;


import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.util.Log;
import android.view.View;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;

import java.util.List;
import java.util.Map;


/**
 * @author jialingliu
 */

public class HomekitActivity extends AppCompatActivity {
    TextView textView;
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE = 65535;
    public static final String TAG = "QuickStart";

    private Thread brightControllThread = null;
    private int brightThreadState = 0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homekit);
        //textView = (TextView) findViewById(R.id.helloworld1);
        // textView.setText("Here is the homekit");
        phHueSDK = PHHueSDK.create();
        findViewById(R.id.buttonOn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOn();
            }
        });
        findViewById(R.id.buttonOff).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOff();
            }
        });
    }

    public void getEmotion(){
        stopThread();

    }

    public void playMusic(){

    }
    public void turnOn() {
        stopThread();
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setOn(true);
            lightState.setBrightness(254);
            bridge.updateLightState(light, lightState, listener);
        }
    }

    public void turnOff() {
        stopThread();
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setOn(false);
            bridge.updateLightState(light, lightState, listener);
        }
    }

    private void stopThread() {
        if (brightControllThread != null) {

            brightThreadState = 0;
            try {
                brightControllThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        brightThreadState = 1;
    }

    // If you want to handle the response from the bridge, create a PHLightListener object.
    PHLightListener listener = new PHLightListener() {

        @Override
        public void onSuccess() {
            Log.d(TAG, "success");
        }

        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
            Log.w(TAG, "Light has updated");
        }

        @Override
        public void onError(int arg0, String arg1) {
        }

        @Override
        public void onReceivingLightDetails(PHLight arg0) {
        }

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {
        }

        @Override
        public void onSearchComplete() {
        }
    };

    @Override
    protected void onDestroy() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {

            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }

            phHueSDK.disconnect(bridge);
            super.onDestroy();
        }
    }
    public void connect(String status){

    }
}
