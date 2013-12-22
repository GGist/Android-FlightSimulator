uniform mat4 u_Matrix;

attribute vec4 a_Position;

varying vec4 test;

void main()
{
	test = a_Position;
	gl_Position = u_Matrix * a_Position;
}