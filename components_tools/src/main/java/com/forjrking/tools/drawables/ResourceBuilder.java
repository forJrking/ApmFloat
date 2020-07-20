package com.forjrking.tools.drawables;

import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.forjrking.tools.Cxt;


/**
 * 创建时间 2019/1/3
 * 描述     文件资源的builder
 *
 * @author zhuxi
 */
public class ResourceBuilder implements DrawableBuilder {
    private int mResId;

    /**
     * @param resId 文件资源id
     */
    public ResourceBuilder(@DrawableRes int resId) {
        if (resId == 0) {
            throw new IllegalArgumentException("Id can not be 0");
        }
        this.mResId = resId;
    }

    @Override
    public Drawable build() {
        return ContextCompat.getDrawable(Cxt.get(), mResId);
    }
}
