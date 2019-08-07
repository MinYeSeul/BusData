package com.example.busdata;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SpecificActivity extends AppCompatActivity {

    TextView textdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific);

        // Id값 연결
        textdata = findViewById(R.id.busdata);

        // 버스리스트뷰에서 전달한 String 정보 받기 위한 Intent 설정
        Intent intent = getIntent();

        // 버스리스트뷰에서 전달한 버스 번호 정보 받기
        String busnum = ((Intent) intent).getStringExtra("busnum");

        // 버스리스트뷰에서 전달한 도착 시간 정보 받기
        String busdata = ((Intent) intent).getStringExtra("busdata");


        // 텍스트데이타에 띄워주기
        textdata.setText(busnum + "번 버스가\n" + busdata + " 도착합니다.");

        //https://ande226.tistory.com/141
        //ActionBar로 뒤로가기 버튼 추가
        ActionBar actionBar = getSupportActionBar();  //제목줄 객체 얻어오기
        actionBar.setTitle("뒤로가기");  //액션바 제목설정
        actionBar.setDisplayHomeAsUpEnabled(true);   //업버튼 <- 만들기

    }
}