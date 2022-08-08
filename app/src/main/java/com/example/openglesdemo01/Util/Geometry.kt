package com.example.openglesdemo01.Util

import kotlin.math.sqrt

class Point(
    val x: Float,
    val y: Float,
    val z: Float,
) {
    fun translateY(distance: Float): Point = Point(x, y + distance, z)

    //根据方向向量计算新的点
    fun translate(vector: Vector):Point {
        return Point(
            x + vector.x,
            y + vector.y,
            z + vector.z
        )

    }
}

class Circle(
    val center:Point,
    val radius:Float
){
    fun scale(scale: Float): Circle = Circle(center, radius * scale)
}

class Cylinder(
    val center: Point,
    val radius: Float,
    val height: Float
)

class Vector(
    val x: Float,
    val y: Float,
    val z: Float
){
    //计算大小
    fun length(): Float = sqrt(x * x + y * y + z * z)

    //计算向量叉积
    fun crossProduct(other: Vector): Vector =
        Vector(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        )

    fun dotProduct(other: Vector): Float = x * other.x + y * other.y + z * other.z

    fun scale(f: Float): Vector = Vector(x * f, y * f, z * f)
}
/**
 * 计算[Plane]与[Ray]的相交点坐标。如果它们互相平行，返回值的三个分量值将会为[Float.NaN]
 */
fun Plane.intersectionPoint(ray: Ray):Point{
    val rayToPlaneVector = ray.point to this.point
    val scaleFactor = rayToPlaneVector.dotProduct(normal) / ray.vector.dotProduct(normal)
    return ray.point.translate(ray.vector.scale(scaleFactor))
}




class Ray(
    val point: Point,
    val vector: Vector
)

infix fun Point.to(target:Point) :Vector = vectorBetween(this,target)

fun vectorBetween(from:Point,target: Point):Vector =
    Vector(
        target.x - from.x,
        target.y - from.y,
        target.z - from.z,
    )

class Sphere(
    val center: Point,
    val radius: Float
)

/**
 * 判断[Sphere]是否与[Ray]在空间上相交
 */
fun Sphere.intersects(ray: Ray): Boolean =
    distanceBetween(center, ray) < radius

/**
 * 求点线距离
 */
fun distanceBetween(point: Point, ray: Ray): Float {

    //计算两个向量
    val centerToP1 = ray.point to point
    val centerToP2 = ray.run { point.translate(vector) } to point

    //计算向量叉积的大小，这个值是三角形面积的两倍
    val areOfTriangleTimesTwo = centerToP1.crossProduct(centerToP2).length()
    //计算方向向量的长度
    val lengthOfBase = ray.vector.length()
    //点线距离=三角形面积x2/底
    return areOfTriangleTimesTwo / lengthOfBase
}

class Plane(
    val point: Point,
    val normal: Vector
)