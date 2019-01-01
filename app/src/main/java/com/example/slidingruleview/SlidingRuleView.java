package com.example.slidingruleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.text.DecimalFormat;


/**
 * Describe :
 * Created by Knight on 2018/12/27
 * 点滴之行,看世界
 **/
public class SlidingRuleView extends View {

    //文字画笔
    private Paint paint;
    //文字足够长 超过屏幕显示宽度 方便后面看滑动效果
    private String currentNum = "1234sdddddddddd423ddddddddddddddddddddddddddddsdddddddddddd1";

    /**
     *
     * 用于滑动实例
     * @param context
     *
     */
    private Scroller mScroller;
    /**
     * 最小移动距离
     */
    private int mTouchMinDistance;
    /**
     * 界面可滚动的左右边界
     */
    private float leftBorder;
    private float rightBorder;

    /**
     * 手指最开始触摸屏幕的X坐标
     *
     */
    private float mXDown;

    /**
     *
     * 当前手指移动结束后停下来的X坐标
     *
     */
    private float mCurrentMoveX;

    /**
     * 上次触发移动事件的坐标
     *
     */
    private float mLastMoveX;
    /**
     * 长刻度线长度
     */
    private float longDegreeLine;
    private float shortDegreeLine;

    /**
     *
     * 刻度线的颜色
     */
    private int lineDegreeColor;
    /**
     * 刻度顶部线
     */
    private float topDegreeLine;
    /**
     * 刻度的间隔
     */
    private float lineDegreeSpace;
    /**
     * 刻度大数目
     *
     */
    private int lineCount;

    /**
     * 左右边界距离
     */
    private float ruleLeftSpacing;
    private float ruleRightSpacing;
    /**
     * 数字颜色
     *
     */
    private int numberColor;
    /**
     * 数字文字大小
     */
    private float numberSize;
    /**
     *
     * 绿色指针粗细
     */
    private float greenPointWidth;
    /**
     * 绿色指针颜色
     */
    private int greenPointColor;

    /**
     * 绿色指针X坐标
     *
     */
    private int greenPointX;
    /**
     * 当前刻度值的大小
     */
    private float currentNumberSize;
    /**
     * 当前刻度值的颜色
     */
    private int currentNumberColor;
    /**
     * 格式化数字
     */
    private DecimalFormat df;
    /**
     * 监控手势速度类
     */
    private VelocityTracker mVelocityTracker;
    //惯性最大最小速度
    protected int mMaximumVelocity, mMinimumVelocity;
    public SlidingRuleView(Context context) {
        this(context,null);

    }

    public SlidingRuleView(Context context,  AttributeSet attrs) {
        this(context, attrs,0);
    }


