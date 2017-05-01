package com.example.jialingliu.emotionplus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.entity.BufferedHttpEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * @author jialingliu
 */

public class RecommendationActivity extends AppCompatActivity {
    private static final int cnt = 6;
    private TextView restaurantText;
    private TextView sportsText;

    private ImageView r1;
    private ImageView r2;
    private ImageView r3;
    private ImageView r4;
    private ImageView r5;
    private ImageView r6;

    private ImageView s1;
    private ImageView s2;
    private ImageView s3;
    private ImageView s4;
    private ImageView s5;
    private ImageView s6;

    private String[] mActivities = new String[]{"anger", "contempt", "disgust", "fear", "happiness", "neutral", "sadness", "surprise"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        initTextViews();

        DisplayImgTask task = new DisplayImgTask();
        task.execute();
    }

    private void initTextViews() {
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/DancingScript-Regular.ttf");

        restaurantText = (TextView) findViewById(R.id.restauranttext);
        sportsText = (TextView) findViewById(R.id.sportstext);

        restaurantText.setTypeface(face);
        sportsText.setTypeface(face);
    }

    private void initImageViews(Map<String, List<Map<String, String>>> recommendations) {
        List<Map<String, String>> restaurants = recommendations.get("restaurants");
        List<Map<String, String>> sports = recommendations.get("sports");

        ImageView[] ivr = new ImageView[] {r1, r2, r3, r4, r5, r6};
        ImageView[] ivs = new ImageView[] {s1, s2, s3, s4, s5, s6};
        int[] idsr = new int[] {R.id.img1, R.id.img2, R.id.img3, R.id.img4, R.id.img5, R.id.img6};
        int[] idss = new int[] {R.id.img7, R.id.img8, R.id.img9, R.id.img10, R.id.img11, R.id.img12};
        // TODO: 4/30/17 display 6 + 6 images
        // restaurants
        for (int i = 0; i < cnt; i++) {
            ivr[i] = (ImageView) findViewById(idsr[i]);
            // TODO: 4/30/17 change to restaurant links
            new DownloadImageTask(ivr[i]).execute("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");
            // TODO: 4/30/17 launch yelp
        }

        // sports
        for (int i = 0; i < cnt; i++) {
            // TODO: 4/30/17
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            String urlStr = params[0];
            Bitmap img = null;

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(urlStr);
            HttpResponse response;
            try {
                response = (HttpResponse)client.execute(request);
                HttpEntity entity = response.getEntity();
                BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                InputStream inputStream = bufferedEntity.getContent();
                img = BitmapFactory.decodeStream(inputStream);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return img;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    private class DisplayImgTask extends AsyncTask<String, Void, Map<String, List<Map<String, String>>>> {
        // Downloading data in non-ui thread
        @Override
        protected Map<String, List<Map<String, String>>> doInBackground(String... url) {
            Map<String, List<Map<String, String>>> data = new HashMap<>();

            EmotionHttpClient emotionClient = new EmotionHttpClient();
            Map<String, String> emotions = emotionClient.getEmotion();

            RecRestaurantHttpClient restaurantClient = new RecRestaurantHttpClient();
            RecSportsHttpClient sportsClient = new RecSportsHttpClient();

            List<Map<String,String>> restaurants = restaurantClient.getRecommendations(emotions);
            List<Map<String, String>> sports = sportsClient.getRecommendations(emotions);

            data.put("restaurants", restaurants);
            data.put("sports", sports);

            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(Map<String, List<Map<String, String>>> data) {
            super.onPostExecute(data);
            initImageViews(data);
        }
    }

    private class EmotionHttpClient {
        private final String BASE_URL = String.format("http://%s:8888/emotion", Constant.PI_IP);
        Map<String, String> getEmotion() {
            Document doc = null;
            try {
                doc = Jsoup.connect(BASE_URL).timeout(1000).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String data = "{\"sadness\": 0.0, \"neutral\": 0.0, \"contempt\": 0.0, \"disgust\": 0.0, \"anger\": 0.0, \"surprise\": 0.0, \"fear\": 0.0, \"happiness\": 1.0}";
            if (doc != null) {
                data = doc.toString();
            }
            return processJson(data);
        }
        private Map<String, String> processJson(String data) {
            Map<String, String> result = new HashMap<>();
            JsonParser jsonParser = new JsonParser();
            JsonObject json = jsonParser.parse(data).getAsJsonObject();
            System.out.println(json.toString());
            for (String activity: mActivities) {
                result.put(activity, Float.toString(json.get(activity).getAsFloat()));
            }
            return result;
        }
    }

    private class RecRestaurantHttpClient {
        private final String BASE_URL = String.format("http://%s:3000/recommendation/restaurant", Constant.RECOMMENDATION_IP);
        List<Map<String, String>> getRecommendations(Map<String, String> map) {
            Document document = null;
            try {
                document = Jsoup.connect(BASE_URL).timeout(1000)
                        .data("neutral", map.get("neutral"))
                        .data("happiness", map.get("happiness"))
                        .data("disgust", map.get("disgust"))
                        .data("anger", map.get("anger"))
                        .data("surprise", map.get("surprise"))
                        .data("fear", map.get("fear"))
                        .data("sadness", map.get("sadness"))
                        .data("contempt", map.get("contempt"))
                        .post();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String data = "[]"; // TODO: 4/30/17 add default recommendations
            if (document != null) {
                data = document.toString();
            }
            return processJson(data);
        }

        private List<Map<String, String>> processJson(String data) {
            List<Map<String, String>> result = new ArrayList<>();
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = jsonParser.parse(data).getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Map<String, String> map = new HashMap<>();
                map.put("", jsonObject.get("").getAsString()); // TODO: 4/30/17 add keys
                map.put("", jsonObject.get("").getAsString());
                result.add(map);
            }
            return result;
        }
    }

    private class RecSportsHttpClient {
        private final String BASE_URL = String.format("http://%s:3000/recommendation/sports", Constant.RECOMMENDATION_IP);
        List<Map<String, String>> getRecommendations(Map<String, String> map) {
            // TODO: 4/30/17 get sports recommendations
            return null;
        }
    }
}
