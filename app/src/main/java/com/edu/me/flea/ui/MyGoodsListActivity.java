package com.edu.me.flea.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;

import android.content.DialogInterface;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseActivity;
import com.edu.me.flea.base.CommonAdapter;
import com.edu.me.flea.base.ViewHolder;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.GoodsDetail;
import com.edu.me.flea.entity.GoodsInfo;
import com.edu.me.flea.utils.DateUtils;
import com.edu.me.flea.utils.ImageLoader;
import com.edu.me.flea.utils.ToastUtils;
import com.edu.me.flea.vm.MyGoodsListViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.internal.cache.DiskLruCache;

@Route(path = Config.Page.MY_GOODS_LIST)
public class MyGoodsListActivity extends BaseActivity<MyGoodsListViewModel> {

    @BindView(R.id.goodsList) ListView goodsLv;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private CommonAdapter<GoodsInfo> mAdapter ;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_goods_list;
    }

    @Override
    protected void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mAdapter = new CommonAdapter<GoodsInfo>(R.layout.item_my_goods_list,mViewModel.getGoods().getValue()) {
            @Override
            public void convert(ViewHolder holder, GoodsInfo item, int position) {
                holder.setTvText(R.id.titleTv,item.title);
                holder.setTvText(R.id.priceTv,"€"+item.price);
                holder.setTvText(R.id.timeTv, DateUtils.formatDate(item.createTime));
                ImageView coverIv = holder.getImageView(R.id.coverIv);
                ImageLoader.loadCover(coverIv,item.cover);
                if(item.dueTime>0){
                    holder.setVisibility(R.id.slantedTv,View.VISIBLE);
                }else{
                    holder.setVisibility(R.id.slantedTv,View.GONE);
                }
            }
        };
        goodsLv.setAdapter(mAdapter);
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
        mViewModel.queryGoods();
    }

    @Override
    protected void inject() {
        ButterKnife.bind(this);
    }

    @Override
    protected void setListener() {
        mViewModel.getGoods().observe(this, new Observer<List<GoodsInfo>>() {
            @Override
            public void onChanged(List<GoodsInfo> goods) {
                mAdapter.refreshView(goods);
                progressBar.setVisibility(View.GONE);
                goodsLv.setVisibility(View.VISIBLE);
            }
        });

        goodsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GoodsInfo goodsInfo = mViewModel.getGoods().getValue().get(i);
                ARouter.getInstance().build(Config.Page.GOODS_DETAIL)
                    .withParcelable(Constants.ExtraName.SNAPSHOT,goodsInfo)
                    .withBoolean(Constants.ExtraName.SHOW_TOOLS,false)
                    .navigation();
            }
        });

        goodsLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                askForDelete(position);
                return true;
            }
        });
    }

    private void askForDelete(int pos)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.prompt));
        builder.setMessage(getString(R.string.ask_for_delete_goods));
        builder.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mViewModel.deleteSnapshot(pos);
            }
        });

        builder.setNeutralButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }


}