package com.linkx.babycare.data.models;

import com.linkx.babycare.R;

/**
 * Created by ulyx.yang on 2016/9/18.
 */
public enum Softy {
    soft(R.string.softy_soft),
    appropriate(R.string.softy_appropriate),
    hard(R.string.softy_hard),
    ;

    int resId;
    Softy(int resId) {
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }
}
