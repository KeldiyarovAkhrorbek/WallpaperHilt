package com.projects.wallpaperkotlin.fragments.filter

import android.content.Context
import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import java.lang.Exception
import java.nio.IntBuffer

class SketchImage private constructor(builder: Builder) {
    // required
    private val context: Context = builder.context
    private val bitmap: Bitmap = builder.bitmap
    private var bmGray: Bitmap? = null
    private var bmInvert: Bitmap? = null
    private var bmBlur: Bitmap? = null
    private var bmBlend: Bitmap? = null

    fun getImageAs(type: Int, value: Int): Bitmap? {
        when (type) {
            ORIGINAL_TO_GRAY -> {
                bmGray = toGrayscale(bitmap, (101 - value).toFloat()) //101-i
                bmInvert = toInverted(bmGray, 1f) //i
                bmBlur = toBlur(bmInvert, 1f) //i
                bmBlend = colorDodgeBlend(bmBlur, bmGray, 100f)
                return bmBlend
            }
            ORIGINAL_TO_SKETCH -> {
                bmGray = toGrayscale(bitmap, (101 - value).toFloat()) //101-i
                bmInvert = toInverted(bmGray, value.toFloat()) //i
                bmBlur = toBlur(bmInvert, 100f) //i
                bmBlend = colorDodgeBlend(bmBlur, bmGray, 100f)
                return bmBlend
            }
            ORIGINAL_TO_COLORED_SKETCH -> {
                bmGray = toGrayscale(bitmap, 100f) //101-i
                bmInvert = toInverted(bmGray, value.toFloat()) //i
                bmBlur = toBlur(bmInvert, value.toFloat()) //i
                bmBlend = colorDodgeBlend(bmBlur, bmGray, 100f)
                return bmBlend
            }
            ORIGINAL_TO_SOFT_SKETCH -> {
                bmGray = toGrayscale(bitmap, (101 - value).toFloat()) //101-i
                bmInvert = toInverted(bmGray, value.toFloat()) //i
                bmBlur = toBlur(bmInvert, 1f) //i
                bmBlend = colorDodgeBlend(bmBlur, bmGray, 100f)
                return bmBlend
            }
            ORIGINAL_TO_SOFT_COLOR_SKETCH -> {
                bmGray = toGrayscale(bitmap, 100f) //101-i
                bmInvert = toInverted(bmGray, value.toFloat()) //i
                bmBlur = toBlur(bmInvert, (101 - value).toFloat()) //i
                bmBlend = colorDodgeBlend(bmBlur, bmGray, 100f)
                return bmBlend
            }
            GRAY_TO_SKETCH -> {
                bmGray = toGrayscale(bitmap, 1f) //101-i
                bmInvert = toInverted(bmGray, value.toFloat()) //i
                bmBlur = toBlur(bmInvert, 100f) //i
                bmBlend = colorDodgeBlend(bmBlur, bmGray, 100f)
                return bmBlend
            }
            GRAY_TO_COLORED_SKETCH -> {
                bmGray = toGrayscale(bitmap, value.toFloat()) //101-i
                bmInvert = toInverted(bmGray, value.toFloat()) //i
                bmBlur = toBlur(bmInvert, value.toFloat()) //i
                bmBlend = colorDodgeBlend(bmBlur, bmGray, 100f)
                return bmBlend
            }
            GRAY_TO_SOFT_SKETCH -> {
                bmGray = toGrayscale(bitmap, 100f) //101-i
                bmInvert = toInverted(bmGray, value.toFloat()) //i
                bmBlur = toBlur(bmInvert, 1f) //i
                bmBlend = colorDodgeBlend(bmBlur, bmGray, 100f)
                return bmBlend
            }
            GRAY_TO_SOFT_COLOR_SKETCH -> {
                bmGray = toGrayscale(bitmap, value.toFloat()) //101-i
                bmInvert = toInverted(bmGray, value.toFloat()) //i
                bmBlur = toBlur(bmInvert, 1f) //i
                bmBlend = colorDodgeBlend(bmBlur, bmGray, 100f)
                return bmBlend
            }
            SKETCH_TO_COLOR_SKETCH -> {
                bmGray = toGrayscale(bitmap, value.toFloat()) //101-i
                bmInvert = toInverted(bmGray, 100f) //i
                bmBlur = toBlur(bmInvert, 100f) //i
                bmBlend = colorDodgeBlend(bmBlur, bmGray, 100f)
                return bmBlend
            }
        }
        return bitmap
    }

    class Builder     // optional
        (// required
        val context: Context, val bitmap: Bitmap
    ) {
        fun build(): SketchImage {
            return SketchImage(this)
        }
    }

