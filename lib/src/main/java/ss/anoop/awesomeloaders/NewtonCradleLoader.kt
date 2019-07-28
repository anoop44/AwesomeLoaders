package ss.anoop.awesomeloaders.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.use
import ss.anoop.awesomeloaders.R

class NewtonCradleLoader @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    private val styleRes: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    private val MIN_WIDTH by lazy {
        dpToPx(48f, resources).toInt()
    }

    private val MIN_HEIGHT by lazy {
        dpToPx(12f, resources).toInt()
    }

    private var circleRadius = 0f

    private val paint = Paint()

    private var cycleDuration: Int = 1500

    private var animator: Animator? = null

    private var lPoint = PointF(0f, 0f)

    private var rPoint = PointF(0f, 0f)

    init {
        if (null != attributeSet) {
            initAttrs(attributeSet)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(widthMeasureSpec)
        } else {
            MIN_WIDTH
        }

        val height = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(heightMeasureSpec)
        } else {
            MIN_HEIGHT
        }

        setMeasuredDimension(width, height)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        animator?.cancel()

        val animatorList = mutableListOf<Animator>()

        val leftSideYAnimator = ValueAnimator.ofFloat(circleRadius, bottom - circleRadius).apply {
            addUpdateListener {
                lPoint.y = it.animatedValue as Float
                invalidate()
            }
        }
        val leftSideXAnimator = ValueAnimator.ofFloat(circleRadius, 3 * circleRadius).apply {
            addUpdateListener {
                lPoint.x = it.animatedValue as Float
                invalidate()
            }
            invalidate()
        }

        animatorList.add(AnimatorSet().apply {
            playTogether(leftSideXAnimator, leftSideYAnimator)
            duration = cycleDuration / 4L
            interpolator = LinearInterpolator()
        })

        animator = AnimatorSet().apply {
            interpolator = LinearInterpolator()
            playSequentially(animatorList)
        }.also {
            it.start()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

    private fun initAttrs(attributeSet: AttributeSet) {
        context.obtainStyledAttributes(
            attributeSet,
            R.styleable.NewtonCradleLoader,
            defStyleAttr,
            styleRes
        ).use {
            circleRadius =
                it.getDimension(R.styleable.NewtonCradleLoader_circleRadius, dpToPx(3f, resources))
            paint.color = it.getColor(R.styleable.NewtonCradleLoader_circleColor, Color.BLACK)
        }
    }
}