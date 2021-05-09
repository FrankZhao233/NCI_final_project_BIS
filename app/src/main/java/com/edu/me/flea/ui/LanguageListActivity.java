package com.edu.me.flea.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.rxbus.RxBus;
import com.edu.me.flea.R;
import com.edu.me.flea.base.CommonAdapter;
import com.edu.me.flea.base.RxEvent;
import com.edu.me.flea.base.ViewHolder;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.entity.LanguageInfo;
import com.edu.me.flea.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = Config.Page.LANGUAGE_LIST)
public class LanguageListActivity extends AppCompatActivity {

    @BindView(R.id.languageLv) ListView languageLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_list);
        ButterKnife.bind(this);
        String defaultLanguage = PreferencesUtils.getString(this, Constants.PrefKey.LANGUAGE,"en");
        CommonAdapter<LanguageInfo> mAdapter = new CommonAdapter<LanguageInfo>(R.layout.item_language, getLanguages()) {
            @Override
            public void convert(ViewHolder holder, LanguageInfo item, int position) {
                holder.setTvText(R.id.nameTv, item.name);
                if (item.symbol.equals(defaultLanguage)) {
                    holder.setVisibility(R.id.checkIv, View.VISIBLE);
                } else {
                    holder.setVisibility(R.id.checkIv, View.GONE);
                }
            }
        };
        languageLv.setAdapter(mAdapter);

        languageLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LanguageInfo info = (LanguageInfo) parent.getAdapter().getItem(position);
                PreferencesUtils.putString(LanguageListActivity.this,
                        Constants.PrefKey.LANGUAGE,info.symbol);
                RxBus.getDefault().post(new RxEvent(Constants.Event.LANGUAGE_CHANGED,null));
                finish();
            }
        });
    }

    private List<LanguageInfo> getLanguages()
    {
        List<LanguageInfo> result = new ArrayList<>();
        LanguageInfo language1 = new LanguageInfo();
        language1.name = "English";
        language1.symbol = "en";
        result.add(language1);

        LanguageInfo language2 = new LanguageInfo();
        language2.name = "中文简体";
        language2.symbol = "zh";
        result.add(language2);

        LanguageInfo language3 = new LanguageInfo();
        language3.name = "Deutsche";
        language3.symbol = "de";
        result.add(language3);

        LanguageInfo language4 = new LanguageInfo();
        language4.name = "français";
        language4.symbol = "fr";
        result.add(language4);

        return result;
    }


}