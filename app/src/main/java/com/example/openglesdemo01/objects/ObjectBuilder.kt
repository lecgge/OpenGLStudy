package com.example.openglesdemo01.objects

import android.opengl.GLES20
import android.opengl.GLES20.*
import com.example.openglesdemo01.Util.Circle
import com.example.openglesdemo01.Util.Cylinder
import kotlin.math.cos
import kotlin.math.sin

class ObjectBuilder {

    private val vertexDataList: MutableList<Float> = mutableListOf()

    private val commands = mutableListOf<DrawCommand>()

    fun appendCircle(circle: Circle, numPoints: Int): ObjectBuilder {
        val numVertices = sizeOfCircleInVertices(numPoints)
        // 添加顶点之前先计算起始顶点的index
        val startVertex = vertexDataList.size / FLOATS_PER_VERTEX
        // 计算2PI
        val pi2: Double = Math.PI * 2
        // 缓存每个点的角度
        var angleInRadians: Double
        vertexDataList.apply {
            // 添加圆心
            add(circle.center.x)
            add(circle.center.y)
            add(circle.center.z)
            // 因为我们想重复加入第一个点，因此允许i赋值为numPoints
            for (i in 0..numPoints) {
                // 计算该点的角度
                angleInRadians = (i.toDouble()) / (numPoints.toDouble()) * pi2
                add(circle.center.x + circle.radius * cos(angleInRadians).toFloat())
                add(circle.center.y)
                add(circle.center.z + circle.radius * sin(angleInRadians).toFloat())
            }
        }
        commands.add {
            glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices)
        }
        return this
    }


    fun appendOpenCylinder(cylinder: Cylinder, numPoints: Int): ObjectBuilder {
        val numVertices = sizeOfCircleInVertices(numPoints)
        // 添加顶点之前先计算起始顶点的index
        val startVertex = vertexDataList.size / FLOATS_PER_VERTEX
        // 计算2PI
        val pi2: Double = Math.PI * 2
        // 下圆周的y分量
        val yStart: Float = cylinder.center.y - (cylinder.height / 2F)
        // 上圆周的y分量
        val yEnd: Float = cylinder.center.y + (cylinder.height / 2F)
        //缓存每个点的角度
        // 缓存每个点的角度
        var angleInRadians: Double
        // 缓存点的x、z分量
        var xPosition: Float
        var zPosition: Float
        vertexDataList.apply {
            // 因为我们想重复加入第一个点，因此允许i赋值为numPoints
            for (i in 0..numPoints) {
                // 计算该点的角度
                angleInRadians = (i.toDouble()) / (numPoints.toDouble()) * pi2

                xPosition = cylinder.center.x + cylinder.radius * cos(angleInRadians).toFloat()
                zPosition = cylinder.center.z + cylinder.radius * sin(angleInRadians).toFloat()

                add(xPosition)
                add(yStart)
                add(zPosition)

                add(xPosition)
                add(yEnd)
                add(zPosition)
            }
        }
        commands.add {
            glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices)
        }
        return this
    }

    fun build(): GeneratedData = GeneratedData(
        vertexData = vertexDataList.toFloatArray(),
        drawCommands = commands
    )


    /**
     * 计算绘制一个圆实际需要的顶点数，等于圆周顶点数+2
     */
    private fun sizeOfCircleInVertices(numPoints: Int): Int = numPoints + 2

    /**
     * 计算绘制一个圆筒实际需要的顶点数，等于(圆周顶点数+1)*2
     */
    private fun sizeOfOpenCylinderInVertices(numPoints: Int): Int = (numPoints + 1) * 2

    companion object {
        private const val FLOATS_PER_VERTEX = 3
    }
}


fun interface DrawCommand {
    fun draw()
}

class GeneratedData(
    val vertexData: FloatArray,
    val drawCommands: List<DrawCommand>
)