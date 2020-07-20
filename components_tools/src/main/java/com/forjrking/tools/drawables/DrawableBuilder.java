package com.forjrking.tools.drawables;

import android.graphics.drawable.Drawable;

/**
 * 创建时间 2019/1/3
 * 描述     Drawable构造器的接口类
 *
 * @author zhuxi
 */
public interface DrawableBuilder {
    /**
     * 此方法构建出新的Drawable给控件设置背景
     *
     * @return 构造好的Drawable
     */
    Drawable build();
}
