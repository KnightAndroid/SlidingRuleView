package com.example.slidingruleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;



/**
 * Describe :
 * Created by Knight on 2018/12/27
 * 点滴之行,看世界
 **/
public class SlidingRuleView extends View {

    //文字画笔
    private Paint paint;
    //文字足够长 超过屏幕显示宽度 方便后面看滑动效果
    private String currentNum = "1234sdddddddddd423dddddddd234dddddd234dddddd23423dddddddd234ddddd234ddddddd23423dddddd23ddd234ddddddd34334ddddddddddddddddddddddddddddddddsdddddddddddd";
    //这个自定义View的高度
    private int height;
    public SlidingRuleView(Context context) {
        this(context,null);

    }

    public SlidingRuleView(Context context,  AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SlidingRuleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context){
        //初始化画笔 抗锯齿
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //画笔的颜色 黑色
        paint.setColor(Color.BLACK);
        //设置填充样式，只绘制图形的轮廓
        paint.setStyle(Paint.Style.STROKE);
        //设置文本大小
        paint.setTextSize(25f);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        //MeasureSpec值由specMode和specSize共同组成，onMeasure两个参数的作用根据specMode的不同，有所区别。
        //当specMode为EXACTLY时，子视图的大小会根据specSize的大小来设置，对于布局参数中的match_parent或者精确大小值
        //当specMode为AT_MOST时，这两个参数只表示了子视图当前可以使用的最大空间大小，而子视图的实际大小不一定是specSize。所以我们自定义View时，重写onMeasure方法主要是在AT_MOST模式时，为子视图设置一个默认的大小，对于布局参数wrap_content。
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            //这个方法确定了当前View的大小
            setMeasuredDimension(widthSpecSize, SystemUtil.dp2px(getContext(),60));
        } else {
            setMeasuredDimension(widthSpecSize, heightSpecSize);
        }
        //这里获取View的高度 方便后面绘制算一些坐标
        height = getMeasuredHeight();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //得到文字的字体属性和测量
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        //文字设置在View的中间
        float y = height / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
        //canvas绘制文本
        canvas.drawText(currentNum, 0,y, paint);
    }
}




//创建滑动实例
//        mScroller = new Scroller(context);

//
//    /**
//     *
//     * 用于滑动实例
//     * @param context
//     *
//     */
//    private Scroller mScroller;
//    /**
//     * 最小移动距离
//     */
//    private int mTouchMinDistance;
//
//    //第一步，获取Android常量距离对象，这个类有UI中所使用到的标准常量，像超时，尺寸，距离
//    ViewConfiguration configuration = ViewConfiguration.get(context);
////获取最小移动距离
//        mTouchMinDistance = configuration.getScaledTouchSlop();

