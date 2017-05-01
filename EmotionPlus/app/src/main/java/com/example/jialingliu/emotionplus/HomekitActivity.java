package com.example.jialingliu.emotionplus;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.util.Log;
import android.view.View;


import com.loopj.android.http.*;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.quickstart.PHHomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


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
    private int index = 0;
    private JSONArray songs = new JSONArray();
    private boolean play = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homekit);
        //textView = (TextView) findViewById(R.id.helloworld1);
        // textView.setText("Here is the homekit");

        phHueSDK = PHHueSDK.create();
        setBrightness(100);


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

        findViewById(R.id.playSong).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(play==true) {
                    findViewById(R.id.playSong).setBackgroundResource(R.drawable.icon_musicplay);
                    play=false;
                    pauseMusic();
                }else{
                    findViewById(R.id.playSong).setBackgroundResource(R.drawable.icon_musicstop);
                    play=true;
                    resumeMusic();
                }
            }
        });


        findViewById(R.id.previousSong).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index>0){
                    index--;
                    try{
                        playMusic();
                    }catch(Exception e){

                    }
                }
            }
        });

        findViewById(R.id.nextSong).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index<songs.length()-1){
                    index++;
                    try{
                        playMusic();
                    }catch(Exception e){

                    }
                }
            }
        });

       /* try {
            getEmotion();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }

    public void getEmotion() throws JSONException{
        //stopThread();
        HttpUtils.get("http://172.29.92.105:8888/emotion", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i("taghere","response"+response);
                try {
                    sendEmotion(response);
                }catch(Exception e){

                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                //JSONObject firstEvent = timeline.get(0);
                //String tweetText = firstEvent.getString("text");

                // Do something with the response
                System.out.println(timeline);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String x, Throwable e){
                Log.i("taghere1","failure");
            }

        });
    }

    public void sendEmotion(JSONObject response) throws Exception{

        StringEntity entity = new StringEntity(response.toString());


        HttpUtils.post(null,"http://172.29.93.218:3000/recommendation/music", entity, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i("taghere","response"+response);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                //JSONObject firstEvent = timeline.get(0);
                //String tweetText = firstEvent.getString("text");

                // Do something with the response
                System.out.println(timeline);
                songs=timeline;
                index=0;
                try {
                    playMusic();
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String x, Throwable e){
                Log.i("taghere1","failure");
            }

        });
    }



    public void playMusic() throws Exception{

        JSONObject song = (JSONObject)songs.get(index);
        String name = song.get("name").toString();
        String singer = song.get("singer").toString();
        ((TextView)findViewById(R.id.textViewSong)).setText(name);
        ((TextView)findViewById(R.id.textViewSinger)).setText(singer);
        String picURL =song.get("img").toString();
        //URL url = new URL("http://"+picURL);
        //Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        //Bitmap bmp = BitmapFactory.decodeStream((InputStream)new URL("http://"+picURL).getContent());
        //BitmapDrawable background = new BitmapDrawable(bmp);
        //((ImageView)findViewById(R.id.cover)).setImageBitmap(bmp);
        new DownloadImageTask((ImageView) findViewById(R.id.cover))
                .execute("http://"+picURL);
        //findViewById(R.id.cover).setBackground(background);
        play=true;
        findViewById(R.id.playSong).setBackgroundResource(R.drawable.icon_musicstop);

        Log.i("name",name);
        HttpUtils.get("http://172.29.93.218:8080/music?song="+name, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i("taghere","response"+response);


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                //JSONObject firstEvent = timeline.get(0);
                //String tweetText = firstEvent.getString("text");

                // Do something with the response
                //findViewById(R.id.playSong).setBackgroundResource(R.drawable.icon_musicplay);

                System.out.println(timeline);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String x, Throwable e){
                Log.i("taghere1","failure");
            }

        });


    }

    public void pauseMusic(){
        HttpUtils.get("http://172.29.93.218:8080/music/pause", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i("taghere","response"+response);
                findViewById(R.id.playSong).setBackgroundResource(R.drawable.icon_musicplay);
                play=false;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                //JSONObject firstEvent = timeline.get(0);
                //String tweetText = firstEvent.getString("text");

                // Do something with the response
                findViewById(R.id.playSong).setBackgroundResource(R.drawable.icon_musicplay);
                play=false;
                System.out.println(timeline);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String x, Throwable e){
                Log.i("taghere1","failure");
            }

        });

    }

    public void resumeMusic(){
        HttpUtils.get("http://172.29.93.218:8080/music/resume", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i("taghere","response"+response);
                findViewById(R.id.playSong).setBackgroundResource(R.drawable.icon_musicstop);
                play=true;

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                //JSONObject firstEvent = timeline.get(0);
                //String tweetText = firstEvent.getString("text");

                // Do something with the response
                findViewById(R.id.playSong).setBackgroundResource(R.drawable.icon_musicstop);
                play=true;
                System.out.println(timeline);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String x, Throwable e){
                Log.i("taghere1","failure");
            }

        });

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

    public void setBrightness(int bright) {
        stopThread();
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        Integer brightness = bright;
        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setOn(true);
            lightState.setBrightness(brightness);
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
