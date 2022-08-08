package com.example.openglesdemo01.renderer

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import com.example.openglesdemo01.R
import com.example.openglesdemo01.Util.MatrixHelper
import com.example.openglesdemo01.Util.isDebugVersion
import com.example.openglesdemo01.Util.readStringFromRaw
import com.example.openglesdemo01.view.ShaderHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AirHockeyRenderer2(private val context: Context) : GLSurfaceView.Renderer {
    private var programId = 0

    private val vertexData: FloatBuffer = ByteBuffer
        .allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(tableVerticesWithTriangles)


    /**
     * 缓存a_Color的位置
     */
    private var aColorLocation = 0


    /**
     * 缓存a_Position的位置
     */
    private var aPositionLocation = 0

    private val projectionMatrix: FloatArray = FloatArray(16)

    /**
     * 缓存u_Matrix的位置
     */
    private var uMatrixLocation = 0


    /**
     * 模型矩阵
     */
    private val modelMatrix = FloatArray(16)


    companion object {

        private const val POSITION_COMPONENT_COUNT = 4

        private const val BYTES_PER_FLOAT = 4

        private const val A_POSITION = "a_Position"

        private const val A_COLOR = "a_Color"
        private const val COLOR_COMPONENT_COUNT = 3
        private const val STRIDE =
            (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT

        private const val U_MATRIX = "u_Matrix"

        //坐标数组
        private val tableVerticesWithTriangles: FloatArray = floatArrayOf(
            // 属性的顺序: X, Y, Z, W, R, G, B
            // 三角形扇形
            0f, 0f, 0f, 1.5f, 1f, 1f, 1f,
            -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
            0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
            0.5f, 0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
            -0.5f, 0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
            // 中线
            -0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,
            0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,
            // 两个木槌
            0f, -0.4f, 0f, 1.25f, 0f, 0f, 1f,
            0f, 0.4f, 0f, 1.75f, 1f, 0f, 0f
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
        uMatrixLocation = glGetUniformLocation(programId, U_MATRIX)
        // 显式声明运行该程序，并且尝试找出两个变量的位置
        glUseProgram(programId)
        aColorLocation = glGetAttribLocation(programId, A_COLOR)
        aPositionLocation = glGetAttribLocation(programId, A_POSITION)

        // 也可以调用flip来变为读取模式
        vertexData.position(0)
        // 传输顶点数组
        glVertexAttribPointer(
            aPositionLocation,
            POSITION_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )


        // 启用属性
        glEnableVertexAttribArray(aPositionLocation)

        // 重置position，使其对准第一个color数据的开头
        vertexData.position(POSITION_COMPONENT_COUNT)
        glVertexAttribPointer(
            aColorLocation,
            COLOR_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        glEnableVertexAttribArray(aColorLocation)


    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        MatrixHelper.perspectiveM(
            projectionMatrix,
            55f,
            width.toFloat() / height.toFloat(),
            1f,
            10f
        )

        setIdentityM(modelMatrix, 0)
        translateM(modelMatrix, 0, 0f, 0f, -2f)

        val temp = FloatArray(16)
        //矩阵相乘
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.size)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0)

        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)

        glDrawArrays(GL_LINES, 6, 2)

        glDrawArrays(GL_POINTS, 8, 1)

        glDrawArrays(GL_POINTS, 9, 1)
    }
}