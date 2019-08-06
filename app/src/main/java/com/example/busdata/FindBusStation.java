package com.example.busdata;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class FindBusStation extends AppCompatActivity {

    //공공데이터 API 사용을 위한 키값
    String key = "QzQ64Y0ttlhXPP7CVvMZKf6NKxitNjOameIBPVADX4f9%2FxPRnLqZkDljqmpTROuyOCabJF8ncXbxDqHGEFAtPA%3D%3D";


    TextView busStation;
    ArrayList<String> stationKey = new ArrayList<>();
    ArrayList<String> stationName = new ArrayList<>();
    ArrayList<String> stationCode = new ArrayList<>();

    //경도와 위도 변수 선언
    double longitude = 0.0;
    double latitude = 0.0;

    int num = 0;

    static String sKey = "";
    static String sName = "";
    static String sCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_bus_station);

        busStation = findViewById(R.id.station_name);

        getGPS();
        Log.d("디버깅2", longitude+"" + latitude);

        new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.O)
            @Override
            public void run() {
                getStationData();
                Log.d("디버깅2", stationKey.get(0) + "" + stationName.get(0));

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        busStation.setText(stationName.get(0) + " " + stationKey.get(0));
                        sKey = stationKey.get(0);
                        sName = stationName.get(0);
                        sCode = stationCode.get(0);
                    }
                });
            }
        }).start();



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
        Log.d("디버깅2",queryUrl);

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
                            stationKey.add(xpp.getText());
                            Log.d("버스코드", stationKey+"");
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
            Log.d("디버깅2", "네트웍 문제");

        } catch (Exception e) {
            Log.d("디버깅2", "기타문제" + e.toString());

            //Auto-generated catch blocke.printStackTrace();
        }
        // buffer.append("파싱 끝\n");
    }

    public void YesButtonClicked(View view) {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void NoButtonClicked(View view) {

        num++;
        if(num >= stationName.size()) {
            busStation.setText("");


            Toast.makeText(FindBusStation.this, "일치하는 버스 정류장이 없습니다." + "\n" + "어플을 종료합니다.", Toast.LENGTH_SHORT).show();

        }
        else {
            busStation.setText(stationName.get(num) + " " + stationKey.get(num));
            sKey = stationKey.get(num);
            sName = stationName.get(num);
            sCode = stationCode.get(num);
        }


    }
}