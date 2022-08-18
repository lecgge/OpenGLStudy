package com.example.airhockey.AirHockeyRender

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.opengl.Matrix.orthoM
import android.util.Log
import com.example.openglesdemo01.R
import com.example.openglesdemo01.ShaderHelper
import com.example.openglesdemo01.isDebugVersion
import com.example.openglesdemo01.readStringFromRaw
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *正方体---第二种实现方式
 *
 */
class DemoRenderer07(private val context: Context) : GLSurfaceView.Renderer {


    /**
     * 缓存a_Color的位置
     */
    private var aColorLocation = 0
    private var programId = 0 //添加一个类变量来缓存程序id
    private var aPositionLocation = 0

    private val projectionMatrix: FloatArray = FloatArray(16)

    /**
     * 缓存u_Matrix的位置
     */
    private var uMatrixLocation = 0

    companion object {  //伴生类
        private const val POSITION_COMPONENT_COUNT = 3//每个顶点有两个分量，x和yz
        private const val BYTES_PER_FLOAT = 4 //Java中的浮点有32位，而一个字节有8位。也就是说每个浮点占用4个字节
        private const val A_POSITION = "a_Position"
        private const val A_COLOR = "a_Color"
        private const val U_MATRIX = "u_Matrix"
        private const val COLOR_COMPONENT_COUNT = 3
        private const val STRIDE =
            (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT  //使用STRIDE来告诉OpenGL每个位置数据之间有多少字节，以便它知道需要跳过多远
    }

    //添加一个属性来存储我们得到的坐标
    private val tableVerticesWithTriangles: FloatArray

    private val tempArray = floatArrayOf(
        // 属性的顺序: X, Y,Z, R, G, B
        // 第一个正四棱锥
        0f, -0.5f, 0f, 1f, 1f, 1f,
        -0.5f, -0.5f, 0.5f, 1f, 0f, 0f,
        0.5f, -0.5f, 0.5f, 0f, 1f, 0f,
        0.5f, -0.5f, -0.5f, 0f, 0f, 1f,
        -0.5f, -0.5f, -0.5f, 0f, 1f, 1f,
        -0.5f, -0.5f, 0.5f, 1f, 0f, 0f,
    )

    init {

        val temp : ArrayList<Float> = ArrayList()
        for (i in 0..50) {
            temp.add(tempArray[0])
            temp.add(tempArray[1]+i*0.01f)
            temp.add(tempArray[2])
            temp.add(tempArray[3])
            temp.add(tempArray[4])
            temp.add(tempArray[5])

            temp.add(tempArray[6])
            temp.add(tempArray[7]+i*0.01f)
            temp.add(tempArray[8])
            temp.add(tempArray[9])
            temp.add(tempArray[10])
            temp.add(tempArray[11])

            temp.add(tempArray[12])
            temp.add(tempArray[13]+i*0.01f)
            temp.add(tempArray[14])
            temp.add(tempArray[15])
            temp.add(tempArray[16])
            temp.add(tempArray[17])

            temp.add(tempArray[18])
            temp.add(tempArray[19]+i*0.01f)
            temp.add(tempArray[20])
            temp.add(tempArray[21])
            temp.add(tempArray[22])
            temp.add(tempArray[23])

            temp.add(tempArray[24])
            temp.add(tempArray[25]+i*0.01f)
            temp.add(tempArray[26])
            temp.add(tempArray[27])
            temp.add(tempArray[28])
            temp.add(tempArray[29])

            temp.add(tempArray[30])
            temp.add(tempArray[31]+i*0.01f)
            temp.add(tempArray[32])
            temp.add(tempArray[33])
            temp.add(tempArray[34])
            temp.add(tempArray[35])

            temp.add(tempArray[0])
            temp.add(-(tempArray[1]+i*0.01f))
            temp.add(tempArray[2])
            temp.add(tempArray[3])
            temp.add(tempArray[4])
            temp.add(tempArray[5])

            temp.add(tempArray[6])
            temp.add(-(tempArray[7]+i*0.01f))
            temp.add(tempArray[8])
            temp.add(tempArray[9])
            temp.add(tempArray[10])
            temp.add(tempArray[11])

            temp.add(tempArray[12])
            temp.add(-(tempArray[13]+i*0.01f))
            temp.add(tempArray[14])
            temp.add(tempArray[15])
            temp.add(tempArray[16])
            temp.add(tempArray[17])

            temp.add(tempArray[18])
            temp.add(-(tempArray[19]+i*0.01f))
            temp.add(tempArray[20])
            temp.add(tempArray[21])
            temp.add(tempArray[22])
            temp.add(tempArray[23])

            temp.add(tempArray[24])
            temp.add(-(tempArray[25]+i*0.01f))
            temp.add(tempArray[26])
            temp.add(tempArray[27])
            temp.add(tempArray[28])
            temp.add(tempArray[29])

            temp.add(tempArray[30])
            temp.add(-(tempArray[31]+i*0.01f))
            temp.add(tempArray[32])
            temp.add(tempArray[33])
            temp.add(tempArray[34])
            temp.add(tempArray[35])
        }

        tableVerticesWithTriangles = FloatArray(temp.size)
        var i = 0
        temp.forEach {
            tableVerticesWithTriangles[i++] = it
        }

        Log.d("TAG", "arraySize: ${tableVerticesWithTriangles.size/36}")

    }

    //将数据复制到本机内存
    // allocateDirect()不使用JVM堆栈而是通过操作系统来创建内存块用作缓冲区，它与当前操作系统能够更好的耦合，因此能进一步提高I/O操作速度。但是分配直接缓冲区的系统开销很大，因此只有在缓冲区较大并长期存在，或者需要经常重用时，才使用这种缓冲区
    private val vertexData: FloatBuffer =
        ByteBuffer//创建了一个名为vertexData的缓冲区,FloatBuffer将用于在native内存中存储数据
            .allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)//分配了一块本机内存
            .order(ByteOrder.nativeOrder())//使用与平台相同的顺序
            .asFloatBuffer()//获得一个反映底层字节的FloatBuffer
            .put(tableVerticesWithTriangles)//将数据从Dalvik的内存复制到native内存


