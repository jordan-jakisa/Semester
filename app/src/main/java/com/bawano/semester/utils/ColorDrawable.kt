package com.bawano.semester.utils

import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import java.util.*
import kotlin.math.abs


class ColorGenerator private constructor(private val colors: List<Int>) {
    companion object {
        var DEFAULT: ColorGenerator = this(
            listOf(
                -0xe9c9c,
                -0xa7aa7,
                -0x65bc2,
                -0x1b39d2,
                -0x98408c,
                -0xa65d42,
                -0xdf6c33,
                -0x529d59,
                -0x7fa87f
            )
        )

        private operator fun invoke(colours: List<Int>) =  ColorGenerator(colours)


        var MATERIAL = this(
            listOf(
                -0x1a8c8d,
                -0xf9d6e,
                -0x459738,
                -0x6a8a33,
                -0x867935,
                -0x9b4a0a,
                -0xb03c09,
                -0xb22f1f,
                -0xb24954,
                -0x7e387c,
                -0x512a7f,
                -0x759b,
                -0x2b1ea9,
                -0x2ab1,
                -0x48b3,
                -0x5e7781,
                -0x6f5b52
            )
        )



    }
    private val randomNumber = Random(System.currentTimeMillis())
    val randomColor: Int
        get() = colors[randomNumber.nextInt(colors.size)]

    fun getColor(key: Any): Int {
        return colors[abs(key.hashCode()) % colors.size]
    }

}


class TextDraw(
    var text: String = "",
    var color: Int= Color.GRAY,
    var width: Int= -1,
    var height: Int= -1,
    var borderThickness: Int = 0,
    var font: Typeface = Typeface.create("sans-serif-light", Typeface.NORMAL),
    var shapeConst: String = RECT,
    var textColor: Int= Color.WHITE,
    var fontSize: Int = -1,
    var isBoldText: Boolean= false,
    var radius: Float = 0f
): ShapeDrawable(
    when(shapeConst){
        ROUND -> OvalShape()
        ROUND_RECT -> {
            val radii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
            RoundRectShape(radii, null, null)
        }
        else -> RectShape()

    }
){

    private val textPaint: Paint = Paint()
    private val borderPaint: Paint = Paint()

    companion object {
        const val ROUND = "ROUND"
        const val ROUND_RECT = "ROUND_RECT"
        const val RECT = "RECT"
        private const val SHADE_FACTOR = 0.9f
    }

    init {
        textPaint.color = textColor
        textPaint.isAntiAlias = true
        textPaint.isFakeBoldText = isBoldText
        textPaint.style = Paint.Style.FILL
        textPaint.typeface = font
        textPaint.textAlign = Paint.Align.CENTER
        text = text.onlyFirstLetters()

        borderPaint.color = getDarkerShade(color)
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderThickness.toFloat()


    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val r: Rect = bounds

        if (borderThickness > 0) drawBorder(canvas)
        val count: Int = canvas.save()
        canvas.translate(r.left.toFloat(), r.top.toFloat())

        val width = if (width < 0) r.width() else width
        val height = if (height < 0) r.height() else height

        val fontSize = if (fontSize < 0) width.coerceAtMost(height) / 2 else fontSize
        textPaint.textSize = fontSize.toFloat()
        canvas.drawText(
            text,
            (width / 2).toFloat(),
            height / 2 - (textPaint.descent() + textPaint.ascent()) / 2,
            textPaint
        )
        canvas.restoreToCount(count)
    }

    private fun getDarkerShade(color: Int): Int {
        return Color.rgb(
            (SHADE_FACTOR * Color.red(color)).toInt(),
            (SHADE_FACTOR * Color.green(color)).toInt(),
            (SHADE_FACTOR * Color.blue(color)).toInt()
        )
    }

    override fun setAlpha(alpha: Int) {
        textPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        textPaint.colorFilter = cf
    }

    override fun getIntrinsicWidth(): Int {
        return width
    }

    override fun getIntrinsicHeight(): Int {
        return height
    }


    private fun drawBorder(canvas: Canvas) {
        val rect = RectF(bounds)
        rect.inset((borderThickness / 2).toFloat(), (borderThickness / 2).toFloat())
        when (shape) {
            is OvalShape -> canvas.drawOval(rect, borderPaint)
            is RoundRectShape -> canvas.drawRoundRect(rect, radius, radius, borderPaint)
            else -> canvas.drawRect(rect, borderPaint)
        }
    }


}