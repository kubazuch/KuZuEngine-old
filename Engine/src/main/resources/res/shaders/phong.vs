#version 150

attribute vec3 pos;
attribute vec2 texCoord;
attribute vec3 normal;

out vec2 texCoordOut;
out vec3 normalOut;
out vec3 worldPosOut;

uniform mat4 transformMat;
uniform mat4 viewMat;
uniform mat4 projectionMat;

// uniform mat4 normalMat;

void main() {
    vec4 worldPosition = transformMat * vec4(pos, 1.0);
    gl_Position = projectionMat * viewMat * worldPosition;
    texCoordOut = texCoord;
    normalOut = (transformMat * vec4(normal, 0.0)).xyz;
    worldPosOut = worldPosition.xyz;
    //    col = transformMat * vec4(pos,1);
}