package com.example.openglesdemo01.Util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLUtils
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.math.tan

/**
 * 将着色器加载到内存中
 * @param resId  raw 目录下的 GLSL文件id
 */
fun Context.readStringFromRaw(@RawRes resId: Int): String {
    //通过Buffer流来读取GLSL文件
    return runCatching {
        val builder = StringBuilder()
        val reader = BufferedReader(InputStreamReader(resources.openRawResource(resId)))
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

fun Context.isDebugVersion(): Boolean =
    kotlin.runCatching {
        (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }.getOrDefault(false)


object MatrixHelper {
    fun perspectiveM(m: FloatArray, yFovInDegrees: Float, aspect: Float, n: Float, f: Float) {
        // 计算弧度制的角度
        val angleInRadians = (yFovInDegrees * Math.PI / 180.0).toFloat()
        // 计算焦距
        val a = (1.0 / tan(angleInRadians / 2.0)).toFloat()

        m[0] = a / aspect
        m[1] = 0f
        m[2] = 0f
        m[3] = 0f
        m[4] = 0f
        m[5] = a
        m[6] = 0f
        m[7] = 0f
        m[8] = 0f
        m[9] = 0f
        m[10] = -((f + n) / (f - n))
        m[11] = -1f
        m[12] = 0f
        m[13] = 0f
        m[14] = -((2f * f * n) / (f - n))
        m[15] = 0f
    }
}

fun Context.loadTexture(@DrawableRes resourceId: Int): Int {
    val textureObjectIds = IntArray(1)
    glGenTextures(1,textureObjectIds,0)
    if (textureObjectIds[0] == 0) {
        LogU.w(message = "Could not generate a new OpenGL texture object.")
        return 0
    }

    val option = BitmapFactory.Options().apply {
        inScaled = false
    }
    val bitmap : Bitmap? = BitmapFactory.decodeResource(resources,resourceId,option)
    if (bitmap == null) {
        LogU.w(message = "Resource ID $resourceId could not be decoded.")
        glDeleteTextures(1,textureObjectIds,0)
        return 0
    }

    glBindTexture(GL_TEXTURE_2D,textureObjectIds[0])

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

    // 将位图数据加载到OpenGL
    GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
    // 回收位图
    bitmap.recycle()
    // 生成MIP映射需要的纹理
    glGenerateMipmap(GL_TEXTURE_2D)
    // 解除纹理绑定（传入的纹理id为0）
    glBindTexture(GL_TEXTURE_2D, 0)
    return textureObjectIds[0]
}
