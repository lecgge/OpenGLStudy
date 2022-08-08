package com.example.openglesdemo01.view

import com.example.openglesdemo01.R


import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {


    private lateinit var glSurfaceView: GLSurfaceView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Play with Points"

        glSurfaceView = GLSurfaceView(this)
        setContentView(glSurfaceView)

        //设置OpenGLES版本为2.0
        glSurfaceView.setEGLContextClientVersion(2)
        //设置Render实例
        glSurfaceView.setRenderer(PointsRender)
        /**渲染模式(render mode)分为两种，
         * 一个是GLSurfaceView主动刷新(continuously)，
         * 不停的回调Renderer的onDrawFrame，
         * 另外一种叫做被动刷新（when dirty），
         * 就是当请求刷新时才调一次onDrawFrame。
         */
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    companion object PointsRender : GLSurfaceView.Renderer {
        private const val VERTEX_SHADER =
            "void main() {\n" +
                    "gl_Position = vec4(0.0, 0.0, 0.0, 1.0);\n" +
                    "gl_PointSize = 20.0;\n" +
                    "}\n"
        private const val FRAGMENT_SHADER =
            "void main() {\n" +
                    "gl_FragColor = vec4(1., 0., 0.0, 1.0);\n" +
                    "}\n"
        private var mGLProgram: Int = -1

        //此方法是用来绘制每帧的，所以每次刷新都会被调一次，所有的绘制都发生在这里。
        override fun onDrawFrame(p0: GL10?) {
            // 清除颜色缓冲区，因为我们要开始新一帧的绘制了，所以先清理，以免有脏数据。
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            // 告诉OpenGL，使用我们在onSurfaceCreated里面准备好了的shader program来渲染
            GLES20.glUseProgram(mGLProgram)
            // 开始渲染，发送渲染点的指令， 第二个参数是offset，第三个参数是点的个数。目前只有一个点，所以是1。
            GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1)
        }

        //此回调，会在surface发生改变时调用，通常是size发生变化。
        override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
            GLES20.glViewport(0, 0, p1, p2)
        }

        /**
         * 这个是最先被回调到的方法，告诉你系统层面，已经ready了，你可以开始做你的事情了。
         * 一般我们会在此方法里面做一些初始化工作，比如编译链接shader程序，初始化buffer等。
         */
        override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
            //把背景，或者叫作画布，画成黑色，不透明
            //用参数指定的(r, g, b, a)这个颜色来初始化颜色缓冲区（color buffer）。
            GLES20.glClearColor(0f, 0f, 0f, 1f)//参数顺序 r, g, b, a

            //编译和链接shader  顶点着色器
            val vsh = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER) //返回的是它的句柄
            GLES20.glShaderSource(vsh, VERTEX_SHADER)
            GLES20.glCompileShader(vsh)

            //编译fragment shader 片元着色器
            val fsh = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
            GLES20.glShaderSource(fsh, FRAGMENT_SHADER)
            GLES20.glCompileShader(fsh)


            mGLProgram = GLES20.glCreateProgram() // 创建shader program句柄
            GLES20.glAttachShader(mGLProgram, vsh)// 把vertex shader添加到program
            GLES20.glAttachShader(mGLProgram, fsh)// 把fragment shader添加到program
            GLES20.glLinkProgram(mGLProgram)// 做链接，可以理解为把两种shader进行融合，做好投入使用的最后准备工作

            GLES20.glValidateProgram(mGLProgram) // 让OpenGL来验证一下我们的shader program，并获取验证的状态

            val status = IntArray(1)
            GLES20.glGetProgramiv(mGLProgram, GLES20.GL_VALIDATE_STATUS, status, 0) // 获取验证的状态
            Log.d(TAG, "validate shader program: " + GLES20.glGetProgramInfoLog(mGLProgram))
        }
    }
}