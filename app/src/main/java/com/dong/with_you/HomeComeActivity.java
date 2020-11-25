package com.dong.with_you;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HomeComeActivity extends AppCompatActivity {

    TextView txt_time, txt_address;
    Button btn_off;
    Gson gson;
    String address;
    int time;

    private iCounterService binder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = iCounterService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private boolean running = true;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(HomeComeActivity.this);
        alert.setMessage("종료하시겠습니까?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceManager.setArrayList(HomeComeActivity.this, "data", Global.item);
                dialog.dismiss();
                moveTaskToBack(true);
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_come);


        txt_time = findViewById(R.id.txt_time);
        txt_address = findViewById(R.id.txt_address);
        btn_off = findViewById(R.id.btn_off);

        Intent intent = getIntent();
        address = intent.getStringExtra("address");
        time = intent.getIntExtra("time",0);
        //시간 설정
        Global.time = time;
        txt_address.setText(address);

        Intent mintent = new Intent(getApplicationContext(), CounterService.class);
        bindService(mintent, connection, BIND_AUTO_CREATE);
        running = true;
        new Thread(new GetCountThread()).start();

        btn_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               createDialog();
            }
        });
    }
//TODO:알림창, 신고
    private class GetCountThread implements Runnable{

        private Handler handler = new Handler();

        @Override
        public void run() {
            while(running){

                if(binder == null){
                    continue;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            txt_time.setText(setText(binder.getCount()));
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                createNotification(1);
            }
            deleteNotification();
        }
    }

    public String setText(int time){
        int hour, min, sec;
        hour = time / 3600;
        min = (time % 3600) / 60;
        sec = ((time % 3600) % 60);

        if(hour != 0){
            return hour+"시간 "+min+"분 "+sec+"초";
        }
        else {
            if(min != 0){
                return min+"분 "+sec+"초";
            }
            else {
                return sec+"초";
            }
        }
    }

    private void createNotification(int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"default");

        try {
            builder.setContentTitle("귀가까지 남은시간")
                    .setContentText(setText(binder.getCount()))
                    .setVibrate(new long[]{0})
                    .setSmallIcon(R.drawable.logo)
                    .setOngoing(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_LOW));
        }
        notificationManager.notify(id,builder.build());
    }

    public void deleteNotification(){
        NotificationManagerCompat.from(this).cancelAll();
    }

    public void createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeComeActivity.this);
        builder.setMessage("안전 귀가를 종료하겠습니까?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                unbindService(connection);
                running = false;
                Intent intent = new Intent(HomeComeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
//                deleteNotification();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
