package com.example.openglesdemo01.renderer

import android.content.Context
import android.content.res.Resources
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.util.Log
import androidx.annotation.RawRes
import com.example.openglesdemo01.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *
 * 三角形
 */
class DemoRenderer03(val context: Context) : GLSurfaceView.Renderer {


    private var uColorLocation: Int = 0
    private var aPositionLocation: Int = 0

    private var programObjectId : Int = 0

    private val vertexData: FloatBuffer = ByteBuffer
        .allocateDirect(tableTriangle.size * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(tableTriangle)

    companion object{
        private const val tag = "Shader"

        private const val U_COLOR = "u_Color"

        private const val A_POSITION = "a_Position"

        private const val POSITION_COMPONENT_COUNT = 2


        const val BYTES_PER_FLOAT = 4

        private val tableTriangle : FloatArray = floatArrayOf(
            -0.5F, -0.5F,
            0.5F, 0.5F,
            -0.5F, 0.5F,
        )
    }
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f,0f,0f,0f)

        val vertexShader = compileVertexShader(ReadStringFromRaw(R.raw.simple_vertex_shader))
        val fragmentShader =
            compileFragmentShader(ReadStringFromRaw(R.raw.simple_fragment_shader))

        programObjectId = glCreateProgram()
        if (programObjectId == 0) {
            return
        }
        glAttachShader(programObjectId,vertexShader)
        glAttachShader(programObjectId,fragmentShader)

        glLinkProgram(programObjectId)
        val linkStatus = IntArray(1)
        glGetProgramiv(programObjectId, GL_LINK_STATUS,linkStatus,0)

        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId)

            return
        }

        glUseProgram(programObjectId)
        uColorLocation = glGetUniformLocation(programObjectId, U_COLOR)
        aPositionLocation = glGetAttribLocation(programObjectId, A_POSITION)


        vertexData.position(0)



        glVertexAttribPointer(
            aPositionLocation, POSITION_COMPONENT_COUNT,
            GL_FLOAT, false, 0, vertexData
        )

        glEnableVertexAttribArray(aPositionLocation)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0,0,width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        glUniform4f(uColorLocation,1f,1f,1f,1f)
        Log.d("TAG", "onSurfaceCreated: $aPositionLocation")
        glDrawArrays(GL_TRIANGLES,0,3)
    }

    fun ReadStringFromRaw(@RawRes resId: Int): String {
        //通过Buffer流来读取GLSL文件
        return runCatching {
            val builder = StringBuilder()
            val reader = BufferedReader(InputStreamReader(context.resources.openRawResource(resId)))
            var nextLine: String? = reader.readLine()
            while (nextLine != null) {
                builder.append(nextLine).append("\n")
                nextLine = reader.readLine()
            }
            reader.close()
            builder.toString()
        }.onFailure {
            when (it) {
                is IOException -> {
                    throw RuntimeException("Could not open resource: $resId", it)
                }
                is Resources.NotFoundException -> {
                    throw RuntimeException("Resource not found: $resId", it)
                }
                else -> {

                }
            }
        }.getOrThrow()
    }


    fun compileVertexShader(shaderCode: String) = compileShader(GL_VERTEX_SHADER, shaderCode)

    fun compileFragmentShader(shaderCode: String) = compileShader(GL_FRAGMENT_SHADER, shaderCode)

    /**
     * 创建新的OpenGL着色器对象，编译着色器代码并且返回代表那段着色器代码的着色器对象
     * @param type GLES20.GL_VERTEX_SHADER　or GLES20.GL_FRAGMENT_SHADER
     * @param shaderCode readStringFromRaw()读取的GLSL代码
     */
    fun compileShader(type: Int, shaderCode: String): Int {
        //创建一个新的着色器对象，将这个对象的ID存入shaderObjectId
        //这个整形值就是OpenGL对象的引用。
        val  shaderObjectId = glCreateShader(type)
        if (type == 0) {
            Log.w(tag, "Could note create new shader")
            return 0
        }
        //将着色器代码上传到着色器对象中
        glShaderSource(shaderObjectId,shaderCode)
        //编译先前上传到sharderObjectId的源代码
        glCompileShader(shaderObjectId)
        //取出编译状态
        val compilerStatus:IntArray = IntArray(1)
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS,compilerStatus,0)

        Log.d(tag, "Results of compiling source:" +
                "\n$shaderCode\n:${glGetShaderInfoLog(shaderObjectId)}")

        if (compilerStatus[0] == 0) {
            // 编译失败，删除着色器
            glDeleteShader(shaderObjectId)
            Log.d(tag, "Compilation of shader failed.")
            return 0
        }
        return shaderObjectId
    }
}