package cloud.techstar.progressbar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator

/**
 * Author: Turtuvshin Byambaa.
 * Project: Safety Inst
 * URL: https://www.github.com/tortuvshin
 */

class TSProgressBar : View {

    private var mOuterCirclePaint: Paint? = null

    private var mInnerCirclePaint: Paint? = null

    private var mThickness: Float = 0.toFloat()

    private var mInnerPadding: Float = 0.toFloat()

    private var mAnimDuration: Int = 0

    private var mOuterCircleRect: RectF? = null

    private var mInnerCircleRect: RectF? = null

    @ColorInt
    private var mOuterCircleColor: Int = 0

    @ColorInt
    private var mInnerCircleColor: Int = 0

    private var mSteps: Int = 0

    private var mSize: Int = 0

    private var mStartAngle: Float = 0.toFloat()

    private var mIndeterminateSweep: Float = 0.toFloat()

    private var mIndeterminateRotateOffset: Float = 0.toFloat()

    private var mIndeterminateAnimator: AnimatorSet? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    protected fun init(attrs: AttributeSet?, defStyle: Int) {

        mOuterCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mInnerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)

        mOuterCircleRect = RectF()
        mInnerCircleRect = RectF()

        val resources = resources

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.TSProgressBar, defStyle, 0)
        mThickness = a.getDimensionPixelSize(R.styleable.TSProgressBar_thickness,
                resources.getDimensionPixelSize(R.dimen.default_thickness)).toFloat()
        mInnerPadding = a.getDimensionPixelSize(R.styleable.TSProgressBar_inner_padding,
                resources.getDimensionPixelSize(R.dimen.default_inner_padding)).toFloat()

        mOuterCircleColor = a.getColor(R.styleable.TSProgressBar_outer_color,
                ContextCompat.getColor(context, R.color.colorPrimary))
        mInnerCircleColor = a.getColor(R.styleable.TSProgressBar_inner_color,
                ContextCompat.getColor(context, R.color.colorAccent))
        mAnimDuration = a.getInteger(R.styleable.TSProgressBar_anim_duration,
                resources.getInteger(R.integer.default_anim_duration))
        mSteps = resources.getInteger(R.integer.default_anim_step)
        mStartAngle = resources.getInteger(R.integer.default_start_angle).toFloat()
        a.recycle()


        setPaint()

    }

    private fun setPaint() {

        mOuterCirclePaint!!.color = mOuterCircleColor
        mOuterCirclePaint!!.style = Paint.Style.STROKE
        mOuterCirclePaint!!.strokeWidth = mThickness
        mOuterCirclePaint!!.strokeCap = Paint.Cap.BUTT
        mInnerCirclePaint!!.color = mInnerCircleColor
        mInnerCirclePaint!!.style = Paint.Style.STROKE
        mInnerCirclePaint!!.strokeWidth = mThickness
        mInnerCirclePaint!!.strokeCap = Paint.Cap.BUTT
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawArc(mOuterCircleRect!!, mStartAngle + mIndeterminateRotateOffset, mIndeterminateSweep, false, mOuterCirclePaint!!)
        canvas.drawArc(mInnerCircleRect!!, mStartAngle + mIndeterminateRotateOffset + 180f, mIndeterminateSweep, false, mInnerCirclePaint!!)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val xPad = paddingLeft + paddingRight
        val yPad = paddingTop + paddingBottom
        val width = measuredWidth - xPad
        val height = measuredHeight - yPad
        mSize = if (width < height) width else height
        setMeasuredDimension(mSize + xPad, mSize + yPad)
        updateRectAngleBounds()
    }


    private fun updateRectAngleBounds() {
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        mOuterCircleRect!!.set(paddingLeft + mThickness, paddingTop + mThickness,
                mSize.toFloat() - paddingLeft.toFloat() - mThickness, mSize.toFloat() - paddingTop.toFloat() - mThickness)
        mInnerCircleRect!!.set(paddingLeft.toFloat() + mThickness + mInnerPadding,
                paddingTop.toFloat() + mThickness + mInnerPadding, mSize.toFloat() - paddingLeft.toFloat() - mThickness - mInnerPadding,
                mSize.toFloat() - paddingTop.toFloat() - mThickness - mInnerPadding)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mSize = if (w < h) w else h
        updateRectAngleBounds()
    }

    private fun createIndeterminateAnimator(step: Float): AnimatorSet {

        val maxSweep = 360f * (mSteps - 1) / mSteps + INDETERMINANT_MIN_SWEEP
        val start = -90f + step * (maxSweep - INDETERMINANT_MIN_SWEEP)


        val frontEndExtend = ValueAnimator.ofFloat(INDETERMINANT_MIN_SWEEP, maxSweep)
        frontEndExtend.duration = (mAnimDuration / mSteps / 2).toLong()
        frontEndExtend.interpolator = DecelerateInterpolator(1f)
        frontEndExtend.addUpdateListener { animation ->
            mIndeterminateSweep = animation.animatedValue as Float
            invalidate()
        }


        val rotateAnimator1 = ValueAnimator.ofFloat(step * 720f / mSteps, (step + .5f) * 720f / mSteps)
        rotateAnimator1.duration = (mAnimDuration / mSteps / 2).toLong()
        rotateAnimator1.interpolator = LinearInterpolator()
        rotateAnimator1.addUpdateListener { animation -> mIndeterminateRotateOffset = animation.animatedValue as Float }


        val backEndRetract = ValueAnimator.ofFloat(start, start + maxSweep - INDETERMINANT_MIN_SWEEP)
        backEndRetract.duration = (mAnimDuration / mSteps / 2).toLong()
        backEndRetract.interpolator = DecelerateInterpolator(1f)
        backEndRetract.addUpdateListener { animation ->
            mStartAngle = animation.animatedValue as Float
            mIndeterminateSweep = maxSweep - mStartAngle + start
            invalidate()
        }


        val rotateAnimator2 = ValueAnimator.ofFloat((step + .5f) * 720f / mSteps, (step + 1) * 720f / mSteps)
        rotateAnimator2.duration = (mAnimDuration / mSteps / 2).toLong()
        rotateAnimator2.interpolator = LinearInterpolator()
        rotateAnimator2.addUpdateListener { animation -> mIndeterminateRotateOffset = animation.animatedValue as Float }

        val set = AnimatorSet()
        set.play(frontEndExtend).with(rotateAnimator1)
        set.play(backEndRetract).with(rotateAnimator2).after(rotateAnimator1)
        return set
    }

    fun resetAnimation() {

        if (mIndeterminateAnimator != null && mIndeterminateAnimator!!.isRunning)
            mIndeterminateAnimator!!.cancel()
        mIndeterminateSweep = INDETERMINANT_MIN_SWEEP

        mIndeterminateAnimator = AnimatorSet()
        var prevSet: AnimatorSet? = null
        var nextSet: AnimatorSet
        for (k in 0 until mSteps) {
            nextSet = createIndeterminateAnimator(k.toFloat())
            val builder = mIndeterminateAnimator!!.play(nextSet)
            if (prevSet != null)
                builder.after(prevSet)
            prevSet = nextSet
        }

        mIndeterminateAnimator!!.addListener(object : AnimatorListenerAdapter() {
            internal var wasCancelled = false

            override fun onAnimationCancel(animation: Animator) {
                wasCancelled = true
            }

            override fun onAnimationEnd(animation: Animator) {
                if (!wasCancelled)
                    resetAnimation()
            }
        })
        mIndeterminateAnimator!!.start()
    }

    fun startAnimation() {
        resetAnimation()
    }

    fun stopAnimation() {
        if (mIndeterminateAnimator != null) {
            mIndeterminateAnimator!!.cancel()
            mIndeterminateAnimator = null
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    override fun setVisibility(visibility: Int) {
        val currentVisibility = getVisibility()
        super.setVisibility(visibility)
        if (visibility != currentVisibility) {
            if (visibility == View.VISIBLE) {
                resetAnimation()
            } else if (visibility == View.GONE || visibility == View.INVISIBLE) {
                stopAnimation()
            }
        }
    }

    companion object {

        private val INDETERMINANT_MIN_SWEEP = 15f
    }
}