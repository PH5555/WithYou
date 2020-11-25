package com.dong.with_you;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CounterService extends Service {

    private boolean isStop;
    private int count;
    double latitude, longitude;
    private GpsTracker gpsTracker;

    public CounterService() {
    }

    iCounterService.Stub binder = new iCounterService.Stub() {
        @Override
        public int getCount() throws RemoteException {
            return count;
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isStop = true;
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        count = Global.time * 60;

        gpsTracker = new GpsTracker(CounterService.this);

        Thread counter = new Thread(new Counter());
        counter.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStop = true;
    }

    private class Counter implements Runnable {

        private Handler handler = new Handler();

        @Override
        public void run() {
            for( ; count > 0; count--){
                if(isStop){
                    break;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("check", ""+count);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            deleteNotification();

            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            for (int i = 0; i < Global.item.size(); i++) {
                sendMsg(Global.item.get(i).getPhone(), PreferenceManager.getString(CounterService.this, "name") +
                        "님이 귀가를 완료했어요!" + "\n" + "현재위치 : " + getCurrentAddress(latitude, longitude));
            }

            Intent intent = new Intent(CounterService.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void sendMsg(String phone, String sms){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, sms, null, null);
    }

    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }

    public void deleteNotification(){
        NotificationManagerCompat.from(this).cancelAll();
    }
}
