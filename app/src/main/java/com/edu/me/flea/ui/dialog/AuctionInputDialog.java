package com.edu.me.flea.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.edu.me.flea.R;
import com.edu.me.flea.interfaces.MoneyTextWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuctionInputDialog extends Dialog {

    @BindView(R.id.valueEt)
    EditText valueEt;

    @BindView(R.id.messageEt)
    EditText messageEt;

    @BindView(R.id.negativeBtn)
    TextView negativeBtn;

    @BindView(R.id.positiveBtn)
    TextView positiveBtn;

    private OnConfirmListener onConfirmListener;

    public AuctionInputDialog(@NonNull Context context) {
        super(context);
    }

    public void setOnConfirmListener(OnConfirmListener l)
    {
        this.onConfirmListener = l;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_auction_input);
        ButterKnife.bind(this);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        setCancelable(true);
        valueEt.addTextChangedListener(new MoneyTextWatcher(valueEt));

        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onConfirmListener != null){
                    String value = valueEt.getText().toString();
                    if(TextUtils.isEmpty(value)){
                        ToastUtils.showShort("please input value of donation");
                        return ;
                    }
                    String message = messageEt.getText().toString().trim();
                    if(TextUtils.isEmpty(message)){
                        ToastUtils.showShort("please leave message");
                        return ;
                    }
                    onConfirmListener.onConfirm(value,message);
                }
                dismiss();
            }
        });
    }

    public interface OnConfirmListener
    {
        void onConfirm(String value,String message);
    }
}
