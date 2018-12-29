package com.example.slidingruleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.text.DecimalFormat;

/**
 * @author yongling
 * @date 2017/10/16
 */

public class HorizontalRulerView extends View {

    private Paint paint;
    private Scroller mScroller;
    private DecimalFormat df;
    /**
     * 当前刻度值
     */
    private String currentNum;
    /**
     * 手指上次所在坐标
     */
    private int lastX;
    /**
     * 刻度数量
     */
    private int count;
    /**
     * 界面可滚动的左边界
     */
    private float leftBorder;
    private float rightBorder;
    /**
     * 左右边界距离
     */
    private float leftSpacing;
    private float rightSpacing;

    /**
     * 刻度间隔
     */
    private float spacing;

    /**
     * 指针坐标
     */
    private int pointerX;

    /**
     * 指针粗细
     */
    private float pointerWidth;
    /**
     * 刻度顶部线
     */
    private float topLine;
    /**
     * 刻度线长度
     */
    private float longLine;
    private float shortLine;
    /**
     * 刻度文字大小
     */
    private float numSize;
    private float currentNumSize;

    private int lineColor;
    private int numColor;
    private int pointerColor;
    private int currentNumColor;


    public HorizontalRulerView(Context context) {
        this(context, null);
    }

    public HorizontalRulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalRulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化一些参数
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable
                .HorizontalRulerView);
        //长的刻度线条
        longLine = typedArray.getDimension(R.styleable.HorizontalRulerView_longLine, 100);
        //
        shortLine = typedArray.getDimension(R.styleable.HorizontalRulerView_shortLine, 60);
        numSize = typedArray.getDimension(R.styleable.HorizontalRulerView_numSize, 40);
        currentNumSize = typedArray.getDimension(R.styleable.HorizontalRulerView_currentNumSize,
                80);

        leftSpacing = typedArray.getDimension(R.styleable.HorizontalRulerView_leftSpacing, 40);
        rightSpacing = typedArray.getDimension(R.styleable.HorizontalRulerView_rightSpacing, 40);

        count = typedArray.getInt(R.styleable.HorizontalRulerView_count, 20);
        spacing = typedArray.getDimension(R.styleable.HorizontalRulerView_spacing, 40);
        topLine = typedArray.getDimension(R.styleable.HorizontalRulerView_topLine, 120);
        pointerWidth = typedArray.getDimension(R.styleable.HorizontalRulerView_pointerWidth, 10);

        lineColor = typedArray.getColor(R.styleable.HorizontalRulerView_lineColor, Color.LTGRAY);
        numColor = typedArray.getColor(R.styleable.HorizontalRulerView_numColor, Color.BLACK);
        pointerColor = typedArray.getColor(R.styleable.HorizontalRulerView_pointerColor,
                0xFF4FBA75);
        currentNumColor = typedArray.getColor(R.styleable.HorizontalRulerView_currentNumColor,
                0xFF4FBA75);

        if (getBackground() == null) {
            setBackgroundColor(0xFFF6F9F6);
        }

        typedArray.recycle();

        init(context);
    }

    private void init(Context context) {

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScroller = new Scroller(context);
        df = new DecimalFormat("0.0");

        leftBorder = leftSpacing;
        rightBorder = spacing * count * 10 + leftSpacing + rightSpacing;

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        pointerX = getMeasuredWidth() / 2;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int minHeight = (int) (topLine + longLine + 120);
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, minHeight);
        } else {
            setMeasuredDimension(widthSpecSize, heightSpecSize);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        float x = leftBorder;
        float y = topLine;

        //画顶部线
        paint.setColor(lineColor);
        paint.setStrokeWidth(3);
        canvas.drawLine(x, y, rightBorder - rightSpacing, y, paint);

        for (int i = 0; i <= count * 10; i++) {
            if (i % 10 == 0) {
                //画长刻度
                paint.setColor(lineColor);
                paint.setStrokeWidth(5);
                canvas.drawLine(x, y, x, y + longLine, paint);

                //画刻度值
                String num = String.valueOf(i / 10);
                float textWidth = paint.measureText(num);
                paint.setColor(numColor);
                paint.setTextSize(numSize);
                paint.setStrokeWidth(1);
                canvas.drawText(num, x - textWidth / 2, y + longLine + 80, paint);
            } else {
                //画短刻度
                paint.setColor(lineColor);
                paint.setStrokeWidth(3);
                canvas.drawLine(x, y, x, y + shortLine, paint);
            }
            x += spacing;
        }
        //画指针
        paint.setColor(pointerColor);
        paint.setStrokeWidth(pointerWidth);
        canvas.drawLine(pointerX + getScrollX(), y, pointerX + getScrollX(), y + longLine + 20,
                paint);

        //画当前刻度值
        paint.setColor(currentNumColor);
        paint.setTextSize(currentNumSize);
        currentNum = df.format((pointerX + getScrollX() - leftBorder) / (spacing * 10.0f));
        float textWidth = paint.measureText(currentNum);
        canvas.drawText(currentNum, pointerX - textWidth / 2 + getScrollX(), topLine - 40, paint);

        //画kg
        paint.setTextSize(currentNumSize / 3);
        canvas.drawText("kg", pointerX + textWidth / 2 + getScrollX() + 8, topLine - 80, paint);

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (lastX - event.getX());
                //滚动至左右边界
                if (getScrollX() + dx < leftBorder - getWidth() / 2) {
                    scrollTo((int) (-getWidth() / 2 + leftSpacing), 0);
                    return true;
                } else if (getScrollX() + getWidth() / 2 + dx + rightSpacing > rightBorder) {
                    scrollTo((int) (rightBorder - getWidth() / 2 - rightSpacing), 0);
                    return true;
                }
                scrollBy(dx, 0);
                lastX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                resizePointer();
                break;
            default:
                break;
        }


        return true;
    }

    /**
     * 移动至最近刻度线
     */
    private void resizePointer() {
        float offset = (pointerX + getScrollX() - leftBorder) % spacing;
        if (offset >= spacing / 2) {
            scrollBy((int) (spacing - offset), 0);
        } else {
            scrollBy((int) (-offset), 0);
        }
    }

    @Override
    public void computeScroll() {
        //  完成平滑滚动
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}
