package com.example.busdata;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class FindBusStation extends AppCompatActivity {

    //공공데이터 API 사용을 위한 키값
    String key = "QzQ64Y0ttlhXPP7CVvMZKf6NKxitNjOameIBPVADX4f9%2FxPRnLqZkDljqmpTROuyOCabJF8ncXbxDqHGEFAtPA%3D%3D";

    TextView text01;
    TextView text02;
    TextView busStation;

    Button yesbtn;
    Button nobtn;

    // 정류장 키값, 이름, 번호를 저장하기 위한 배열 선언
    ArrayList<String> stationKey = new ArrayList<>();
    ArrayList<String> stationName = new ArrayList<>();
    ArrayList<String> stationCode = new ArrayList<>();

    //경도와 위도 변수 선언
    double longitude = 0.0;
    double latitude = 0.0;

    int num = 0;

    // 찾아낸 인근 정류장의 키값, 이름, 번호를 따로 저장하기 위한 문자열 선언
    static String sKey = "";
    static String sName = "";
    static String sCode = "";

    //TTS 변수
    TextToSpeech tts;

    boolean networkCheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_bus_station);

        busStation = findViewById(R.id.station_name);
        text01 = findViewById(R.id.text001);
        text02 = findViewById(R.id.text002);

        nobtn = findViewById(R.id.yesbtn);
        yesbtn = findViewById(R.id.nobtn);


        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                    //tts.setLanguage(Locale.ENGLISH);
                }
            }
        });


        // gps 가져오기
        getGPS();
        Log.d("디버깅2", longitude+"" + latitude);

        new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.O)
            @Override
            public void run() {
                getStationData();
                //Log.d("디버깅2", stationKey.get(0) + "" + stationName.get(0));

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (stationName.size() == 0 || stationKey.size() == 0) {
                            if (!networkCheck) {
                                busStation.setText("");
                                text01.setText("네트워크 연결이 안되어 있습니다.");
                                text02.setText("");

                                speakText();

                                yesbtn.setEnabled(false);
                                nobtn.setEnabled(false);
                                yesbtn.setVisibility(View.INVISIBLE);
                                nobtn.setVisibility(View.INVISIBLE);
                            }

                            else if (longitude == 0.0 || latitude == 0.0) {
                                busStation.setText("");
                                text01.setText("GPS연결이 안되어 있습니다.");
                                text02.setText("");

                                speakText();
                                yesbtn.setEnabled(false);
                                nobtn.setEnabled(false);
                                yesbtn.setVisibility(View.INVISIBLE);
                                nobtn.setVisibility(View.INVISIBLE);
                            }

                        }
                        else {
                            busStation.setText(stationName.get(0) + " " + stationKey.get(0));
                            sKey = stationKey.get(0);
                            sName = stationName.get(0);
                            sCode = stationCode.get(0);

                            speakText();
                        }
                    }
                });
            }
        }).start();



    }


    private void speakText() {

        // 영어를 합쳐서 읽지 않기 위해 한글자씩 끊어 읽도록 함 (ex : YMCA를 Y M C A 로 바꿔줌)
        for(int i = 0; i < sName.length(); i++) {
            int index = sName.charAt(i);

            if (index >= 65 && index <= 122) {
                sName = sName.replace(sName.charAt(i)+"", sName.charAt(i) + " ");
            }
        }

        // 버스 번호를 합쳐서 읽지 않기 위해 한글자씩 끊어 읽도록 함 (ex : 38613을 3, 8, 6, 1, 3 으로 나눠줌)
        String[] sKeySplit = sKey.split("", sKey.length() + 1);


        //http://stackoverflow.com/a/29777304 참고
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(text01.getText().toString() + sName + Arrays.toString(sKeySplit) + text02.getText().toString());
        } else {
            ttsUnder20(text01.getText().toString() + sName + Arrays.toString(sKeySplit) + text02.getText().toString());
        }

    }

    private void getGPS() {

        //gps정보 가져오기 링크 : https://bottlecok.tistory.com/54
        //Location Manager 생성
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //API버전확인
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FindBusStation.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }

        //위도, 경도 value 가져오기
        else {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // location 변수에 최근 gps정보 할당
            if(location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            } else {
                latitude = 0.0;
                longitude = 0.0;
            }
            //특정 시간이 지나거나 gps정보가 특정거리 이상 변경 되었을 때 gps 정보 업데이트
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    //시간 설정 (단위 ms)
                    1000,
                    //거리 설정  (단위 m)
                    1,
                    //gpslistner 연결
                    gpsLocationListener);

        }
    }

    //https://bottlecok.tistory.com/54 참고
    //location listener 선언부분 : gps 정보가 바뀌는 이벤트를 받아주는 listener
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
        //사용하지 않는 메서드들
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    private void getStationData() {

        StringBuffer buffer = new StringBuffer();
        String queryUrl = "http://openapi.tago.go.kr/openapi/service/BusSttnInfoInqireService/getCrdntPrxmtSttnList?"
                + "serviceKey=" + key + "&gpsLati=" + latitude + "&gpsLong=" + longitude;

        try {

            URL url = new URL(queryUrl);
            InputStream is2 = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();


            xpp.setInput(new InputStreamReader(is2, "UTF-8"));

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();

            //현재값 2, 스타트다큐 0, 엔드다큐 1, 스타트태그 2; 택스트 4, 엔드태그 3
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("파싱 시작...\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        //태그 이름 얻어오기
                        tag = xpp.getName();

                        // 첫번째 검색결과
                        if (tag.equals("item"));
                        else if(tag.equals("nodeid")) {
                            buffer.append("정류장 키");
                            buffer.append(":");
                            xpp.next();
                            //TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append(xpp.getText());
                            //줄바꿈 문자 추가
                            buffer.append("\n");
                            // 버스정류장 키값 추가
                            stationCode.add(xpp.getText());
                            Log.d("버스정류장 이름", stationCode+"");
                        }
                        else if(tag.equals("nodenm")) {
                            buffer.append("정류장 이름");
                            buffer.append(":");
                            xpp.next();
                            //TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append(xpp.getText());
                            //줄바꿈 문자 추가
                            buffer.append("\n");
                            // 버스정류장 이름 추가
                            stationName.add(xpp.getText());
                            Log.d("버스정류장 이름", stationName+"");
                        }
                        else if (tag.equals("nodeno")) {
                            buffer.append("정류장 id");
                            buffer.append(" : ");
                            xpp.next();
                            //TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append(xpp.getText());
                            //줄바꿈 문자 추가
                            buffer.append("\n");
                            // 버스정류장 번호 추가
                            stationKey.add(xpp.getText());
                            Log.d("버스번호", stationKey+"");
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        //태그 이름 얻어오기
                        tag = xpp.getName();
                        //첫번째 검색결과종료..줄바꿈
                        if (tag.equals("item")) buffer.append("\n");
                        break;
                }
                eventType = xpp.next();
            }
        } catch (MalformedURLException e) {
            Log.d("디버깅2", "잘못된 url");

        } catch (SocketException e) {
            Log.d("디버깅2", "타임아웃");

        } catch (IOException e) {
            networkCheck = false;
            Log.d("디버깅2", "네트웍 문제");

        } catch (Exception e) {
            Log.d("디버깅2", "기타문제" + e.toString());

            //Auto-generated catch blocke.printStackTrace();
        }
        // buffer.append("파싱 끝\n");
    }

    public void YesButtonClicked(View view) {
        // 예 버튼을 누르면 MainActivity로 넘어감
        Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
        startActivity(intent);
        finish();
    }

    public void NoButtonClicked(View view) {
        // 아니오 버튼을 누르면 다음 정류장 추천
        num++;
        // 배열의 마지막까지 추천을 다 하면 gps가 정확하지 않다는 멘트 띄움
        if(num >= stationName.size()) {
            busStation.setText("");
            text01.setText("GPS가 정확하지 않습니다. 다시 시도해주세요.");
            text02.setText("");
            // 읽어주기
            speakText();

        }
        else {
            // 배열의 마지막이 되기 전에는 다음 정류장 추천 _ 버스정류장 이름 코드 바꿔서 setText
            busStation.setText(stationName.get(num) + " " + stationKey.get(num));
            sKey = stationKey.get(num);
            sName = stationName.get(num);
            sCode = stationCode.get(num);
            // 읽어주기
            speakText();

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