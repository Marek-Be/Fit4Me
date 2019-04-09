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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        db.addDay("",2000,4000);
        db.addDay("",3000,4000);
        db.addDay("",1000,4000);
        db.addDay("",3000,7000);
        db.addDay("",3000,6000);
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
        int[] steps = db.getSteps();
        //float goal = (float) db.getGoal();
        int[] goals = db.getGoals();

        int length = steps.length;
        int counter = 0;
        float steps1,steps2,steps3,steps4,steps5;
        float goal1,goal2,goal3,goal4,goal5;

        steps1 = (float) steps[0];
        steps2 = (float) steps[1];
        steps3 = (float) steps[2];
        steps4 = (float) steps[3];
        steps5 = (float) steps[4];

        goal1 = (float) goals[0];
        goal2 = (float) goals[1];
        goal3 = (float) goals[2];
        goal4 = (float) goals[3];
        goal5 = (float) goals[4];


        BarEntry v1e2 = new BarEntry(0f, (steps1/goal1)*100); //y axis and x axis are other way around
        valueSet1.add(v1e2);

        BarEntry v1e3 = new BarEntry(1f, (steps2/goal2)*100);
        valueSet1.add(v1e3);

        BarEntry v1e4 = new BarEntry(2f, (steps3/goal3)*100);
        valueSet1.add(v1e4);

        BarEntry v1e5 = new BarEntry(3f, (steps4/goal4)*100);
        valueSet1.add(v1e5);

        BarEntry v1e6 = new BarEntry(4f, (steps5/goal5)*100);
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

    /*public String getDate(){
        Calendar now = Calendar.getInstance();   // Gets the current date and time
        int year = now.get(Calendar.YEAR);
        int day = now.get(Calendar.DAY_OF_MONTH);
        int month = now.get(Calendar.MONTH);
        return day + "/" + month + "/" + year;

    }*/





}


