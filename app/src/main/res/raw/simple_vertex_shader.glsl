// attribute修饰符表示只读的顶点数据，只用在顶点着色器中。
//数据来自当前的顶点状态或者顶点数组。
//它必须是全局范围声明的，不能在函数内部。
//一个attribute可以是浮点数类型的标量，向量，或者矩阵。
//不可以是数组或则结构体

uniform mat4 u_Matrix;
attribute vec4 a_Position;

void main() {
    gl_Position = u_Matrix * a_Position;
    gl_PointSize = 10.0;
}

