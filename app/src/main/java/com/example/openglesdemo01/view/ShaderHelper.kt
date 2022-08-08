package com.example.openglesdemo01.view

import android.opengl.GLES20.*
import android.util.Log
import com.example.openglesdemo01.Util.LogU

object ShaderHelper {
    private const val tag = "ShaderHelper"

    fun compileVertexShader(shaderCode: String) = compileShader(GL_VERTEX_SHADER, shaderCode)

    fun compileFragmentShader(shaderCode: String) = compileShader(GL_FRAGMENT_SHADER, shaderCode)

    fun compileShader(type: Int, shaderCode: String): Int {
        val  shaderObjectId = glCreateShader(type)
        if (type == 0) {
            LogU.w(tag, message = "Could note create new shader")
            return 0
        }
        glShaderSource(shaderObjectId,shaderCode)
        glCompileShader(shaderObjectId)
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
        val programObjectId = glCreateProgram()
        if (programObjectId == 0) {
            LogU.w(tag, "Could not create new program")
            return 0
        }
        glAttachShader(programObjectId,vertexShaderId)
        glAttachShader(programObjectId,fragmentShaderId)
        glLinkProgram(programObjectId)
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
        glValidateProgram(programObjectId)
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

