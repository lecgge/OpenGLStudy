@file:Suppress("UNUSED_EXPRESSION")

package com.example.openglesdemo01.view

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.openglesdemo01.renderer.AirHockeyRenderer5

class AirGLSurfaceView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
): GLSurfaceView(context, attrs) {

    private val renderer = AirHockeyRenderer5(context)

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY

        setOnTouchListener { v, event ->
            val normalizedX = (event.x / width.toFloat()) * 2 - 1
            val normalizedY = -((event.y / height.toFloat()) * 2 - 1)

            when(event?.actionMasked){
                MotionEvent.ACTION_DOWN ->{
                    queueEvent {
                       renderer.handleTouchPress(normalizedX,normalizedY)
                    }
                    true
                }

                MotionEvent.ACTION_MOVE ->{
                    queueEvent {
                        renderer.handleTouchDrag(normalizedX, normalizedY)
                    }
                    true
                }

                else ->{
                    performClick()
                    false
                }
            }
            true
        }
    }
}