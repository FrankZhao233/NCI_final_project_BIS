package com.edu.me.flea.base;


import androidx.annotation.StringRes;

public interface IBaseView {
    void showLoading(@StringRes int msgRes);
    void closeLoading();
    void closePage();
}
