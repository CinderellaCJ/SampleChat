package com.cj.samplechat.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.cj.samplechat.R;
import com.cj.samplechat.utils.ToastUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddContactActivity extends Activity {

    private ProgressDialog progressDialog;

    @BindView(R.id.edt_contact_add)
    EditText edtContactAdd;
    @BindView(R.id.btn_contact_add)
    Button btnContactAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_contact_add)
    public void onClick() {
        String userName = edtContactAdd.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            ToastUtils.showShort(this, "请输入用户名");
        }
        addContact(userName);
    }

    private void addContact(final String userName) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在发送请求");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(userName,"求加好友");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            ToastUtils.showShort(AddContactActivity.this,"请求发送成功,等待对方验证");

                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            ToastUtils.showShort(AddContactActivity.this,"请求发送失败！" + e.getMessage());

                        }
                    });
                }
            }
        }).start();
    }
}
