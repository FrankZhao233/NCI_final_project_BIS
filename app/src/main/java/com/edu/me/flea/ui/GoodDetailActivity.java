package com.edu.me.flea.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.donkingliang.labels.LabelsView;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseActivity;
import com.edu.me.flea.base.CommonAdapter;
import com.edu.me.flea.base.ViewHolder;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.ChatParams;
import com.edu.me.flea.entity.GoodsDetail;
import com.edu.me.flea.module.GlideApp;
import com.edu.me.flea.utils.DateUtils;
import com.edu.me.flea.utils.Utils;
import com.edu.me.flea.vm.GoodsDetailViewModel;
import com.edu.me.flea.widget.ListViewForScrollView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = Config.Page.GOODS_DETAIL)
public class GoodDetailActivity extends BaseActivity<GoodsDetailViewModel> {

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
    @BindView(R.id.contentLayout) ViewGroup contentLayout;

    private CommonAdapter<String> mAdapter;

    @Autowired(name = Constants.ExtraName.ID)
    String mId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_good_detail;
    }

    @Override
    protected void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mAdapter = new CommonAdapter<String>(R.layout.item_goods_image,null) {

            RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_empty_image)
                .error(R.drawable.ic_empty_image)
                .centerCrop();

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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initData() {
        mViewModel.queryGoodsDetail(mId);
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
                timeTv.setText("Posted at " + DateUtils.formatDate(goodsDetail.createTime));
                priceTv.setText("$"+goodsDetail.price);
                contentTv.setText(goodsDetail.content);
                List<String> labels = new ArrayList<>();
                String[] tags = goodsDetail.tags.split(",");
                for(int i=0;i<tags.length;i++){
                    String tag = tags[i].trim();
                    if(!TextUtils.isEmpty(tag)){
                        labels.add(tag);
                    }
                }
                labelsView.setLabels(labels);
                mAdapter.refreshView(goodsDetail.images);
                contentLayout.setVisibility(View.VISIBLE);
                toolLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(goodsDetail.creatorId.equals(user.getUid())){
                    orderBtn.setVisibility(View.GONE);
                }else{
                    orderBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodsDetail detail = mViewModel.getGoodsDetail().getValue();
                ChatParams chatParams = new ChatParams();
                chatParams.peerUid = detail.creatorId;
                chatParams.peerNickName = detail.creatorName;
                chatParams.cover = detail.images.get(0);
                chatParams.price = detail.price;
                chatParams.title = detail.title;
                chatParams.detailId = detail.id;
                ARouter.getInstance().build(Config.Page.CHAT)
                    .withParcelable(Constants.ExtraName.CHAT_PARAM,chatParams)
                    .navigation();
            }
        });
    }
}