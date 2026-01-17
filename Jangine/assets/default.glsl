#type vertex
#version 330 core

layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aUV_Coordinates;
layout (location = 2) in float aTexIndex;

uniform mat4 uProjectionMatrix;
uniform mat4 uViewMatrix;

out vec2 fUV_Coordinates;
out float fTexIndex;

void main() {
    fUV_Coordinates = aUV_Coordinates;
    fTexIndex = aTexIndex;

    gl_Position = uProjectionMatrix * uViewMatrix * vec4(aPos, 0.0, 1.0);
}


#type fragment
#version 330 core

in vec2 fUV_Coordinates;
in float fTexIndex;

uniform sampler2D uTextures[8];

out vec4 color;

void main() {
    int index = int(fTexIndex);
    color = texture(uTextures[index], fUV_Coordinates);
}