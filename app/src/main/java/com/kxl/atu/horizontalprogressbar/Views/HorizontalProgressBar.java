package com.kxl.atu.horizontalprogressbar.Views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.icu.text.DecimalFormat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import static android.R.attr.mode;

/**
 * Created by atu on 2017/6/5.
 *      a:进度条有动画效果
 *      b:进度条上面有百分比样式的绘制
 *      c:百分比tip框跟随进度条移动需要注意的事项
 */

public class HorizontalProgressBar extends View {

    //tip框的高度
    private int tipHeight;

    //进度条margintop
    private int progressMarginTop;

    //背景色画笔
    private Paint bgPaint;

    //当前进度
    private float currentProgress;

    //进度
    private float mProgress;

    //进度画笔
    private Paint progressPaint;

    //进度条画笔的宽度
    private int progressPaintWidth;

    //tip框的宽度
    private int tipWidth;

    //提示框画笔的宽度
    private int tipPaintWidth;

    //三角形的高度
    private int triangleHeight;

    //画笔背景色
    private int bgColor = 0xFFe1e5e8;

    //画笔进度色
    private int progressColor = 0xFFf66b12;

    //提示框画笔颜色
    private Paint tipPaint;

    //文字画笔
    private Paint textPaint;

    //百分比文字字体大小
    private float textPaintSize;

    //view的真实高度
    private int mViewHeight;
    private int mHeight;
    private int mWidth;

    //绘制tip框的矩形
    private RectF receF = new RectF();

    //圆角矩形的圆角半径
    private int roundRectRadius;

    //进度移动的距离
    private float moveDis;

    //画三角形的path
    private Path path = new Path();

    //文字内容
    private String textString = "0";
    private Rect textRect = new Rect();

    private ValueAnimator progressAnimation;
    //动画执行时间
    private long duration = 1000;
    //动画延迟启动时间
    private long startDelay = 500;
    //进度监听回调
    private ProgressListener progressListener;

    public interface ProgressListener{
        void currentProgressListener(float currentProgress);
    }
    public HorizontalProgressBar setProgressListener(ProgressListener listener){
        progressListener = listener;
        return this;
    }

    public HorizontalProgressBar(Context context) {
        this(context,null);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        bgPaint = getPaint(progressPaintWidth,bgColor,Paint.Style.STROKE);
        progressPaint = getPaint(progressPaintWidth,progressColor, Paint.Style.STROKE);
        tipPaint = getPaint(tipPaintWidth,progressColor, Paint.Style.FILL);

        initTextPaint();
    }

