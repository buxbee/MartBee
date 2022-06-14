package com.example.MartBee;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class MapActivity extends AppCompatActivity {

    private Button showList, prev, next;
    private ImageView imageView;
    private String floor, start, name, mode;
    private Intent intent;
    private HashMap<String, ArrayList<Object>> storeCategory;
    private ArrayList<String> databaseCategory;
    private ArrayList<Object> XY;
    private Handler mHandler;
    ArrayList<Integer[]> crd;
    public ArrayList<String> route;
    ArrayList<String> list;

    enum TOUCH_MODE {
        NONE,   // 터치 안했을 때
        SINGLE, // 한손가락 터치
        MULTI   //두손가락 터치
    }

    private TOUCH_MODE touchMode;
    private Matrix matrix;      //기존 매트릭스
    private Matrix savedMatrix; //작업 후 이미지에 매핑할 매트릭스
    private PointF startPoint;  //한손가락 터치 이동 포인트
    private PointF midPoint;    //두손가락 터치 시 중신 포인트
    private float oldDistance;  //터치 시 두손가락 사이의 거리
    private double oldDegree = 0; // 두손가락의 각도


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        imageView = findViewById(R.id.image);
        showList = findViewById(R.id.showList);
        prev = findViewById(R.id.prevFloor);
        next = findViewById(R.id.nextFloor);

        intent = getIntent();
        floor = intent.getStringExtra("floor"); // 층
        start = intent.getStringExtra("startPoint"); // 시작 지점
        name = intent.getStringExtra("name"); // 마트 이름
        mode = intent.getStringExtra("mode"); // 모드

        mHandler = new Handler();
        route = new ArrayList<>();
        crd = new ArrayList<>();
        list = new ArrayList<>();

        // datastore, collection
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference cRef = firestore.collection("category");
        storeCategory = new HashMap<>();
        cRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                        Log.d("datastore", String.valueOf(documentSnapshot.getData()));
//                        Log.d("datastore", documentSnapshot.getId());
                        XY = new ArrayList<>();
                        XY.add(documentSnapshot.get("x"));
                        XY.add(documentSnapshot.get("y"));
                        storeCategory.put(documentSnapshot.getId(), XY);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "firestore 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // realtime db
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference("user");
        databaseCategory = new ArrayList<>();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Log.d("data", String.valueOf(snapshot.getValue()));
                    databaseCategory.add(String.valueOf(snapshot.getValue()));
                    list.add(String.valueOf(snapshot.getValue()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "DatabaseError", Toast.LENGTH_SHORT).show();
            }
        });

        new dijk().test(start);

        // navigation crd set
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mode != null && mode.equals("1")) {
                    // navigation


                    for (int i = 0; i < route.size(); i++) {
//                        Log.d("get", route.get(i));
                        ArrayList<Object> tempValue = storeCategory.get(route.get(i));

                        if (tempValue != null) {
                            String x = String.valueOf(tempValue.get(0));
                            String y = String.valueOf(tempValue.get(1));
                            x = x.substring(1, x.length() - 1);
                            y = y.substring(1, y.length() - 1);

                            crd.add(new Integer[]{Integer.parseInt(x), Integer.parseInt(y)});
                        }
                    }
                }
            }
        }, 500);


        // uri to bitmap
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference pathReference = storageReference.child(name);

        if (pathReference == null) {
        } else {
            StorageReference submitProfile = storageReference.child(name + "/" + floor + ".png");
            submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        new MyView(MapActivity.this, uri, route);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
//                    imageView.setImageURI(uri);
//                    Glide.with(MapActivity.this).load(uri).into(imageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "이미지 로딩에 실패하였습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }


        matrix = new Matrix();
        savedMatrix = new Matrix();

        imageView.setOnTouchListener(onTouch);
        imageView.setScaleType(ImageView.ScaleType.MATRIX); // 스케일 타입을 매트릭스로 해줘야 움직인다.

        // button
        showList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListCustomDialog customDialog = new ListCustomDialog(MapActivity.this, new ListCustomDialogClickListener() {
                    @Override
                    public void onCloseClick() {

                    }
                });
                customDialog.setCanceledOnTouchOutside(true);
                customDialog.setCancelable(true);
                customDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                customDialog.show();

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floor = Integer.toString(Integer.parseInt(floor) - 1);
                StorageReference submitProfile = storageReference.child(name + "/" + floor + ".png");
                submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
//                        Glide.with(MapActivity.this).load(uri).into(imageView);
                        try {
                            new MyView(MapActivity.this, uri, route);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "이미지 로딩에 실패하였습니다", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floor = Integer.toString(Integer.parseInt(floor) + 1);
                StorageReference submitProfile = storageReference.child(name + "/" + floor + ".png");
                submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
//                        Glide.with(MapActivity.this).load(uri).into(imageView);
                        try {
                            new MyView(MapActivity.this, uri, route);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "아직 준비되지 않은 마트입니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public class MyView extends View{
        private BitmapDrawable marker, arrow;
        private Bitmap tempBitmap, map;
        private Canvas tempCanvas;
        Paint paint;

        public MyView(Context context, Uri uri, ArrayList<String> route) throws FileNotFoundException {
            super(context);

            paint = new Paint();

            Glide.with(getApplicationContext()).asBitmap().load(uri).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    map = resource.copy(resource.getConfig(), true);
                    tempBitmap = Bitmap.createBitmap(resource.getWidth(), resource.getHeight(), resource.getConfig());
                    tempCanvas = new Canvas(tempBitmap);

                    marker = (BitmapDrawable) getResources().getDrawable(R.drawable.marker2);
                    arrow = (BitmapDrawable) getResources().getDrawable(R.drawable.arrow);
                    draw(tempCanvas);

                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    Log.e("onLoadCleared", String.valueOf(placeholder));
                }
            });
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            tempCanvas.drawBitmap(map, 0, 0, null);
            Bitmap markerBitmap = marker.getBitmap();
            Bitmap arrowBitmap = arrow.getBitmap();


            if (mode != null && mode.equals("1")) {
                paint.setStrokeWidth(5f);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.BLACK);
                for (int i = 0; i < crd.size() - 1; i++) {
                    canvas.drawLine(crd.get(i)[0], crd.get(i)[1], crd.get(i + 1)[0], crd.get(i + 1)[1], paint);

                }
            }

            //Draw marker
            for (String group : databaseCategory) {
                if (storeCategory.containsKey(group)) {
                    ArrayList<Object> tempValue = storeCategory.get(group);
                    assert tempValue != null;
                    String x = String.valueOf(tempValue.get(0));
                    String y = String.valueOf(tempValue.get(1));
                    x = x.substring(1, x.length() - 1);
                    y = y.substring(1, y.length() - 1);

                    tempCanvas.drawBitmap(markerBitmap, Integer.parseInt(x) - 30, Integer.parseInt(y) - 30, null);

                }
//                ArrayList<Object> tempValue = storeCategory.get(start);
//                assert tempValue != null;
//                String x = String.valueOf(tempValue.get(0));
//                String y = String.valueOf(tempValue.get(1));
//                x = x.substring(1, x.length() - 1);
//                y = y.substring(1, y.length() - 1);
//
//                tempCanvas.drawBitmap(arrowBitmap, Integer.parseInt(x) - 30, Integer.parseInt(y) - 30, null);

            }

            //Attach the canvas to the ImageView
            tempCanvas.save();
            imageView.setImageBitmap(tempBitmap);

            matrix.postTranslate(0, 100);
            matrix.postScale(2f, 2f);
            imageView.setImageMatrix(matrix);

        }

    }

    public class dijk extends Application {

        String start;
        Handler handler;


        public void test(String startPoint) {

            start = startPoint;
            Dijkstra d = new Dijkstra(20);
            handler = new Handler();

            d.input("축산", "수산", 2);
            d.input("축산", "건해산물", 5);
            d.input("수산", "과일", 2);
            d.input("수산", "스테이크", 5);
            d.input("과일", "계란", 3);
            d.input("과일", "해산물", 5);
            d.input("계란", "매장입구", 6);
            d.input("건해산물", "스테이크", 2);
            d.input("건해산물", "베이크랩", 1);
            d.input("스테이크", "해산물", 2);
            d.input("해산물", "곡물", 3);
            d.input("해산물", "해벗", 2);
            d.input("해벗", "매장입구", 1);
            d.input("매장입구", "에스컬레이터(up)", 3);
            d.input("매장입구", "에스컬레이터(down)", 10);
            d.input("에스컬레이터(up)", "r", 11);
            d.input("베이크랩", "와인", 4);
            d.input("와인", "냉동식품", 5);
            d.input("냉동식품", "행사장", 4);
            d.input("곡물", "계산대", 2);
            d.input("곡물", "조미인스턴트", 3);
            d.input("조미인스턴트", "행사장", 3);
            d.input("행사장", "과자", 2);
            d.input("과자", "계산대", 8);
            d.input("계산대", "에스컬레이터(down)", 6);
            d.input("에스컬레이터(down)", "화장실", 5);
//            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//            CollectionReference dRef = firestore.collection("distance");
//            dRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                            for (String key : documentSnapshot.getData().keySet()) {
//                                d.input(documentSnapshot.getId(), key, Integer.parseInt(String.valueOf(documentSnapshot.getData().get(key))));
//                            }
//                        }
//                    }
//                }
//            });


//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference mRef = database.getReference("user");
//            mRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        list.add(String.valueOf(snapshot.getValue()));
//                    }
//                    Log.d("list", String.valueOf(list));
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                }
//            });

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    d.reArray(list);
                }
            }, 400);
        }

        public ArrayList<String> returnRoute() {
            return route;
        }

        class Dijkstra {

            private int n;
            private int[][] weight;
            private String[] saveRoute, items;
            private String[] vertex = {
                    "매장입구",
                    "해벗",
                    "해산물",
                    "계란",
                    "과일",
                    "수산",
                    "스테이크",
                    "건해산물",
                    "축산",
                    "베이크랩",
                    "와인",
                    "냉동식품",
                    "곡물",
                    "조미인스턴트",
                    "행사장",
                    "계산대",
                    "과자",
                    "에스컬레이터(down)",
                    "화장실",
                    "에스컬레이터(up)"};


            public Dijkstra(int n) {
                super();
                this.n = n;
                weight = new int[n][n];
                saveRoute = new String[n];
            }

            public int stringToInt(String s) {
                int x = 0;
                for (int i = 0; i < vertex.length; i++) {
                    if (vertex[i].equals(s)) x = i;

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
                                saveRoute[j] = vertex[minVertex];
                            }
                        }
                    }
                }


                for (int i = 0; i < n; i++) {
                    if (k.equals(vertex[i])) {
                        String tempRoute = "";

                        int index = i;
                        while (!saveRoute[index].equals(vertex[index])) {
                            tempRoute = tempRoute + (saveRoute[index] + " ");
                            index = stringToInt(saveRoute[index]);
                        }

                        items = tempRoute.split(" ");

                        Collections.reverse(Arrays.asList(items));

                        route.addAll(Arrays.asList(items));
                        route.add(vertex[i]);
                    }
                }
