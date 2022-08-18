uniform mat4 uMVPMatrix;
in vec3 aPosition;
out vec3 vPosition;
void main()
{
    gl_Position = uMVPMatrix * vec4(aPosition, 1);

    vPosition = aPosition;

}