package com.cj.samplechat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cj.samplechat.ECApplication;
import com.cj.samplechat.R;
import com.cj.samplechat.bean.User;
import com.cj.samplechat.db.DBManager;
import com.cj.samplechat.utils.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity {

    @BindView(R.id.edt_login_user)
    EditText edtLoginUser;
    @BindView(R.id.edt_login_pwd)
    EditText edtLoginPwd;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.btn_login)
    Button btnLogin;

    private String userName;
    private String passWord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        edtLoginUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtLoginPwd.setText(null);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.btn_login, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                signIn();
                break;
            case R.id.btn_register:
                //signUp();
                startActivity(new Intent(this,RegisterActivity.class));
                break;
        }
    }

    private void signIn() {
        userName = edtLoginUser.getText().toString().trim();
        passWord = edtLoginPwd.getText().toString().trim();
        if (TextUtils.isEmpty(userName)){
            ToastUtils.showShort(this,"用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(passWord)){
            ToastUtils.showShort(this,"密码不能为空");
            return;
        }
        DBManager.getInstance().closeDB();
        ECApplication.getInstance().setCurrentUserName(userName);
        EMClient.getInstance().login(userName, passWord, new EMCallBack() {
            @Override
            public void onSuccess() {

                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                getFriendsList();

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(int code, String error) {
                Log.d("cj", "登录失败" + code + ";" + error);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    private void getFriendsList() {
        try {
            List<String> userNames = EMClient.getInstance().contactManager().getAllContactsFromServer();
            Map<String, User> users = new HashMap<String, User>();
            for (String username : userNames) {
                User user = new User(username);
                users.put(username, user);
            }
            ECApplication.getInstance().setContactList(users);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }


}
