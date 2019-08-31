package com.example.busdata;

import android.annotation.TargetApi;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class SpecificActivity extends AppCompatActivity {

    TextView textdata;
    //TTS 변수
    TextToSpeech tts;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific);

        // Id값 연결
        textdata = findViewById(R.id.busdata);

        // 몇 분 뒤 도착하는지 저장해뒀던 분 두 개 split으로 나누기
        String[] data = ListViewActivity.busWhen[0].split(" ");

        // int형으로 바꿔주기 위해 int형 배열 선언
        int[] dataN = new int[20];

        // 공백이 아니라면 int형으로 바꿔줌
        for (int j = 0; j < data.length; j++) {
            if (!data[j].isEmpty()) {
                dataN[j] = Integer.parseInt(data[j]);

            }
            Arrays.sort(dataN);
        }

        // 0보다 크다면 textdata에 추가해주기
        for(int i = 0; i < dataN.length; i++) {
            if (dataN[i] > 0) {
                textdata.append(ListViewActivity.busNumber + "번 버스가" + "\n" + dataN[i] + "분 뒤에 도착합니다.\n\n");

            }
        }


        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ttsGreater21(textdata.getText().toString());
                } else {
                    ttsUnder20(textdata.getText().toString());
                }
            }
        });



        //https://ande226.tistory.com/141
        //ActionBar로 뒤로가기 버튼 추가
        ActionBar actionBar = getSupportActionBar();  //제목줄 객체 얻어오기
        actionBar.setTitle("뒤로가기");  //액션바 제목설정
        actionBar.setDisplayHomeAsUpEnabled(true);   //업버튼 <- 만들기

    }

    //https://webnautes.tistory.com/847 -->음성인식 기능 레퍼런스
    //무조건 tts를 위해서 onclick할때 override 되어야 오류가 뜨지 않음. 그냥 두면 됨.
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (tts != null) {
            //음성인식 다하면 끝내기
            tts.stop();
            tts.shutdown();
        }
    }

    //API 버전 20 아래용
    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        //해쉬맵 만들기
        HashMap<String, String> map = new HashMap<>();
        //그냥 음성인식기술 해쉬맵에 넣어주는 코드
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        //text 파라미터로 전달받은 인자를 해쉬맵이랑 연결해서 음성으로 바꿔서 말해줌
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    //API버전 21 이상용
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = this.hashCode() + "";
        //text 파라미터로 전달받은 인자를 음성으로 바꿔서 말해줌
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);

    }
}