package ss.anoop.awesomeloaders

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.res.use
import ss.anoop.awesomeloaders.utils.dpToPx
import kotlin.math.min

class PinWheelLoader @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    private val defStyleRes: Int = 0
) : View(context, attributes, defStyleRes) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(1f, resources)
    }

    private val MIN_DIMENSION by lazy {
        dpToPx(24f, resources).toInt()
    }

    private var path: Path? = null

    private var animator: Animator? = null

    init {
        if (attributes != null) {
            initAttrs(attributes)
        }

        setLayerType(LAYER_TYPE_HARDWARE, paint)
    }

    private fun initAttrs(attributes: AttributeSet) {
        context.obtainStyledAttributes(attributes, R.styleable.PinWheelLoader, defStyleRes, 0).use {
            paint.color = it.getColor(R.styleable.PinWheelLoader_strokeColor, Color.BLACK)
        }
    }


    private fun initPath() {
        val smallestDimen = min(measuredWidth, measuredHeight)
        val hPadding = (measuredWidth - smallestDimen).div(2f)
        val vPadding = (measuredHeight - smallestDimen).div(2f)
        val leafDimen = measuredWidth.div(2f) - hPadding

        path = Path().apply {
            moveTo(measuredWidth.div(2f), vPadding)
            lineTo(measuredWidth.div(2f), measuredHeight - vPadding)
            moveTo(hPadding, measuredHeight.div(2f))
            lineTo(measuredWidth - hPadding, measuredHeight.div(2f))
            quadTo(
                measuredWidth.div(2f) + leafDimen.div(2),
                measuredHeight.div(2f) - leafDimen.div(2f),
                measuredWidth.div(2f),
                measuredHeight.div(2f)
            )

            quadTo(
                hPadding + leafDimen.div(2f),
                vPadding + leafDimen.div(2f),
                measuredWidth.div(2f),
                vPadding
            )

            moveTo(hPadding, measuredHeight.div(2f))
            quadTo(
                hPadding + leafDimen.div(2f),
                measuredHeight.div(2f) + leafDimen.div(2f),
                measuredWidth.div(2f),
                measuredHeight.div(2f)
            )

            quadTo(
                measuredWidth.div(2f) + leafDimen.div(2f),
                measuredHeight.div(2f) + leafDimen.div(2f),
                measuredWidth.div(2f),
                measuredHeight - vPadding
            )
        }

        startAnimation()
    }

    private fun startAnimation() {
        animator?.cancel()

        animator = ValueAnimator.ofFloat(0f, 7200f).apply {
            duration = 5000
            interpolator = OvershootInterpolator()
            addUpdateListener(::onAnimationUpdate)
        }.also {
            it.start()
        }
    }

    private fun onAnimationUpdate(valueAnimator: ValueAnimator) {
        rotation = valueAnimator.animatedValue as Float
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = if (getMode(widthMeasureSpec) == EXACTLY) {
            getSize(widthMeasureSpec)
        } else {
            MIN_DIMENSION
        }

        val height = if (getMode(widthMeasureSpec) == EXACTLY) {
            getSize(widthMeasureSpec)
        } else {
            MIN_DIMENSION
        }

        setMeasuredDimension(width, height)

        initPath()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.run {
            drawPath(path, paint)
        }
    }
}