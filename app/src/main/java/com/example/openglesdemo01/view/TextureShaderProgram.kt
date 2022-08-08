package com.example.openglesdemo01.view

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.*
import com.example.openglesdemo01.R

class TextureShaderProgram(
    context: Context,
    vertexShaderResId: Int = R.raw.texture_vertex_shader,
    fragmentShaderResId: Int = R.raw.texture_fragment_shader
) : ShaderProgram(context, vertexShaderResId, fragmentShaderResId) {

    private val uMatrixLocation: Int = getUniformLocation(U_MATRIX)

    private val uTextureShaderLocation: Int = getUniformLocation(U_TEXTURE_UNIT)

    val aPositionLocation: Int = getAttribLocation(A_POSITION)

    val aTextureCoordinatesLocation: Int = getAttribLocation(A_TEXTURE_COORDINATES)

    companion object {
        private const val U_MATRIX = "u_Matrix"
        private const val U_TEXTURE_UNIT = "u_TextureUnit"

        private const val A_POSITION = "a_Position"
        private const val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
    }

    fun setUniforms(matrix: FloatArray, textureId: Int) {
        //传递矩阵的值
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        //将活动纹理单位设置为纹理单位0
        glActiveTexture(GL_TEXTURE0)
        //将纹理绑定到该单元上
        glBindTexture(GL_TEXTURE_2D,textureId)
        //指定sampler对应的的纹理单元
        glUniform1i(uTextureShaderLocation,0)
    }
}