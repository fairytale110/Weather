package com.example.weatherdemo.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.weatherdemo.R
import com.example.weatherdemo.bean.Weather
import com.example.weatherdemo.utils.fix
import com.example.weatherdemo.utils.weatherDayOfMonth
import com.example.weatherdemo.utils.weatherHour
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * TemperatureView
 *
 * line chart custom
 */
open class TemperatureView : View {
    private val TAG: String = "TemperatureView";
    private val MAX_TEMPERATURE_LINES = 5;
    private val MIN_HOUR_LINE_SPACE = 80F;
    private val TEMPERATURE_OFFSET = 5F;

    private val CORD_BASE_LINE_PADDING = 40F;
    private val CORD_BASE_LINE_WIDTH = 1.5F;
    private val CORD_LINE_WITH = 1F;

    private val CORD_BASE_LINE_COLOR = Color.BLACK;
    private val CORD_LINE_COLOR = Color.LTGRAY;
    private val DATA_LINE_COLOR = Color.BLUE;

    private val VIEW_SCALE_MIN = 2F;
    private val VIEW_SCALE_MAX = 5F;
    private val VIEW_SCALE_SETP = 0.1F;
    private val VIEW_SCALE_TRIGGER_OFFSET = 8F; // 触发缩放效果的触摸偏移

    private var mViewPadding: Float = 10.0F; // 图表绘制边界

    private var mTemperatureLineSpace: Float = 10.0F; // 温度水平线差值

    private var mTemperatureLines: Int = MAX_TEMPERATURE_LINES; // 温度水平参考线数量
    private var mHourLineWidth: Float = MIN_HOUR_LINE_SPACE; // 时间刻度间距

    private var mTemperatureMaxValue: Float = 50.0F; // 温度水平线差值
    private var mTemperatureMinValue: Float = -50.0F; // 温度水平线差值

    private var dotScale = VIEW_SCALE_MAX / 2.5F; // 缩放比例

    private val mTemperatureValue: MutableList<Double> = mutableListOf();
    private val mHourlyValue: MutableList<String> = mutableListOf();

    private val mPaintCordLine: Paint = Paint();
    private val mPaintCordValue: Paint = Paint();
    private val mPaintDataLine: Paint = Paint();

    private var xPoint1Start = 0F;
    private var xPoint1End = 0F;
    private var xPoint2Start = 0F;
    private var xPoint2End = 0F;
    private var xTouchDistance = 0F;
    private var xScaleDistance = 0F;

    private var mIsScrollToStart = false;
    private var mIsScrollToEnd = false;

