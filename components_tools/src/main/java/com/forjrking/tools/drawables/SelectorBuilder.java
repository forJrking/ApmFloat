package com.forjrking.tools.drawables;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/**
 * 创建时间 2019/1/3
 * 描述     多状态Drawable的构造器
 *
 * @author zhuxi
 */
public class SelectorBuilder {
    private DrawableBuilder mPressed;
    private DrawableBuilder mNormal;
    private DrawableBuilder mUnable;
    private DrawableBuilder mChecked;
    private DrawableBuilder mSelected;

    private SelectorBuilder(DrawableBuilder normal) {
        this.mNormal = normal;
    }

    /**
     * 设置长长状态下的样式
     *
     * @param normal 正常状态下的样式
     */
    public static SelectorBuilder normal(DrawableBuilder normal) {
        if (normal == null) {
            throw new IllegalArgumentException("Normal drawable can not be null");
        }
        return new SelectorBuilder(normal);
    }

    /**
     * 设置点击样式
     *
     * @param pressed 点击后的样式
     */
    public SelectorBuilder pressed(DrawableBuilder pressed) {
        this.mPressed = pressed;
        return this;
    }

    /**
     * 设置不可用的样式
     *
     * @param unable 不可用时的样式
     */
    public SelectorBuilder unable(DrawableBuilder unable) {
        this.mUnable = unable;
        return this;
    }

    /**
     * 设置选中的样式
     *
     * @param selected 选中时的样式
     */
    public SelectorBuilder selected(DrawableBuilder selected) {
        this.mSelected = selected;
        return this;
    }

    /**
     * 设置选中的样式
     *
     * @param checked 选中时的样式
     */
    public SelectorBuilder checked(DrawableBuilder checked) {
        this.mChecked = checked;
        return this;
    }

    public Drawable build() {
        StateListDrawable drawable = new StateListDrawable();
        if (mPressed != null) {
            int pressedState = android.R.attr.state_pressed;
            drawable.addState(new int[]{pressedState}, mPressed.build());
        }
        if (mUnable != null) {
            int enableState = android.R.attr.state_enabled;
            drawable.addState(new int[]{-enableState}, mUnable.build());
        }
        if (mChecked != null) {
            int checkedState = android.R.attr.state_checked;
            drawable.addState(new int[]{checkedState}, mChecked.build());
        }
        if (mSelected != null) {
            int selectedState = android.R.attr.state_selected;
            drawable.addState(new int[]{selectedState}, mSelected.build());
        }
        drawable.addState(new int[0], mNormal.build());
        return drawable;
    }
}
