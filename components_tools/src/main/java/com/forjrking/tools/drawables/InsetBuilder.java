package com.forjrking.tools.drawables;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;

import com.forjrking.tools.DpUtil;


/**
 * 创建时间 2019/1/3
 * 描述     inset样式的构造器
 *
 * @author zhuxi
 */
public class InsetBuilder implements DrawableBuilder {
    private DrawableBuilder mBuilder;
    private int mInsetLeft;
    private int mInsetRight;
    private int mInsetTop;
    private int mInsetBottom;

    /**
     * @param builder 用于显示的内容样式
     */
    public InsetBuilder(DrawableBuilder builder) {
        if (builder == null) {
            throw new NullPointerException("Builder can not be null");
        }
        this.mBuilder = builder;
    }

    /**
     * 设置统一的边距
     *
     * @param inset 统一的边距
     */
    public InsetBuilder setInset(int inset) {
        inset = DpUtil.dp2px(inset);
        this.mInsetLeft = inset;
        this.mInsetRight = inset;
        this.mInsetTop = inset;
        this.mInsetBottom = inset;
        return this;
    }

    /**
     * 单独设置边距
     *
     * @param insetLeft   左边距
     * @param insetTop    上边距
     * @param insetRight  右边距
     * @param insetBottom 下边距
     */
    public InsetBuilder setInset(int insetLeft, int insetTop, int insetRight, int insetBottom) {
        this.mInsetLeft = DpUtil.dp2px(insetLeft);
        this.mInsetRight = DpUtil.dp2px(insetRight);
        this.mInsetTop = DpUtil.dp2px(insetTop);
        this.mInsetBottom = DpUtil.dp2px(insetBottom);
        return this;
    }

    @Override
    public Drawable build() {
        return new InsetDrawable(mBuilder.build(), mInsetLeft, mInsetTop, mInsetRight, mInsetBottom);
    }
}
