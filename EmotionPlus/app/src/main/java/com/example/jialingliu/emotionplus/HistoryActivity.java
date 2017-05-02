package com.example.jialingliu.emotionplus;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
    private TextView tv1 = null;
    private TextView tv2 = null;
    private TextView tv3 = null;
    private TextView tv4 = null;
    private TextView tv5 = null;
    private TextView tv6 = null;
    private TextView tv7 = null;
    private String[] mActivities = new String[]{"anger", "contempt", "disgust", "fear", "happiness", "neutral", "sadness", "surprise"};
    private String[] daysIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_history);

        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setTypeface(mTfLight);
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundColor(Color.rgb(60, 65, 82));

        daysIndex = new String[] {getString(R.string.today), getString(R.string.yesterday), getString(R.string.the_day_before_yesterday),
                getString(R.string.three_days_ago), getString(R.string.four_days_ago), getString(R.string.five_days_ago), getString(R.string.six_days_ago)};

        initChart();

        DisplayChartTask task = new DisplayChartTask();
        task.execute();
    }

    private void initChart() {
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
        mChart.animateXY(1400, 1400,
                Easing.EasingOption.EaseInOutQuad, Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(15f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

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
        yAxis.setAxisMaximum(20f);
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

    private void initTextViews(List<Map<String, Integer>> emotionsData) {
        TextView[] tvs = new TextView[] {tv1, tv2, tv3, tv4, tv5, tv6, tv7};
        int[] ids = new int[] {R.id.day1history, R.id.day2history, R.id.day3history,
                R.id.day4history, R.id.day5history, R.id.day6history, R.id.day7history};
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Light.ttf");

        for (int i = 0; i < 7; i++) {
            tvs[i] = (TextView) findViewById(ids[i]);
            tvs[i].setText(daysIndex[i] + ": " + getPrimaryEmotion(emotionsData, i));
            tvs[i].setTypeface(tf);
//            final int j = i;
//            tvs[i].setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    setData(j);
//                }
//            });
        }
    }

    private String getPrimaryEmotion(List<Map<String, Integer>> emotionsData, int index) {
        Map<String, Integer> map = emotionsData.get(index);
        Map.Entry<String, Integer> maxEntry = null;

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0 && !entry.getKey().equals("sum"))
            {
                maxEntry = entry;
            }
        }
        if (maxEntry == null || maxEntry.getValue().equals(0)) {
            return "no emotion records!";
        }
        return "primary " + maxEntry.getKey() + "!";
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

    public void setData(List<Map<String, Integer>> emotionsData, int index) {
        ArrayList<RadarEntry> entry1 = new ArrayList<>();
        ArrayList<RadarEntry> entry2 = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        Map<String, Integer> map = emotionsData.get(index);
        float val1 = 100 * ((float)map.get("anger")) / map.get("sum");
        float val2 = 100 * ((float)map.get("contempt")) / map.get("sum");
        float val3 = 100 * ((float)map.get("disgust")) / map.get("sum");
        float val4 = 100 * ((float)map.get("fear")) / map.get("sum");
        float val5 = 100 * ((float)map.get("happiness")) / map.get("sum");
        float val6 = 100 * ((float)map.get("neutral")) / map.get("sum");
        float val7 = 100 * ((float)map.get("sadness")) / map.get("sum");
        float val8 = 100 * ((float)map.get("surprise")) / map.get("sum");

        entry1.add(new RadarEntry(val1));
        entry1.add(new RadarEntry(val2));
        entry1.add(new RadarEntry(val3));
        entry1.add(new RadarEntry(val4));
        entry1.add(new RadarEntry(val5));
        entry1.add(new RadarEntry(val6));
        entry1.add(new RadarEntry(val7));
        entry1.add(new RadarEntry(val8));

        Map<String, Integer> map2 = emotionsData.get(index + 1);
        float val9 = 100 * ((float)map2.get("anger")) / map.get("sum");
        float val10 = 100 * ((float)map2.get("contempt")) / map.get("sum");
        float val11 = 100 * ((float)map2.get("disgust")) / map.get("sum");
        float val12 = 100 * ((float)map2.get("fear")) / map.get("sum");
        float val13 = 100 * ((float)map2.get("happiness")) / map.get("sum");
        float val14 = 100 * ((float)map2.get("neutral")) / map.get("sum");
        float val15 = 100 * ((float)map2.get("sadness")) / map.get("sum");
        float val16 = 100 * ((float)map2.get("surprise")) / map.get("sum");

        entry2.add(new RadarEntry(val9));
        entry2.add(new RadarEntry(val10));
        entry2.add(new RadarEntry(val11));
        entry2.add(new RadarEntry(val12));
        entry2.add(new RadarEntry(val13));
        entry2.add(new RadarEntry(val14));
        entry2.add(new RadarEntry(val15));
        entry2.add(new RadarEntry(val16));

        RadarDataSet set1 = new RadarDataSet(entry1, daysIndex[index]);
        set1.setColor(Color.rgb(121, 162, 175));
        set1.setFillColor(Color.rgb(121, 162, 175));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        RadarDataSet set2 = new RadarDataSet(entry2, daysIndex[index + 1]);
        set2.setColor(Color.rgb(103, 110, 129));
        set2.setFillColor(Color.rgb(103, 110, 129));
        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);
        ArrayList<IRadarDataSet> sets = new ArrayList<>();

        sets.add(set1);
        sets.add(set2);

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
            EmotionHttpClient client = new EmotionHttpClient();
            return client.getEmotionData();
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(List<Map<String, Integer>> result) {
            super.onPostExecute(result);
            setData(result, 0);
            initTextViews(result);
        }
    }

    private class EmotionHttpClient {
        private final String BASE_URL = String.format("http://%s:8888/emotion/week", Constant.PI_IP);
        /**
         * get the most recent week emotion data from resberry-pi server
         * @return a list of maps, the frequency of each emotion on a specific day, plus total counts of emotions
         */
        List<Map<String, Integer>>  getEmotionData() {
            List<Map<String, Integer>> result;
            try {
                Document document = Jsoup.connect(BASE_URL).timeout(3000).get();
                result = processJson(document.body().text());
                return result;
            } catch(Throwable t) {
                t.printStackTrace();
            }
            System.out.println("server is down");
            String data = "{\"data\": [{\"neutral\": 14, \"happiness\": 17, \"disgust\": 12, \"anger\": 15, \"surprise\": 8, \"fear\": 5, \"sadness\": 6, \"contempt\": 10}, {\"neutral\": 10, \"happiness\": 1, \"disgust\": 5, \"anger\": 15, \"surprise\": 9, \"fear\": 12, \"sadness\": 13, \"contempt\": 13}, {\"neutral\": 2, \"happiness\": 9, \"disgust\": 14, \"anger\": 1, \"surprise\": 11, \"fear\": 6, \"sadness\": 12, \"contempt\": 8}, {\"neutral\": 6, \"happiness\": 14, \"disgust\": 2, \"anger\": 3, \"surprise\": 18, \"fear\": 16, \"sadness\": 4, \"contempt\": 9}, {\"neutral\": 2, \"happiness\": 0, \"disgust\": 19, \"anger\": 10, \"surprise\": 11, \"fear\": 16, \"sadness\": 17, \"contempt\": 17}, {\"neutral\": 7, \"happiness\": 19, \"disgust\": 14, \"anger\": 1, \"surprise\": 18, \"fear\": 12, \"sadness\": 17, \"contempt\": 0}, {\"neutral\": 0, \"happiness\": 0, \"disgust\": 0, \"anger\": 0, \"surprise\": 0, \"fear\": 0, \"sadness\": 0, \"contempt\": 0}]}";
            result = processJson(data);
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
                for (String mActivity : mActivities) {
                    sum += jsonObject.get(mActivity).getAsInt();
                    map.put(mActivity, jsonObject.get(mActivity).getAsInt());
                }
                map.put("sum", sum);

                result.add(map);
            }
            return result;
        }
    }
}
