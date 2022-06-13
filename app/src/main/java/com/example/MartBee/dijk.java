package com.example.MartBee;

import android.app.Application;
import android.os.Handler;
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
    Handler handler;

    public void test(String startPoint, int firestoreSize) {

        Dijkstra d = new Dijkstra(firestoreSize); // 노드 개수
        start = startPoint;
        list = new ArrayList<>();
        handler = new Handler();

        // 거리 설정
        // input 부분
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference dRef = firestore.collection("distance");
        dRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        for (String key : documentSnapshot.getData().keySet()) {
//                            Log.d(key, Integer.parseInt(String.valueOf(documentSnapshot.getData().get(key))));
                            d.input(documentSnapshot.getId(), key, Integer.parseInt(String.valueOf(documentSnapshot.getData().get(key))));
                        }
//                        vertex.add(documentSnapshot.getId());
                    }
                }
            }
        });

        // 살 목록
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

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                d.reArray(list);

            }
        }, 300);
    }

    class Dijkstra {

        private int n;
        private int[][] weight;
        private String[] saveRoute;
        private String[] vertex = {"축산", "수산", "과일", "계란", "건해산물", "스테이크",
                "해산물", "해벗", "매장입구", "베이크랩", "와인", "냉동식품", "조미인스턴트",
                "행사장", "곡물", "계산대", "과자", "에스컬레이터(down)", "화장실", "에스컬레이터(up)"};

        public Dijkstra(int n) {
            super();
            this.n = n;
            weight = new int[n][n];
            saveRoute = new String[n];
        }

        public int stringToInt(String s) {
            int x = 0;
            for (int i = 0; i < vertex.length; i++) {
                if (vertex[i].equals(s)) { x = i;}

            }
            return x;
        }

        public void reArray(ArrayList<String> r) {

            int[] array = new int[r.size()];

            for (int i = 0; i < r.size(); i++) {
                for (int j = 0; j < vertex.length; j++) {
                    if (r.get(i).equals(vertex[j])) {
                        array[i] = j;
                    }
                }
            }

            Arrays.sort(array);
            // array [1, 3]

            // start = 출발 지점
            if (start.equals("매장입구")) {
                algorithm("매장입구", vertex[array[0]]);

                for (int i = 0; i < array.length - 1; i++) {
                    algorithm(vertex[array[i]], vertex[array[i + 1]]);
                }
            } else if (start.equals("에스컬레이터(up)")) {
                algorithm("에스컬레이터(up)", vertex[array[0]]);

                for (int i = 0; i < array.length - 1; i++) {
                    algorithm(vertex[array[i]], vertex[array[i + 1]]);
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
            for (int i = 0; i < vertex.length; i++) {
                if (a.equals(vertex[i]))
                    x = i;
            }
            // x = 8
            distance[x] = 0;
            visited[x] = true;
            saveRoute[x] = vertex[x];

            int y = stringToInt(k);

            for (int i = 0; i < n; i++) {
                if (!visited[i] && weight[x][i] != 0) {
                    distance[i] = weight[x][i];
                    saveRoute[i] = vertex[x];
                }
            }

            for (int i = 0; i < n - 1; i++) {

                int minDistance = Integer.MAX_VALUE;
                int minVertex = -1;

                Log.d("visited", Arrays.toString(visited));
                Log.d("distance", Arrays.toString(distance));

                for (int j = 0; j < n; j++) {
//                    Log.d("j", String.valueOf(j));
                    if (!visited[j] && distance[j] != Integer.MAX_VALUE) {
                        Log.d("condition", "checked");
                        if (distance[j] < minDistance) {
                            Log.d("condition2", "checked");
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
                            saveRoute[j] = vertex[minVertex];
                        }
                    }
                }
            }


            for (int i = 0; i < n; i++) {
                if (k.equals(vertex[i]) == true) {
                    System.out.println("시작 꼭지점 " + a + "부터 꼭지점 " + k + "까지의 거리 :" + distance[i]);

                }
            }


            for (int i = 0; i < n; i++) {
                if (k.equals(vertex[i]) == true) {
                    String route = "";

                    int index = i;
                    while (true) {
                        if (saveRoute[index] == vertex[index]) break;
                        route += " " + saveRoute[index];
                        index = stringToInt(saveRoute[index]);
                    }
                    StringBuilder sb = new StringBuilder(route);
//                    System.out.print(sb.reverse() + vertex[i]);
//                    System.out.println("\n");
                }
            }
            Log.d("finish", "finish");
        }
    }
}

//    Handler handler;
//    ArrayList<String> list;
//    ArrayList<String> vertex = new ArrayList<>();
//    String start;
//
//    public void test(String startPoint, int firestoreSize) {
//
//        start = startPoint;
//        Dijkstra d = new Dijkstra(firestoreSize); // 노드 개수
//
//        // 거리 설정
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        CollectionReference dRef = firestore.collection("distance");
//        dRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                        for (String key : documentSnapshot.getData().keySet()) {
////                            Log.d(key, Integer.parseInt(String.valueOf(documentSnapshot.getData().get(key))));
//                            d.input(documentSnapshot.getId(), key, Integer.parseInt(String.valueOf(documentSnapshot.getData().get(key))));
//                        }
//                        vertex.add(documentSnapshot.getId());
//                    }
//                }
//            }
//        });
//
//        // 살 목록
//        list = new ArrayList<>();
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference mRef = database.getReference("user");
//        mRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    list.add(String.valueOf(snapshot.getValue()));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getApplicationContext(), "DatabaseError", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                d.reArray(list);
//
//            }
//        }, 300);
//    }
//
//    class Dijkstra {
//
//        int n;
//        int[][] weight;
//        String[] saveRoute;
//
//
//        public Dijkstra(int n) {
//            super();
//            this.n = n;
//            weight = new int[n][n];
//            saveRoute = new String[n];
//            handler = new Handler();
////            vertex.add("축산");
////            vertex.add("수산");
////            vertex.add("과일");
////            vertex.add("채소");
////            vertex.add("계란");
////            vertex.add("두부");
////            vertex.add("건해산물");
////            vertex.add("스테이크");
////            vertex.add("버거");
////            vertex.add("해산물");
////            vertex.add("해벗");
////            vertex.add("매장입구");
////            vertex.add("베이크랩");
////            vertex.add("와인");
////            vertex.add("주류");
////            vertex.add("음료");
////            vertex.add("유제품");
////            vertex.add("냉장식품");
////            vertex.add("냉동식품");
////            vertex.add("조미인스턴트");
////            vertex.add("행사장");
////            vertex.add("곡물");
////            vertex.add("계산대");
////            vertex.add("과자");
////            vertex.add("커피");
////            vertex.add("에스컬레이터(down)");
////            vertex.add("화장실");
////            vertex.add("에스컬레이터(up)");
//
//        }
//
//        public int stringToInt(String s) {
//            int x = 0;
//            for (int i = 0; i < vertex.size(); i++) {
//                if (vertex.get(i).equals(s)) {  x = i;   }
//            }
//            Log.d("x value", String.valueOf(x));
//            return x;
//        }
//
//        public void reArray(ArrayList<String> list) {
//
//            int[] array = new int[list.size()];
//
//            // 살 품목과 카테고리가 일치한다면
//            for (int i = 0; i < list.size(); i++) {
//                for (int j = 0; j < vertex.size(); j++) {
//                    if (list.get(i).equals(vertex.get(j))) {
//                        array[i] = j;
//                    }
//                }
//            }
//            Arrays.sort(array);
//
//            if (start.equals("매장입구")) {
//                algorithm("매장입구", vertex.get(array[0]));
//
//                for (int i = 0; i < array.length - 1; i++) {
//                    algorithm(vertex.get(array[i]), vertex.get(array[i + 1]));
//                }
//            } else if (start.equals("에스컬레이터(up)")) {
//                algorithm("에스컬레이터(up)", vertex.get(array[0]));
//                for (int i = 0; i < array.length - 1; i++) {
//                    algorithm(vertex.get(array[i]), vertex.get(array[i + 1]));
//                }
//
//            }
//
//
//        }
//
//        public void input(String v1, String v2, int w) {
//            int x1 = stringToInt(v1);
//            int x2 = stringToInt(v2);
//            weight[x1][x2] = w;
//            weight[x2][x1] = w;
//        }
//
//        public void algorithm(String a, String k) {
//            // 출발, 도착
//            Log.d("list", String.valueOf(list));
////            Log.d("vertex", String.valueOf(vertex.size()));
//
//            boolean[] visited = new boolean[n];
//            int[] distance = new int[n];
//
//            Log.d("algorithm", "reArray sort");
//
//            for (int i = 0; i < n; i++) {
//                distance[i] = Integer.MAX_VALUE;
//            }
//
//
//            int x = stringToInt(a);
////            for (int i = 0; i < vertex.size(); i++) {
////                if (a.equals(vertex.get(i))) {
////                    x = i; // 시작 꼭짓점
////                    Log.d("x", String.valueOf(x));
////                }
////            }
//            distance[x] = 0;
//            visited[x] = true;
//            saveRoute[x] = vertex.get(x);
//
//            for (int i = 0; i < n; i++) {
//                if (!visited[i] && weight[x][i] != 0) {
//                    distance[i] = weight[x][i];
//                    saveRoute[i] = vertex.get(x);
//                }
//            }
//
//            Log.d("vertex", String.valueOf(vertex));
//            Log.d(a, k);
//
//            for (int i = 0; i < n - 1; i++) {
//
//                int minDistance = Integer.MAX_VALUE;
//                int minVertex = -1;
//
//                Log.d("check", String.valueOf(i));
//                Log.d("visited", Arrays.toString(visited));
//                Log.d("distance", Arrays.toString(distance));
//
//                for (int j = 0; j < n; j++) {
//                    Log.d("check2", String.valueOf(j));
//                    if (!visited[j] && distance[j] != Integer.MAX_VALUE) {
//                        // 방문하지 않았고 distance가 MAX_VALUE가 아닐 때
//                        Log.d("condition1", "condition1");
//                        if (distance[j] < minDistance) {
//                            Log.d("condition2", "condition2");
//                            minDistance = distance[j];
//                            minVertex = j;
//                        }
//                    }
////                    Log.d(String.valueOf(i), String.valueOf(j));
////                    Log.d("minVertex", String.valueOf(minVertex));
////                    Log.d(vertex.get(j), String.valueOf(visited[j]));
//
//                }
//
//
//                visited[minVertex] = true;
//                for (int j = 0; j < n; j++) {
//                    if (!visited[j] && weight[minVertex][j] != 0) {
//                        if (distance[j] > distance[minVertex] + weight[minVertex][j]) {
//                            distance[j] = distance[minVertex] + weight[minVertex][j];
//                            saveRoute[j] = vertex.get(minVertex);
//
//                        }
//                    }
//                }
//
//            }
//
//
//            for (int i = 0; i < n; i++) {
//                if (k.equals(vertex.get(i))) {
//                    StringBuilder route = new StringBuilder();
//
//                    int index = i;
//                    while (!saveRoute[index].equals(vertex.get(index))) {
//                        route.append(" ").append(saveRoute[index]);
//                        index = stringToInt(saveRoute[index]);
//                    }
//                    StringBuilder sb = new StringBuilder(route.toString());
//                    Log.d("result", sb.reverse() + vertex.get(i));
////                    System.out.print(sb.reverse() + list.get(i));
////                    System.out.println("\n");
//                }
//            }
//
//        }
//    }
