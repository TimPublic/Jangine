package internal.ecs;


public class ChainBuilder {


    private ECS _ecs;
    private int _entityID;


    public ChainBuilder start(ECS system) {
        if (system == null) {return null;}
        if (_entityID != -1) {
            System.err.println("[CHAIN BUILDER ERROR] : Already building an entity!");
            System.err.println("|-> Current entity : " + _entityID);

            System.exit(1);
        }

        _ecs = system;
        _entityID = _ecs.addEntity();

        return this;
    }
    public ChainBuilder start(ECS system, int entityID) {
        if (system == null) {return null;}
        if (_entityID != -1) {
            System.err.println("[CHAIN BUILDER ERROR] : Already building an entity!");
            System.err.println("|-> Current entity : " + _entityID);

            System.exit(1);
        }

        _ecs = system;
        _entityID = entityID;

        return this;
    }
    public int finish() {
        int id;

        id = _entityID;

        _ecs = null;
        _entityID = -1;

        return id;
    }

    public ChainBuilder add(ECS_Component component) {
        if (_entityID == -1) {
            System.err.println("[CHAIN BUILDER ERROR] : Initialization not started!");

            System.exit(1);
        }

        _ecs.addComponent(_entityID, component);

        return this;
    }
    public ChainBuilder addSys(ECS_ComponentSystem<?> componentSystem, Class<? extends ECS_Component> componentClass) {
        if (_entityID == -1) {
            System.err.println("[CHAIN BUILDER ERROR] : Initialization not started!");

            System.exit(1);
        }

        _ecs.addComponentSystem(componentSystem, componentClass);

        return this;
    }

    public ChainBuilder rmv(ECS_Component component) {
        if (_entityID == -1) {
            System.err.println("[CHAIN BUILDER ERROR] : Initialization not started!");

            System.exit(1);
        }

        _ecs.rmvComponent(_entityID, component);

        return this;
    }
    public ChainBuilder rmvSys(Class<? extends ECS_Component> componentClass) {
        if (_entityID == -1) {
            System.err.println("[CHAIN BUILDER ERROR] : Initialization not started!");

            System.exit(1);
        }

        _ecs.rmvComponentSystem(componentClass);

        return this;
    }


}