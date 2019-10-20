package com.project.homework_2.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import android.graphics.*
import android.graphics.Bitmap
import com.project.homework_2.models.User


/**
 * Отображает аватар пользователя, а в слчае отсутствия первую букву имени
 * @property paint Paint
 * @property startX Float стартовая коорд-а X отрисовки
 * @property startY Float стартовая коорд-а Y отрисовки
 * @property size Int размер изображения отрисовки
 * @property user User? пользователь
 */
class AvatarView(cxt: Context, attrsSet: AttributeSet): AppCompatImageView(cxt, attrsSet) {

    companion object {
        const val DEFAULT_BACKGROUND_COLOR = "#edc9af"
        const val TEXT_SIZE = 60f
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = TEXT_SIZE
        isFakeBoldText = true
    }
    private var startX = 0f
    private var startY = 0f
    private var size = 0

    var user: User? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width >= height) {
            size = height
            startX = (width - height) / 2f
            startY = 0f
        } else {
            size = width
            startX = 0f
            startY = (height - width) / 2f
        }

        user?.run {
            if (avatar == null) {
                drawDefaultImage(fullname[0], canvas) // Передается первая буква имени
            } else {
                val roundBitmap = getRoundedBitmap(avatar, size)
                canvas.drawBitmap(roundBitmap, startX, startY, null)
            }
        }
    }

    /**
     * В случае отсутствия аватара пользователя
     * @param letter Char
     * @param canvas Canvas
     */
    private fun drawDefaultImage(letter: Char, canvas: Canvas) {
        val radius = size / 2f
        with(paint) {
            color = Color.parseColor(DEFAULT_BACKGROUND_COLOR)
            canvas.drawCircle(startX + radius, startY + radius, radius, this)

            color = Color.WHITE
            canvas.drawText(letter.toString().capitalize(), startX + radius - TEXT_SIZE / 3, startY + radius + TEXT_SIZE / 3, this)
        }
    }

    /**
     * Аватар пользователя (bitmap изобр.) в форме окружности
     * @param bitmap Bitmap
     * @param radius Int
     * @return Bitmap
     */
    private fun getRoundedBitmap(bitmap: Bitmap, radius: Int): Bitmap {
        val finalBitmap =
            if (bitmap.width != radius || bitmap.height != radius)
                Bitmap.createScaledBitmap(bitmap, radius, radius, false)
            else
                bitmap

        val output = Bitmap.createBitmap(finalBitmap.width, finalBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
        }

        canvas.drawCircle(finalBitmap.width / 2f, finalBitmap.height / 2f, width / 2f, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(finalBitmap, 0f, 0f, paint)

        return output
    }

}