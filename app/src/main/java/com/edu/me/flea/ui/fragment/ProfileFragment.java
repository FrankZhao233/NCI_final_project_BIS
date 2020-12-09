package com.edu.me.flea.ui.fragment;


import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseFragment;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.vm.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends BaseFragment<ProfileViewModel> {

    @BindView(R.id.nickNameTv)
    TextView nickNameTv;

    @BindView(R.id.emailTv)
    TextView emailTv;

    @BindView(R.id.logoutLayout)
    ViewGroup logoutVg;

    private FirebaseUser mUser;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    protected void inject() {
        super.inject();
        ButterKnife.bind(this,getView());
    }

    @Override
    protected void initView() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser != null){
            Log.d(Config.TAG,"id==>"+mUser.getUid());
            Log.d(Config.TAG,"name==>"+mUser.getDisplayName());
            nickNameTv.setText("u"+mUser.getDisplayName());
            emailTv.setText(mUser.getEmail());
            logoutVg.setVisibility(View.VISIBLE);
        }else{
            logoutVg.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListener() {
        nickNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUser == null) {
                    ARouter.getInstance().build(Config.Page.LOGIN).navigation();
                }
            }
        });

        logoutVg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Prompt");
            builder.setMessage("do you want to logout?");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    mUser = null;
                    nickNameTv.setText("Login");
                    emailTv.setText("");
                    ARouter.getInstance().build(Config.Page.LOGIN).navigation();
                    logoutVg.setVisibility(View.GONE);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            }
        });
    }
}
