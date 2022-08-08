attribute vec4 a_Position;

void main() {
    gl_Position = a_Position;
    // 使用gl_PointSize 设置点的大小。
    //原理： 当OpenGlad把一个点分解为片段的时候，它会生成一些片段，
    //它们是以gl_PointSize为中心的四边形，这个四边形的每条边的长度与gl_PointSize相等。
    gl_PointSize = 10.0;
}
