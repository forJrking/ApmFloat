package com.forjrking.tools.drawables;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;

import com.forjrking.tools.DpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建时间 2019/1/3
 * 描述     多重样式叠加的Drawable构建器，后添加的覆盖在新添加的上面
 *
 * @author zhuxi
 */
public class LayerListBuilder implements DrawableBuilder {
    private List<Drawable> mDrawableList = new ArrayList<>();
    private List<LayerInset> mInsetList = new ArrayList<>();

    /**
     * 添加图层
     *
     * @param layer 新添加的图层
     */
    public LayerListBuilder addLayer(DrawableBuilder layer) {
        return addLayer(layer, new LayerInset());
    }

    /**
     * 添加图层，带属性
     *
     * @param layer 新添加的图层
     * @param inset 图层属性设置
     */
    public LayerListBuilder addLayer(DrawableBuilder layer, LayerInset inset) {
        mDrawableList.add(layer.build());
        mInsetList.add(inset);
        return this;
    }

    @Override
    public Drawable build() {
        if (mDrawableList.isEmpty()) {
            return new ColorDrawable(Color.TRANSPARENT);
        } else {
            Drawable[] temp = new Drawable[mDrawableList.size()];
            LayerDrawable layerDrawable = new LayerDrawable(mDrawableList.toArray(temp));
            for (int x = 0; x < mDrawableList.size(); x++) {
                LayerInset inset = mInsetList.get(x);
                layerDrawable.setLayerInset(x, inset.left, inset.top, inset.right, inset.bottom);
                layerDrawable.setId(x, inset.id);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    layerDrawable.setLayerGravity(x, inset.gravity);
                    layerDrawable.setLayerSize(x, inset.width, inset.height);
                }
            }
            return layerDrawable;
        }
    }

    /**
     * layer中图层的属性类
     */
    public static class LayerInset {
        private int left, right, top, bottom;
        private int width = -1;
        private int height = -1;
        private int id = View.NO_ID;
        private int gravity = Gravity.NO_GRAVITY;

        public LayerInset id(int id) {
            this.id = id;
            return this;
        }

        public LayerInset padding(int left, int top, int right, int bottom) {
            this.left = DpUtil.dp2px(left);
            this.top =DpUtil.dp2px(top);
            this.right = DpUtil.dp2px(right);
            this.bottom = DpUtil.dp2px(bottom);
            return this;
        }

        public LayerInset padding(int padding) {
            padding = DpUtil.dp2px(padding);
            this.left = padding;
            this.top = padding;
            this.right = padding;
            this.bottom = padding;
            return this;
        }

        @TargetApi(Build.VERSION_CODES.M)
        public LayerInset width(int width) {
            this.width =  DpUtil.dp2px(width);
            return this;
        }

        @TargetApi(Build.VERSION_CODES.M)
        public LayerInset height(int height) {
            this.height = DpUtil.dp2px(height);
            return this;
        }

        @TargetApi(Build.VERSION_CODES.M)
        public LayerInset gravity(int gravity) {
            this.gravity = gravity;
            return this;
        }
    }
}
