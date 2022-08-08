package com.example.openglesdemo01

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.openglesdemo01.view.AirGLSurfaceView

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {


    private lateinit var glSurfaceView: AirGLSurfaceView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        glSurfaceView = AirGLSurfaceView(this)
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