#version 130

attribute vec3 pos;
attribute vec2 texCoord;
attribute vec3 normal;

out vec3 vertex_normal;

uniform mat4 transformMat;
uniform mat4 viewMat;
uniform mat4 projectionMat;
uniform mat4 normalMat;

void main() {
    gl_Position = projectionMat * viewMat * transformMat *  vec4(pos, 1);
    vertex_normal = normalize(vec3(projectionMat * normalMat * vec4( normal, 0.0)));
}