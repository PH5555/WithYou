package com.dong.with_you;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
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
    ImageView click;
    SlidingUpPanelLayout pannel;
    String destination;
    List<Address> searchAddr;

    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

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
        txt_current.setText(getCurrentAddress(latitude, longitude));

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
                createDialog();
            }
        });
    }

    public void createDialog(){
        CustomDialog customDialog = new CustomDialog(MainActivity.this);
        customDialog.callFunction();
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

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

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

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_address.getWindowToken(), 0);
    }
}