    /**
     * 初始化文字画笔
     */
    private void initTextPaint() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textPaintSize);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    /**
     * 统一处理paint
     *
     * @param progressPaintWidth
     * @param bgColor
     * @param stroke
     * @return
     */
    private Paint getPaint(int progressPaintWidth, int bgColor, Paint.Style stroke) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(progressPaintWidth);
        paint.setColor(bgColor);
        paint.setStyle(stroke);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);

        return paint;
    }

    /**
     * 初始化画笔宽度及view大小
     */
    private void init() {
        progressPaintWidth = dp2px(4);
        tipHeight = dp2px(15);
        tipWidth = dp2px(30);
        tipPaintWidth = dp2px(1);
        progressMarginTop = dp2px(8);
        triangleHeight = dp2px(3);

        //真实的view高度
        mViewHeight = tipHeight + tipPaintWidth + triangleHeight + progressPaintWidth + progressMarginTop;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景色
        canvas.drawLine(getPaddingLeft(),tipHeight + progressMarginTop,
                getWidth(),tipHeight + progressMarginTop,bgPaint);
        //绘制真是进度
        canvas.drawLine(getPaddingLeft(),tipHeight + progressMarginTop,
                currentProgress,tipHeight + progressMarginTop,
                progressPaint);
        //绘制tip
        drawTipView(canvas);
        //绘制文字
        drawText(canvas,textString);
    }

    private void drawText(Canvas canvas, String string) {
        textRect.left = (int) moveDis;
        textRect.top = 0;
        textRect.right = (int) (tipWidth+moveDis);
        textRect.bottom = tipHeight;
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        //文字绘制到整个布局的中心位置
        canvas.drawText(textString + "%", textRect.centerX(), baseline, textPaint);
    }

    /**
     * 绘制进度上面的百分比提示view
     * @param canvas
     */
    private void drawTipView(Canvas canvas) {
        drawRoundRect(canvas);
        drawTriangle(canvas);
    }

    /**
     * 画三角形
     * @param canvas
     */
    private void drawTriangle(Canvas canvas) {
        path.moveTo(tipWidth/2 - triangleHeight + moveDis,triangleHeight);
        path.lineTo(tipWidth / 2 + moveDis, tipHeight + triangleHeight);
        path.lineTo(tipWidth / 2 + triangleHeight + moveDis, tipHeight);
        canvas.drawPath(path, tipPaint);
        path.reset();
    }

    /**
     * 画圆角矩形
     * @param canvas
     */
    private void drawRoundRect(Canvas canvas) {
        receF.set(moveDis,0,tipWidth + moveDis,tipHeight);
        canvas.drawRoundRect(receF,roundRectRadius,roundRectRadius,tipPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(measureWidth(widthMode,width),measureHeight(heightMode,height));
    }

    /**
     * 测量高度
     * @param mode
     * @param height
     * @return
     */
    private int measureHeight(int mode, int height) {
        switch (mode){
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                mHeight = mViewHeight;
                break;
            case MeasureSpec.EXACTLY:
                mHeight = height;
                break;
        }
        return mHeight;
    }

    /**
     * 测量宽度
     * @param mnode
     * @param width
     * @return
     */
    private int measureWidth(int mnode, int width) {
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                mWidth = width;
                break;
        }
        return mWidth;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public HorizontalProgressBar setProgress(float progress){
        mProgress = progress;
        initAnimation();
        return this;
    }


    /**
     * 进度动画，通过插值的方式改变移动的距离
     */
    private void initAnimation() {
        progressAnimation = ValueAnimator.ofFloat(0,mProgress);
        progressAnimation.setDuration(duration);
        progressAnimation.setStartDelay(startDelay);
        progressAnimation.setInterpolator(new LinearInterpolator());
        progressAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                //进度数值只显示整数
                textString = formatNum(format2Int(value));
                //把当前百分比进度转换为view宽度对应的比例
                currentProgress = value * mWidth / 100;
                if(null != progressListener){
                    progressListener.currentProgressListener(currentProgress);
                }
                if (currentProgress>=(tipWidth/2) &&
                        currentProgress<=(mWidth - tipWidth/2)){
                    moveDis = currentProgress - tipWidth/2;
                }
                invalidate();
            }

        });
        progressAnimation.start();
    }

    /**
     * 开始动画
     */
    public void startProgressAnimation(){
        if (progressAnimation != null &&
                !progressAnimation.isRunning() &&
                !progressAnimation.isStarted()){
            progressAnimation.start();
        }
    }

    /**
     * 暂停动画
     */
    public void pauseProgressAnimation(){
        if (progressAnimation != null ){
            progressAnimation.pause();
        }
    }

    /**
     * 结束动画
     */
    public void stopProgressAnimation(){
        if (progressAnimation != null ){
            progressAnimation.end();
        }
    }

    /**
     * 恢复动画
     */
    public void resumeProgressAnimation(){
        if (progressAnimation != null ){
            progressAnimation.resume();
        }
    }

    /**
     * 格式化数字（保留一位小数）
     * @param anInt
     * @return
     */
    private String formatNum(int anInt) {
        DecimalFormat decimalFormat = new DecimalFormat("0");
        return decimalFormat.format(anInt);
    }

    /**
     * dp 2 px
     * @param dpVal
     * @return
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal,getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal,getResources().getDisplayMetrics());
    }

    public static int format2Int(double i){
        return (int) i;
    }
}
