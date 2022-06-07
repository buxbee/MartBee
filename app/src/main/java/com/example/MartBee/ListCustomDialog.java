package com.example.MartBee;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListCustomDialog extends Dialog {

    private Context context;
    private ListCustomDialogClickListener listCustomDialogClickListener;
    private EditText editText;
    private Button saveBtn, closeBtn, deleteButton;
    ListView list;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference("message2");


    public ListCustomDialog(@NonNull Context context, ListCustomDialogClickListener listCustomDialogClickListener) {
        super(context);
        this.context = context;
        this.listCustomDialogClickListener = listCustomDialogClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_custom_dialog);

        closeBtn = findViewById(R.id.closeBtn);
        saveBtn = findViewById(R.id.saveBtn);
        deleteButton=findViewById(R.id.delete);
        editText=findViewById(R.id.listInput);

        list=(ListView)findViewById(R.id.list);
        List<String> data=new ArrayList<>();
        ArrayAdapter<String> adapter=new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,data);
        list.setAdapter(adapter);

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String data2 = snapshot.getValue(String.class);
                    data.add(data2);
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
                String data1=editText.getText().toString();
                DatabaseReference conditionRef=mRootRef.push();
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
                        String element=data.get(checked).toString();
                        mRootRef.child(element).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(ListCustomDialog., "삭제 성공", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("error: "+e.getMessage());
//                                Toast.makeText(getApplicationContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
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
//                listCustomDialogClickListener.onCloseClick();
                dismiss();
            }
        });
    }
}
