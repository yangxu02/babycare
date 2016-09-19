package com.linkx.babycare.view.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.linkx.babycare.view.fragments.EditFragment;
import com.linkx.babycare.view.fragments.HistoryChartFragment;
import com.linkx.babycare.view.fragments.HistoryDataFragment;

/**
 * Created by ulyx.yang on 2016/9/15.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    private final int pagrCounts;

    public PagerAdapter(FragmentManager fm, int pagrCounts) {
        super(fm);
        this.pagrCounts = pagrCounts;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return EditFragment.newInstance();
            case 1:
                return HistoryDataFragment.newInstance();
            case 2:
                return HistoryChartFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return pagrCounts;
    }
}
