package com.linkx.babycare.data.models;

import com.linkx.babycare.R;

/**
 * Created by ulyx.yang on 2016/9/18.
 */
public enum Quantity {
    few(R.string.quantity_few),
    appropriate(R.string.quantity_appropriate),
    lot(R.string.quantity_lot),
    ;

    int resId;
    Quantity(int resId) {
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }
}
