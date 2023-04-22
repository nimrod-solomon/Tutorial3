package com.example.tutorial3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.util.ArrayList;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class MainActivity extends AppCompatActivity {
    LineChart mpLineChart;
    int counter = 1;
    int val = 40;
    double gaussianRandom = 40.0;
    private Handler mHandlar = new Handler();  //Handlar is used for delay definition in the loop



    public MainActivity() throws FileNotFoundException {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }

        mpLineChart = (LineChart) findViewById(R.id.line_chart);

        LineDataSet lineDataSet1 =  new LineDataSet(dataValues1(), "Original Data");
        lineDataSet1.setColor(Color.BLUE);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        // create and add the second dataset
        LineDataSet lineDataSet2 = new LineDataSet(dataValues1(), "Gaussian Data");
        lineDataSet2.setColor(Color.RED);
        dataSets.add(lineDataSet2);

        LineData data = new LineData(dataSets);
        mpLineChart.setData(data);
        mpLineChart.invalidate();

        Button buttonClear = (Button) findViewById(R.id.button1);
        Button buttonCsvShow = (Button) findViewById(R.id.button2);
        Button buttonBarchartShow = (Button) findViewById(R.id.button3);

        buttonCsvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenLoadCSV();
            }
        });

        buttonBarchartShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenBarplotShow();
            }
        });





        LineDataSet finalLineDataSet = lineDataSet1;
        LineDataSet finalLineDataSet2 = lineDataSet2;

        Runnable DataUpdate = new Runnable(){
            @Override
            public void run() {

                data.addEntry(new Entry(counter,val),0);
                data.addEntry(new Entry(counter, (float) gaussianRandom),1);
                finalLineDataSet.notifyDataSetChanged(); // let the data know a dataSet changed
                finalLineDataSet2.notifyDataSetChanged();
                mpLineChart.notifyDataSetChanged(); // let the chart know it's data changed
                mpLineChart.invalidate(); // refresh
                val = (int) (Math.random() * 80);
                gaussianRandom = (double) (Math.random() * 20 + 40); // mean = 40, std = 20

                saveToCsv("/sdcard/csv_dir/", "random_data.csv", String.valueOf(counter), String.valueOf(val));
                saveToCsv("/sdcard/csv_dir/", "gaussian_data.csv", String.valueOf(counter), String.valueOf(gaussianRandom));

                counter += 1;
                mHandlar.postDelayed(this,500);


            }
        };

        buttonClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Clear",Toast.LENGTH_SHORT).show();
                LineData data = mpLineChart.getData();
                ILineDataSet set = data.getDataSetByIndex(0);
                set.clear();
                set = data.getDataSetByIndex(1);
                set.clear();
                val=40;
                gaussianRandom = 40.0;
                counter = 1;

            }
        });


        mHandlar.postDelayed(DataUpdate,500);
    }


    private ArrayList<Entry> dataValues1()
    {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        //dataVals.add(new Entry(0,0));
        return dataVals;
    }

    private void saveToCsv(String dirPath, String filename, String value1, String value2) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String csvFilePath = dirPath + File.separator + filename;
        try {
            FileWriter csvWriter = new FileWriter(csvFilePath, true);
            csvWriter.append(value1);
            csvWriter.append(",");
            csvWriter.append(value2);
            csvWriter.append("\n");
            csvWriter.close();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this,"ERROR",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

   private void OpenLoadCSV(){
     Intent intent = new Intent(this,LoadCSV.class);
     startActivity(intent);
   }

    private void OpenBarplotShow(){
        Intent intent = new Intent(this,barChart.class);
        startActivity(intent);
    }


}
