package com.edu.me.flea.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseActivity;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.utils.FileUtil;
import com.edu.me.flea.utils.ToastUtils;
import com.edu.me.flea.vm.PublishViewModel;
import com.tbruyelle.rxpermissions3.Permission;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import io.reactivex.rxjava3.functions.Consumer;

@Route(path = Config.Page.PUBLISH)
public class PublishActivity extends BaseActivity<PublishViewModel> implements BGASortableNinePhotoLayout.Delegate{

    private static final int REQ_PHOTO_PREVIEW = 1;
    private static final int REQ_PHOTO_PICKER = 2;
    private static final int REQ_CHOOSE_PHOTO = 3;

    @BindView(R.id.titleEt)
    EditText titleEt;

    @BindView(R.id.contentEt)
    EditText contentEt;

    @BindView(R.id.productPhotos)
    BGASortableNinePhotoLayout mPhotosGrid;

    @BindView(R.id.priceEt)
    EditText priceEt;

    @BindView(R.id.tagsEt)
    EditText tagsEt;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish;
    }

    @Override
    protected void initView() {
        mPhotosGrid.setMaxItemCount(6);
        mPhotosGrid.setEditable(true);
        mPhotosGrid.setPlusEnable(true);
        mPhotosGrid.setSortable(false);

        titleEt.setText("new phone,very cheap contract with me");
        contentEt.setText("this is a new phone,use a month ,price is very cheap,i want change a new one ");
        priceEt.setText("103.23");
        tagsEt.setText("phone,cheap");
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
        mPhotosGrid.setDelegate(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.post_product_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.publish_product:
                if(checkValidity()){
                    String content = contentEt.getText().toString();
                    ArrayList<String> files = mPhotosGrid.getData();
                    String price = priceEt.getText().toString();
                    String tags = tagsEt.getText().toString();
                    String title = titleEt.getText().toString();
                    mViewModel.publish(this,title,content,files,price,tags);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean checkValidity()
    {
        String title = titleEt.getText().toString();
        if(TextUtils.isEmpty(title) ){
            ToastUtils.showShort("please input the content");
            return false;
        }

        String content = contentEt.getText().toString();
        if(TextUtils.isEmpty(content) ){
            ToastUtils.showShort("please input the content");
            return false;
        }

        ArrayList<String> files = mPhotosGrid.getData();
        if(files == null || files.isEmpty()){
            ToastUtils.showShort("please choose a picture at least");
            return false;
        }
        String price = priceEt.getText().toString();
        if(TextUtils.isEmpty(price)){
            ToastUtils.showShort("please input the price");
            return false;
        }
        String tags = tagsEt.getText().toString();
        if(TextUtils.isEmpty(tags)){
            ToastUtils.showShort("please add some tags");
            return false;
        }
        return true;
    }

    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
            .subscribe(new Consumer<Permission>() {
                @Override
                public void accept(Permission permission) throws Throwable {
                    if(permission.granted){
                        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
                        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intentToPickPic, REQ_CHOOSE_PHOTO);
                    }else if(permission.shouldShowRequestPermissionRationale){
                        ToastUtils.showShort("we need permission to pick photos from album");
                    }
                }
            });
    }

    @Override
    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        mPhotosGrid.removeItem(position);
    }

    @Override
    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        Intent photoPickerPreviewIntent = new BGAPhotoPickerPreviewActivity.IntentBuilder(this)
                .previewPhotos(models)
                .selectedPhotos(models)
                .maxChooseCount(mPhotosGrid.getMaxItemCount())
                .currentPosition(position)
                .isFromTakePhoto(false)
                .build();
        startActivityForResult(photoPickerPreviewIntent, REQ_PHOTO_PREVIEW);
    }

    @Override
    public void onNinePhotoItemExchanged(BGASortableNinePhotoLayout sortableNinePhotoLayout, int fromPosition, int toPosition, ArrayList<String> models) {
        //None
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_CHOOSE_PHOTO) {
            Uri uri = data.getData();
            String filePath = FileUtil.getFilePathByUri(this, uri);
            ArrayList<String> list = new ArrayList<>();
            list.add(filePath);
            mPhotosGrid.addMoreData(list);
        } else if (requestCode == REQ_PHOTO_PREVIEW) {
            mPhotosGrid.setData(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
        }
    }
}