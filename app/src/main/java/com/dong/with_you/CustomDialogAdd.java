package com.dong.with_you;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

public class CustomDialogAdd {
    private Context context;
    RecyclerView recyclerView;

    public CustomDialogAdd(Context context) {
        this.context = context;
    }

    public void callFunction(RecyclerView mrecyclerView) {

        recyclerView = mrecyclerView;
        final Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.custom_dialog_add);
        dlg.show();

        final EditText edt_name = (EditText) dlg.findViewById(R.id.edt_name);
        final EditText edt_phone = (EditText) dlg.findViewById(R.id.edt_phone);
        final Button btn_ok = (Button) dlg.findViewById(R.id.btn_ok);
        final Button btn_cancle = (Button) dlg.findViewById(R.id.btn_cancle);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edt_name.getText().toString().length() == 0 || edt_phone.getText().toString().length() == 0) {
                    Toast.makeText(context, "정확히 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Item item = new Item(edt_name.getText().toString(), edt_phone.getText().toString());
                    Global.item.add(item);
                    Adapter adapter = new Adapter(context, Global.item);
                    recyclerView.setAdapter(adapter);
                    dlg.dismiss();
                }
            }
        });
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "취소 했습니다.", Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            }
        });
    }
}
