package com.example.MartBee;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import java.util.List;


public class CustomDialog extends Dialog{

    private Context context;
    private CustomDialogClickListener customDialogClickListener;
    private RadioGroup radioGroup;
    String floor, startPoint;
    Button yes, no;

    public CustomDialog(@NonNull Context context, CustomDialogClickListener customDialogClickListener) {
        super(context);
        this.context = context;
        this.customDialogClickListener = customDialogClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);

        floor = setSpinner();
        startPoint = setStartPoint();

        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogClickListener.onPositiveClick(floor, startPoint);
                dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogClickListener.onNegativeClick();
                dismiss();
            }
        });
    }

    private String setStartPoint() {
        radioGroup = findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.elevator:
                        startPoint = "0";
                        break;
                    case R.id.escalator:
                        startPoint = "1";
                        break;
                }
            }
        });
        return startPoint;
    }

    private String setSpinner() {
        Spinner spinner = findViewById(R.id.spinner);
        String items[] = {"1", "2", "3", "4"}; // 임의로 설정

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // position 층의 지도 보이기
                floor = items[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {            }

        });
        return floor;
    }
}
