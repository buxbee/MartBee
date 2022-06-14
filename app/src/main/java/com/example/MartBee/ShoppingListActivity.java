package com.example.MartBee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {

    Button saveBtn, closeBtn, deleteButton;
    Fragment listFragment;
    ListView list;
    EditText editText;
    ImageView imageView;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference("user");
    String[] key = new String[50];
    int cnt=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        saveBtn = findViewById(R.id.saveBtn);
        closeBtn = findViewById(R.id.listCloseBtn);
        deleteButton=findViewById(R.id.delete);
        listFragment = new ListFragment();
        imageView = findViewById(R.id.image);
        editText=findViewById(R.id.listInput);

        list=(ListView)findViewById(R.id.list);
        List<String> data=new ArrayList<>();
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,data);
        list.setAdapter(adapter);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name"); // 마트명
        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String data1=dataSnapshot.getValue(String.class);
                    key[cnt++]=dataSnapshot.getKey();
                    data.add(data1);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();
                String data1=editText.getText().toString();

                DatabaseReference conditionRef=mRootRef.push();

                key[cnt++]=conditionRef.getKey();
                conditionRef.setValue(data1);
                data.add(data1);
                adapter.notifyDataSetChanged();

                editText.setText(null);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                adapter.notifyDataSetChanged();
                int count, checked;
                count= adapter.getCount();
                if(count>0){
                    checked=list.getCheckedItemPosition();
                    if(checked>-1 && checked<count){
                        mRootRef.child(key[checked]).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "삭제 성공", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("error: "+e.getMessage());
                                Toast.makeText(getApplicationContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
                        data.remove(checked);
                        list.clearChoices();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog(ShoppingListActivity.this, new CustomDialogClickListener() {
                    @Override
                    public void onPositiveClick(String floor, String startPoint, String mode) {

                        Intent toMapIntent = new Intent(ShoppingListActivity.this, MapActivity.class);
                        toMapIntent.putExtra("name", name); // 마트명
                        toMapIntent.putExtra("floor", floor); // n층
                        toMapIntent.putExtra("startPoint", startPoint); // 시작 지점
                        toMapIntent.putExtra("mode", mode); // pin or 최적 경로

                        startActivity(toMapIntent);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
                customDialog.setCanceledOnTouchOutside(true);
                customDialog.setCancelable(true);

                customDialog.show();
                Window window = customDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            }
        });
    }}