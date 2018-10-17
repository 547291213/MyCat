package com.example.xkfeng.mycat.Util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.DrawableRes;
import android.view.View;

/**
 * Created by initializing on 2018/10/5.
 */

public class DensityUtil {

    /**
     * @param context 内容上下文
     * @param dpValue dp值
     * @return 对应屏幕分辨率的px值
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String getResourcesUri(Context context, @DrawableRes int id) {
        Resources resources = context.getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(id) + "/" + resources.getResourceTypeName(id) + "/" + resources.getResourceEntryName(id);
        return uriPath;
    }

    /**
     * 直角三角形求外边边长
     *
     * @param x x轴偏移量
     * @param y y轴偏移量
     * @return 偏移距离
     */
    public static float getDistance(double x, double y) {
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 计算两点之间的距离
     * @param pointF
     * @param pointF1
     * @return 距离
     */
    public static float getDistance(PointF pointF, PointF pointF1) {
        float distance = (float) Math.sqrt(Math.pow(pointF.y - pointF1.y, 2)
                + Math.pow(pointF.x - pointF1.x, 2));
        return distance ;
    }

    /**
     * 通过圆心，半径，直线斜率求得一对切点坐标
     *
     * @param pMiddle 圆心
     * @param radius  半径
     * @param lineK   斜率
     * @return
     */
    public static PointF[] getIntersectionPoints(PointF pMiddle, float radius, Double lineK) {
        PointF[] points = new PointF[2];

        float radian, xOffset = 0, yOffset = 0;
        if (lineK != null) {

            radian = (float) Math.atan(lineK);
            xOffset = (float) (Math.cos(radian) * radius);
            yOffset = (float) (Math.sin(radian) * radius);
        } else {
            xOffset = radius;
            yOffset = 0;
        }
        points[0] = new PointF(pMiddle.x + xOffset, pMiddle.y + yOffset);
        points[1] = new PointF(pMiddle.x - xOffset, pMiddle.y - yOffset);

        return points;
    }

    /**
     * Get point between p1 and p2 by percent.
     * 根据百分比获取两点之间的某个点坐标
     * @param p1
     * @param p2
     * @param percent
     * @return
     */
    public static PointF getPointByPercent(PointF p1, PointF p2, float percent) {
        return new PointF(evaluateValue(percent, p1.x , p2.x), evaluateValue(percent, p1.y , p2.y));
    }

    /**
     * 根据分度值，计算从start到end中，fraction位置的值。fraction范围为0 -> 1
     * @param fraction
     * @param start
     * @param end
     * @return
     */
    public static float evaluateValue(float fraction, Number start, Number end){
        return start.floatValue() + (end.floatValue() - start.floatValue()) * fraction;
    }

    /** 获取状态栏高度
     * @param v
     * @return
     */
    public static int getStatusBarHeight(View v) {
        if (v == null) {
            return 0;
        }
        Rect frame = new Rect();
        v.getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

}
