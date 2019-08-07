package com.example.busdata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
    }

    // 5분 뒤에 도착하는 모든 버스 버튼을 눌렀을 때 실행됨
    public void mincall(View view) {
        // MainActivity 와 연결
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    // 특정 버스의 정보만 듣기 버튼을 눌렀을 때 실행
    public void specificcall(View view) {
        // 버스 리스트뷰와 연결
        Intent intent = new Intent(getApplicationContext(), ListViewActivity.class);
        startActivity(intent);
        finish();
    }
}