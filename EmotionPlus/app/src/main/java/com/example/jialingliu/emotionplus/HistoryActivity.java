package com.example.jialingliu.emotionplus;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author jialingliu
 */

public class HistoryActivity extends BaseActivity {
    private RadarChart mChart;
    private static final int cnt = 8;
    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_history);
        DisplayChartTask task = new DisplayChartTask();
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.radar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                for (IDataSet<?> set : mChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (mChart.getData() != null) {
                    mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
                    mChart.invalidate();
                }
                break;
            }
            case R.id.actionToggleRotate: {
                if (mChart.isRotationEnabled())
                    mChart.setRotationEnabled(false);
                else
                    mChart.setRotationEnabled(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleFilled: {

                ArrayList<IRadarDataSet> sets = (ArrayList<IRadarDataSet>) mChart.getData()
                        .getDataSets();

                for (IRadarDataSet set : sets) {
                    if (set.isDrawFilledEnabled())
                        set.setDrawFilled(false);
                    else
                        set.setDrawFilled(true);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlightCircle: {

                ArrayList<IRadarDataSet> sets = (ArrayList<IRadarDataSet>) mChart.getData()
                        .getDataSets();

                for (IRadarDataSet set : sets) {
                    set.setDrawHighlightCircleEnabled(!set.isDrawHighlightCircleEnabled());
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionSave: {
                if (mChart.saveToPath("title" + System.currentTimeMillis(), "")) {
                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show();
                break;
            }
            case R.id.actionToggleXLabels: {
                mChart.getXAxis().setEnabled(!mChart.getXAxis().isEnabled());
                mChart.notifyDataSetChanged();
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleYLabels: {

                mChart.getYAxis().setEnabled(!mChart.getYAxis().isEnabled());
                mChart.invalidate();
                break;
            }
            case R.id.animateX: {
                mChart.animateX(1400);
                break;
            }
            case R.id.animateY: {
                mChart.animateY(1400);
                break;
            }
            case R.id.animateXY: {
                mChart.animateXY(1400, 1400);
                break;
            }
            case R.id.actionToggleSpin: {
                mChart.spin(2000, mChart.getRotationAngle(), mChart.getRotationAngle() + 360, Easing.EasingOption
                        .EaseInCubic);
                break;
            }
        }
        return true;
    }

    public void setData(List<Map<String, Integer>> emotionData, int index) {
        ArrayList<RadarEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        Map<String, Integer> map = emotionData.get(index);
        float val1 = 100 * ((float)map.get("anger")) / map.get("sum");
        float val2 = 100 * ((float)map.get("contempt")) / map.get("sum");
        float val3 = 100 * ((float)map.get("disgust")) / map.get("sum");
        float val4 = 100 * ((float)map.get("fear")) / map.get("sum");
        float val5 = 100 * ((float)map.get("happiness")) / map.get("sum");
        float val6 = 100 * ((float)map.get("neutral")) / map.get("sum");
        float val7 = 100 * ((float)map.get("sadness")) / map.get("sum");
        float val8 = 100 * ((float)map.get("surprise")) / map.get("sum");
        System.out.println(val1);
        System.out.println(val2);
        System.out.println(val3);
        System.out.println(val4);
        System.out.println(val5);
        System.out.println(val6);
        System.out.println(val7);
        System.out.println(val8);


        entries.add(new RadarEntry(val1));
        entries.add(new RadarEntry(val2));
        entries.add(new RadarEntry(val3));
        entries.add(new RadarEntry(val4));
        entries.add(new RadarEntry(val5));
        entries.add(new RadarEntry(val6));
        entries.add(new RadarEntry(val7));
        entries.add(new RadarEntry(val8));

        RadarDataSet set = new RadarDataSet(entries, "Today");
        set.setColor(Color.rgb(103, 110, 129));
        set.setFillColor(Color.rgb(103, 110, 129));
        set.setDrawFilled(true);
        set.setFillAlpha(180);
        set.setLineWidth(2f);
        set.setDrawHighlightCircleEnabled(true);
        set.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set);

        RadarData data = new RadarData(sets);
        data.setValueTypeface(mTfLight);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mChart.invalidate();
    }
    private class DisplayChartTask extends AsyncTask<String, Void, List<Map<String, Integer>>> {

        // Downloading data in non-ui thread
        @Override
        protected List<Map<String, Integer>> doInBackground(String... url) {
            // TODO: 4/30/17
            EmotionHttpClient client = new EmotionHttpClient();
            return client.getEmotionData();
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(List<Map<String, Integer>> result) {
            super.onPostExecute(result);
            // TODO: 4/30/17 display Radar chart
            TextView tv = (TextView) findViewById(R.id.textView);
            tv.setTypeface(mTfLight);
            tv.setTextColor(Color.WHITE);
            tv.setBackgroundColor(Color.rgb(60, 65, 82));
            mChart = (RadarChart) findViewById(R.id.chart1);
            mChart.setBackgroundColor(Color.rgb(60, 65, 82));

            mChart.getDescription().setEnabled(false);

            mChart.setWebLineWidth(1f);
            mChart.setWebColor(Color.LTGRAY);
            mChart.setWebLineWidthInner(1f);
            mChart.setWebColorInner(Color.LTGRAY);
            mChart.setWebAlpha(100);

            // create a custom MarkerView (extend MarkerView) and specify the layout
            // to use for it
            MarkerView mv = new RadarMarkerView(context, R.layout.radar_markerview);
            mv.setChartView(mChart); // For bounds control
            mChart.setMarker(mv); // Set the marker to the chart

            setData(result, 0);

            mChart.animateXY(1400, 1400,
                    Easing.EasingOption.EaseInOutQuad, Easing.EasingOption.EaseInOutQuad);

            XAxis xAxis = mChart.getXAxis();
            xAxis.setTypeface(mTfLight);
            xAxis.setTextSize(15f);
            xAxis.setYOffset(0f);
            xAxis.setXOffset(0f);
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                private String[] mActivities = new String[]{"anger", "contempt", "disgust", "fear", "happiness", "neutral", "sadness", "surprise"};
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return mActivities[(int) value % mActivities.length];
                }
            });
            xAxis.setTextColor(Color.WHITE);

            YAxis yAxis = mChart.getYAxis();
            yAxis.setTypeface(mTfLight);
            yAxis.setLabelCount(cnt, false);
            yAxis.setTextSize(9f);
            yAxis.setAxisMinimum(0f);
            yAxis.setAxisMaximum(50f);
            yAxis.setDrawLabels(false);

            Legend l = mChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);
            l.setTypeface(mTfLight);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(5f);
            l.setTextColor(Color.WHITE);
        }
    }
}

class EmotionHttpClient {
    private static final String BASE_URL = "http://172.29.92.105:8888/emotion/week";
    /**
     * get the most recent week emotion data from resberry-pi server
     * @return a list of maps, the frequency of each emotion on a specific day, plus total counts of emotions
     */
    List<Map<String, Integer>>  getEmotionData() {
        List<Map<String, Integer>> result = new ArrayList<>();
        HttpURLConnection con = null;
        InputStream is = null;
        try {
            con = (HttpURLConnection)(new URL(BASE_URL).openConnection());
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();

            // Let's read the response
            StringBuilder buffer = new StringBuilder();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) buffer.append(line).append("\r\n");
            is.close();
            con.disconnect();
            System.out.println(buffer.toString());
            result = processJson(buffer.toString());
            return result;
        } catch(Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch(Exception ignored) {}
            try {
                if (con != null) {
                    con.disconnect();
                }
            } catch(Throwable ignored) {}
        }
        return result;
    }
    private List<Map<String, Integer>> processJson(String data) {
        JsonParser jsonParser = new JsonParser();
        JsonObject json = jsonParser.parse(data).getAsJsonObject();
        System.out.println(json.toString());
        JsonArray jsonArray = json.get("data").getAsJsonArray();
        List<Map<String, Integer>>  result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            Map<String, Integer> map = new HashMap<>();
            int sum = 0;
            sum += jsonObject.get("neutral").getAsInt() + jsonObject.get("happiness").getAsInt() + jsonObject.get("disgust").getAsInt() + jsonObject.get("anger").getAsInt() + jsonObject.get("surprise").getAsInt() + jsonObject.get("fear").getAsInt() + jsonObject.get("sadness").getAsInt() + jsonObject.get("contempt").getAsInt();
            map.put("neutral", jsonObject.get("neutral").getAsInt());
            map.put("happiness", jsonObject.get("happiness").getAsInt());
            map.put("disgust", jsonObject.get("disgust").getAsInt());
            map.put("anger", jsonObject.get("anger").getAsInt());
            map.put("surprise", jsonObject.get("surprise").getAsInt());
            map.put("fear", jsonObject.get("fear").getAsInt());
            map.put("sadness", jsonObject.get("sadness").getAsInt());
            map.put("contempt", jsonObject.get("contempt").getAsInt());
            map.put("sum", sum);
            result.add(map);
        }
        return result;
    }
}