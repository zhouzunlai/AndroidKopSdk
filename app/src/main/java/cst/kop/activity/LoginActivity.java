package cst.kop.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cst.kop.R;
import cst.kop.tools.AppUtils;
import cst.kop.tools.DialogCallback;
import cst.kop.tools.DialogUtils;
import cst.kop.tools.TwoDialogCallback;
import cst.kop.widget.TwoEditTextDialog;
import platform.cston.httplib.Cston;
import platform.cston.httplib.search.AuthUser;
import platform.cston.httplib.search.OnResultListener;

/**
 * Created by zhou-pc on 2016/9/13.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.getAppinfo(this);
        String authFlag = AppUtils.getAuthFlag(this);
        if (AuthUser.getInstance().isAuthorization()) {
            if (authFlag == null || !authFlag.equals("CANCEL")) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }
        setContentView(R.layout.activity_login);
        TextView setInfo = (TextView) findViewById(R.id.text_setting_info);
        TextView instructions = (TextView) findViewById(R.id.text_instructions);
        Button cooperAuth = (Button) findViewById(R.id.btn_cooperation_auth);
        Button unCooperAuth = (Button) findViewById(R.id.btn_un_cooperation_auth);
        setInfo.setOnClickListener(this);
        instructions.setOnClickListener(this);
        // cooperAuth.setOnClickListener(this);
        unCooperAuth.setOnClickListener(this);
    }

    TwoEditTextDialog loginDialog;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_setting_info:
                loginDialog = new TwoEditTextDialog(this, new TwoDialogCallback() {
                    @Override
                    public void OnPositiveBtnClick(String appid, String appsec) {
                        if (appid.isEmpty() || appsec.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "请填写appid和appsecret", Toast.LENGTH_SHORT).show();
                        } else {
                            Cston.Auth.setApp_Info(appid, appsec);
                            AppUtils.setAppinfo(LoginActivity.this, appid, appsec);
                            loginDialog.dismiss();
                        }
                    }

                    @Override
                    public void OnNegativeBtnClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
                loginDialog.show();
                loginDialog.setDialogTitle("设置应用参数");
                loginDialog.setTextHint(Cston.appid(), Cston.appkey());
                break;
            case R.id.text_instructions:
                startActivity(new Intent(this, InstructionsActivity.class));
                break;
            case R.id.btn_cooperation_auth:
                DialogUtils.showOneEditAlertDialogChoose(this, "设置电话号码", "您的电话", true, false, false, new DialogCallback() {
                    @Override
                    public void OnLeftBtnClick(Dialog dlg, String appid, String appsec) {

                    }

                    @Override
                    public void OnOneLeftBtnClick(Dialog dlg, String phone) {
                        if (phone.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "请输入电话号码", Toast.LENGTH_SHORT).show();
                        } else {
                            dlg.dismiss();
                            AuthUser.getInstance().Authorization(LoginActivity.this, phone, new OnResultListener.OnAuthorizationListener() {
                                @Override
                                public void onAuthorizationResult(boolean b, String s) {
                                    if (b) {
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "授权失败：" + s, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
                break;
            case R.id.btn_un_cooperation_auth:
                AuthUser.getInstance().Authorization(LoginActivity.this, new OnResultListener.OnAuthorizationListener() {
                    @Override
                    public void onAuthorizationResult(boolean isSuccess, String result) {
                        if (isSuccess) {
                            AppUtils.clearnAuthFlag(LoginActivity.this);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "授权失败：" + result, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }


}
