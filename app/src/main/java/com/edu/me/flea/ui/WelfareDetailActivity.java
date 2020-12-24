package com.edu.me.flea.ui;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.edu.me.flea.R;
import com.edu.me.flea.base.BaseActivity;
import com.edu.me.flea.base.RecyclerAdapter;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.ContributionInfo;
import com.edu.me.flea.entity.GoodsInfo;
import com.edu.me.flea.entity.MessageInfo;
import com.edu.me.flea.entity.WelfareInfo;
import com.edu.me.flea.ui.dialog.WelfareInputDialog;
import com.edu.me.flea.utils.ImageLoader;
import com.edu.me.flea.vm.WelfareDetailViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = Config.Page.WELFARE_DETAIL)
public class WelfareDetailActivity extends BaseActivity<WelfareDetailViewModel> {

    @BindView(R.id.coverIv) ImageView coverIv;
    @BindView(R.id.titleTv) TextView titleTv;
    @BindView(R.id.descriptionTv) TextView descriptionTv;
    @BindView(R.id.destinationTv) TextView destinationTv;
    @BindView(R.id.currentTv) TextView currentTv;
    @BindView(R.id.donateBtn) Button donateBtn;
    @BindView(R.id.contributorsRv) RecyclerView contributorsRv;
    @BindView(R.id.contributorTv) TextView contributorTv;

    @Autowired(name = Constants.ExtraName.WELFARE_DETAIL)
    WelfareInfo mWelfareInfo;

    private RecyclerAdapter<ContributionInfo> mAdapter = new RecyclerAdapter<ContributionInfo>(){
        @Override
        public ViewHolder<ContributionInfo> onCreateViewHolder(View root, int viewType) {
            return new ContributorHolder(root);
        }

        @Override
        public int getItemLayout(ContributionInfo contributionInfo, int position) {
            return R.layout.item_contributor;
        }
    };

    private static class ContributorHolder extends RecyclerAdapter.ViewHolder<ContributionInfo>{
        ImageView avatarIv;
        public ContributorHolder(View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatarIv);

        }

        @Override
        protected void onBind(ContributionInfo contributionInfo) {
            ImageLoader.loadAvatar(avatarIv,contributionInfo.uid);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welfare_detail;
    }

    @Override
    protected void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if(mWelfareInfo != null){
            titleTv.setText(mWelfareInfo.title);
            descriptionTv.setText(mWelfareInfo.description);
            ImageLoader.loadNetImage(coverIv,mWelfareInfo.cover);
            String destFmt = getString(R.string.welfare_destination);
            destinationTv.setText(String.format(destFmt,String.valueOf(mWelfareInfo.destination)));
            String currentFmt = getString(R.string.welfare_current);
            currentTv.setText(String.format(currentFmt,String.valueOf(mWelfareInfo.current)));
        }
        contributorsRv.setLayoutManager(new GridLayoutManager(this,10));
        contributorsRv.setAdapter(mAdapter);
    }



    @Override
    protected void initData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mViewModel.checkDonate(mWelfareInfo);
        }
    }

    @Override
    protected void inject() {
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
    }

    @Override
    protected void setListener() {
        donateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    ARouter.getInstance().build(Config.Page.LOGIN).navigation();
                } else {
                    WelfareInputDialog dialog = new WelfareInputDialog(WelfareDetailActivity.this);
                    dialog.setOnConfirmListener(new WelfareInputDialog.OnConfirmListener() {
                        @Override
                        public void onConfirm(String value, String message) {
                            mViewModel.donate(value, message, mWelfareInfo);
                        }
                    });
                    dialog.show();
                }
            }
        });

        mViewModel.donateOver.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
               disableButton();
            }
        });

        mViewModel.getContributions().observe(this, new Observer<List<ContributionInfo>>() {
            @Override
            public void onChanged(List<ContributionInfo> contributors) {
                if(contributors != null){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    for(ContributionInfo contributor:contributors){
                        if(user.getUid().equals(contributor.uid)){
                            disableButton();
                            break;
                        }
                    }
                    contributorTv.setVisibility(View.VISIBLE);
                    mAdapter.addAllData(contributors);
                }
            }
        });

    }

    public void disableButton()
    {
        donateBtn.setBackgroundResource(R.color.donate_btn_disable_color);
        donateBtn.setText("YOU HAVE ALREADY DONATED");
        donateBtn.setEnabled(false);
    }
}