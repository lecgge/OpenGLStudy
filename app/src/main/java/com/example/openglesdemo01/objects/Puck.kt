package com.example.openglesdemo01.objects

import com.example.openglesdemo01.Util.Circle
import com.example.openglesdemo01.Util.Cylinder
import com.example.openglesdemo01.Util.Point
import com.example.openglesdemo01.data.VertexArray
import com.example.openglesdemo01.view.ColorShaderProgram

class Puck(
    val radius: Float,
    val height: Float,
    numPointsAroundPuck: Int
) {
    private val vertexArray: VertexArray
    private val drawList: List<DrawCommand>

    init {
        val data = createPuck(
            puck = Cylinder(
                center = Point(0F, 0F, 0F),
                radius = radius,
                height = height
            ),
            numPoints = numPointsAroundPuck
        )
        vertexArray = VertexArray(data.vertexData)
        drawList = data.drawCommands
    }

    private fun createPuck(puck: Cylinder, numPoints: Int): GeneratedData {
        // 添加顶部的圆，再添加配套的圆筒
        return ObjectBuilder()
            .appendCircle(
                circle = Circle(puck.center.translateY(puck.height / 2F), puck.radius),
                numPoints = numPoints
            ).appendOpenCylinder(puck, numPoints)
            .build()
    }


    fun bindData(colorShaderProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
            dataOffset = 0,
            attributeLocation = colorShaderProgram.aPositionLocation,
            componentCount = POSITION_COMPONENT_COUNT,
            stride = 0
        )
    }
    fun draw() {
        drawList.forEach {
            it.draw()
        }
    }


    companion object{
        private const val POSITION_COMPONENT_COUNT = 3
    }

}