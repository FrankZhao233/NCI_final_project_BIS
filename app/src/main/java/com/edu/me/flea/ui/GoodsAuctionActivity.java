package com.edu.me.flea.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseActivity;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.utils.FileUtil;
import com.edu.me.flea.utils.ToastUtils;
import com.edu.me.flea.vm.GoodsAuctionViewModel;
import com.tbruyelle.rxpermissions3.Permission;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import io.reactivex.rxjava3.functions.Consumer;

@Route(path = Config.Page.GOODS_AUCTION)
public class GoodsAuctionActivity extends BaseActivity<GoodsAuctionViewModel> implements BGASortableNinePhotoLayout.Delegate{

    private static final int REQ_PHOTO_PREVIEW = 1;
    private static final int REQ_PHOTO_PICKER = 2;
    private static final int REQ_CHOOSE_PHOTO = 3;

    @BindView(R.id.titleEt) EditText titleEt;
    @BindView(R.id.contentEt) EditText contentEt;
    @BindView(R.id.productPhotos) BGASortableNinePhotoLayout mPhotosGrid;
    @BindView(R.id.priceEt) EditText priceEt;
    @BindView(R.id.tagsEt) EditText tagsEt;
    @BindView(R.id.dateTv) TextView dateTv;
    @BindView(R.id.timeTv) TextView timeTv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_goods_auction;
    }

    @Override
    protected void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mPhotosGrid.setMaxItemCount(6);
        mPhotosGrid.setEditable(true);
        mPhotosGrid.setPlusEnable(true);
        mPhotosGrid.setSortable(false);
        setTitle("Goods Auction");
//        titleEt.setText("new phone,very cheap contract with me");
//        contentEt.setText("this is a new phone,use a month ,price is very cheap,i want change a new one ");
//        priceEt.setText("103.23");
//        tagsEt.setText("phone,cheap");
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

        dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        timeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });
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
                    long dueTime = getDueTime();
                    mViewModel.publish(this,title,content,files,price,tags,dueTime);
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private long getDueTime()
    {
        String date = dateTv.getText().toString().trim();
        String time = timeTv.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date result = sdf.parse(date+" "+time);
            return result.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean checkValidity()
    {
        String title = titleEt.getText().toString();
        if(TextUtils.isEmpty(title) ){
            ToastUtils.showShort("please input the title");
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
        String date = dateTv.getText().toString().trim();
        if(TextUtils.isEmpty(date)){
            ToastUtils.showShort("please set date of due time");
            return false;
        }
        String time = timeTv.getText().toString().trim();
        if(TextUtils.isEmpty(time)){
            ToastUtils.showShort("please set time of due time");
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

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hourStr;
                if (hourOfDay<10) {
                    hourStr = "0"+hourOfDay;
                } else {
                    hourStr = String.valueOf(hourOfDay);
                }
                String minuteStr;
                if (minute<10) {
                    minuteStr = "0"+minute;
                } else {
                    minuteStr = String.valueOf(minute);
                }
                timeTv.setText(String.format("%s:%s:00",hourStr,minuteStr));
            }
        },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)
                ,true).show();
    }

    private void showDatePickerDialog()
    {
        Calendar calendar=Calendar.getInstance();
        DatePickerDialog dialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String monthStr;
                if (month<10) {
                    monthStr = "0"+month;
                } else {
                    monthStr = String.valueOf(month);
                }
                String dayStr;
                if (dayOfMonth<10) {
                    dayStr = "0"+dayOfMonth;
                } else {
                    dayStr = String.valueOf(dayOfMonth);
                }
                dateTv.setText(String.format("%d-%s-%s",year,monthStr,dayStr));
            }
        }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
}