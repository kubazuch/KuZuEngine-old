#version 130
layout(points) in;
layout(line_strip, max_vertices=2) out;

in vec3 vertex_normal[];

out vec4 vertex_color;

const float MAGNITUDE = 0.4;

void main() {
    vec3 normal = vertex_normal[0];
    vertex_color = abs(normal);

    gl_Position = gl_in[0].gl_Position;
    EmitVertex();

    gl_Position = gl_in[0].gl_Position + vec4(normal, 0.0) * MAGNITUDE;
    EmitVertex();

    EndPrimitive();
}