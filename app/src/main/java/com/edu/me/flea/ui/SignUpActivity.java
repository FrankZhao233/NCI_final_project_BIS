package com.edu.me.flea.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseActivity;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.vm.SignUpViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = Config.Page.REGISTER)
public class SignUpActivity extends BaseActivity<SignUpViewModel> {

    @BindView(R.id.nickNameEt) EditText nickNameEt;
    @BindView(R.id.emailEt) EditText emailEt;
    @BindView(R.id.passwordEt) EditText pwdEt;
    @BindView(R.id.loginBtn) Button loginBtn;
    @BindView(R.id.singUpBtn) Button singUpBtn;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void initView() {
        removeToolbarElevation();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void inject() {
        ButterKnife.bind(this);
    }

    @Override
    protected void setListener() {
        singUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(progressBar.getVisibility() == View.VISIBLE){
                    return ;
                }
                if (checkValidity()) {
                    String email = emailEt.getText().toString();
                    String pwd = pwdEt.getText().toString();
                    String nickName = nickNameEt.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    mViewModel.signUp(SignUpActivity.this,nickName,email,pwd);
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(Config.Page.LOGIN).navigation();
                finish();
            }
        });
    }

    private boolean checkValidity()
    {
        String nickName = nickNameEt.getText().toString();
        if (TextUtils.isEmpty(nickName)) {
            Toast.makeText(this,getString(R.string.please_input_name),Toast.LENGTH_SHORT).show();
            return false;
        }

        String email = emailEt.getText().toString();
        String pwd = pwdEt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this,getString(R.string.please_input_email),Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this,getString(R.string.please_input_pwd),Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}