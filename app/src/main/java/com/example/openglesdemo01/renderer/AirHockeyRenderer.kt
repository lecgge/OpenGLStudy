package com.example.openglesdemo01.renderer

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.openglesdemo01.R
import com.example.openglesdemo01.Util.isDebugVersion
import com.example.openglesdemo01.Util.readStringFromRaw
import com.example.openglesdemo01.view.ShaderHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AirHockeyRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private var programId = 0

    /**
     * 使用ByteBuffer.allocateDirect()分配一块本地内存，这块内存不会被垃圾回收器管理，传入空间大小，单位字节
     * order()告诉字节缓冲区按照本地字节序组织内存
     * asFloatBuffer() 得到一个可以反映底层字节的FloatBuffer类实例。
     */

    private val vertexData: FloatBuffer = ByteBuffer
        .allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(tableVerticesWithTriangles)


    /**
     * 缓存u_Color变量的位置
     */
    private var uColorLocation = 0

    /**
     * 缓存a_Position的位置
     */
    private var aPositionLocation = 0

    companion object {
        private const val POSITION_COMPONENT_COUNT = 2

        private const val BYTES_PER_FLOAT = 4

        private const val U_COLOR = "u_Color"

        private const val A_POSITION = "a_Position"

        //坐标数组
        private val tableVerticesWithTriangles: FloatArray = floatArrayOf(
            //第一个三角形
            -0.5F, -0.5F,
            0.5F, 0.5F,
            -0.5F, 0.5F,
            //第二个三角形
            -0.5F, -0.5F,
            0.5F, -0.5F,
            0.5F, 0.5F,
            //中线
            -0.5F, 0F,
            0.5F, 0F,
            //顶点
            0f, -0.25f,
            0f, 0.25f,
            //矩形
            -0.6f,-0.6f,
            -0.6f,0.4f,
            -0.5f,0.5f,

            -0.6f,-0.6f,
            -0.5f,0.5f,
            -0.5f,-0.5f,
            //矩形
            0.5f,-0.5f,
            0.5f,0.5f,
            0.6f,0.4f,

            0.5f,-0.5f,
            0.6f,0.4f,
            0.6f,-0.6f,


        )
    }

    val vertexShaderCode = context.readStringFromRaw(R.raw.simple_vertex_shader)
    val fragmentShaderCode = context.readStringFromRaw(R.raw.simple_fragment_shader)

    override fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?
    ) {
        // 设置ClearColor
        glClearColor(0F, 0F, 0F, 0F)
        // 读取glsl代码
        val vertexShaderCode = context.readStringFromRaw(R.raw.simple_vertex_shader)
        val fragmentShaderCode = context.readStringFromRaw(R.raw.simple_fragment_shader)
        // 创建着色器，这里未验证为0的情况
        val vertexShader = ShaderHelper.compileVertexShader(vertexShaderCode)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderCode)
        // 链接着色器，创建程序
        programId = ShaderHelper.linkProgram(vertexShader, fragmentShader)
        // 在Debug状态下验证程序
        if (context.isDebugVersion()) {
            ShaderHelper.validateProgram(programId)
        }
        // 显式声明运行该程序，并且尝试找出两个变量的位置
        glUseProgram(programId)
        uColorLocation = glGetUniformLocation(programId, U_COLOR)
        aPositionLocation = glGetAttribLocation(programId, A_POSITION)

        // 将缓冲区的读取指针设置为0，确保它会从头开始读取数据
        vertexData.position(0)
        // 传输顶点数组，告诉OpenGL，它可以在缓冲区vertexData中找到a_Position对应的数据
        /**
         * 第一个参数 ： int index ->这个是属性位置
         * 第二个参数 ： int size ->这个是每个属性的数据的计数，由于我们只定义了顶点的x,y，因此我们在这里传入2；
         * 但是我们在顶点着色器中定义了每个顶点是vec4,它有四个分量。如果一个分量没有被指定值，默认情况下，OpenGL会把前3个分量设为0，最后一个分量设为1
         * 第三个参数 ： int type 这是数据的类型。
         * 第四个参数 ： boolean normalized 只有使用整数数据的时候，这个参数才有意义。因此可以暂时把它安全地忽略
         * 第五个参数 ： int stride 只有当一个数组存储多余一个属性时，它才有意义。如：在顶点数组中，不仅存储了顶点数据，还存储了颜色数据
         * 第六个参数 ： Buffer ptr 这个参数告诉OpenGL去哪读取数据，
         */
        glVertexAttribPointer(
            aPositionLocation, POSITION_COMPONENT_COUNT,
            GL_FLOAT, false, 0, vertexData
        )
        // 启用属性
        glEnableVertexAttribArray(aPositionLocation)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
        //调用glUniform4f()来更新着色器代码中u_Color的值
        glUniform4f(uColorLocation,1f,1f,1f,1f)
        //第一个参数告诉OpenGL我们想要画三角形。要绘制三角形，我们需要在每个三角形中至少传递三个顶点。
        // 第二个参数告诉OpenGL从顶点数组的开头开始读取顶点（offset），
        // 第三个参数告诉OpenGL读取六个顶点。
        glDrawArrays(GL_TRIANGLES,0,6)


        glUniform4f(uColorLocation,1f,0f,0f,1f)
        glDrawArrays(GL_LINES,6,2)

        glUniform4f(uColorLocation,0f,0f,1f,1f)
        glDrawArrays(GL_POINTS,8,1)

        glUniform4f(uColorLocation,1f,0f,0f,1f)
        glDrawArrays(GL_POINTS,9,1)

        glUniform4f(uColorLocation,0.2125f, 0.7154f, 0.0721f,1f)
        glDrawArrays(GL_TRIANGLES, 10, 6)
        glDrawArrays(GL_TRIANGLES,16,6)
    }
}