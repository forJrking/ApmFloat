package com.forjrking.tools.drawables;

import android.content.res.ColorStateList;
import android.util.SparseIntArray;

import androidx.annotation.ColorRes;

import com.forjrking.tools.Cxt;


/**
 * 创建时间 2019/1/4
 * 描述     字体颜色构建器
 *
 * @author zhuxi
 */
public class ColorStateListBuilder {
    private int mNormalColorId;
    private SparseIntArray array = new SparseIntArray();

    private ColorStateListBuilder(@ColorRes int colorId) {
        this.mNormalColorId = colorId;
    }

    /**
     * 正常显示的颜色
     *
     * @param colorId 颜色id
     */
    public static ColorStateListBuilder normal(@ColorRes int colorId) {
        if (colorId == 0) {
            throw new IllegalArgumentException("Id can not be 0");
        }
        return new ColorStateListBuilder(colorId);
    }

    /**
     * 设置按下的颜色
     *
     * @param colorId 按下的颜色id
     */
    public ColorStateListBuilder pressed(@ColorRes int colorId) {
        int pressed = android.R.attr.state_pressed;
        array.put(pressed, colorId);
        return this;
    }

    /**
     * 设置不可用时的颜色
     *
     * @param colorId 不可用的颜色id
     */
    public ColorStateListBuilder unable(@ColorRes int colorId) {
        int unable = -android.R.attr.state_enabled;
        array.put(unable, colorId);
        return this;
    }

    /**
     * 设置选中时的颜色
     *
     * @param colorId 选中的颜色id
     */
    public ColorStateListBuilder selected(@ColorRes int colorId) {
        int selected = android.R.attr.state_selected;
        array.put(selected, colorId);
        return this;
    }

    /**
     * 设置checkbox中被选中时的颜色
     *
     * @param colorId 选中的颜色id
     */
    public ColorStateListBuilder checked(@ColorRes int colorId) {
        int checked = android.R.attr.state_checked;
        array.put(checked, colorId);
        return this;
    }

    public ColorStateList build() {
        int size = array.size() + 1;
        int[] color = new int[size];
        int[][] colorState = new int[size][];
        for (int x = 0; x < size; x++) {
            if (x != size - 1) {
                colorState[x] = new int[]{array.keyAt(x)};
                color[x] = Cxt.getColor(array.valueAt(x));
            } else {
                colorState[x] = new int[]{};
                color[x] = Cxt.getColor(mNormalColorId);
            }
        }
        return new ColorStateList(colorState, color);
    }
}
