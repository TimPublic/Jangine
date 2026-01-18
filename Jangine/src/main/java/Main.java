import internal.ecs.ECS;
import internal.ecs.specific.rendering.RenderComponent;
import internal.ecs.specific.rendering.RenderComponentSystem;
import internal.ecs.specific.rendering.color.ColoredMeshComponent;
import internal.ecs.specific.rendering.color.ColoredMeshComponentSystem;
import internal.ecs.specific.rendering.texture.TexturedMeshComponent;
import internal.ecs.specific.rendering.texture.TexturedMeshComponentSystem;
import internal.ecs.specific.texture.TextureComponent;
import internal.ecs.specific.texture.TextureComponentSystem;
import internal.main.Engine;
import internal.rendering.container.Window;
import internal.rendering.mesh.ColoredMesh;
import internal.rendering.mesh.TexturedMesh;
import internal.rendering.shader.ShaderTest;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;


public class Main {


    public static void main(String[] args) {
        Engine engine;

        engine = Engine.get();

        engine.run();
    }


}