//                route = new ArrayList<>(Arrays.asList(items));
            }
        }
    }

    private View.OnTouchListener onTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v.equals(imageView)) {
                int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        touchMode = TOUCH_MODE.SINGLE;
                        donwSingleEvent(event);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (event.getPointerCount() == 2) { // 두손가락 터치를 했을 때
                            touchMode = TOUCH_MODE.MULTI;
                            downMultiEvent(event);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (touchMode == TOUCH_MODE.SINGLE) {
                            moveSingleEvent(event);
                        } else if (touchMode == TOUCH_MODE.MULTI) {
                            moveMultiEvent(event);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        touchMode = TOUCH_MODE.NONE;
                        break;
                }
            }
            return true;
        }
    };

    private PointF getMidPoint(MotionEvent e) {

        float x = (e.getX(0) + e.getX(1)) / 2;
        float y = (e.getY(0) + e.getY(1)) / 2;

        return new PointF(x, y);
    }

    private float getDistance(MotionEvent e) {
        float x = e.getX(0) - e.getX(1);
        float y = e.getY(0) - e.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void donwSingleEvent(MotionEvent event) {
        savedMatrix.set(matrix);
        startPoint = new PointF(event.getX(), event.getY());
    }

    private void downMultiEvent(MotionEvent event) {
        oldDistance = getDistance(event);
        if (oldDistance > 5f) {
            savedMatrix.set(matrix);
            midPoint = getMidPoint(event);
            double radian = Math.atan2(event.getY() - midPoint.y, event.getX() - midPoint.x);
            oldDegree = (radian * 180) / Math.PI;
        }
    }

    private void moveSingleEvent(MotionEvent event) {
        matrix.set(savedMatrix);
        matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
        imageView.setImageMatrix(matrix);
    }

    private void moveMultiEvent(MotionEvent event) {
        float newDistance = getDistance(event);
        if (newDistance > 5f) {
            matrix.set(savedMatrix);
            float scale = newDistance / oldDistance;
            matrix.postScale(scale, scale, midPoint.x, midPoint.y);

            double nowRadian = Math.atan2(event.getY() - midPoint.y, event.getX() - midPoint.x);
            double nowDegress = (nowRadian * 180) / Math.PI;
            float degree = (float) (nowDegress - oldDegree);
            matrix.postRotate(degree, midPoint.x, midPoint.y);


            imageView.setImageMatrix(matrix);

        }
    }
}