    private var mScrollDistance = 0F
    private var mLastMovePos = 0F

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView();
    }

    private fun initView() {
        log("initView");
        setBackgroundColor(context.getColor(R.color.gray_item_background));

        mPaintCordLine.style = Paint.Style.STROKE;
        mPaintCordLine.color = CORD_BASE_LINE_COLOR;
        mPaintCordLine.strokeWidth = CORD_BASE_LINE_WIDTH;

        mPaintCordValue.style = Paint.Style.FILL;
        mPaintCordValue.color = CORD_BASE_LINE_COLOR;
        mPaintCordValue.textSize = 14F;

        mPaintDataLine.style = Paint.Style.STROKE;
        mPaintDataLine.color = DATA_LINE_COLOR;
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas);
        if (mTemperatureValue.isEmpty()) {
            return;
        }
        drawHorizontalLines(canvas);
        drawVerticalLines(canvas);
        drawData(canvas);
    }

    private fun drawVerticalLines(canvas: Canvas) {
        mPaintCordLine.color = CORD_BASE_LINE_COLOR;
        mPaintCordLine.strokeWidth = CORD_BASE_LINE_WIDTH;
        canvas.drawLine(
            CORD_BASE_LINE_PADDING,
            mViewPadding,
            CORD_BASE_LINE_PADDING,
            measuredHeight - mViewPadding,
            mPaintCordLine
        );

        val hourCordCount =
            ((measuredWidth - mViewPadding * 2F) / (mHourLineWidth * dotScale)).roundToInt();
        val lineY = measuredHeight - mViewPadding * 2 - CORD_BASE_LINE_PADDING;
        val hourValueSpace =
            ((mHourlyValue.size / dotScale.toFloat()) / hourCordCount.toFloat()).roundToInt();

        for (i in 0 until mHourlyValue.size / hourValueSpace) {
            var lineX = CORD_BASE_LINE_PADDING + mHourLineWidth * dotScale * i + mScrollDistance;

            val lineXLast =
                CORD_BASE_LINE_PADDING + mHourLineWidth * dotScale * (mHourlyValue.size / hourValueSpace - 1) + mScrollDistance;
            if (lineXLast < measuredWidth - mViewPadding) {
                lineX += (measuredWidth - mViewPadding - lineXLast);
            }
            val lineXFirst = CORD_BASE_LINE_PADDING + mScrollDistance;
            if (lineXFirst > CORD_BASE_LINE_PADDING) {
                lineX -= (lineXFirst - CORD_BASE_LINE_PADDING);
            }

            if (hourValueSpace * i >= mHourlyValue.size) { // 第一个不绘制
                continue;
            }

            canvas.drawLine(lineX, lineY, lineX, lineY + mViewPadding, mPaintCordLine);

            val dateValue = mHourlyValue[hourValueSpace * i];
            var hourValue: String = dateValue.weatherHour();
            hourValue = if (hourValue.equals("00:00")) dateValue.weatherDayOfMonth() else hourValue;
            val bounds = Rect();
            mPaintCordValue.getTextBounds(hourValue, 0, hourValue.length, bounds);
            val textX = lineX - bounds.width() / 2F;
            canvas.drawText(
                hourValue,
                textX,
                lineY + mViewPadding + bounds.height(),
                mPaintCordValue
            );
        }
    }

    private fun drawHorizontalLines(canvas: Canvas) {
        mPaintCordLine.color = CORD_LINE_COLOR;
        mPaintCordLine.strokeWidth = CORD_LINE_WITH;

        val maxLineY = measuredHeight.toFloat() - CORD_BASE_LINE_PADDING - mViewPadding * 2;
        val lineSpace = maxLineY / (mTemperatureLines - 1); // -1用于保证均分剩余的画布高度
        var lineY: Float;
        var temperatureValue: Float;
        for (i in 0 until mTemperatureLines) {
            mPaintCordLine.color = if (i == 0) CORD_BASE_LINE_COLOR else CORD_LINE_COLOR;

            lineY = max(maxLineY - lineSpace * i, mViewPadding); // 防止线条越界
            // 绘制温度参考线
            canvas.drawLine(
                CORD_BASE_LINE_PADDING,
                lineY,
                width.toFloat() - mViewPadding,
                lineY,
                mPaintCordLine
            );

            temperatureValue =
                min(mTemperatureMinValue + mTemperatureLineSpace * i, mTemperatureMaxValue);
            val tempStr = temperatureValue.fix().toString();
            val bounds = Rect()
            mPaintCordValue.getTextBounds(tempStr, 0, tempStr.length, bounds);
            lineY += bounds.height() / 2F;
            // 绘制参考线的温度值
            canvas.drawText(tempStr, mViewPadding, lineY, mPaintCordValue);
        }
    }

    private fun drawData(canvas: Canvas) {
        val path = Path();
        var isStartPointAdded = false;
        for (index in 0 until mTemperatureValue.count()) {
            val tem: Double = mTemperatureValue[index];

            val percentX = index.toFloat() / mTemperatureValue.size.toFloat();
            var x =
                CORD_BASE_LINE_PADDING + (percentX * dotScale) * (measuredWidth - CORD_BASE_LINE_PADDING - mViewPadding * 2) + mScrollDistance;

            // 控制边界
            var xLast =
                CORD_BASE_LINE_PADDING + dotScale * (measuredWidth - CORD_BASE_LINE_PADDING - mViewPadding * 2) + mScrollDistance;
            if (xLast < measuredWidth - mViewPadding) {
                x += (measuredWidth - mViewPadding - xLast);
            }
            var xFirst = CORD_BASE_LINE_PADDING + mScrollDistance;
            if (xFirst > CORD_BASE_LINE_PADDING) {
                x -= (xFirst - CORD_BASE_LINE_PADDING);
            }
            if (index == mTemperatureValue.size - 1) {
                mIsScrollToEnd = x <= measuredWidth - mViewPadding * 2;
            }

            if (index == 0) {
                mIsScrollToStart = x > CORD_BASE_LINE_PADDING + mViewPadding * 2;
            }

            val maxLineY = measuredHeight.toFloat() - CORD_BASE_LINE_PADDING - mViewPadding * 2F;
            val dotPercent =
                (tem - mTemperatureMinValue) / (mTemperatureMaxValue - mTemperatureMinValue);
            val y = mViewPadding + (1 - dotPercent) * maxLineY;
            if (!isStartPointAdded) {
                path.moveTo(x, y.toFloat());
                isStartPointAdded = true;
                continue;
            }
            path.lineTo(x, y.toFloat());
        }
        canvas.drawPath(path, mPaintDataLine);
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event);
        }
        if (event.pointerCount == 1 && event.action == MotionEvent.ACTION_DOWN) {
            mLastMovePos = event.getX();
            return true;
        }
        if (event.pointerCount == 1 && event.action == MotionEvent.ACTION_MOVE && dotScale > VIEW_SCALE_MIN) {
            val distance = event.getX() - mLastMovePos;
            if (abs(distance) > 0F) {
                mLastMovePos = event.getX();
                mScrollDistance += distance / 2F;
                invalidate();
            }
            return true;
        }

        // 双指缩放
        if (event.pointerCount >= 2 && event.actionMasked == MotionEvent.ACTION_POINTER_DOWN) {
            xPoint1Start = event.getX(0);
            xPoint2Start = event.getX(1);
            xTouchDistance = abs(xPoint2Start - xPoint1Start);
            return true;
        } else if (event.pointerCount >= 2 && event.actionMasked == MotionEvent.ACTION_MOVE) {
            xPoint1End = event.getX(0);
            xPoint2End = event.getX(1);
            val xTouchDistanceOnMove = abs(xPoint2End - xPoint1End);
            val xScaleDistanceTemp = xTouchDistanceOnMove - xTouchDistance;
            if (abs(xScaleDistanceTemp - xScaleDistance) > VIEW_SCALE_TRIGGER_OFFSET) {
                xScaleDistance = xScaleDistanceTemp;
                dotScale += if (xScaleDistance > 0F) VIEW_SCALE_SETP else -VIEW_SCALE_SETP;
                mScrollDistance += if (xScaleDistance > 0F) -VIEW_SCALE_SETP else VIEW_SCALE_SETP;
                if (dotScale < VIEW_SCALE_MIN) {
                    dotScale = VIEW_SCALE_MIN;
                }
                if (dotScale > VIEW_SCALE_MAX) {
                    dotScale = 5F;
                }
                if (dotScale == VIEW_SCALE_MIN && abs(mScrollDistance) > 0F) {
                    mScrollDistance = 0F;
                }
                invalidate();
            }
            return true;
        }
        return true;
    }

    private fun log(log: String?) {
        Log.d(TAG, "$log");
    }

    open fun setData(weather: Weather.Hourly?) {
        log("setData size: ${weather?.temperature_2m?.size ?: 0}");
        mTemperatureValue.clear();
        mTemperatureValue.addAll(weather?.temperature_2m ?: listOf());
        mHourlyValue.clear();
        mHourlyValue.addAll(weather?.time ?: listOf());

        mTemperatureMaxValue =
            (mTemperatureValue.maxOrNull()?.toFloat() ?: 0F) + TEMPERATURE_OFFSET;
        mTemperatureMinValue =
            (mTemperatureValue.minOrNull()?.toFloat() ?: 0F) - TEMPERATURE_OFFSET;
        mTemperatureLineSpace =
            (mTemperatureMaxValue - mTemperatureMinValue) / (mTemperatureLines - 1);

        invalidate();
    }
}