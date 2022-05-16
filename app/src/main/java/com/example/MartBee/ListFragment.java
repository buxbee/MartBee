package com.example.MartBee;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListFragment extends Fragment {

<<<<<<< Updated upstream
    private static final String TAG = "ListFragment";
    RecyclerView recyclerView;
    ListAdapter adapter;
    private ArrayList<ListNote> items;
=======
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ListNote> listNoteArrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
>>>>>>> Stashed changes

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        listNoteArrayList = new ArrayList<>(); // User 객체를 담을 어레이 리스트 (어댑터쪽으로)

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);
<<<<<<< Updated upstream

        initUI(rootView);
        return rootView;

    }

    public void saveList(ArrayList<ListNote> items, EditText listInput, ListNote listText) {

        if (listText == null) {
            Toast.makeText(getContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
        else {

        }

        //EditText 안의 글 초기화
        listInput.setText("");

        Toast.makeText(getContext(), " 추가되었습니다.", Toast.LENGTH_SHORT).show();

    }


    private void initUI(ViewGroup rootView){
=======
>>>>>>> Stashed changes
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
        recyclerView.setLayoutManager(layoutManager);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("category"); // DB 테이블 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                listNoteArrayList.clear(); // 기존 배열리스트가 존재하지않게 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터 List를 추출해냄
                    ListNote listNote = snapshot.getValue(ListNote.class); // 만들어뒀던 User 객체에 데이터를 담는다.
                    listNoteArrayList.add(listNote); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침해야 반영이 됨
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("ListFragment", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
        adapter = new CustomAdapter(listNoteArrayList, getContext());
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

<<<<<<< Updated upstream
        adapter = new ListAdapter(items, getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter);
=======
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
>>>>>>> Stashed changes
    }
}
