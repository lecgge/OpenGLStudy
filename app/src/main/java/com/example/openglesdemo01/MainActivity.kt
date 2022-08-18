package com.example.openglesdemo01

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.airhockey.AirHockeyRender.DemoRenderer05
import com.example.airhockey.AirHockeyRender.DemoRenderer06
import com.example.airhockey.AirHockeyRender.DemoRenderer07

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var glSurfaceView : GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glSurfaceView = GLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(DemoRenderer07(this))
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