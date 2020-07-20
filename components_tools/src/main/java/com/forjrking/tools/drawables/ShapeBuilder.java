package com.forjrking.tools.drawables;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;

import androidx.annotation.ColorRes;
import androidx.annotation.IntDef;

import com.forjrking.tools.Cxt;
import com.forjrking.tools.DpUtil;
import com.forjrking.tools.log.KLog;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 创建时间 2019/1/3
 * 描述     shape样式的构建类，所有距离值均可传dp
 *
 * @author zhuxi
 */
public class ShapeBuilder implements DrawableBuilder {
    /**
     * Shape is a rectangle, possibly with rounded corners
     */
    public static final int RECTANGLE = GradientDrawable.RECTANGLE;

    /**
     * Shape is an ellipse
     */
    public static final int OVAL = GradientDrawable.OVAL;

    /**
     * Shape is a line
     */
    public static final int LINE = GradientDrawable.LINE;

    /**
     * Shape is a ring.
     */
    public static final int RING = GradientDrawable.RING;


    @IntDef({RECTANGLE, OVAL, LINE, RING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Shape {
    }

    private float mRadius;
    private float[] mRadii;
    private float mStrokeWidth;
    private float mDashWidth;
    private float mDashGap;
    private int mShape;
    private int mSolidColor;
    private int mStrokeColor;
    private int[] mGradientColor;
    private int mAngle;

    /**
     * 设置四个角的圆角
     *
     * @param dpRadius 圆角的半径
     */
    public ShapeBuilder corner(float dpRadius) {
        this.mRadius = DpUtil.dp2px(dpRadius);
        return this;
    }

    /**
     * DES: 设置四个角的圆角
     * AUTHOR: 秦王
     * TIME: 2019/12/25 22:02
     *
     * @param dpRadius 半径值
     * @return drawables.ShapeBuilder
     **/
    public ShapeBuilder adapterCorner(int dpRadius) {
        this.mRadius = DpUtil.dp2px(dpRadius);
        return this;
    }

    /**
     * 分别设置四个角的圆角
     *
     * @param leftTop     左上角圆角半径
     * @param rightTop    右上角圆角半径
     * @param leftBottom  左下角圆角半径
     * @param rightBottom 右下角圆角半径
     */
    public ShapeBuilder corner(float leftTop, float rightTop, float leftBottom, float rightBottom) {
        leftTop = DpUtil.dp2px(leftTop);
        rightTop = DpUtil.dp2px(rightTop);
        leftBottom = DpUtil.dp2px(leftBottom);
        rightBottom = DpUtil.dp2px(rightBottom);
        this.mRadii = new float[]{
                leftTop, leftTop,
                rightTop, rightTop,
                rightBottom, rightBottom,
                leftBottom, leftBottom
        };
        return this;
    }

    /**
     * 设置填充色
     *
     * @param colorId 填充的颜色
     */
    public ShapeBuilder solid(@ColorRes int colorId) {
        this.mSolidColor = Cxt.getColor(colorId);
        return this;
    }

    /**
     * 设置填充色
     *
     * @param colorString 颜色的string值
     */
    public ShapeBuilder solid(String colorString) {
//        KLog.e("颜色的string值","colorString="+colorString);
        try {
            if (!TextUtils.isEmpty(colorString)) {
                this.mSolidColor = Color.parseColor(colorString.trim());
            }
        } catch (Exception e) {
            KLog.e(e);
        }
        return this;
    }

    /**
     * 设置填充色
     *
     * @param colorString 颜色的string值
     */
    public ShapeBuilder solid(String colorString, @ColorRes int defaultColor) {
//        KLog.e("颜色的string值","colorString="+colorString);
        if (!TextUtils.isEmpty(colorString)) {
            if (colorString.startsWith("#")) {
                this.mSolidColor = Color.parseColor(colorString.trim());
            } else {
                this.mSolidColor = Cxt.getColor(defaultColor);
            }
        }
        return this;
    }

    /**
     * 设置Drawable的形状
     *
     * @param shape 形状
     */
    public ShapeBuilder shape(@Shape int shape) {
        this.mShape = shape;
        return this;
    }

    /**
     * 设置线条的颜色和粗细
     *
     * @param colorId 线条颜色
     * @param dpWidth 线条粗细，dp
     */
    public ShapeBuilder stroke(@ColorRes int colorId, float dpWidth) {
        this.mStrokeWidth = DpUtil.dp2px(dpWidth);
        this.mStrokeColor = Cxt.getColor(colorId);
        return this;
    }

    /**
     * 设置线条的颜色和粗细
     *
     * @param colorString 线条颜色
     * @param dpWidth     线条粗细，dp
     */
    public ShapeBuilder stroke(@NotNull String colorString, float dpWidth) {
        this.mStrokeWidth = DpUtil.dp2px(dpWidth);
        try {
            if (!TextUtils.isEmpty(colorString)) {
                this.mStrokeColor = Color.parseColor(colorString.trim());
            }
        } catch (Exception e) {
            KLog.e(e);
        }
        return this;
    }

    /**
     * 设置线条的颜色和粗细
     *
     * @param colorInt 线条颜色
     */
    public ShapeBuilder strokeInt(float dpWidth, @NotNull int colorInt) {
        this.mStrokeWidth = DpUtil.dp2px(dpWidth );
        this.mStrokeColor = colorInt;
        return this;
    }

    /**
     * 设置虚线
     *
     * @param dpDashGap   实线的距离
     * @param dpDashWidth 实线的宽度
     */
    public ShapeBuilder dash(float dpDashGap, float dpDashWidth) {
        this.mDashGap = DpUtil.dp2px(dpDashGap);
        this.mDashWidth =DpUtil.dp2px( dpDashWidth);
        return this;
    }

    /**
     * 设置渐变色
     *
     * @param startColor 起始颜色
     * @param endColor   结束颜色
     * @param angle      渐变角度，必须为45的倍数
     *                   <!-- angle 0 从右至左渐变 -->
     *                   <!-- angle 90 从下至上渐变 -->
     *                   <!-- angle 180 从左至右渐变 -->
     *                   <!-- angle 270 从上至下渐变 -->
     */
    public ShapeBuilder gradient(@ColorRes int startColor, @ColorRes int endColor, int angle) {
        if (angle % 45 != 0) {
            throw new IllegalArgumentException("'angle' attribute to be a multiple of 45");
        }
        int[] color = new int[2];
        color[0] = Cxt.getColor(startColor);
        color[1] = Cxt.getColor(endColor);
        this.mGradientColor = color;
        this.mAngle = angle;
        return this;
    }

    @Override
    public Drawable build() {
        GradientDrawable gradientDrawable;
        if (mGradientColor != null) {
            int angle = mAngle;
            angle %= 360;
            GradientDrawable.Orientation orientation;
            switch (angle) {
                case 45:
                    orientation = GradientDrawable.Orientation.BL_TR;
                    break;
                case 90:
                    orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                    break;
                case 135:
                    orientation = GradientDrawable.Orientation.BR_TL;
                    break;
                case 180:
                    orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                    break;
                case 225:
                    orientation = GradientDrawable.Orientation.TR_BL;
                    break;
                case 270:
                    orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                    break;
                case 315:
                    orientation = GradientDrawable.Orientation.TL_BR;
                    break;
                case 0:
                default:
                    orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                    break;
            }
            gradientDrawable = new GradientDrawable(orientation, mGradientColor);
        } else {
            gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(mSolidColor);
        }
        if (mStrokeWidth != 0) {
            if (mDashWidth != 0 && mDashGap != 0) {
                gradientDrawable.setStroke((int) (mStrokeWidth + .5f), mStrokeColor, mDashWidth, mDashGap);
            } else {
                gradientDrawable.setStroke((int) (mStrokeWidth + .5f), mStrokeColor);
            }
        }
        if (mRadius != 0) {
            gradientDrawable.setCornerRadius(mRadius);
        } else if (mRadii != null) {
            gradientDrawable.setCornerRadii(mRadii);
        }
        gradientDrawable.setShape(mShape);
        return gradientDrawable;
    }
}
