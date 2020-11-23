package com.dong.with_you;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CustomDialog{
    private Context context;

    public CustomDialog(Context context) {
        this.context = context;
    }

    public void callFunction() {

        final Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.custom_dialog);
        dlg.show();

        final EditText edt_time = (EditText) dlg.findViewById(R.id.edt_time);
        final Button btn_ok = (Button) dlg.findViewById(R.id.btn_ok);
        final Button btn_cancle = (Button) dlg.findViewById(R.id.btn_cancle);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edt_time.getText().toString().length() == 0) {
                    Toast.makeText(context, "시간을 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(context, HomeComeActivity.class);
                    context.startActivity(intent);

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
