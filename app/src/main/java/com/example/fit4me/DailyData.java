package com.example.fit4me;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;

//bar chart where each bar represents the amount of steps taken in that day
//x axis is steps taken
public class DailyData extends AppCompatActivity {
    private DatabaseHandler db;
    private HorizontalBarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_data);
        db = new DatabaseHandler(this,null);
        setGraph();
    }

    public class MyXAxisValueFormatter extends ValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value) {
            return mValues[(int) value];
        }

    }

    public void setGraph(){
        chart = (HorizontalBarChart) findViewById(R.id.chart);
        chart.setDrawBarShadow(false);

        // custom description
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.getLegend().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawValueAboveBar(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(false);

        YAxis left = chart.getAxisLeft();
        left.setAxisMaximum(100f);
        left.setAxisMinimum(0f);
        left.setEnabled(false);

        xAxis.setLabelCount(5); //Past 5 days

        String[] values = getDays();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

        YAxis right = chart.getAxisRight();
        right.setDrawAxisLine(true);
        right.setDrawGridLines(false);
        right.setEnabled(false);

        setGraphData();

        chart.animateY(2000);
    }

    private void setGraphData() {

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();

        //x values will be the total steps of that day
        //y values will be the date
        int[] steps = db.getSteps();
        int[] goals = db.getPastGoals();

        float stepCount;
        float goal;
        float stepPercent;
        BarEntry v1e;

        for(int i = 0; i < 5; i++){
            stepCount = (float) steps[i];
            goal = (float) goals[i];
            stepPercent = Math.min((stepCount/goal)*100, 100);
            v1e = new BarEntry((float) (4-i), stepPercent); //y axis and x axis are other way around
            valueSet1.add(v1e);
        }

        //BarDataSet is used to display the data in the bar chart
        BarDataSet barDataSet = new BarDataSet(valueSet1,"Bar Data Set");

        barDataSet.setColors(
                ContextCompat.getColor(chart.getContext(), R.color.blue),
                ContextCompat.getColor(chart.getContext(), R.color.goalAchieved),
                ContextCompat.getColor(chart.getContext(), R.color.green),
                ContextCompat.getColor(chart.getContext(), R.color.red),
                ContextCompat.getColor(chart.getContext(), R.color.blue));

        //Need to initialize a BarData object with bardataset to load data into bar
        //This BarData object is then passed into setData() method to load BarChart
        chart.setDrawBarShadow(true);
        barDataSet.setBarShadowColor(Color.argb(40,150,150,150));
        BarData data = new BarData(barDataSet);

        //setting bar width
        //to increase spacing between bars set value of barwidth to < 1f
        data.setBarWidth(0.9f);

        //setting the data and refreshing the graph
        chart.setData(data);
        chart.invalidate();
    }

    public String[] getDays(){
        Calendar cal = Calendar.getInstance();

        String[] pastDays = new String[5];
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)-1;
        for (int i = 4; i >= 0; i--) {
            dayOfWeek--;
            if (dayOfWeek < 0)
                dayOfWeek = 6;
            pastDays[i] = days[dayOfWeek];
        }
        return pastDays;
    }
}


