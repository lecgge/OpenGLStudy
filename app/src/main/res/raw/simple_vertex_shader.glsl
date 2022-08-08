// attribute修饰符表示只读的顶点数据，只用在顶点着色器中。
//数据来自当前的顶点状态或者顶点数组。
//它必须是全局范围声明的，不能在函数内部。
//一个attribute可以是浮点数类型的标量，向量，或者矩阵。
//不可以是数组或则结构体

attribute vec4 a_Position;
attribute vec4 a_Color;

varying vec4 v_Color;

void main() {
    v_Color = a_Color;
    gl_Position = a_Position;
    // 使用gl_PointSize 设置点的大小。
    //原理： 当OpenGlad把一个点分解为片段的时候，它会生成一些片段，
    //它们是以gl_PointSize为中心的四边形，这个四边形的每条边的长度与gl_PointSize相等。
    gl_PointSize = 10.0;
}

