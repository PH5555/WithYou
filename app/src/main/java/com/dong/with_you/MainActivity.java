package com.dong.with_you;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    double latitude, longitude;
    TextView txt_current, txt_name,txt_name2, txt_address,txt_address2, txt_destination;
    LinearLayout txt_help, showAdress,showAdress2,first, second;
    EditText edt_address;
    Button btn_back, btn_on;
    ImageView click, telephone;
    SlidingUpPanelLayout pannel;
    String destination, currentAddress;
    List<Address> searchAddr;

    private GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpsTracker = new GpsTracker(MainActivity.this);

        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); //getMapAsync must be called on the main thread.

        txt_current = findViewById(R.id.txt_current);
        txt_address = findViewById(R.id.txt_address);
        txt_address2 = findViewById(R.id.txt_address2);
        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        txt_destination = findViewById(R.id.txt_destination);
        txt_name = findViewById(R.id.txt_name);
        txt_name2 = findViewById(R.id.txt_name2);
        showAdress = findViewById(R.id.showAdress);
        txt_help = findViewById(R.id.txt_help);
        click = findViewById(R.id.click);
        edt_address = findViewById(R.id.edt_address);
        pannel = findViewById(R.id.pannel);
        btn_back = findViewById(R.id.btn_back);
        btn_on = findViewById(R.id.btn_on);
        telephone = findViewById(R.id.telephone);

        currentAddress = getCurrentAddress(latitude, longitude);
        txt_current.setText(currentAddress);

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if(edt_address.getText().length() == 0) {
                    Toast.makeText(MainActivity.this, "주소를 정확히 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
                else {
                    String address = edt_address.getText().toString();
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        searchAddr = geocoder.getFromLocationName(address, 4);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(searchAddr.size() == 0) {
                        hideKeyboard();
                        Toast.makeText(MainActivity.this, "주소를 정확히 입력해주세요!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        hideKeyboard();
                        txt_help.setVisibility(View.GONE);
                        showAdress.setVisibility(View.VISIBLE);
                        txt_name.setText(edt_address.getText().toString());
                        txt_name2.setText(edt_address.getText().toString());
                        destination = searchAddr.get(0).getAddressLine(0);
                        txt_address.setText(destination);
                        txt_address2.setText(destination);
                    }
                }
            }
        });

        showAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pannel.setPanelHeight(400);
                pannel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                first.setVisibility(View.GONE);
                second.setVisibility(View.VISIBLE);
                pannel.setTouchEnabled(false);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pannel.setPanelHeight(240);
                pannel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                second.setVisibility(View.GONE);
                first.setVisibility(View.VISIBLE);
                pannel.setTouchEnabled(true);
            }
        });

        btn_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(destination);
            }
        });

        telephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setMessage("종료하시겠습니까?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceManager.setArrayList(MainActivity.this, "data", Global.item);
                dialog.dismiss();
                finish();
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

    public void createDialog(String address){
        CustomDialog customDialog = new CustomDialog(MainActivity.this);
        customDialog.callFunction(address);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng current = new LatLng(latitude, longitude);

        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions
                .position(current)
                .title("현위치");

        map.addMarker(makerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 16f));
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

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_address.getWindowToken(), 0);
    }
}
