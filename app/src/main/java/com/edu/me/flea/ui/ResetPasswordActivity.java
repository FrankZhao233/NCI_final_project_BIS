package com.edu.me.flea.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseActivity;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.utils.CheckUtils;
import com.edu.me.flea.utils.ToastUtils;
import com.edu.me.flea.vm.ResetPasswordViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * reset password page
 * send email to reset your password
 *
 * */
@Route(path = Config.Page.RESET_PWD)
public class ResetPasswordActivity extends BaseActivity<ResetPasswordViewModel> {

    @BindView(R.id.resetBtn)
    Button resetBtn;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.emailEt)
    EditText emailEt;

    @BindView(R.id.backBtn)
    Button backBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_password;
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
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString();
                if(TextUtils.isEmpty(email)){
                    ToastUtils.showShort(getString(R.string.please_input_email));
                    return ;
                }

                if(!CheckUtils.isValidEmail(email)) {
                    ToastUtils.showShort(getString(R.string.invalid_email));
                    return ;
                }

                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ToastUtils.showShort(getString(R.string.reset_pwd_success_tips));
                            } else {
                                ToastUtils.showShort("Failed to send reset email!");
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
            }
        });

    }
}