    public SlidingRuleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化一些参数
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable
                .SlidingRuleView);

        //刻度线的颜色
        lineDegreeColor = typedArray.getColor(R.styleable.SlidingRuleView_lineDegreeColor, Color.LTGRAY);
        //顶部的直线距离View顶部距离
        topDegreeLine = typedArray.getDimension(R.styleable.SlidingRuleView_topDegreeLine, SystemUtil.dp2px(getContext(),45));
        //刻度间隔
        lineDegreeSpace = typedArray.getDimension(R.styleable.SlidingRuleView_lineDegreeSpace, SystemUtil.dp2px(getContext(),10));
        //刻度大数目 默认30
        lineCount = typedArray.getInt(R.styleable.SlidingRuleView_lineCount, 30);
        //长的刻度线条长度
        longDegreeLine = typedArray.getDimension(R.styleable.SlidingRuleView_longDegreeLine, SystemUtil.dp2px(getContext(),35));
        //左间隔
        ruleLeftSpacing = typedArray.getDimension(R.styleable.SlidingRuleView_ruleLeftSpacing, SystemUtil.dp2px(getContext(),5));
        //右间隔
        ruleRightSpacing = typedArray.getDimension(R.styleable.SlidingRuleView_ruleRightSpacing, SystemUtil.dp2px(getContext(),5));
        //数字颜色
        numberColor = typedArray.getColor(R.styleable.SlidingRuleView_numberColor, Color.BLACK);
        //数字大小
        numberSize = typedArray.getDimension(R.styleable.SlidingRuleView_numberSize, SystemUtil.dp2px(getContext(),15));
        //短刻度值的长度
        shortDegreeLine = typedArray.getDimension(R.styleable.SlidingRuleView_shortDegreeLine, SystemUtil.dp2px(getContext(),20));
        //绿色指针粗细
        greenPointWidth = typedArray.getDimension(R.styleable.SlidingRuleView_greenPointWidth, SystemUtil.dp2px(getContext(),4));
        //绿色指针颜色
        greenPointColor = typedArray.getColor(R.styleable.SlidingRuleView_greenPointColor, 0xFF4FBA75);
        //当前刻度的颜色
        currentNumberColor = typedArray.getColor(R.styleable.SlidingRuleView_currentNumberColor,
                0xFF4FBA75);
        //当前刻度的大小
        currentNumberSize = typedArray.getDimension(R.styleable.SlidingRuleView_currentNumberSize,
                SystemUtil.dp2px(getContext(),30));
        init(context);
        typedArray.recycle();
    }


    private void init(Context context){
        //添加速度追踪器
        mVelocityTracker = VelocityTracker.obtain();
        //获取最大速度
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        //获取最小速度
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();
        //初始化画笔 抗锯齿
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //数字小数点一位
        df = new DecimalFormat("0.0");
        //创建滑动实例
        mScroller = new Scroller(context);
        //第一步，获取Android常量距离对象，这个类有UI中所使用到的标准常量，像超时，尺寸，距离
        ViewConfiguration configuration = ViewConfiguration.get(context);
        //获取最小移动距离
        mTouchMinDistance = configuration.getScaledTouchSlop();
        //增加左边界距离
        leftBorder = ruleLeftSpacing;
        //确定刻顶部度长线右边界 格数 * 之间的间隔 * 大数目（间隔）之间是有10小间隔的
        rightBorder = lineDegreeSpace * lineCount * 10+ ruleLeftSpacing + ruleRightSpacing;
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
            setMeasuredDimension(widthSpecSize, SystemUtil.dp2px(getContext(),120));
        } else {
            setMeasuredDimension(widthSpecSize, heightSpecSize);
        }
        //这里获取View的高度 方便后面绘制算一些坐标
        //height = getMeasuredHeight();
        //绿色指针的x坐标
        greenPointX =getMeasuredWidth() / 2;


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //确定顶部长线的左端
        float x = leftBorder;
        //确定顶部长线
        float y = topDegreeLine;
        //设置画笔颜色
        paint.setColor(lineDegreeColor);
        //设置刻度线宽度
        paint.setStrokeWidth(3);
        canvas.drawLine(x, y, rightBorder - ruleRightSpacing, y, paint);
        //循环绘制
        for(int i = 0;i <= lineCount * 10;i++){
            //画长刻度
            if(i % 10 == 0){
                paint.setColor(lineDegreeColor);
                paint.setStrokeWidth(5);
                canvas.drawLine(x, y, x, y + longDegreeLine, paint);

                //画刻度值
                String number = String.valueOf(i / 10);
                //得到文字宽度
                float textWidth = paint.measureText(number);
                //绘制颜色
                paint.setColor(numberColor);
                //绘制文字大小
                paint.setTextSize(numberSize);
                paint.setStrokeWidth(1);
                canvas.drawText(number, x - textWidth / 2, y + longDegreeLine + SystemUtil.dp2px(getContext(),25), paint);
            }else {
                //画短刻度
                paint.setColor(lineDegreeColor);
                paint.setStrokeWidth(3);
                canvas.drawLine(x, y, x, y + shortDegreeLine, paint);
            }
            x += lineDegreeSpace;
        }

        //画指针
        paint.setColor(greenPointColor);
        paint.setStrokeWidth(greenPointWidth);
        canvas.drawLine(greenPointX + getScrollX(), y, greenPointX + getScrollX(), y + longDegreeLine + SystemUtil.dp2px(getContext(),3),
                paint);

        //绘制当前刻度值
        //画当前刻度值
        paint.setColor(currentNumberColor);
        //设置大小
        paint.setTextSize(currentNumberSize);
        //确定数字的值。用移动多少来确定
        currentNum = df.format((greenPointX + getScrollX() - leftBorder) / (lineDegreeSpace * 10.0f));
        //测量数字宽度
        float textWidth = paint.measureText(currentNum);
        canvas.drawText(currentNum, greenPointX - textWidth / 2 + getScrollX(), topDegreeLine - SystemUtil.dp2px(getContext(),15), paint);

        //画kg 大小是刻度值的3分之一
        paint.setTextSize(currentNumberSize / 3);
        canvas.drawText("kg", greenPointX + textWidth / 2 + getScrollX() + SystemUtil.dp2px(getContext(),3), topDegreeLine - SystemUtil.dp2px(getContext(),30), paint);
    }

    //        //得到文字的字体属性和测量
