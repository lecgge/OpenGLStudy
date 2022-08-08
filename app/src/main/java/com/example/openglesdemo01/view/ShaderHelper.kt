package com.example.openglesdemo01.view

import android.opengl.GLES20
import android.opengl.GLES20.*
import android.util.Log
import com.example.openglesdemo01.Util.LogU

object ShaderHelper {
    private const val tag = "ShaderHelper"

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
            LogU.w(tag, message = "Could note create new shader")
            return 0
        }
        //将着色器代码上传到着色器对象中
        glShaderSource(shaderObjectId,shaderCode)
        //编译先前上传到sharderObjectId的源代码
        glCompileShader(shaderObjectId)
        //取出编译状态
        val compilerStatus:IntArray = IntArray(1)
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS,compilerStatus,0)

        LogU.v(tag, message = "Results of compiling source:" +
                "\n$shaderCode\n:${glGetShaderInfoLog(shaderObjectId)}")

        if (compilerStatus[0] == 0) {
            // 编译失败，删除着色器
            glDeleteShader(shaderObjectId)
            LogU.w(tag, "Compilation of shader failed.")
            return 0
        }
        return shaderObjectId
    }

    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int) : Int {
        //创建OpenGL程序对象，得到新建对象的ID
        val programObjectId = glCreateProgram()
        if (programObjectId == 0) {
            LogU.w(tag, "Could not create new program")
            return 0
        }
        //把顶点着色器和对象着色器都附加到程序对象上。
        glAttachShader(programObjectId,vertexShaderId)
        glAttachShader(programObjectId,fragmentShaderId)
        //链接程序
        glLinkProgram(programObjectId)
        //获取链接状态
        val linkStatus = IntArray(1)
        glGetProgramiv(programObjectId, GL_LINK_STATUS,linkStatus,0)
        LogU.v(tag, "Results of linking program:\n${glGetProgramInfoLog(programObjectId)}")

        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId)
            LogU.w(tag, "Linking of program failed.")
            return 0
        }
        return programObjectId
    }

    fun validateProgram(programObjectId:Int):Boolean{
        //验证这个程序
        glValidateProgram(programObjectId)
        //获取验证状态
        val validateStatus = IntArray(1)
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS,validateStatus,0)
        LogU.v(tag, "Results of validating program: " + validateStatus[0]
                + "\nLog:" + glGetProgramInfoLog(programObjectId))
        return validateStatus[0] != 0
    }

    fun buildProgram(vertexShaderSource: String, fragmentShaderSource: String): Int {
        val vertexShaderId = compileVertexShader(vertexShaderSource)
        val fragmentShaderId = compileFragmentShader(fragmentShaderSource)
        val program = linkProgram(vertexShaderId, fragmentShaderId)
        validateProgram(program)
        return program
    }
}

