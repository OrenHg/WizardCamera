package com.shevelev.wizard_camera.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import android.os.Handler
import android.util.AttributeSet
import android.util.SizeF
import android.view.Gravity
import android.view.SurfaceHolder
import android.widget.FrameLayout
import com.shevelev.wizard_camera.bitmap.filters.GLSurfaceFilterBase

class GLSurfaceViewBitmap
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private lateinit var filter: GLSurfaceFilterBase

    private lateinit var bitmapSize: SizeF

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val areaWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        val areaHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()

        var testSize = (areaHeight / bitmapSize.height) * bitmapSize.width

        if(testSize <= areaWidth) {
            setMeasuredDimension(testSize.toInt(), areaHeight.toInt())
        } else {
            testSize = (areaWidth / bitmapSize.width) * bitmapSize.height
            setMeasuredDimension(areaWidth.toInt(), testSize.toInt())
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        super.surfaceDestroyed(holder)
        filter.release()
    }

    fun getBitmap(callback: (Bitmap?) -> Unit) = filter.startGetFrameAsBitmap(handler, callback)

    private fun setBitmapParameters(bitmap: Bitmap) {
        bitmapSize = SizeF(bitmap.width.toFloat(), bitmap.height.toFloat())
    }

    companion object {
        fun createAndAddToView(
            context: Context,
            root: FrameLayout,
            bitmap: Bitmap,
            filter: GLSurfaceFilterBase): GLSurfaceViewBitmap {
            val surfaceView = GLSurfaceViewBitmap(context)

            surfaceView.setBitmapParameters(bitmap)

            root.addView(surfaceView)

            val layoutParams = surfaceView.layoutParams as FrameLayout.LayoutParams
            layoutParams.gravity = Gravity.CENTER
            surfaceView.layoutParams = layoutParams

            surfaceView.setEGLContextClientVersion(3)
            surfaceView.setRenderer(filter)
            surfaceView.renderMode = RENDERMODE_CONTINUOUSLY

            filter.attachSurface(surfaceView)
            surfaceView.filter = filter

            if(root.childCount > 1) {           // To avoid an image flashing
                root.postDelayed({ root.removeViewAt(0) }, 250L)
            }

            return surfaceView
        }
    }
}