package com.example.busdata;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class SpecificActivity extends AppCompatActivity {

    TextView textdata;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific);

        // Id값 연결
        textdata = findViewById(R.id.busdata);


        /**
        // 버스리스트뷰에서 전달한 String 정보 받기 위한 Intent 설정
        Intent intent = getIntent();

        // 버스리스트뷰에서 전달한 버스 번호 정보 받기
        String busnum = ((Intent) intent).getStringExtra("busnum");

        // 버스리스트뷰에서 전달한 도착 시간 정보 받기
        String busdata = ((Intent) intent).getStringExtra("busdata");
        Log.d("버스정보", ListViewActivity.busnum.toString() + ListViewActivity.busmin.toString());
         */

        String[] data = ListViewActivity.busWhen[0].split(" ");
        int[] dataN = new int[20];

        for (int j = 0; j < data.length; j++) {
            if (!data[j].isEmpty()) {
                dataN[j] = Integer.parseInt(data[j]);

            }
            Arrays.sort(dataN);
        }


        for(int i = 0; i < dataN.length; i++) {
            if(dataN[i] > 0) {
                textdata.append(ListViewActivity.busNumber + "번 버스가 " + dataN[i] + "분 뒤에 도착합니다.\n\n");
            }
        }

        // 텍스트데이타에 띄워주기
        // textdata.setText(ListViewActivity.busNumber + "번 버스가" + ListViewActivity.busWhen[0] +  "분 뒤에 도착합니다.");

        //https://ande226.tistory.com/141
        //ActionBar로 뒤로가기 버튼 추가
        ActionBar actionBar = getSupportActionBar();  //제목줄 객체 얻어오기
        actionBar.setTitle("뒤로가기");  //액션바 제목설정
        actionBar.setDisplayHomeAsUpEnabled(true);   //업버튼 <- 만들기

    }
}