//        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
//        //文字设置在View的中间
//        float y = height / 2 + (Math.abs(fontMetrics.ascent) + fontMetrics.descent) / 2;
//        //canvas绘制文本
//        canvas.drawText(currentNum, 0,y, paint);
//        //得到左右边界
//        leftBorder = getLeft();
//        rightBorder = (int)paint.measureText(currentNum);


    //                //如果右滑时 内容左边界超过初始化时候的左边界 就还是初始化时候的状态
//                if(getScrollX() + scrolledX < leftBorder - getWidth() / 2){
//                    scrollTo((int)(- getWidth() / 2 +leftBorder),0);
//                    return true;
//                }
//                //同理 如果左滑  这里判断右边界
//                else if(getScrollX() + getWidth() / 2 + scrolledX > rightBorder){
//                    scrollTo((int)(rightBorder - getWidth() /2 - ruleRightSpacing),0);
//                    return true;
//                }else{
//
//                    //左右边界中 自由滑动
//                    scrollBy(scrolledX,0);
//                }
    @Override
    public boolean onTouchEvent(MotionEvent ev){
        mVelocityTracker.addMovement(ev);
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                //记录初始触摸屏幕下的坐标
                mXDown = ev.getRawX();
                mLastMoveX = mXDown;
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentMoveX = ev.getRawX();
                //本次的滑动距离
                int scrolledX = (int) (mLastMoveX - mCurrentMoveX);
                //左右边界中 自由滑动
                scrollBy(scrolledX,0);
                mLastMoveX = mCurrentMoveX;
                break;
            case MotionEvent.ACTION_UP:
                //处理松手后的Fling 获取当前事件的速率，1毫秒运动了多少个像素的速率，1000表示一秒
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                //获取横向速率
                int velocityX = (int) mVelocityTracker.getXVelocity();
                //滑动速度大于最小速度 就滑动
                if (Math.abs(velocityX) > mMinimumVelocity) {
                    fling(-velocityX);
                }
                //刻度之间检测
                moveRecently();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
        }
        return true;
    }




    private void moveRecently(){
        float distance = (greenPointX + getScrollX() - leftBorder) % lineDegreeSpace;
        //指针的位置在小刻度中间位置往后（右）
        if (distance >= lineDegreeSpace / 2) {
            scrollBy((int) (lineDegreeSpace - distance), 0);
        } else {
            scrollBy((int) (-distance), 0);
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }

    private void fling(int vX) {
        mScroller.fling(getScrollX(), 0, vX, 0,(int)(- rightBorder),  (int)rightBorder, 0, 0);
    }
    @Override
    public void computeScroll() {
        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //这是最后mScroller的最后一次滑动 进行刻度边界检测
            if(!mScroller.computeScrollOffset()){
                moveRecently();
            }

        }

    }

    //重写滑动方法，设置到边界的时候不滑,并显示边缘效果。滑动完输出刻度。
    @Override
    public void scrollTo( int x, int y) {
        //左边界检测
        if (x <  leftBorder - getWidth() / 2) {
            x = (int)(- getWidth() / 2 +leftBorder);
        }
        //有边界检测
        if (x + getWidth() / 2> rightBorder) {
            x = (int)(rightBorder - getWidth() /2 - ruleRightSpacing);
        }
        if (x != getScrollX()) {

            super.scrollTo(x, y);
        }

    }

}





