package com.example.fit4me;

import android.graphics.Color;
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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

public class DailyData extends AppCompatActivity {

    private String starID;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_data);
        db = new DatabaseHandler(this,null,null,1);

        String name = getIntent().getStringExtra("username");
        TextView text = findViewById(R.id.progress_text);
        text.setText(String.format("%s's Progress", name));

        //display data for current week in some sort of graphical way

        HorizontalBarChart chart = (HorizontalBarChart) findViewById(R.id.chart);

        BarDataSet set1;
        set1 = new BarDataSet(getDataSet(), "The year 2017");

        set1.setColors(Color.parseColor("#F78B5D"), Color.parseColor("#FCB232"), Color.parseColor("#FDD930"), Color.parseColor("#ADD137"), Color.parseColor("#A0C25A"));

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);

        // hide Y-axis
        YAxis left = chart.getAxisLeft();
        left.setDrawLabels(false);

        // custom X-axis labels
        String[] values = new String[] { "1 star", "2 stars", "3 stars", "4 stars", "5 stars"};
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

        chart.setData(data);

        // custom description
        Description description = new Description();
        description.setText("Rating");
        chart.setDescription(description);

        // hide legend
        chart.getLegend().setEnabled(false);

        chart.animateY(1000);
        chart.invalidate();



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

    private ArrayList<BarEntry> getDataSet() {

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();

        //x values will be the total of that bars date
        //y values will be the date
        BarEntry v1e2 = new BarEntry(1, 20f);
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(2, 40f);
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(3, 60f);
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(4, 80f);
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(5, 100f);
        valueSet1.add(v1e6);

        return valueSet1;
    }

}


