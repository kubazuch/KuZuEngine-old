#version 150

in vec2 texCoord0;
in vec3 normal0;
in vec3 worldPos0;

invariant out vec4 fragColor;

struct BaseLight {
    vec3 color;
    float intensity;
};

struct DirectionalLight {
    BaseLight base;
    vec3 direction;
};

uniform vec3 eyePos;
uniform sampler2D diffuse;

uniform float specularIntensity;
uniform float specularPower;

uniform DirectionalLight directionalLight;

vec4 calcLight(BaseLight base, vec3 direction, vec3 normal, vec3 worldPos) {
    float diffuseFactor = dot(normal, -direction);

    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);
    if (diffuseFactor > 0) {
        diffuseColor = vec4(base.color, 1.0) * base.intensity * diffuseFactor;

        vec3 directionToEye = normalize(eyePos - worldPos);
        //        vec3 reflectDirection = normalize(reflect(direction, normal));
        vec3 halfDirection = normalize(directionToEye - direction);

        //        float specularFactor = dot(directionToEye, reflectDirection);
        float specularFactor = dot(halfDirection, normal);
        specularFactor = pow(specularFactor, specularPower);

        if (specularFactor > 0) {
            specularColor = vec4(base.color, 1.0) * specularIntensity * specularFactor;
        }
    }

    return diffuseColor + specularColor;
}

vec4 calcDirectionalLight(DirectionalLight directionalLight, vec3 normal, vec3 worldPos) {
    return calcLight(directionalLight.base, -directionalLight.direction, normal, worldPos);
}

void main() {
    vec4 totalLight = vec4(0, 0, 0, 1);
    vec4 color = texture(diffuse, texCoord0);

    vec3 normal = normalize(normal0);

    totalLight += calcDirectionalLight(directionalLight, normal, worldPos0);

    fragColor = color * totalLight;
}