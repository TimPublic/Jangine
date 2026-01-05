#type vertex
#version 330 core


layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aUV_Coordinates;


uniform mat4 uProjectionMatrix;
uniform mat4 uViewMatrix;


out vec4 fColor;
out vec2 fUV_Coordinates;


void main() {
    fColor = aColor;
    fUV_Coordinates = aUV_Coordinates;

    gl_Position = uProjectionMatrix * uViewMatrix * vec4(aPos, 1.0);
}


#type fragment
#version 330 core


in vec4 fColor;
in vec2 fUV_Coordinates;

uniform sampler2D TEXTURE_SAMPLER;


out vec4 color;


void main() {
    color = texture(TEXTURE_SAMPLER, fUV_Coordinates);
}