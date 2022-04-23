package com.example.indoornavi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        Button closeBtn = (Button) findViewById(R.id.listCloseBtn);
        Fragment listFragment = (Fragment) new ListFragment();

        Intent intent = getIntent();
        String position = intent.getStringExtra("position");

        getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDo();

                Toast.makeText(getApplicationContext(), " 추가되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUp(closeBtn, position);

            }
        });
    }

    private void saveToDo() {
        EditText listInput = findViewById(R.id.listInput);

        //EditText에 적힌 글을 가져오기
        String todo = listInput.getText().toString();

        //저장과 동시에 EditText 안의 글 초기화
        listInput.setText("");
    }

    private void popUp(Button closeBtn, String position) {
        // 몇층에서 출발하는지
        // 어디서 출발하는지

        // 초기 배열리스트 및 초기화 목록
        String Set[] = {"엘리베이터", "에스컬레이터"};

        final int[] idx = {0};
        String title = "출발 지점";
        // radio 버튼 이름 정의
        String no = "취소";
        String yes = "확인";

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AlertDialog
                new AlertDialog.Builder(ShoppingListActivity.this)
                        .setTitle(title)
                        .setCancelable(true)
                        .setSingleChoiceItems(Set, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 실시간으로 변경된 인덱스 값 확인
                                idx[0] = which;
                            }
                        })
                        .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (idx[0] == 0) {
                                    Log.e("알림", ""+idx[0]);
                                    Toast.makeText(getApplication(), Set[0] + "에서 출발합니다", Toast.LENGTH_SHORT).show();
                                } else if (idx[0] == 1) {
                                    Log.e("알림", ""+idx[0]);
                                    Toast.makeText(getApplication(), Set[1] + "에서 출발합니다", Toast.LENGTH_SHORT).show();
                                }

                                if (position == "0") {

                                    Intent toMapIntent = new Intent(ShoppingListActivity.this, MapActivity.class);
                                    toMapIntent.putExtra("position", position);
                                    toMapIntent.putExtra("startingPoint", Set[idx[0]]);

                                    startActivity(toMapIntent);
                                }
                            }
                        })
                        .setNegativeButton(no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        });
    }

}