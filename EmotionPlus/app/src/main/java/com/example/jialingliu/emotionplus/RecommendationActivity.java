package com.example.jialingliu.emotionplus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.entity.BufferedHttpEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

//import cz.msebera.android.httpclient.HttpResponse;

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

    private TextView txt1;
    private TextView txt2;
    private TextView txt3;
    private TextView txt4;
    private TextView txt5;
    private TextView txt6;

    private TextView txt7;
    private TextView txt8;


    private String[] mActivities = new String[]{"anger", "contempt", "disgust", "fear", "happiness", "neutral", "sadness", "surprise"};

    private String head = "http://";

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
        ImageView[] ivs = new ImageView[] {s1, s2};
        int[] idsr = new int[] {R.id.img1, R.id.img2, R.id.img3, R.id.img4, R.id.img5, R.id.img6,
                R.id.txt1, R.id.txt2, R.id.txt2, R.id.txt2, R.id.txt2, R.id.txt2};
        int[] idss = new int[] {R.id.img7, R.id.img8};

        TextView[] txtr = new TextView[] {txt1, txt2, txt3, txt4, txt5, txt6};
        TextView[] txts = new TextView[] {txt7, txt8};

        // TODO: 4/30/17 display 6 + 6 images
        // restaurants
        for (int i = 0; i < cnt; i++) {
            final Map<String, String> restaurant = restaurants.get(i);
            ivr[i] = (ImageView) findViewById(idsr[i]);

            new DownloadImageTask(ivr[i]).execute(head + restaurant.get("img"));

            ivr[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(restaurant.get("url"))));
                }
            });

            txtr = (TextView) findViewById()
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
            String urlStr = params[0].replaceAll("\\s+", "%20");
            Bitmap img = null;
            System.out.println(urlStr);

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(urlStr);
            cz.msebera.android.httpclient.HttpResponse response;
            try {
                response = client.execute(request);
                HttpEntity entity = response.getEntity();
                BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                InputStream inputStream = bufferedEntity.getContent();
                img = BitmapFactory.decodeStream(inputStream);
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
            String emotions = emotionClient.getEmotion();

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
        String getEmotion() {
            Document doc = null;
            try {
                doc = Jsoup.connect(BASE_URL).timeout(1000).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String data = "{\"sadness\": 0.0, \"neutral\": 0.0, \"contempt\": 0.0, \"disgust\": 0.0, \"anger\": 0.0, \"surprise\": 0.0, \"fear\": 0.0, \"happiness\": 1.0}";
            if (doc != null) {
                data = doc.body().text();
            }
            return data;
        }
    }

    private class RecRestaurantHttpClient {
        private final String BASE_URL = String.format("http://%s:3000/recommendation/restaurant", Constant.RECOMMENDATION_IP);
        List<Map<String, String>> getRecommendations(String data) {
            Response response = null;
            try {
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, data);
                Request request = new Request.Builder()
                        .url(BASE_URL)
                        .post(body)
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "5126d0c8-6818-69bb-737d-78253c012102")
                        .build();
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String recommendations = "[{\"name\":\"La PanotiQ Bakery Cafe\",\"img\":\"172.29.93.218:3000/restraunts/La PanotiQ Bakery Cafe.jpg\",\"url\":\"https://www.yelp.com/biz/la-panotiq-bakery-cafe-mountain-view?osq=desserts\"},{\"name\":\"Sugar Mama Desserts\",\"img\":\"172.29.93.218:3000/restraunts/Sugar Mama Desserts.jpg\",\"url\":\"https://www.yelp.com/biz/sugar-mama-desserts-sunnyvale-2?osq=desserts\"},{\"name\":\"Sugar Butter Flour\",\"img\":\"172.29.93.218:3000/restraunts/Sugar Butter Flour.jpg\",\"url\":\"https://www.yelp.com/biz/sugar-butter-flour-sunnyvale?osq=desserts\"},{\"name\":\"Sweet & Yummy Bakery\",\"img\":\"172.29.93.218:3000/restraunts/Sweet & Yummy Bakery.jpg\",\"url\":\"https://www.yelp.com/biz/sweet-and-yummy-bakery-mountain-view?osq=desserts\"},{\"name\":\"Tin Pot Creamery\",\"img\":\"172.29.93.218:3000/restraunts/Tin Pot Creamery.jpg\",\"url\":\"https://www.yelp.com/biz/tin-pot-creamery-palo-alto?osq=desserts\"},{\"name\":\"Teaspoon\",\"img\":\"172.29.93.218:3000/restraunts/Teaspoon.jpg\",\"url\":\"https://www.yelp.com/biz/teaspoon-mountain-view?osq=desserts\"}]"; // TODO: 4/30/17 add default recommendations
            if (response != null) {
                try {
                    recommendations = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return processJson(recommendations);
        }

        private List<Map<String, String>> processJson(String data) {
            System.out.println("Recommended restaurants got ");
            System.out.println(data);
            List<Map<String, String>> result = new ArrayList<>();
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = jsonParser.parse(data).getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Map<String, String> map = new HashMap<>();
                map.put("name", jsonObject.get("name").getAsString());
                map.put("img", jsonObject.get("img").getAsString());
                map.put("url", jsonObject.get("url").getAsString());
                result.add(map);
            }
            return result;
        }
    }

    private class RecSportsHttpClient {
        private final String BASE_URL = String.format("http://%s:3000/recommendation/sports", Constant.RECOMMENDATION_IP);
        List<Map<String, String>> getRecommendations(String data) {
            Response response = null;
            try {
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, data);
                Request request = new Request.Builder()
                        .url(BASE_URL)
                        .post(body)
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "5126d0c8-6818-69bb-737d-78253c012102")
                        .build();
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String recommendations = "[{\"name\":\"jogging\",\"img\":\"172.29.93.218:3000/sports/jogging.jpg\"},{\"name\":\"tai-chi\",\"img\":\"172.29.93.218:3000/sports/tai-chi.jpg\"}]";
            if (response != null) {
                try {
                    recommendations = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return processJson(recommendations);
        }

        private List<Map<String, String>> processJson(String data) {
            System.out.println("Recommended restaurants got ");
            System.out.println(data);
            List<Map<String, String>> result = new ArrayList<>();
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = jsonParser.parse(data).getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Map<String, String> map = new HashMap<>();
                map.put("name", jsonObject.get("name").getAsString());
                map.put("img", jsonObject.get("img").getAsString());
                result.add(map);
            }
            return result;
        }
    }
}
