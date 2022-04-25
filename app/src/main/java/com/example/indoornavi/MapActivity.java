package com.example.indoornavi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.MartBee.R;

public class MapActivity extends AppCompatActivity {

    Button showList = (Button) findViewById(R.id.showList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

    }

}

