package com.example.MartBee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity {

    Button saveBtn, closeBtn;
    Fragment listFragment;
    ArrayList<ListNote> listArray;
    EditText listInput;
    ListNote listText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        saveBtn = (Button) findViewById(R.id.saveBtn);
        closeBtn = (Button) findViewById(R.id.listCloseBtn);
        listFragment = (Fragment) new ListFragment();
        // 임시로 데이터 저장
        listArray = new ArrayList<>();

        Intent intent = getIntent();
        String position = intent.getStringExtra("position"); // 마트명

        getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                listInput = (EditText)findViewById(R.id.listInput);
//                listText = (ListNote) listInput.getText();
//
//                ListFragment fragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.container);
//                assert fragment != null;
//                fragment.saveList(listArray, listInput, listText);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog(ShoppingListActivity.this, new CustomDialogClickListener() {
                    @Override
                    public void onPositiveClick(String floor, String startPoint) {
                        Intent toMapIntent = new Intent(ShoppingListActivity.this, MapActivity.class);
                        toMapIntent.putExtra("floor", floor);
                        toMapIntent.putExtra("startPoint", startPoint);

                        startActivity(toMapIntent);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
                customDialog.setCanceledOnTouchOutside(true);
                customDialog.setCancelable(true);
                customDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                customDialog.show();
            }
        });
    }
}