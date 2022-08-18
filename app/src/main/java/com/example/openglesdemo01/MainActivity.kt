package com.example.openglesdemo01

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.openglesdemo01.renderer.DemoRenderer01
import com.example.openglesdemo01.renderer.DemoRenderer02
import com.example.openglesdemo01.renderer.DemoRenderer03
import com.example.openglesdemo01.renderer.DemoRenderer04

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var glSurfaceView : GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glSurfaceView = GLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(DemoRenderer04(this))
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        setContentView(glSurfaceView)

    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

}