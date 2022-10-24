package com.udacity.fwd.downloader


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val FONT_SIZE = 55.0f
        private const val CIRCLE_COLOR = Color.YELLOW
        private const val CIRCLE_RECT_COLOR = Color.GRAY
        private const val ANIMATION_DURATION = 3000L
    }


    private var widthSize = 0

    //Whenever the hight changes, the dimensions of the circle rectangle changes too
    private var heightSize: Int by Delegates.observable(0) { _, _, new ->
        circleRect = RectF(
            new.toFloat() * 0.1f,
            new.toFloat() * 0.1f,
            new.toFloat() * 0.9f,
            new.toFloat() * 0.9f
        )
    }

    private var circleAngle = 0f

    private var text = ""
    private var animationText = ""
    private var currentText = ""
    private var fillColor = Color.BLACK
    private var textColor = Color.WHITE


    private lateinit var circleRect: RectF


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.LEFT
        textSize = FONT_SIZE
        typeface = Typeface.create("", Typeface.BOLD)
    }


    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, old, new ->

        when (new) {
            is ButtonState.Clicked -> {
                currentText = text

                animateButton()
            }
            is ButtonState.Loading -> {
                currentText = animationText
                drawCircleRect = true
                isEnabled = false
            }
            //completed
            else -> {
                currentText = text
                drawCircleRect = false
                isEnabled = true
            }
        }
    }

    private lateinit var animatorSet: AnimatorSet


    private var drawCircleRect = false
    private fun animateButton() {
        val animator1 = ValueAnimator.ofArgb(Color.BLACK, fillColor).apply {
            addUpdateListener {
                fillColor = it.animatedValue as Int

                //inverting the text color for high contrast
                textColor = Color.valueOf(fillColor).invert().toArgb()
            }
        }


        val animator2 = ValueAnimator.ofInt(1, widthSize).apply {

            addUpdateListener {
                widthSize = it.animatedValue as Int
            }


        }
        val animator3 = ValueAnimator.ofFloat(0f, 360f).apply {

            addUpdateListener {
                circleAngle = it.animatedValue as Float

                // since all animators will work in parallel ,
                // invalidate only in the most frequent animator
                invalidate()

            }
        }


        animatorSet = AnimatorSet().apply {
            playTogether(animator1, animator2, animator3)
            duration = ANIMATION_DURATION

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    buttonState = ButtonState.Loading
                }

                override fun onAnimationEnd(animation: Animator?) {
                    buttonState = ButtonState.Completed
                }
            })

        }
//        set.start()
        animatorSet.start()
    }


    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            textColor = getColor(R.styleable.LoadingButton_textColor, Color.BLACK)
            fillColor = getColor(R.styleable.LoadingButton_fillColor, Color.GRAY)
            text = getString(R.styleable.LoadingButton_text) ?: "Loading Button"
            animationText = getString(R.styleable.LoadingButton_animationText) ?: "Loading Button"
            currentText = text
        }

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //draw button rectangle and fill with "fillColor"
        paint.color = fillColor
        canvas.drawRect(0F, 0F, widthSize.toFloat(), heightSize.toFloat(), paint)

        //draw the text in the middle of the rectangle
        paint.color = textColor
        canvas.save()
        canvas.translate((widthSize / 4).toFloat(), (heightSize / 4).toFloat())
        canvas.clipRect(0f, 0f, widthSize.toFloat() / 2, heightSize.toFloat() / 2)
        canvas.drawText(currentText, 0f, FONT_SIZE, paint)
        canvas.restore()
        if (drawCircleRect) {
            //draw the gray rectangle and the circle at the end of the button rectangle
            canvas.save()
            paint.color = CIRCLE_COLOR
            canvas.translate((widthSize - heightSize).toFloat(), 0f)
            canvas.clipRect(0f, 0f, heightSize.toFloat(), heightSize.toFloat())
            canvas.drawColor(CIRCLE_RECT_COLOR)
            canvas.drawArc(
                circleRect,
                0f,
                circleAngle,
                true,
                paint
            )
            canvas.restore()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        widthSize = w
        heightSize = h

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }


    override fun performClick(): Boolean {
        // the button will be disabled during the animation,
        // so the performClick will be called only if the button is enabled
        buttonState = ButtonState.Clicked

        return super.performClick()
    }


    fun stopAnimation() {
        if (::animatorSet.isInitialized) {
            animatorSet.pause()
            animatorSet.reverse()
        }
    }

    //extension function to get the inverted color
    fun Color.invert(): Color {
        return Color.valueOf(1.0f - this.red(), 1.0f - green(), 1.0f - blue(), alpha())
    }
}