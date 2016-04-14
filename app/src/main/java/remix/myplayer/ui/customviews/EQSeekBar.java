package remix.myplayer.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import remix.myplayer.R;
import remix.myplayer.utils.DensityUtil;

/**
 * Created by taeja on 16-4-13.
 */
public class EQSeekBar extends View {
    public interface OnSeekBarChangeListener{
        void onProgressChanged(EQSeekBar seekBar, int position, boolean fromUser);
        void onStartTrackingTouch(EQSeekBar seekBar);
        void onStopTrackingTouch(EQSeekBar seekBar);
    }

    private OnSeekBarChangeListener mOnSeekBarChangeListener;
    private static final String TAG = "CustomSeekBar";
    private Context mContext;
    /**
     * 控件高度与宽度
     */
    private int mViewWidth;
    private int mViewHeight;



    /**
     * 底部提示字体画笔
     */
    private Paint mTipTextPaint;

    /**
     * 底部提示字体大小
     */
    private int mTipTextSize = 12;

    /**
     * 顶部提示字体大小
     */
    private int mFreTextSize = 12;

    /**
     * 顶部提示字体画笔
     */
    private Paint mFreTextPaint;

    /**
     * 轨道垂直中心点
     */
    private int mTrackCenterY;

    /**
     * 使能状态下轨道的颜色
     */
    private int mEnableTrackColor;

    /**
     * 非使能状态下轨道颜色
     */
    private int mTrackColor;

    /**
     * 轨道画笔
     */
    private Paint mTrackPaint;

    /**
     * 使能状态下已完成轨道的颜色
     */
    private int mEnableProgressColor;

    /**
     * 非使能状态下已完成轨道颜色
     */
    private int  mProgressColor;

    /**
     * 已完成轨道的画笔
     */
    private Paint mProgressPaint;

    /**
     * 轨道的高度与长度
     */
    private int mTrackHeigh;
    private int mTrackWidth;

    /**
     * 总共几个小圆点
     */
    private int mDotNum;

    /**
     * 两个小圆点之间的距离
     */
    private int mDotBetween;

    /**
     * 所有小圆点的坐标
     */
    private ArrayList<Integer> mDotPosition  = new ArrayList<>();

    /**
     * Thumb的高度与宽度
     */
    private int mThumbWidth = 50;
    private int mThumbHeight = 50;

    /**
     * ThumbDrawable 以及两个状态
     */
    private Drawable mThumbDrawable = null;
    private int[] mThumbNormal = null;
    private int[] mThumbPressed = null;

    /**
     * Thumb所在位置
     */
    private int mThumbCenterX;
    private int mThumbCenterY;

    /**
     * 是否初始化完成
     */
    private boolean mInit = false;

    /**
     * 当前索引
     */
    private int mPositon;

    /**
     * 底部提示文字
     *
     */
    private String mFreText = "100";

    /**
     * 顶部提示文字
     */
    private String mDBText = "0";

    /**
     * DB值
     */
    private int mDB = 0;

    /**
     * 当前进度
     */
    private int mProgress = 0;

    /**
     * 进度最大值
     */
    private int mMax = 3000;

    /**
     * 是否能够拖动
     */
    private boolean mCanDrag = true;

    public EQSeekBar(Context context) {
        super(context);
        init(context,null);

    }

