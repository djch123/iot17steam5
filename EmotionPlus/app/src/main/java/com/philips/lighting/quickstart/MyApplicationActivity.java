package com.philips.lighting.quickstart;

import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.example.jialingliu.emotionplus.R;

/**
 * MyApplicationActivity - The starting point for creating your own Hue App.
 * Currently contains a simple view with a button to change your lights to random colours.  Remove this and add your own app implementation here! Have fun!
 *
 * @author SteveyO
 */
public class MyApplicationActivity extends Activity {
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE = 65535;
    public static final String TAG = "QuickStart";

    private Thread brightControllThread = null;
    private int brightThreadState = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
        phHueSDK = PHHueSDK.create();
       /* findViewById(R.id.buttonRand).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                randomLights();
            }
        });
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
        findViewById(R.id.buttonBreathe).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                breathe();
            }
        });
        findViewById(R.id.buttonAlert).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                alert();
            }
        });*/

    }

    /**
     * random brightness
     */
    public void randomLights() {
        stopThread();
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        Integer brightness = new Random().nextInt(255);
        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setOn(true);
            lightState.setBrightness(brightness);
            bridge.updateLightState(light, lightState, listener);
        }
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

    public void breathe() {
        stopThread();
        brightControllThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                long counter = 0;
                long period = 5000;
                PHBridge bridge = phHueSDK.getSelectedBridge();
                List<PHLight> allLights = bridge.getResourceCache().getAllLights();
                for (PHLight light : allLights) {
                    PHLightState lightState = new PHLightState();
                    lightState.setOn(true);
                    bridge.updateLightState(light, lightState, listener);
                }
                while (brightThreadState != 0) {
                    counter = (System.currentTimeMillis() - startTime) % period;
                    for (PHLight light : allLights) {
                        PHLightState lightState = new PHLightState();
                        lightState.setBrightness((int) ((period / 2 - Math.abs(counter - period / 2f)) * 200 / (period / 2)), true);
                        bridge.updateLightState(light, lightState, listener);
                    }
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        brightControllThread.start();
    }

    public void alert() {
        stopThread();
        brightControllThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                long counter = 0;
                boolean on = true;
                PHBridge bridge = phHueSDK.getSelectedBridge();
                List<PHLight> allLights = bridge.getResourceCache().getAllLights();
                for (PHLight light : allLights) {
                    PHLightState lightState = new PHLightState();
                    lightState.setOn(true);
                    bridge.updateLightState(light, lightState, listener);
                }

                while (brightThreadState != 0) {
                    if (on) {
                        for (PHLight light : allLights) {
                            PHLightState lightState = new PHLightState();
                            lightState.setBrightness(254);
                            bridge.updateLightState(light, lightState, listener);
                        }
                        on = false;
                    } else {
                        for (PHLight light : allLights) {
                            PHLightState lightState = new PHLightState();
                            lightState.setBrightness(0);
                            bridge.updateLightState(light, lightState, listener);
                        }
                        on = true;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        brightControllThread.start();
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
}
