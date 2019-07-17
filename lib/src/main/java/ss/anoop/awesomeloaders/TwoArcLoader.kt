package ss.anoop.awesomeloaders

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.use
import ss.anoop.awesomeloaders.utils.dpToPx
import kotlin.math.min


private val TAG = "TwoArcLoader"

class TwoArcLoader @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    private val styleAttrs: Int = 0,
    private val styleRes: Int = 0
) : View(context, attributeSet, styleAttrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private var rotationAngle = 0f

    private var smallestDimen = 0f

    private val rectF = RectF(0f, 0f, 0f, 0f)

    private val path1 = Path()
    private val path2 = Path()

    private var cycleDuration = 1000L

    private val MIN_DIMENSION by lazy {
        dpToPx(32f, resources).toInt()
    }

    private var animator: Animator? = null

    init {
        if (null != attributeSet) {
            initAttrs(attributeSet)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = cycleDuration
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            startDelay = 0
            addUpdateListener(::onAnimationUpdate)
        }.also { it.start() }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(widthMeasureSpec)
        } else {
            MIN_DIMENSION
        }

        val height = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(heightMeasureSpec)
        } else {
            MIN_DIMENSION
        }

        setMeasuredDimension(width, height)

        smallestDimen = min(width, height).toFloat()
        rectF.apply {
            left = paint.strokeWidth / 2
            top = left
            bottom = smallestDimen - top
            right = smallestDimen - top
        }

        val paddingSide = (width - smallestDimen.toInt()) / 2
        val paddingTop = (height - smallestDimen.toInt()) / 2

        setPadding(paddingSide, paddingTop, paddingSide, paddingTop)

        path1.addArc(rectF, 0f, 120f)
        path2.addArc(rectF, 180f, 120f)

    }


    override fun onDraw(canvas: Canvas) {

        canvas.apply {
            translate(paddingLeft.toFloat(), paddingTop.toFloat())
            rotate(
                rotationAngle,
                (width - paddingLeft.times(2)) / 2f,
                (height - paddingTop.times(2)) / 2f
            )
            drawPath(path1, paint)
            drawPath(path2, paint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }

    private fun initAttrs(attributeSet: AttributeSet) {
        context.obtainStyledAttributes(attributeSet, R.styleable.TwoArcLoader, styleAttrs, styleRes)
            .use {
                paint.color = it.getColor(R.styleable.TwoArcLoader_strokeColor, Color.BLACK)
                paint.strokeWidth =
                    it.getDimension(R.styleable.TwoArcLoader_strokeWidth, dpToPx(2f, resources))
                cycleDuration = it.getInteger(R.styleable.TwoArcLoader_cycleDuration, 1000).toLong()
            }
    }

    private fun onAnimationUpdate(valueAnimator: ValueAnimator) {

        rotationAngle = valueAnimator.animatedValue as Float
        postInvalidate()
    }
}