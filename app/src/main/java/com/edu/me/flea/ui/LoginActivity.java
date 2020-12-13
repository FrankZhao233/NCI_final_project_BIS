package com.edu.me.flea.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

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
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.vm.LoginViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = Config.Page.LOGIN)
public class LoginActivity extends BaseActivity<LoginViewModel> {

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

    @BindView(R.id.resetBtn)
    Button resetBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
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
                ARouter.getInstance().build(Config.Page.REGISTER).navigation();
                closePage();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(Config.Page.RESET_PWD).navigation();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(checkValidity()){
                String email = emailEt.getText().toString();
                String pwd = pwdEt.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                // there was an error
                                Log.d(Config.TAG,"error==>"+task.getException().getMessage());
                                Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login successful. Welcome to the app!", Toast.LENGTH_SHORT).show();
                                postEvent(Constants.Event.LOGIN_DONE);
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

    @Override
    protected void onBusEvent(int what, Object event) {
        super.onBusEvent(what, event);
        switch (what){
            case Constants.Event.SING_UP_DONE:
                finish();
                break;
        }
    }
}