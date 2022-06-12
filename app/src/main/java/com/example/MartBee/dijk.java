package com.example.MartBee;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;


public class dijk extends Application {

    ArrayList<String> list;
    String start;

    public void test(String startPoint) {
        start = startPoint;
        Dijkstra d = new Dijkstra(20); // 노드 개수

        // 살 목록
        list = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference("user");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    list.add(String.valueOf(snapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "DatabaseError", Toast.LENGTH_SHORT).show();
            }
        });


        // 거리 설정
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference dRef = firestore.collection("distance");
        dRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        for (String key : documentSnapshot.getData().keySet()) {
//                            Log.d(key, String.valueOf(documentSnapshot.getData().get(key)));
                            d.input(documentSnapshot.getId(), key, Integer.parseInt(String.valueOf(documentSnapshot.getData().get(key))));
                        }
                    }
                }
            }
        });

        d.reArray(list);
    }

    class Dijkstra {

        private int n;
        private int[][] weight;
        private String[] saveRoute;


        public Dijkstra(int n) {
            super();
            this.n = n;
            weight = new int[n][n];
            saveRoute = new String[n];
        }

        public int stringToInt(String s) {
            int x = 0;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(s)) x = i;
            }
            return x;
        }

        public void reArray(ArrayList<String> r) {
            int[] array = new int[r.size()];
            for (int i = 0; i < r.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (r.get(i).equals(list.get(j))) {
                        array[i] = j;

                    }
                }
            }
            Arrays.sort(array);

            if (start.equals("i")) {
                algorithm("i", list.get(array[0]));

                for (int i = 0; i < array.length - 1; i++) {
                    algorithm(list.get(array[i]), list.get(array[i + 1]));
                }
            } else if (start.equals("t")) {
                algorithm("t", list.get(array[0]));
                for (int i = 0; i < array.length - 1; i++) {
                    algorithm(list.get(array[i]), list.get(array[i + 1]));
                }

            }


        }

        public void input(String v1, String v2, int w) {
            int x1 = stringToInt(v1);
            int x2 = stringToInt(v2);
            weight[x1][x2] = w;
            weight[x2][x1] = w;
        }

        public void algorithm(String a, String k) {
            boolean[] visited = new boolean[n];
            int[] distance = new int[n];

            for (int i = 0; i < n; i++) {
                distance[i] = Integer.MAX_VALUE;
            }


            int x = 0;
            for (int i = 0; i < list.size(); i++) {
                if (a.equals(list.get(i)))
                    x = i;

            }

            distance[x] = 0;
            visited[x] = true;
            saveRoute[x] = list.get(x);

            for (int i = 0; i < n; i++) {
                if (!visited[i] && weight[x][i] != 0) {
                    distance[i] = weight[x][i];
                    saveRoute[i] = list.get(x);
                }
            }

            for (int i = 0; i < n - 1; i++) {
                int minDistance = Integer.MAX_VALUE;
                int minVertex = -1;
                for (int j = 0; j < n; j++) {
                    if (!visited[j] && distance[j] != Integer.MAX_VALUE) {
                        if (distance[j] < minDistance) {
                            minDistance = distance[j];
                            minVertex = j;
                        }
                    }
                }
                visited[minVertex] = true;
                for (int j = 0; j < n; j++) {
                    if (!visited[j] && weight[minVertex][j] != 0) {
                        if (distance[j] > distance[minVertex] + weight[minVertex][j]) {
                            distance[j] = distance[minVertex] + weight[minVertex][j];
                            saveRoute[j] = list.get(minVertex);
                        }
                    }
                }
            }


            for (int i = 0; i < n; i++) {
                if (k.equals(list.get(i))) {
                    System.out.println("시작 꼭지점 " + a + "부터 꼭지점 " + k + "까지의 거리 :" + distance[i]);

                }
            }

            for (int i = 0; i < n; i++) {
                if (k.equals(list.get(i))) {
                    StringBuilder route = new StringBuilder();

                    int index = i;
                    while (!saveRoute[index].equals(list.get(index))) {
                        route.append(" ").append(saveRoute[index]);
                        index = stringToInt(saveRoute[index]);
                    }
                    StringBuilder sb = new StringBuilder(route.toString());
                    Log.d("result", sb.reverse() + list.get(i));
//                    System.out.print(sb.reverse() + list.get(i));
//                    System.out.println("\n");
                }
            }

        }
    }
}