package com.edu.me.flea.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = "/app/sign_up")
public class SignUpActivity extends BaseActivity<SignUpViewModel> {

    @BindView(R.id.emailEt)
    EditText emailEt;

    @BindView(R.id.passwordEt)
    EditText pwdEt;

    @BindView(R.id.loginBtn)
    Button loginBtn;

    @BindView(R.id.singUpBtn)
    Button singUpBtn;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void initView() {
        getSupportActionBar().setElevation(0);
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
                if (checkValidity()) {
                    String email = emailEt.getText().toString();
                    String pwd = pwdEt.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    //create user
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    Log.d(Config.TAG,"error==>"+task.getException().getMessage());
                                    Toast.makeText(SignUpActivity.this, "Error: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Sign up successful. Welcome to the app!", Toast.LENGTH_SHORT).show();
                                    ARouter.getInstance().build("/app/login").navigation();
                                    finish();
                                }
                            }
                        });
                }
            }
        });
    }

    private boolean checkValidity()
    {
        String email = emailEt.getText().toString();
        String pwd = pwdEt.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"please input email",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(pwd)) {
            Toast.makeText(this,"please input password",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}