package com.edu.me.flea.ui.adpater;

import android.content.Context;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.edu.me.flea.ui.fragment.HomeFragment;
import com.edu.me.flea.ui.fragment.NotificationsFragment;
import com.edu.me.flea.ui.fragment.ProfileFragment;
import com.edu.me.flea.ui.fragment.WelfareFragment;

public class FragmentAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private SparseArray<Fragment> mFragments = new SparseArray<Fragment>();

    public FragmentAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm);
        this.mContext = context;
        mFragments.put(0, HomeFragment.newInstance());
        mFragments.put(1, WelfareFragment.newInstance());
        mFragments.put(2, NotificationsFragment.newInstance());
        mFragments.put(3, ProfileFragment.newInstance());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments != null? mFragments.size() : 0;
    }



//    override fun getItemPosition(`object`: Any): Int {
//        return PagerAdapter.POSITION_NONE
//    }
//
//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        var fragment =  super.instantiateItem(container, position) as Fragment
//        mFragments.put(position,fragment)
//        return fragment
//    }
//
//    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
//        mFragments.remove(position)
//    }
}
