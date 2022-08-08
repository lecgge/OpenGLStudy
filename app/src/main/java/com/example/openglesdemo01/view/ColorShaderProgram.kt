package com.example.openglesdemo01.view

import android.content.Context

import androidx.compose.ui.graphics.Color
import android.opengl.GLES20.*
import com.example.openglesdemo01.R

class ColorShaderProgram(
    context: Context
): ShaderProgram(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader) {

    private val uMatrixLocation: Int = getUniformLocation(U_MATRIX)
    private val uColorLocation: Int = getUniformLocation(U_COLOR)

    val aPositionLocation: Int = getAttribLocation(A_POSITION)

    fun setUniforms(matrix: FloatArray, color: Color) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        glUniform4f(uColorLocation, color.red, color.green, color.blue, color.alpha)
    }

    companion object {
        private const val U_MATRIX = "u_Matrix"
        private const val U_COLOR = "u_Color"

        private const val A_POSITION = "a_Position"
    }
}
