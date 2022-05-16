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

<<<<<<< Updated upstream
        saveBtn = (Button) findViewById(R.id.saveBtn);
        closeBtn = (Button) findViewById(R.id.listCloseBtn);
        listFragment = (Fragment) new ListFragment();
=======
        listInput = findViewById(R.id.listInput);
        saveBtn = findViewById(R.id.saveBtn);
        closeBtn = findViewById(R.id.listCloseBtn);
        listFragment = new ListFragment();
        // 임시로 데이터 저장
        listArray = new ArrayList<>();
>>>>>>> Stashed changes

        Intent intent = getIntent();
        String position = intent.getStringExtra("position"); // 마트명


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        items = new ArrayList<>(); // shoppingList

        database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference("shoppingList");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
<<<<<<< Updated upstream
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 데이터를 받아온다
                items.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ListNote shoppingList = snapshot.getValue(ListNote.class);
                    items.add(shoppingList);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 시
                Toast.makeText(ShoppingListActivity.this, "에러 발생", Toast.LENGTH_SHORT).show();
=======
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();
>>>>>>> Stashed changes
            }
        });

        adapter = new ListAdapter(items, this);
        recyclerView.setAdapter(adapter);


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