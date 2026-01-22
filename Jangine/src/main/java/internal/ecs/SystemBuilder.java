package internal.ecs;


import internal.rendering.container.Scene;


public class SystemBuilder {


    private ECS _ecs;


    private SystemBuilder() {
        _ecs = null;
    }


    public static SystemBuilder start(ECS system) {
        SystemBuilder instance;

        instance = new SystemBuilder();

        if (system == null) {return null;}

        instance._ecs = system;

        return instance;
    }
    public static SystemBuilder start(Scene scene) {
        SystemBuilder instance;
        ECS system;

        instance = new SystemBuilder();
        system = new ECS(scene);

        instance._ecs = system;

        return instance;
    }
    public ECS finish() {
        ECS system;

        system = _ecs;
        _ecs = null;

        return system;
    }

    public SystemBuilder add(ECS_ComponentSystem<? extends ECS_Component> componentSystem, Class<? extends ECS_Component> componentClass, boolean overwrite) {
        if (componentSystem == null) {return null;}

        _ecs.addComponentSystem(componentSystem, componentClass, overwrite);

        return this;
    }
    public SystemBuilder rmv(Class<? extends ECS_Component> componentClass) {
        _ecs.rmvComponentSystem(componentClass);

        return this;
    }


}