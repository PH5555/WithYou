package com.dong.with_you;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    ImageView home, btn_change, btn_add;
    EditText edt_name;
    RecyclerView recyclerview;
    Gson gson;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(ContactActivity.this);
        alert.setMessage("종료하시겠습니까?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceManager.setArrayList(ContactActivity.this, "data", Global.item);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        home = findViewById(R.id.home);
        btn_change = findViewById(R.id.btn_change);
        edt_name = findViewById(R.id.edt_name);
        btn_add = findViewById(R.id.btn_add);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this)) ;

        checkData();

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                edt_name.setText(edt_name.getText().toString());
                edt_name.clearFocus();
                Toast.makeText(ContactActivity.this, "이름 저장이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                PreferenceManager.setString(ContactActivity.this, "name", edt_name.getText().toString());
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_name.getWindowToken(), 0);
    }

    public void checkData(){
        String text = PreferenceManager.getString(ContactActivity.this, "name");
        if (!text.equals("")) {
            edt_name.setText(text);
        }

        Global.item = PreferenceManager.getArrayList(ContactActivity.this, "data");
        if(Global.item != null) {
            Adapter adapter = new Adapter(ContactActivity.this, Global.item);
            recyclerview.setAdapter(adapter);
        }
    }

    public void createDialog(){
        CustomDialogAdd customDialog = new CustomDialogAdd(ContactActivity.this);
        customDialog.callFunction(recyclerview);
    }
}
