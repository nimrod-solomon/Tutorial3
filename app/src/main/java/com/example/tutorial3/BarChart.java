package com.example.tutorial3;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import java.util.List;



public class barChart extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        Button buttonLiveChart = findViewById(R.id.live_chart);
        Button buttonOpenCSV = findViewById(R.id.opencsv);
        BarChart barChart = findViewById(R.id.barchart);
        Legend legend = barChart.getLegend();
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setTextSize(12f);
        LegendEntry l1=new LegendEntry("Mean", Legend.LegendForm.DEFAULT,10f,2f,null, Color.BLUE);
        LegendEntry l2=new LegendEntry("Std", Legend.LegendForm.CIRCLE,10f,2f,null, Color.RED);
        legend.setCustom(new LegendEntry[]{l1,l2});

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.RED);

        ArrayList<String[]> originalCsvData = CsvRead("/sdcard/csv_dir/random_data.csv");
        ArrayList<String[]> gaussianCsvData = CsvRead("/sdcard/csv_dir/gaussian_data.csv");

        List<List<Entry>> dataValues = DataValues(originalCsvData, gaussianCsvData);

        // Extract originalDataVals and gaussianDataVals from dataValues
        List<Entry> originalDataVals = dataValues.get(0);
        List<Entry> gaussianDataVals = dataValues.get(1);

        // Calculate mean and standard deviation of originalDataVals
        float sum = 0;
        int count = originalDataVals.size();
        for (Entry entry : originalDataVals) {
            sum += entry.getY();
        }
        float meanOriginal = sum / count;

        float sumSquares = 0;
        for (Entry entry : originalDataVals) {
            float diff = entry.getY() - meanOriginal;
            sumSquares += diff * diff;
        }
        float stdOriginal = (float) Math.sqrt(sumSquares / count);

        sum = 0;
        count = gaussianDataVals.size();
        for (Entry entry : gaussianDataVals) {
            sum += entry.getY();
        }
        float meanGaussian = sum / count;

        sumSquares = 0;
        for (Entry entry : gaussianDataVals) {
            float diff = entry.getY() - meanGaussian;
            sumSquares += diff * diff;
        }

        float stdGaussian = (float) Math.sqrt(sumSquares / count);

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f,  meanOriginal));
        entries.add(new BarEntry(1f, stdOriginal));
        entries.add(new BarEntry(3f,  meanGaussian));
        entries.add(new BarEntry(4f,  stdGaussian));

        BarDataSet set  = new BarDataSet(entries, "BarDataSet");
        set.setColors(colors);
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(15f);
        BarData data = new BarData(set);
        data.setBarWidth(0.9f);
        barChart.setData(data);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Original", "", "Gaussian", ""}));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(16f);
        barChart.invalidate();

        buttonLiveChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickLiveChart();
            }
        });

        buttonOpenCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickOpenCSV();
            }
        });
    }

    public static double calculateMean(ArrayList<String[]> data) {
        double sum = 0;
        int count = 0;
        for (String[] row : data) {
            for (String value : row) {
                sum += Double.parseDouble(value);
                count++;
            }
        }
        return sum / count;
    }

    public static double calculateStdDev(ArrayList<String[]> data) {
        double mean = calculateMean(data);
        double sum = 0;
        int count = 0;
        for (String[] row : data) {
            for (String value : row) {
                double doubleValue = Double.parseDouble(value);
                sum += Math.pow(doubleValue - mean, 2);
                count++;
            }
        }
        return Math.sqrt(sum / (count - 1));
    }

    private void ClickLiveChart() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void ClickOpenCSV() {
        Intent intent = new Intent(this, LoadCSV.class);
        startActivity(intent);
    }

    private ArrayList<String[]> CsvRead(String filename) {
        ArrayList<String[]> csvData = new ArrayList<>();
        try {
            File file = new File(filename);
            CSVReader reader = new CSVReader(new FileReader(file));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine != null) {
                    csvData.add(nextLine);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return csvData;
    }

    private List<List<Entry>> DataValues(ArrayList<String[]> originalCsvData, ArrayList<String[]> gaussianCsvData) {
        List<Entry> originalDataVals = new ArrayList<>();
        List<Entry> gaussianDataVals = new ArrayList<>();

        int size = Math.min(originalCsvData.size(), gaussianCsvData.size());

        for (int i = 0; i < size; i++) {
            originalDataVals.add(new Entry(i, Integer.parseInt(originalCsvData.get(i)[1])));
            gaussianDataVals.add(new Entry(i, Float.parseFloat(gaussianCsvData.get(i)[1])));
        }

        // If one data set is larger, pad the smaller one with default values
        if (originalCsvData.size() > gaussianCsvData.size()) {
            for (int i = size; i < originalCsvData.size(); i++) {
                gaussianDataVals.add(new Entry(i, 0));
            }
        } else if (gaussianCsvData.size() > originalCsvData.size()) {
            for (int i = size; i < gaussianCsvData.size(); i++) {
                originalDataVals.add(new Entry(i, 0));
            }
        }

        List<List<Entry>> dataValues = new ArrayList<>();
        dataValues.add(originalDataVals);
        dataValues.add(gaussianDataVals);
        return dataValues;
    }


}
