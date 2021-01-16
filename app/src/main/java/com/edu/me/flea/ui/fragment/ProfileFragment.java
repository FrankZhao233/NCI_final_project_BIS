package com.edu.me.flea.ui.fragment;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;

import com.alibaba.android.arouter.launcher.ARouter;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseFragment;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.ui.ImageCropActivity;
import com.edu.me.flea.utils.FileUtil;
import com.edu.me.flea.utils.ImageLoader;
import com.edu.me.flea.utils.PreferencesUtils;
import com.edu.me.flea.vm.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tbruyelle.rxpermissions3.Permission;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.File;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.functions.Consumer;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends BaseFragment<ProfileViewModel>  {

    private static final int requestSelectPhoto = 100;
    private static final int requestCropPhoto = 101;

    @BindView(R.id.nickNameTv) TextView nickNameTv;
    @BindView(R.id.emailTv) TextView emailTv;
    @BindView(R.id.avatarIv) ImageView avatarIv;
    @BindView(R.id.logoutLayout) ViewGroup logoutVg;
    @BindView(R.id.goodsLayout) ViewGroup goodsVg;
    @BindView(R.id.contributionsLayout) ViewGroup contributionVg;
    @BindView(R.id.navigatorIv) ImageView navIv;
    @BindView(R.id.languageLayout) ViewGroup languageVg;

    private FirebaseUser mUser;

    public static ProfileFragment newInstance()
    {
        return new ProfileFragment();
    }

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
            nickNameTv.setText(mUser.getDisplayName());
            emailTv.setText(mUser.getEmail());
            logoutVg.setVisibility(View.VISIBLE);
            String signature = PreferencesUtils.getString(getActivity(),Constants.PrefKey.AVATAR_SIGNATURE,"");
            ImageLoader.loadAvatar(avatarIv,mUser.getUid(),signature);
        }else{
            logoutVg.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListener() {
        avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxPermissions rxPermissions = new RxPermissions(getActivity());
                rxPermissions.requestEachCombined(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Permission>() {
                        @Override
                        public void accept(Permission permission) throws Throwable {
                            if(permission.granted){
                                pickFromGallery();
                            }
                        }
                    });
            }
        });
        languageVg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(Config.Page.LANGUAGE_LIST).navigation();
            }
        });

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
            builder.setTitle(getString(R.string.prompt));
            builder.setMessage(getString(R.string.question_logout));
            builder.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    mUser = null;
                    nickNameTv.setText(getString(R.string.btn_login));
                    emailTv.setText(getString(R.string.hint_email));
                    ARouter.getInstance().build(Config.Page.LOGIN).navigation();
                    logoutVg.setVisibility(View.GONE);
                }
            });

            builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            }
        });

        mViewModel.getUploadAvatar().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String id) {
                String signature = UUID.randomUUID().toString();
                ImageLoader.loadAvatar(avatarIv,id,signature);
                PreferencesUtils.putString(getActivity(),Constants.PrefKey.AVATAR_SIGNATURE,signature);
            }
        });

        goodsVg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUser == null) {
                    ARouter.getInstance().build(Config.Page.LOGIN).navigation();
                }else {
                    ARouter.getInstance().build(Config.Page.MY_GOODS_LIST).navigation();
                }
            }
        });

        contributionVg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUser == null) {
                    ARouter.getInstance().build(Config.Page.LOGIN).navigation();
                } else {
                    ARouter.getInstance().build(Config.Page.MY_WELFARE).navigation();
                }
            }
        });

        navIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUser == null) {
                    ARouter.getInstance().build(Config.Page.LOGIN).navigation();
                } else {
                    ARouter.getInstance().build(Config.Page.PROFILE_EDITOR).navigation();
                }
            }
        });
    }

    private void pickFromGallery() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, requestSelectPhoto);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == requestSelectPhoto) {
                Uri uri = data.getData();
                String filePath = FileUtil.getFilePathByUri(getActivity(), uri);

                Intent intent = new Intent(getContext(), ImageCropActivity.class);
                intent.putExtra(Constants.ExtraName.ORIGINAL_PHOTO_PATH,filePath);
                startActivityForResult(intent,requestCropPhoto);

            }else if(requestCode == requestCropPhoto){
                String avatarPath = (Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "Pictures/avatar_crop.jpg");
                mViewModel.uploadAvatar(getContext(),avatarPath);
            }
        }
    }

}
