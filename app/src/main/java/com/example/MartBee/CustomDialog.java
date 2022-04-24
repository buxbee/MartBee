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
import android.widget.Spinner;

import androidx.annotation.NonNull;

import java.util.List;


public class CustomDialog extends Dialog{

    private Context context;
    private CustomDialogClickListener customDialogClickListener;
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

        setSpinner();

        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogClickListener.onPositiveClick();
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

    private void setSpinner() {
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {            }

        });
    }
}
