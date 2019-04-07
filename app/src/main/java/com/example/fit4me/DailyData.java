package com.example.fit4me;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

public class DailyData extends AppCompatActivity {
    //bar chart where each bar represents the amount of steps taken in that day
    //x axis is steps taken
    private String starID;
    DatabaseHandler db;
    HorizontalBarChart chart;

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

        xAxis.setLabelCount(5); //5 stars (days)

        String[] values = new String[] { "1st day", "2nd day", "3rd day", "4th day", "5th day"};
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

        //x values will be the total of that bars date
        //y values will be the date
        BarEntry v1e2 = new BarEntry(0f, 27f);
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(1f, 45f);
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(2f, 65f);
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(3f, 77f);
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(4f, 93f);
        valueSet1.add(v1e6);

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

}


