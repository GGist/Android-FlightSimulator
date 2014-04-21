uniform mat4 u_Matrix;

attribute vec4 a_Position;

varying float height;

void main()
{
	height = a_Position.y;
	gl_Position = u_Matrix * a_Position;
}