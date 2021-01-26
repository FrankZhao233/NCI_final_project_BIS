package com.edu.me.flea.ui;

import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.donkingliang.labels.LabelsView;
import com.edu.me.flea.FleaApplication;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseActivity;
import com.edu.me.flea.base.CommonAdapter;
import com.edu.me.flea.base.ViewHolder;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.AuctionInfo;
import com.edu.me.flea.entity.GoodsDetail;
import com.edu.me.flea.entity.GoodsInfo;
import com.edu.me.flea.module.GlideApp;
import com.edu.me.flea.ui.dialog.AuctionInputDialog;
import com.edu.me.flea.ui.dialog.CommentDialog;
import com.edu.me.flea.utils.BitmapUtil;
import com.edu.me.flea.utils.DateUtils;
import com.edu.me.flea.utils.ImageLoader;
import com.edu.me.flea.utils.Utils;
import com.edu.me.flea.vm.GoodsDetailViewModel;
import com.edu.me.flea.widget.ListViewForScrollView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@Route(path = Config.Page.GOODS_DETAIL)
public class GoodsDetailActivity extends BaseActivity<GoodsDetailViewModel> {

    @BindView(R.id.avatarIv) ImageView avatarIv;
    @BindView(R.id.nickNameTv) TextView nickNameTv;
    @BindView(R.id.timeTv) TextView timeTv;
    @BindView(R.id.titleTv) TextView titleTv;
    @BindView(R.id.priceTv) TextView priceTv;
    @BindView(R.id.contentTv) TextView contentTv;
    @BindView(R.id.imageLv) ListViewForScrollView imageLv;
    @BindView(R.id.orderBtn) Button orderBtn;
    @BindView(R.id.toolLayout) ViewGroup toolLayout;
    @BindView(R.id.labelsView) LabelsView labelsView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.contentLayout) ScrollView contentLayout;
    @BindView(R.id.commentTv) TextView commentTv;
    @BindView(R.id.dueTimeTv) TextView dueTimeTv;
    @BindView(R.id.auctionBtn) Button auctionBtn;
    @BindView(R.id.pricesLv) ListView auctionLv;
    @BindView(R.id.userLayout) ViewGroup userLayout;
    @BindView(R.id.goodsContentLayout) ViewGroup goodsInfoLayout;

    private CommonAdapter<String> mAdapter;
    private CommonAdapter<AuctionInfo> mAuctionAdapter;

    private Map<Integer, Integer> imgHeightMap = new HashMap<>();
    private List<Bitmap> localImagePaths = new ArrayList<>();

    private RequestOptions requestOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_empty_image)
            .error(R.drawable.ic_empty_image)
            .centerCrop();

    @Autowired(name = Constants.ExtraName.SNAPSHOT) GoodsInfo mSnapshot;
    @Autowired(name = Constants.ExtraName.SHOW_TOOLS) boolean bShowTools = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_good_detail;
    }

    @Override
    protected void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (bShowTools) {
            toolLayout.setVisibility(View.VISIBLE);
        } else {
            toolLayout.setVisibility(View.GONE);
        }
        mAdapter = new CommonAdapter<String>(R.layout.item_goods_image,null) {


            @Override
            public void convert(ViewHolder holder, String item, int position) {
                ImageView imageIv = holder.getImageView(R.id.imageIv);
                String location = String.format(Config.GOODS_FULL_REF_PATH_FMT, item);
                StorageReference gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(location);
                GlideApp.with(Utils.getContext())
                        .load(gsReference)
                        .apply(requestOptions)
                        .into(imageIv);
            }
        };

        imageLv.setAdapter(mAdapter);
        mAuctionAdapter = new CommonAdapter<AuctionInfo>(R.layout.item_auction_info,null) {
            @Override
            public void convert(ViewHolder holder, AuctionInfo item, int position) {
                holder.setTvText(R.id.priceTv,"€"+item.price);
                holder.setTvText(R.id.nameTv,item.nickName);
                holder.setTvText(R.id.timeTv,DateUtils.formatDate(item.createTime));
                ImageView avatarIv = holder.getImageView(R.id.avatarIv);
                ImageLoader.loadAvatar(avatarIv,item.uid);
            }
        };
        auctionLv.setAdapter(mAuctionAdapter);
        auctionBtn.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.goods_detail_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
            return true;
        }else if(id == R.id.menu_save){
            if(progressBar.getVisibility() != View.VISIBLE){
                RxPermissions permissions = new RxPermissions(this);
                permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Throwable {
                            if(aBoolean){
                                List<String> images = mViewModel.getGoodsDetail().getValue().images;
                                saveToSdcard(images);
                            }
                        }
                    });

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initData() {
        mViewModel.queryGoodsDetail(mSnapshot);
    }

    @Override
    protected void inject() {
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
    }

    @Override
    protected void setListener() {
        mViewModel.getGoodsDetail().observe(this, new Observer<GoodsDetail>() {
            @Override
            public void onChanged(GoodsDetail goodsDetail) {
                nickNameTv.setText(goodsDetail.creatorName);
                timeTv.setText(getString(R.string.post_at) + DateUtils.formatDate(goodsDetail.createTime));
                priceTv.setText("€"+goodsDetail.price);
                contentTv.setText(goodsDetail.content);
                List<String> labels = new ArrayList<>();
                String[] tags = goodsDetail.tags.split(",");
                for(int i=0;i<tags.length;i++){
                    String tag = tags[i].trim();
                    if(!TextUtils.isEmpty(tag)){
                        labels.add(tag);
                    }
                }
                ImageLoader.loadAvatar(avatarIv,goodsDetail.creatorId);
                labelsView.setLabels(labels);
                mAdapter.refreshView(goodsDetail.images);
                contentLayout.setVisibility(View.VISIBLE);
                toolLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null){
                    toolLayout.setVisibility(View.VISIBLE);
                } else {
                    if(goodsDetail.creatorId.equals(user.getUid())){
                        toolLayout.setVisibility(View.GONE);
                    }else{
                        toolLayout.setVisibility(View.VISIBLE);
                    }
                }

                if(goodsDetail.dueTime>0){
                    dueTimeTv.setVisibility(View.VISIBLE);
                    dueTimeTv.setText("due time: "+DateUtils.formatDate(goodsDetail.dueTime));
                    auctionBtn.setVisibility(View.VISIBLE);
                    auctionLv.setVisibility(View.VISIBLE);
                    mViewModel.queryAuctionInfo();
                }else{
                    dueTimeTv.setVisibility(View.GONE);
                    auctionBtn.setVisibility(View.GONE);
                    auctionLv.setVisibility(View.GONE);
                }
            }
        });

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null) {
                    ARouter.getInstance().build(Config.Page.LOGIN).navigation();
                }else{
                    mViewModel.want(mSnapshot);
                }
            }
        });

        commentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentDialog dialog = new CommentDialog(GoodsDetailActivity.this,mSnapshot.detailId);
                dialog.show();
            }
        });

        auctionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null) {
                    ARouter.getInstance().build(Config.Page.LOGIN).navigation();
                }else {
                    AuctionInputDialog dialog = new AuctionInputDialog(GoodsDetailActivity.this);
                    dialog.setOnConfirmListener(new AuctionInputDialog.OnConfirmListener() {
                        @Override
                        public void onConfirm(String value, String message) {
                            mViewModel.doAuction(value, message);
                        }
                    });
                    dialog.show();
                }
            }
        });

        mViewModel.getAuctionEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                auctionBtn.setEnabled(false);
            }
        });

        mViewModel.getAuctions().observe(this, new Observer<List<AuctionInfo>>() {
            @Override
            public void onChanged(List<AuctionInfo> auctions) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    String uid = user.getUid();
                    boolean bFound = false;
                    for(AuctionInfo item:auctions){
                        if(uid.equals(item.uid)){
                            bFound = true;
                            break;
                        }
                    }
                    auctionBtn.setEnabled(!bFound);
                    mAuctionAdapter.refreshView(auctions);
                }
            }
        });

    }

    private void saveToSdcard(List<String> imageUrls)
    {
        int leftRightMargin = SizeUtils.dp2px(10) ;
        getAllImageHeight(imageUrls,leftRightMargin).subscribe(new io.reactivex.functions.Consumer<Integer>() {
            @Override
            public void accept(Integer allHeight) throws Exception {
                int imgHeight = (int) (allHeight + SizeUtils.dp2px(10) * imageUrls.size());
                Log.d(Config.TAG,"all image height="+imgHeight);

                int topHeight = userLayout.getMeasuredHeight();
                Log.d(Config.TAG,"topHeight="+topHeight);

                int contentHeight = goodsInfoLayout.getMeasuredHeight();
                Log.d(Config.TAG,"contentHeight="+contentHeight);

                Log.d(Config.TAG,"imgHeight="+imgHeight);
                int allBitmapHeight = topHeight + contentHeight + imgHeight;

                Bitmap.Config config = Bitmap.Config.RGB_565;
                int screenWidth = ScreenUtils.getScreenWidth();
                Bitmap bitmapAll = Bitmap.createBitmap(screenWidth, allBitmapHeight, config);

                Canvas canvas = new Canvas(bitmapAll);
                canvas.drawColor(Color.WHITE);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setDither(true);
                paint.setFilterBitmap(true);

                Bitmap topBitmap = BitmapUtil.getCacheBitmapFromView(userLayout);
                canvas.drawBitmap(topBitmap, leftRightMargin, 0, paint);

                Bitmap contentBitmap = BitmapUtil.getCacheBitmapFromView(goodsInfoLayout);
                canvas.drawBitmap(contentBitmap, leftRightMargin, topHeight, paint);

                int top = (int) (topHeight + goodsInfoLayout.getMeasuredHeight()+SizeUtils.dp2px(10));
                for(int i=0;i<localImagePaths.size();i++){
                    Bitmap  temp = localImagePaths.get(i);
                    canvas.drawBitmap(temp, leftRightMargin, top, paint);
                    top += imgHeightMap.get(i) + SizeUtils.dp2px(10);
                }
                BitmapUtil.saveBitmapToSdCard(FleaApplication.getApp(),bitmapAll);
            }
        });
    }

    private Observable<Integer> getAllImageHeight(List<String> imageUrlList,int leftRightMargin) {
        imgHeightMap.clear();
        localImagePaths.clear();
        int screenWidth = ScreenUtils.getScreenWidth();
        return Observable.just(imageUrlList).map(new Function<List<String>, Integer>() {
            @Override
            public Integer apply(@NonNull List<String> strings) throws Exception {
                int sumHeight = 0;
                for (int i = 0; i < imageUrlList.size(); i++) {
                    String url = imageUrlList.get(i);
                    String location = String.format(Config.GOODS_FULL_REF_PATH_FMT, url);
                    StorageReference gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(location);
                    FutureTarget<Bitmap> bitmapTarget = GlideApp.with(Utils.getContext())
                            .asBitmap()
                            .load(gsReference)
                            .apply(requestOptions)
                            .submit();
                    try {
                        Bitmap bitmapFile  = bitmapTarget.get();
                        Log.d(Config.TAG,"local image path===>width"+bitmapFile.getWidth()+",height=>"+bitmapFile.getHeight());
                        Bitmap bitmapTemp = BitmapUtil.fitBitmap(bitmapFile,
                                screenWidth - leftRightMargin * 2);
                        Log.d(Config.TAG,"fit image path===>width"+bitmapTemp.getWidth()+",height=>"+bitmapTemp.getHeight());
                        sumHeight += bitmapTemp.getHeight();
                        localImagePaths.add(bitmapTemp);
                        imgHeightMap.put(i, bitmapTemp.getHeight());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(Config.TAG,"get image height exception===>"+e.getMessage());
                    }
                }
                return sumHeight;
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }


}