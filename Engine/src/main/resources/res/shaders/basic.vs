#version 400 core

in vec3 pos;
in vec2 texCoord;

out vec2 texCoordOut;
//out vec4 col;

uniform mat4 transformMat;
uniform mat4 viewMat;
uniform mat4 projectionMat;

void main() {
    vec4 posT = projectionMat * viewMat * transformMat * vec4(pos, 1.0);
    gl_Position = posT;
    texCoordOut = texCoord;
    //    col = transformMat * vec4(pos,1);
}