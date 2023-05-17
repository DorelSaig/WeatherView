package com.dorel.weatherview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    WeatherViewBasic gradient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gradient=(WeatherViewBasic) findViewById(R.id.gradientImage);
        gradient.setUrlImage("https://img1.etsystatic.com/002/0/6449624/il_fullxfull.382134677_so6e.jpg", R.drawable.weather,
                R.drawable.weather, ImageView.ScaleType.CENTER_CROP);
    }
}