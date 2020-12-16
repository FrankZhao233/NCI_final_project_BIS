package com.edu.me.flea.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.edu.me.flea.R;
import com.edu.me.flea.base.CommonAdapter;
import com.edu.me.flea.base.ViewHolder;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.CommentInfo;
import com.edu.me.flea.entity.GoodsInfo;
import com.edu.me.flea.utils.DateUtils;
import com.edu.me.flea.utils.ImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentDialog extends Dialog {

    @BindView(R.id.closeIv) ImageView closeIv;
    @BindView(R.id.commentList) ListView commentLv;
    @BindView(R.id.msgEt) EditText msgEt;
    @BindView(R.id.sendBtn) ImageView sendBtn;

    private List<CommentInfo> mComments = new ArrayList<>();
    private CommonAdapter<CommentInfo> mAdapter;
    private String mGoodsId;

    public CommentDialog(@NonNull Context context,String goodsId) {
        super(context, R.style.dialog_bottom_full);
        this.mGoodsId = goodsId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialogAnimation);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_goods_comment);
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        );
        ButterKnife.bind(this);
        setListener();
        mAdapter = new CommonAdapter<CommentInfo>(R.layout.item_goods_comment,mComments) {
            @Override
            public void convert(ViewHolder holder, CommentInfo item, int position) {
                holder.setTvText(R.id.nickNameTv,item.creatorName);
                holder.setTvText(R.id.timeTv, DateUtils.formatDate(item.createTime));
                ImageView avatarId = holder.getImageView(R.id.avatarIv);
                ImageLoader.loadAvatar(avatarId,item.creatorId);
                holder.setTvText(R.id.contentTv,item.content);
            }
        };
        commentLv.setAdapter(mAdapter);
        loadData();
    }

    private void setListener()
    {
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = msgEt.getText().toString();
                if(!TextUtils.isEmpty(msg)){
                    msgEt.setText("");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    final Map<String,Object> values = new HashMap<>();
                    values.put("content",msg);
                    values.put("creatorId",user.getUid());
                    values.put("creatorName",user.getDisplayName());
                    values.put("goodsId",mGoodsId);
                    values.put("createTime",System.currentTimeMillis());

                    FirebaseFirestore.getInstance()
                        .collection(Constants.Collection.COMMENT)
                        .document(mGoodsId)
                        .collection(Constants.Collection.COMMENT)
                        .add(values).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                CommentInfo info = new CommentInfo();
                                info.goodsId = values.get("goodsId").toString();
                                info.creatorId = values.get("creatorId").toString();
                                info.creatorName = values.get("creatorName").toString();
                                info.content = values.get("content").toString();
                                info.createTime = (long)values.get("createTime");
                                mComments.add(info);
                                mAdapter.refreshView(mComments);
                            }
                        });
                }
            }
        });
    }

    private void loadData()
    {
        FirebaseFirestore.getInstance()
            .collection(Constants.Collection.COMMENT)
            .document(mGoodsId)
            .collection(Constants.Collection.COMMENT)
            .get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                    mComments.clear();
                    for(DocumentSnapshot documentSnapshot:documentSnapshots){
                        CommentInfo commentInfo = documentSnapshot.toObject(CommentInfo.class);
                        mComments.add(commentInfo);
                    }
                    mAdapter.refreshView(mComments);
                }
            });
    }


}