    override fun onSurfaceCreated(gl: GL10?, confing: EGLConfig?) {
        //  GLES20.glClearColor(1F,0F,0F,1F)//参数依次是red、green、blue、alpha,我们将红色设置为最大强度，屏幕将在被清除时变为红色（the screen will become red when cleared）（因为clearColor就是clear事件发生之后应当显示的颜色
        //设置ClearColor
        GLES20.glClearColor(0f, 0f, 0f, 0f)

        //读取着色器代码
        val vertexShaderCode = context.readStringFromRaw(R.raw.ball_vertex_shader)
        val framentShaderCode = context.readStringFromRaw(R.raw.ball_fragment_shader)
        // 创建着色器，这里验证为0的情况
        val vertexShader = ShaderHelper.compileVertextShader(vertexShaderCode)
        val fragmentShader = ShaderHelper.compileFragmentShader(framentShaderCode)

        programId = ShaderHelper.linkProgram(vertexShader, fragmentShader)

        uMatrixLocation = glGetUniformLocation(programId, U_MATRIX)
        //缓存a_Position的位置
        aPositionLocation = glGetAttribLocation(programId, A_POSITION)
        // 缓存a_Color的位置
        aColorLocation = glGetAttribLocation(programId, A_COLOR)


        glUseProgram(programId)//启用创建的OpenGL程序
        //将顶点数据数组与Attribute关联将顶点数据数组与Attribute关联
        vertexData.position(0)
        glVertexAttribPointer(
            aPositionLocation,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )//
        glEnableVertexAttribArray(aPositionLocation)//启用顶点数组

        /**
         * 重置position，使其对准第一个color数据的开头
         */
        vertexData.position(POSITION_COMPONENT_COUNT)//OpenGL开始读取颜色属性时，我们希望它从第一个颜色属性开始
        GLES20.glVertexAttribPointer(
            aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData
        )
        GLES20.glEnableVertexAttribArray(aColorLocation)//为颜色属性启用顶点属性

        /**
         * 判断是否为debug版本
         */
        if (context.isDebugVersion()) {
            ShaderHelper.validateProgram(programId)
        }

        glEnable(GL_DEPTH_TEST)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)//这个方法设置了viewport的大小，这告诉了OpenGL可用于渲染的surface的大小
        /**
         * 创建正交投影矩阵
         */

        //是否为横屏
        val isLandscape = width > height
        val aspectRatio =
            if (isLandscape) (width.toFloat()) / (height.toFloat()) else (height.toFloat()) / (width.toFloat())//toFloat()保证输出的是浮点数，确定比例大小
        if (isLandscape) {
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        } else {
            //竖屏或正方形
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
        }


    }

    /**
     * 往屏幕绘制内容
     */
    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)//我们调用glClear(GL_COLOR_BUFFER_BIT)擦除了屏幕上的所有颜色，并使用我们之前调用glClearColor()来定义的颜色填充屏幕。


        Matrix.rotateM(projectionMatrix, 0, 0.3f, 1f, 1f, 0f)


        for (i in 0 until (tableVerticesWithTriangles.size/36)) {
            glDrawArrays(GL_TRIANGLE_FAN, i*6, 6)
        }



        glUniformMatrix4fv(
            uMatrixLocation,
            1,
            false,
            projectionMatrix,
            0
        )//uMatrixLocation uniform变量的位置 ，1是需要修改的矩阵的个数，false指明矩阵是列优先矩阵还是行优先矩阵，列优先矩阵应传入false，projectionMatrix表示矩阵的一维数组，0表示偏移量


    }


}