package com.vixir.flipr.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;

/**
 * Created by DELL on 30-04-2017.
 */

class ViewUtils {

    public static int getActionBarSize(@NonNull Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, value, true);
        int actionBarSize = TypedValue.complexToDimensionPixelSize(
                value.data, context.getResources().getDisplayMetrics());
        return actionBarSize;
    }

}
