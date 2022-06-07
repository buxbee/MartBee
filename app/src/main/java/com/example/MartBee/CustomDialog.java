package com.example.MartBee;

import static java.sql.Types.NULL;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Arrays;


public class CustomDialog extends Dialog {

    private Context context;
    private CustomDialogClickListener customDialogClickListener;
    private RadioGroup radioGroup1, radioGroup2;
    String floor, startPoint, mode;
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
        String[] result = setStartPoint();
        if (result.length==2){
            startPoint = result[0];
            mode = result[1];
        }


        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (flag) {
//                    customDialogClickListener.onPositiveClick(floor, startPoint, mode);
//                    dismiss();
//                }
//                else {
//                    Toast.makeText(getContext(), "버튼을 선택해주세요.", Toast.LENGTH_SHORT).show();
//                }

                customDialogClickListener.onPositiveClick(floor, startPoint, mode);
                dismiss();

            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogClickListener.onNegativeClick();
                Log.d("check", Arrays.toString(result));
                dismiss();
            }
        });
    }

    private String[] setStartPoint() {
        radioGroup1 = findViewById(R.id.radioGroup1);
        radioGroup2 = findViewById(R.id.radioGroup2);

        String[] set = new String[2];


        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.elevator:
                        set[0] = "0";
                        break;
                    case R.id.escalator:
                        set[0] = "1";
                        break;
                    case R.id.entrance:
                        set[0] = "2";
                        break;
                    default:
                        break;
                }
            }
        });
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.pin:
                        set[1] = "0";
                        break;
                    case R.id.navigation:
                        set[1] = "1";
                        break;
                }
            }
        });
        return set;
    }

    private String setSpinner() {
        Spinner spinner = findViewById(R.id.spinner);
        String items[] = {"1", "2", "3"}; // 임의로 설정

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
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
        return floor;
    }
}