    public EQSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public EQSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attributeSet){
        mInit = false;
        mContext = context;
        TypedArray typedArray = mContext.obtainStyledAttributes(attributeSet,R.styleable.EQSeekBar);
        //初始化thumbdrawable及其状态
        mThumbDrawable = typedArray.getDrawable(R.styleable.EQSeekBar_eqthumb);
        if(mThumbDrawable == null)
            mThumbDrawable = getResources().getDrawable(R.drawable.bg_thumb);
        mThumbNormal = new int[]{-android.R.attr.state_focused, -android.R.attr.state_pressed,
                -android.R.attr.state_selected, -android.R.attr.state_checked};
        mThumbPressed = new int[]{android.R.attr.state_focused, android.R.attr.state_pressed,
                android.R.attr.state_selected, android.R.attr.state_checked};

        //计算thumb的大小
        mThumbHeight = mThumbDrawable.getIntrinsicHeight();
        mThumbWidth = mThumbDrawable.getIntrinsicWidth();
        mThumbCenterX = mThumbHeight / 2;
        mTrackCenterY = mThumbHeight / 2;


        //使能状态轨道颜色与已完成轨道的颜色
        mEnableTrackColor = typedArray.getColor(R.styleable.EQSeekBar_eqenabletrackcolor, Color.parseColor("#6c6a6c"));
        mEnableProgressColor = typedArray.getColor(R.styleable.EQSeekBar_eqenableprogresscolor, Color.parseColor("#782899"));

        //非使能状态轨道颜色与已完成轨道的颜色
        mTrackColor = typedArray.getColor(R.styleable.EQSeekBar_eqtrackcolor,Color.parseColor("#6c6a6c"));
        mProgressColor = typedArray.getColor(R.styleable.EQSeekBar_eqprogresscolor, Color.parseColor("#FFC125"));

        //间隔点数量
        mDotNum = typedArray.getInteger(R.styleable.EQSeekBar_eqdotnum,31);

        //轨道宽度
        mTrackWidth = (int)typedArray.getDimension(R.styleable.EQSeekBar_eqtrackwidth, DensityUtil.dip2px(mContext,4));

        //顶部提示文字画笔
        mFreTextSize = DensityUtil.dip2px(getContext(),13);
        mFreTextPaint = new Paint();
        mFreTextPaint.setAntiAlias(true);
//        mFreTextPaint.setColor(Color.parseColor("#ffffffff"));
        mFreTextPaint.setColor(Color.parseColor("#1b1c19"));
        mFreTextPaint.setStyle(Paint.Style.STROKE);
        mFreTextPaint.setTextSize(mFreTextSize);
        mFreTextPaint.setTextAlign(Paint.Align.CENTER);

        //底部提示文字画笔
        mTipTextSize = DensityUtil.dip2px(getContext(),14);
        mTipTextPaint = new Paint();
        mTipTextPaint.setAntiAlias(true);
//        mTipTextPaint.setColor(Color.parseColor("#ffffffff"));
        mTipTextPaint.setColor(Color.parseColor("#1b1c19"));
        mTipTextPaint.setStyle(Paint.Style.STROKE);
        mTipTextPaint.setTextSize(DensityUtil.dip2px(getContext(),mTipTextSize));
        mTipTextPaint.setTextAlign(Paint.Align.CENTER);

        //整个轨道的画笔
        mTrackPaint = new Paint();
        mTrackPaint.setAntiAlias(true);
        mTrackPaint.setColor(mEnableTrackColor);
        mTrackPaint.setStyle(Paint.Style.STROKE);
        mTrackPaint.setStrokeWidth(mTrackWidth);

        //已完成轨道的画笔
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mTrackWidth);

        typedArray.recycle();
    }

    public void setFreText(String freText){
        mFreText = freText;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //整个轨道
        canvas.drawLine((mViewWidth - 0) / 2 , mThumbHeight, (mViewWidth - 0) / 2, mThumbHeight + mTrackHeigh, mTrackPaint);
        //已完成轨道
        canvas.drawLine((mViewWidth - 0) / 2, mThumbHeight, (mViewWidth - 0) / 2, mThumbCenterY, mProgressPaint);

//      //顶部与底部文字
        canvas.drawText(mDBText,mViewWidth / 2, mTipTextSize ,mFreTextPaint);
        canvas.drawText(mFreText, mViewWidth / 2, mFreTextSize +  mThumbHeight + mTrackHeigh, mFreTextPaint);

        //thumb
        mThumbDrawable.setBounds((mViewWidth - mThumbWidth) / 2, mThumbCenterY - mThumbWidth / 2, (mViewWidth - mThumbWidth) / 2 + mThumbWidth , mThumbCenterY + mThumbWidth / 2);
        mThumbDrawable.draw(canvas);
    }

    private void seekTo(int eventY,boolean isUp){
        if(!mCanDrag)
            return;

        //设置thumb状态
        mThumbDrawable.setState(isUp ? mThumbNormal : mThumbPressed);
        if(eventY > mDotPosition.get(mDotPosition.size() - 1) || eventY < mThumbHeight){
            invalidate();
            return;
        }

        int temp = Integer.MAX_VALUE;
        for(int i = 0 ; i < mDotPosition.size() ;i++){
            if(Math.abs(mDotPosition.get(i) - eventY) < temp){
                mPositon = i;
                temp = Math.abs(mDotPosition.get(i) - eventY);
            }
        }
        //寻找与当前触摸点最近的值
        if(isUp){

            mThumbCenterY = mDotPosition.get(mPositon);
        } else {
            mThumbCenterY = eventY;
        }

        //计算progress
        mProgress = (int)(1.0 *  (eventY - mThumbHeight) / mTrackHeigh * mMax);

        if(mOnSeekBarChangeListener != null)
            mOnSeekBarChangeListener.onProgressChanged(this,mPositon,true);

        //确定DB值
        mDB = 15 - mPositon;

        if(mDB > 0)
            mDBText = "+" + mDB;
        else if(mDB < 0)
            mDBText = "-" + mDB;
        else
            mDBText = "0";

        Log.d(TAG,"DB: " + mDB);

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG,"EventY:" + event.getY());
        int eventY = (int)event.getY();
        seekTo(eventY,event.getAction() == MotionEvent.ACTION_UP);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        if((mViewWidth = getMeasuredWidth()) > 0 && (mViewHeight = getMeasuredHeight()) > 0){
            int paddingtop = getPaddingTop();
            int paddingbottom = getPaddingBottom();
            mTrackHeigh = mViewHeight - paddingtop - paddingbottom - mThumbHeight * 2;
            //计算轨道宽度 两个小圆点之间的距离
            mDotBetween = mTrackHeigh / (mDotNum - 1);
            mDotPosition.clear();
            //设置所有小圆点的坐标
            for(int i = 0 ; i < mDotNum ; i++){
                mDotPosition.add(mThumbWidth + mDotBetween * i);
            }
            mThumbCenterY = mDotPosition.get(mPositon);
            mInit = true;
        }
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l){
        mOnSeekBarChangeListener = l;
    }

    public void setMax(int max){
        mMax = max;
    }
    public int getMax(){
        return mMax;
    }

    public void setProgress(int progress){
        if(progress > mMax)
            progress = mMax;
        if(progress < 0)
            progress = 0;
        mProgress = progress;
        int eventY = (int)((1.0 * mProgress / mMax) * mTrackHeigh + mThumbHeight);
        seekTo(eventY,true);
        //mThumbCenterY
//        int temp = Integer.MAX_VALUE;
//        for(int i = 0 ; i < mDotPosition.size() ;i++){
//            if(Math.abs(mDotPosition.get(i) - eventY) < temp){
//                mPositon = i;
//                temp = Math.abs(mDotPosition.get(i) - eventY);
//            }
//        }
    }
    public int getProgress(){
        return mProgress;
    }

    public void setDrag(boolean canDrag){
        mCanDrag = canDrag;
    }

    public boolean canDrag(){
        return mCanDrag;
    }

    public void setProgressColor(int color){
        mProgressColor = color;
        if(mProgressPaint != null) {
            mProgressPaint.setColor(mProgressColor);
            invalidate();
        }
    }

    public void setTrackColor(int color){
        mEnableTrackColor = color;
        if(mTrackPaint != null) {
            mTrackPaint.setColor(color);
            invalidate();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        mCanDrag = enabled;
        mProgressPaint.setColor(enabled ? mEnableProgressColor : mProgressColor);
        mTrackPaint.setColor(enabled ? mEnableTrackColor : mTrackColor);
        invalidate();
    }

    //是否初始化完成
    public boolean isInit(){
        return mInit;
    }
}

