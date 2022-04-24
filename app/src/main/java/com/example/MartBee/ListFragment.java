package com.example.MartBee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private static final String TAG = "ListFragment";
    RecyclerView recyclerView;
    ListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);

        initUI(rootView);
        return rootView;
    }

    public void saveList(ArrayList<ListNote> listArray, EditText listInput, ListNote listText) {

        if (listText == null) {
            Toast.makeText(getContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
        else {
            listArray.add(listText);
            adapter.setItems(listArray);
            adapter.notifyItemInserted(listArray.size()-1);
        }

        //EditText 안의 글 초기화
        listInput.setText("");

        Toast.makeText(getContext(), " 추가되었습니다.", Toast.LENGTH_SHORT).show();

    }


    private void initUI(ViewGroup rootView){
        recyclerView = rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ListAdapter();
        recyclerView.setAdapter(adapter);
    }
}
