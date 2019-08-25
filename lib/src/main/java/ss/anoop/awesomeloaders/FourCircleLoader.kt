package ss.anoop.awesomeloaders

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.View.MeasureSpec.*
import android.view.animation.LinearInterpolator
import androidx.core.content.res.use
import ss.anoop.awesomeloaders.utils.DefaultAnimatorListener
import ss.anoop.awesomeloaders.utils.dpToPx
import kotlin.math.min

class FourCircleLoader @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    private val defStyleRes: Int = 0
) : View(context, attributeSet, defStyleRes) {

    private val MIN_DIMENSION by lazy {
        dpToPx(32f, context.resources).toInt()
    }
    private val paintList = arrayOf(
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF33D1")
            style = Paint.Style.FILL
        },
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#DFEF1E")
            style = Paint.Style.FILL
        },
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#EC4832")
            style = Paint.Style.FILL
        },
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4EF3EE")
            style = Paint.Style.FILL
        })

    private val circlePoint1 = PointF()

    private val circlePoint2 = PointF()

    private val circlePoint3 = PointF()

    private val circlePoint4 = PointF()

    private var circleRadius = dpToPx(4f, context.resources)

    private var animatorSet = AnimatorSet()

    private var cycleDuration = 4000L

    private var repeat = INFINITE

    private var animationCount = 1

    private val delayAfterRotation = 100

    init {
        attributeSet?.let(::initAttrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = if (getMode(widthMeasureSpec) == EXACTLY) {
            getSize(widthMeasureSpec)
        } else {
            MIN_DIMENSION
        }

        val height = if (getMode(heightMeasureSpec) == EXACTLY) {
            getSize(heightMeasureSpec)
        } else {
            MIN_DIMENSION
        }

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val minDimension = min(w, h)
        val hPadding = (w - minDimension).div(2)
        val vPadding = (h - minDimension).div(2)
        setPadding(hPadding, vPadding, hPadding, vPadding)

        circlePoint1.apply {
            x = w.div(2f)
            y = vPadding + circleRadius
        }
        circlePoint2.apply {
            x = w - circleRadius - hPadding
            y = h.div(2f)
        }
        circlePoint3.apply {
            x = w.div(2f)
            y = h - circleRadius - vPadding
        }
        circlePoint4.apply {
            x = circleRadius + hPadding
            y = h.div(2f)
        }

        startAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.run {
            drawCircle(circlePoint1.x, circlePoint1.y, circleRadius, paintList[0])
            drawCircle(circlePoint2.x, circlePoint2.y, circleRadius, paintList[1])
            drawCircle(circlePoint3.x, circlePoint3.y, circleRadius, paintList[2])
            drawCircle(circlePoint4.x, circlePoint4.y, circleRadius, paintList[3])
        }
    }

    private fun startAnimation() {
        animatorSet.cancel()
        animatorSet = AnimatorSet()

        val translationDuration = (width - paddingLeft.times(2)).times(3).toLong()

        val rotation = ValueAnimator.ofFloat(rotation, rotation + 360f).apply {
            duration = (cycleDuration - translationDuration.times(4)
                    + translationDuration.times(0.6).toLong()
                    + delayAfterRotation.times(2)
                    ).div(2)
            interpolator = LinearInterpolator()
            addUpdateListener { rotation = it.animatedValue as Float }
        }

        val verticalTranslation =
            ValueAnimator.ofFloat(circleRadius, height - circleRadius).apply {
                startDelay = rotation.duration
                duration = translationDuration
                interpolator = LinearInterpolator()
                addUpdateListener {
                    circlePoint1.y = it.animatedValue as Float
                    circlePoint3.y = height - it.animatedValue as Float
                    invalidate()
                }
            }

        val horizontalTranslation =
            ValueAnimator.ofFloat(circleRadius, width - circleRadius).apply {
                startDelay = rotation.duration + translationDuration.times(0.34).toLong()
                duration = translationDuration.times(0.66).toLong()
                interpolator = LinearInterpolator()
                addUpdateListener {
                    circlePoint4.x = it.animatedValue as Float
                    circlePoint2.x = width - it.animatedValue as Float
                    invalidate()
                }
            }

        val reverseVerticalTranslation =
            ValueAnimator.ofFloat(height - circleRadius, circleRadius).apply {
                duration = translationDuration.times(0.66).toLong()
                interpolator = LinearInterpolator()
                addUpdateListener {
                    startDelay = rotation.duration.times(2) +
                            translationDuration +
                            translationDuration.times(0.34).toLong() +
                            delayAfterRotation
                    circlePoint1.y = it.animatedValue as Float
                    circlePoint3.y = height - it.animatedValue as Float
                    invalidate()
                }
            }

        val reverseHorizontalTranslation =
            ValueAnimator.ofFloat(width - circleRadius, circleRadius).apply {
                duration = translationDuration
                startDelay = rotation.duration.times(2) + translationDuration + delayAfterRotation
                interpolator = LinearInterpolator()
                addUpdateListener {
                    circlePoint4.x = it.animatedValue as Float
                    circlePoint2.x = width - it.animatedValue as Float
                    invalidate()
                }
            }

        animatorSet.apply {
            addListener(object : DefaultAnimatorListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    if (repeat == INFINITE || animationCount < repeat) {
                        animationCount++
                        animatorSet.apply {
                            startDelay = delayAfterRotation.toLong()
                        }.also { it.start() }
                    }
                }
            })
            playTogether(
                rotation,
                verticalTranslation,
                horizontalTranslation,
                rotation.clone().apply {
                    startDelay = rotation.duration + translationDuration + delayAfterRotation
                },
                reverseHorizontalTranslation,
                reverseVerticalTranslation
            )
        }.start()
    }

    private fun initAttrs(attributeSet: AttributeSet) {
        context.obtainStyledAttributes(attributeSet, R.styleable.FourCircleLoader, defStyleRes, 0)
            .use {
                cycleDuration =
                    it.getInteger(R.styleable.FourCircleLoader_cycleDuration, cycleDuration.toInt())
                        .toLong()
                repeat = it.getInteger(R.styleable.FourCircleLoader_repeatCount, repeat)
                circleRadius = it.getDimension(R.styleable.FourCircleLoader_circleRadius, circleRadius)
                val colorList = it.getResourceId(R.styleable.FourCircleLoader_circleColors, 0)
                if (colorList != 0) {
                    val colorsArray = resources.getIntArray(colorList)
                    for (index in 0..min(colorsArray.size.minus(1), 3)) {
                        paintList[index].color = colorsArray[index]
                    }
                }
            }
    }
}