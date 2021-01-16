package com.edu.me.flea.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseActivity;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.UserInfo;
import com.edu.me.flea.utils.FileUtil;
import com.edu.me.flea.utils.ImageLoader;
import com.edu.me.flea.utils.PreferencesUtils;
import com.edu.me.flea.utils.ToastUtils;
import com.edu.me.flea.vm.ProfileEditorViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tbruyelle.rxpermissions3.Permission;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.File;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.functions.Consumer;

@Route(path = Config.Page.PROFILE_EDITOR)
public class ProfileEditorActivity extends BaseActivity<ProfileEditorViewModel> {

    private static final int requestSelectPhoto = 100;
    private static final int requestCropPhoto = 101;

    @BindView(R.id.operateBtn) FloatingActionButton operateBtn;
    @BindView(R.id.signatureEt) EditText signatureEt;
    @BindView(R.id.addressEt) EditText addressEt;
    @BindView(R.id.hobbyEt) EditText hobbyEt;
    @BindView(R.id.nickNameTv) TextView nicknameTv;
    @BindView(R.id.avatarIv) ImageView avatarIv;
    @BindView(R.id.sexRg) RadioGroup sexRg;
    @BindView(R.id.femaleCb) RadioButton femaleCb;
    @BindView(R.id.maleCb) RadioButton maleCb;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.contentLayout) ViewGroup contentLayout;

    private boolean bEditMode = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_editor;
    }

    @Override
    protected void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            nicknameTv.setText(user.getDisplayName());
            String signature = PreferencesUtils.getString(ProfileEditorActivity.this,
                    Constants.PrefKey.AVATAR_SIGNATURE,"");
            ImageLoader.loadAvatar(avatarIv,user.getUid(),signature);
        }
        setEditState(false);
    }

    @Override
    protected void initData() {
        mViewModel.queryUser();
    }

    @Override
    protected void inject() {
        ButterKnife.bind(this);
    }

    @Override
    protected void setListener() {
        operateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bEditMode) {
                    int id = sexRg.getCheckedRadioButtonId();
                    if (id <= 0) {
                        ToastUtils.showShort(getString(R.string.please_choose_gender));
                        return;
                    }
                    String gender = "female";
                    if (id == R.id.maleCb) {
                        gender = "male";
                    }
                    String signature = signatureEt.getText().toString();
                    String address = addressEt.getText().toString();
                    String hobby = hobbyEt.getText().toString();
                    mViewModel.saveUser(gender, signature, address, hobby);
                }
                setEditState(!bEditMode);
            }
        });

        mViewModel.getUser().observe(this, new Observer<UserInfo>() {
            @Override
            public void onChanged(UserInfo userInfo) {
                if("male".equals(userInfo.gender)){
                    sexRg.check(R.id.maleCb);
                }else if("female".equals(userInfo.gender)){
                    sexRg.check(R.id.femaleCb);
                }
                signatureEt.setText(userInfo.signature);
                addressEt.setText(userInfo.address);
                hobbyEt.setText(userInfo.hobby);

                progressBar.setVisibility(View.GONE);
                operateBtn.setVisibility(View.VISIBLE);
                contentLayout.setVisibility(View.VISIBLE);
            }
        });

        avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxPermissions rxPermissions = new RxPermissions(ProfileEditorActivity.this);
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

        mViewModel.getOperation().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                setEditState(false);
            }
        });

        mViewModel.getUploadAvatar().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String id) {
                String signature = UUID.randomUUID().toString();
                ImageLoader.loadAvatar(avatarIv,id,signature);
                PreferencesUtils.putString(ProfileEditorActivity.this,Constants.PrefKey.AVATAR_SIGNATURE,signature);
            }
        });
    }

    private void pickFromGallery() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, requestSelectPhoto);
    }

    public void setEditState(boolean bEnable)
    {
        bEditMode = bEnable;
        signatureEt.setEnabled(bEnable);
        addressEt.setEnabled(bEnable);
        hobbyEt.setEnabled(bEnable);
        sexRg.setEnabled(bEnable);
        femaleCb.setEnabled(bEnable);
        maleCb.setEnabled(bEnable);

        if(!bEnable){
            operateBtn.setImageResource(R.drawable.ic_baseline_edit_24);
        }else{
            operateBtn.setImageResource(R.drawable.ic_baseline_done_24);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK) {
            if (requestCode == requestSelectPhoto) {
                Uri uri = data.getData();
                String filePath = FileUtil.getFilePathByUri(this, uri);

                Intent intent = new Intent(this, ImageCropActivity.class);
                intent.putExtra(Constants.ExtraName.ORIGINAL_PHOTO_PATH,filePath);
                startActivityForResult(intent,requestCropPhoto);

            }else if(requestCode == requestCropPhoto){
                String avatarPath = (Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "Pictures/avatar_crop.jpg");
                mViewModel.uploadAvatar(this,avatarPath);
            }
        }
    }
}