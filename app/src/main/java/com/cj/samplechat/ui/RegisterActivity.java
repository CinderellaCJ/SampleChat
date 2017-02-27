package com.cj.samplechat.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.cj.samplechat.ECApplication;
import com.cj.samplechat.R;
import com.cj.samplechat.utils.ToastUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends Activity {

    @BindView(R.id.edt_register_user)
    EditText edtRegisterUser;
    @BindView(R.id.edt_register_pwd)
    EditText edtRegisterPwd;
    @BindView(R.id.edt_register_pwd_confirm)
    EditText edtRegisterPwdConfirm;
    @BindView(R.id.btn_register)
    Button btnRegister;

    private String userName;
    private String passWord;
    private String passWordConfrim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_register)
    public void onClick() {
        userName = edtRegisterUser.getText().toString().trim();
        passWord = edtRegisterPwd.getText().toString().trim();
        passWordConfrim = edtRegisterPwdConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(userName)){
            ToastUtils.showShort(this,"用户名不能为空");
            edtRegisterUser.requestFocus();
        }else if (TextUtils.isEmpty(passWord)){
            ToastUtils.showShort(this,"密码不能为空");
            edtRegisterPwd.requestFocus();
        }else if (TextUtils.isEmpty(passWordConfrim)){
            ToastUtils.showShort(this,"确认密码不能为空");
            edtRegisterPwdConfirm.requestFocus();
        }else if (!passWord.equals(passWordConfrim)){
            ToastUtils.showShort(this,"两次密码输入不一致，请重新输入");
        }else if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(passWordConfrim)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().createAccount(userName, passWord);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ECApplication.getInstance().setCurrentUserName(userName);
                                ToastUtils.showShort(RegisterActivity.this,"注册成功");
                                finish();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        Log.d("cj", "注册失败" + e.getMessage() + e.getErrorCode());
                    }
                }
            }).start();

        }
    }
}