    private fun toGrayscale(bmpOriginal: Bitmap, saturation: Float): Bitmap {
        val width: Int
        val height: Int
        height = bmpOriginal.height
        width = bmpOriginal.width
        val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(saturation / 100)
        val f = ColorMatrixColorFilter(cm)
        paint.colorFilter = f
        c.drawBitmap(bmpOriginal, 0f, 0f, paint)
        return bmpGrayscale
    }

    private fun toInverted(src: Bitmap?, i: Float): Bitmap {
        val colorMatrix_Inverted = ColorMatrix(
            floatArrayOf(
                -1f,
                0f,
                0f,
                0f,
                255f,
                0f,
                -1f,
                0f,
                0f,
                255f,
                0f,
                0f,
                -1f,
                0f,
                255f,
                0f,
                0f,
                0f,
                i / 100,
                0f
            )
        )
        val colorFilter: ColorFilter = ColorMatrixColorFilter(
            colorMatrix_Inverted
        )
        val bitmap = Bitmap.createBitmap(
            src!!.width, src.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.colorFilter = colorFilter
        canvas.drawBitmap(src, 0f, 0f, paint)
        return bitmap
    }

    private fun toBlur(input: Bitmap?, i: Float): Bitmap? {
        return try {
            val rsScript = RenderScript.create(context)
            val alloc = Allocation.createFromBitmap(rsScript, input)
            val blur = ScriptIntrinsicBlur.create(rsScript, Element.U8_4(rsScript))
            blur.setRadius(i * 25 / 100)
            blur.setInput(alloc)
            val result = Bitmap.createBitmap(input!!.width, input.height, Bitmap.Config.ARGB_8888)
            val outAlloc = Allocation.createFromBitmap(rsScript, result)
            blur.forEach(outAlloc)
            outAlloc.copyTo(result)
            rsScript.destroy()
            result
        } catch (e: Exception) {
            // TODO: handle exception
            input
        }
    }

    /**
     * Blends 2 bitmaps to one and adds the color dodge blend mode to it.
     */
    fun colorDodgeBlend(source: Bitmap?, layer: Bitmap?, i: Float): Bitmap {
        val base = source!!.copy(Bitmap.Config.ARGB_8888, true)
        val blend = layer!!.copy(Bitmap.Config.ARGB_8888, false)
        val buffBase = IntBuffer.allocate(base.width * base.height)
        base.copyPixelsToBuffer(buffBase)
        buffBase.rewind()
        val buffBlend = IntBuffer.allocate(blend.width * blend.height)
        blend.copyPixelsToBuffer(buffBlend)
        buffBlend.rewind()
        val buffOut = IntBuffer.allocate(base.width * base.height)
        buffOut.rewind()
        while (buffOut.position() < buffOut.limit()) {
            val filterInt = buffBlend.get()
            val srcInt = buffBase.get()
            val redValueFilter = Color.red(filterInt)
            val greenValueFilter = Color.green(filterInt)
            val blueValueFilter = Color.blue(filterInt)
            val redValueSrc = Color.red(srcInt)
            val greenValueSrc = Color.green(srcInt)
            val blueValueSrc = Color.blue(srcInt)
            val redValueFinal = colordodge(redValueFilter, redValueSrc, i)
            val greenValueFinal = colordodge(greenValueFilter, greenValueSrc, i)
            val blueValueFinal = colordodge(blueValueFilter, blueValueSrc, i)
            val pixel =
                Color.argb((i * 255).toInt() / 100, redValueFinal, greenValueFinal, blueValueFinal)
            buffOut.put(pixel)
        }
        buffOut.rewind()
        base.copyPixelsFromBuffer(buffOut)
        blend.recycle()
        return base
    }

    private fun colordodge(in1: Int, in2: Int, i: Float): Int {
        val image = in2.toFloat()
        val mask = in1.toFloat()
        return (if (image == 255f) image else Math.min(
            255f,
            (mask.toLong() shl (i * 8).toInt() / 100) / (255 - image)
        )).toInt()
    }

    companion object {
        const val ORIGINAL_TO_GRAY = 0
        const val ORIGINAL_TO_SKETCH = 1
        const val ORIGINAL_TO_COLORED_SKETCH = 2
        const val ORIGINAL_TO_SOFT_SKETCH = 3
        const val ORIGINAL_TO_SOFT_COLOR_SKETCH = 4
        const val GRAY_TO_SKETCH = 5
        const val GRAY_TO_COLORED_SKETCH = 6
        const val GRAY_TO_SOFT_SKETCH = 7
        const val GRAY_TO_SOFT_COLOR_SKETCH = 8
        const val SKETCH_TO_COLOR_SKETCH = 9
    }

}