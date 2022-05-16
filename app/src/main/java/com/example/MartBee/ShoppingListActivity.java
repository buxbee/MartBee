package com.example.MartBee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity {

    Button saveBtn, closeBtn;
    Fragment listFragment;
    ArrayList<ListNote> listArray;
    EditText listInput;
    ListNote listText;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ListNote> items;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        saveBtn = (Button) findViewById(R.id.saveBtn);
        closeBtn = (Button) findViewById(R.id.listCloseBtn);
        saveBtn = findViewById(R.id.saveBtn);
        closeBtn = findViewById(R.id.listCloseBtn);
        listFragment = new ListFragment();

        Intent intent = getIntent();
        String name = intent.getStringExtra("position"); // 마트명

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();
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