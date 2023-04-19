package com.example.tutorial3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import java.util.List;


public class LoadCSV extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_csv);
        Button backButton = findViewById(R.id.button_back);
        LineChart lineChart = findViewById(R.id.line_chart);

        ArrayList<String[]> originalCsvData = CsvRead("/sdcard/csv_dir/random_data.csv");
        ArrayList<String[]> gaussianCsvData = CsvRead("/sdcard/csv_dir/gaussian_data.csv");

        List<List<Entry>> dataValues = DataValues(originalCsvData, gaussianCsvData);

        LineDataSet lineDataSet1 = new LineDataSet(dataValues.get(0), "Original Data");
        LineDataSet lineDataSet2 = new LineDataSet(dataValues.get(1), "Gaussian Data");

        lineDataSet1.setColor(Color.BLUE);
        lineDataSet2.setColor(Color.RED);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickBack();
            }
        });
    }

    private void ClickBack() {
        Intent intent = new Intent(this, MainActivity.class);
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
