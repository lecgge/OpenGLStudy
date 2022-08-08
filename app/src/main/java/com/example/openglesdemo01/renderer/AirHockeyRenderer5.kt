package com.example.openglesdemo01.renderer

import android.content.Context
import android.media.Image
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import androidx.compose.ui.graphics.Color
import com.example.openglesdemo01.R
import com.example.openglesdemo01.Util.*
import com.example.openglesdemo01.objects.Mallet
import com.example.openglesdemo01.objects.Puck
import com.example.openglesdemo01.objects.Table
import com.example.openglesdemo01.view.ColorShaderProgram
import com.example.openglesdemo01.view.ShaderHelper
import com.example.openglesdemo01.view.TextureShaderProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AirHockeyRenderer5(private val context: Context) : GLSurfaceView.Renderer {

    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    private var malletPressed = false
    private lateinit var blueMalletPosition: Point

    private lateinit var redMalletPosition: Point

    private lateinit var previousBlueMalletPosition: Point


    /**
     * 透视投影矩阵
     */
    private val projectionMatrix: FloatArray = FloatArray(16)

    /**
     * 模型矩阵
     */
    private val modelMatrix = FloatArray(16)

    /**
     * [viewProjectionMatrix]的逆矩阵
     */
    private val invertedViewProjectionMatrix = FloatArray(16)

    /**
     * 冰球的位置和速度
     */
    private lateinit var puckPosition: Point
    private lateinit var puckVector: Vector


    private lateinit var table: Table
    private lateinit var mallet: Mallet
    private lateinit var puck: Puck

    private lateinit var textureShaderProgram: TextureShaderProgram
    private lateinit var colorShaderProgram: ColorShaderProgram

    private var texture: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 设置Clear颜色
        glClearColor(0F, 0F, 0F, 0F)

        table = Table()
        mallet = Mallet(0.08f,0.15f,32)
        puck = Puck(0.06f,0.02f,32)

        textureShaderProgram = TextureShaderProgram(context)
        colorShaderProgram = ColorShaderProgram(context)

        texture = context.loadTexture(R.drawable.img)

        blueMalletPosition = Point(0f, mallet.height / 2f, 0.4f)

        puckPosition = Point(0f, puck.height / 2f, 0f)
        puckVector = Vector(0f,0f,0f)
    }

    override fun onDrawFrame(gl: GL10?) {
        //根据速度向量更新冰球的位置
        puckPosition = puckPosition.translate(puckVector)
        //分别判断是否应当反转速度向量的x分量或z分量
        if (puckPosition.x < leftBound + puck.radius || puckPosition.x > rightBound - puck.radius) {
            puckVector = Vector(-puckVector.x, puckVector.y, puckVector.z).scale(0.8f)
        }
        if (puckPosition.z < farBound + puck.radius || puckPosition.z > nearBound - puck.radius) {
            puckVector = Vector(puckVector.x, puckVector.y, -puckVector.z).scale(0.8f)
        }

        //保证冰球不超出边界
        puckPosition = Point(
            clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
            puckPosition.y,
            clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius)
        )
        // 清除之前绘制的内容
        glClear(GL_COLOR_BUFFER_BIT)
        multiplyMM(viewProjectionMatrix,0,projectionMatrix,0,viewMatrix,0)
        //求4x4矩阵的逆矩阵
        invertM(invertedViewProjectionMatrix,0,viewProjectionMatrix,0)

        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        positionTableInScene()
        textureShaderProgram.useProgram()
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, texture)
        table.bindData(textureShaderProgram)
        table.draw()
        // 绘制Mallet
        positionObjectInScene(0F, mallet.height / 2F, -0.4F)
        colorShaderProgram.useProgram()
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, Color(1f, 0f, 0f))
        mallet.bindData(colorShaderProgram)
        mallet.draw()

        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y,blueMalletPosition.z)
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, Color(0f, 0f, 1f))
        // 请注意，我们不必两次定义对象数据，我们只要指定不同的位置，使用不同的颜色。
        mallet.draw()
        // 绘制Puck
        positionObjectInScene(puckPosition.x,puckPosition.y,puckPosition.z)
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, Color(0.8f, 0.8f, 1f))
        puck.bindData(colorShaderProgram)
        puck.draw()
        //模拟摩擦力
        puckVector = puckVector.scale(0.99f)
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        MatrixHelper.perspectiveM(projectionMatrix, 55f, width.toFloat() / height.toFloat(), 1f, 10f)

        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f)
    }

    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        setIdentityM(modelMatrix, 0)
        translateM(modelMatrix, 0, x, y, z)
        multiplyMM(
            modelViewProjectionMatrix, 0, viewProjectionMatrix,
            0, modelMatrix, 0
        )
    }


    private fun positionTableInScene() {
        setIdentityM(modelMatrix, 0)
        // 这张桌子是用X、Y坐标定义的，所以我们把它旋转90度，使它平放在xoz平面上
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f)
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
            0, modelMatrix, 0)
    }


    // 在AirHockeyRenderer添加以下方法，待会我们再实现它们
    fun handleTouchPress(normalizedX: Float, normalizedY: Float) {
        //计算出触摸点在三维世界中 形成的线
        val ray: Ray = convertNormalized2DPointToRay(normalizedX,normalizedY)
        //创建木槌的包围球
        val malletBoundingSphere = Sphere(
            Point(
                blueMalletPosition.x,
                blueMalletPosition.y,
                blueMalletPosition.z,
            ),
            mallet.height / 2f
        )
        //判定是否触摸到木槌，设置malletPressed的值
        malletPressed = malletBoundingSphere.intersects(ray)
    }
    fun handleTouchDrag(normalizedX: Float, normalizedY: Float) {
        if (malletPressed) {
            val ray = convertNormalized2DPointToRay(normalizedX,normalizedY)
            //定义一个平面以表示我们的桌子
            val plane = Plane(Point(0f, 0f, 0f), Vector(0f, 1f, 0f))
            //找出触摸点与平面相交的位置，我们将沿着这个平面移动木槌
            val touchedPoint : Point = plane.intersectionPoint(ray)
            //保存上一个位置
            previousBlueMalletPosition = blueMalletPosition
            blueMalletPosition = Point(
                clamp(
                    touchedPoint.x,
                    leftBound + mallet.baseRadius,
                    rightBound - mallet.baseRadius
                ),
                mallet.height / 2f,
                clamp(
                    touchedPoint.z,
                    0f+mallet.baseRadius,
                    nearBound - mallet.baseRadius
                )
            )
        }

        val distance:Float = vectorBetween(blueMalletPosition,puckPosition).length()
        //如果距离小于两者半径,说明发生了碰撞
        if (distance < puck.radius + mallet.baseRadius) {
            //设置最初的速度矢量
            puckVector = previousBlueMalletPosition to blueMalletPosition
        }
    }

    private fun convertNormalized2DPointToRay(normalizedX: Float, normalizedY: Float): Ray {
        //先在NDC坐标空间下进行考虑，给出触摸点对应的空间上的两点
        // 这件事很简单，NDC就是个正方体，肯定是其中某个点的z坐标为-1，而另一个点的z坐标为1
        val nearPointNdc = floatArrayOf(normalizedX, normalizedY, -1f, 1f)
        val farPointNdc = floatArrayOf(normalizedX, normalizedY, 1f, 1f)
        val nearPointWorld = FloatArray(4)
        val farPointWorld = FloatArray(4)
        //接下来用逆矩阵对两个点分别撤销变换，得到真实的世界坐标系下的坐标
        multiplyMV(
            nearPointWorld,0,invertedViewProjectionMatrix,0,nearPointNdc,0
        )
        multiplyMV(
            farPointWorld,0,invertedViewProjectionMatrix,0,farPointNdc,0
        )

        divideByW(nearPointWorld)
        divideByW(farPointWorld)

        val nearPointRay = Point(nearPointWorld[0],nearPointWorld[1],nearPointWorld[2])
        val farPointRay = Point(farPointWorld[0],farPointWorld[1],farPointWorld[2])

        return Ray(
            nearPointRay,
            nearPointRay to farPointRay
        )
    }

    private fun divideByW(vector: FloatArray) {
        vector[0] /= vector[3]
        vector[1] /= vector[3]
        vector[2] /= vector[3]
    }


    /**
     * 边界
     */
    private val leftBound = -0.5F
    private val rightBound = 0.5F
    private val farBound = -0.8F
    private val nearBound = 0.8F

    // 保证value的值不小于min而且不大于max
    private fun clamp(value: Float, min: Float, max: Float): Float {
        /*return when {
            value < min -> {
                min
            }
            value > max -> {
                max
            }
            else -> {
                value
            }
        }*/
        // 这行代码和上面的注释代码的含义一致，
        return max.coerceAtMost(value.coerceAtLeast(min))
    }



}