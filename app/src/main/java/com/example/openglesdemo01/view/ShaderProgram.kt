package com.example.openglesdemo01.view

import android.content.Context
import android.opengl.GLES20.*
import com.example.openglesdemo01.Util.readStringFromRaw

abstract class ShaderProgram(
    context: Context,
    vertexShaderResId: Int,
    fragmentShaderResId: Int
) {
    protected val programId: Int

    init {
        with(context) {
            programId = ShaderHelper.buildProgram(
                readStringFromRaw(vertexShaderResId),
                readStringFromRaw(fragmentShaderResId)
            )
        }
    }

    fun getAttribLocation(name: String): Int = glGetAttribLocation(programId, name)

    fun getUniformLocation(name: String): Int = glGetUniformLocation(programId, name)

    fun useProgram() {
        glUseProgram(programId)
    }
}