package internal.ecs;


/**
 * Is used to easily chain entity creation into a simple chain,
 * making the process of adding components or adding component systems
 * much easier.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class ChainBuilder {


    private ECS _ecs;
    private int _entityID;


    private static ChainBuilder _instance;


    private ChainBuilder() {

    }


    // -+- PROCESS MANAGEMENT -+- //

    /**
     * Starts the creation process by returning the chain builder and creates a new entity,
     * which will be returned upon calling {@link ChainBuilder#finish()}.
     *
     * @param system the entity component system this entity will be living in
     *
     * @return the chain builder to start chain creation
     *
     * @author Tim Kloepper
     */
    public static ChainBuilder start(ECS system) {
        if (_instance == null) {_instance = new ChainBuilder();}

        if (system == null) {return null;}
        if (_instance._entityID != -1) {
            System.err.println("[CHAIN BUILDER ERROR] : Already building an entity!");
            System.err.println("|-> Current entity : " + _instance._entityID);

            System.exit(1);
        }

        _instance._ecs = system;
        _instance._entityID = _instance._ecs.addEntity();

        return _instance;
    }
    /**
     * Starts the creation process by returning the chain builder
     * with an already existing entity, which has to be provided.
     *
     * @param system the entity component system, the specified entity lives in
     * @param entityID the entity which will be operated on
     *
     * @return the chain builder to start chain creation
     *
     * @author Tim Kloepper
     */
    public static ChainBuilder start(ECS system, int entityID) {
        if (_instance == null) {_instance = new ChainBuilder();}

        if (system == null) {return null;}
        if (_instance._entityID != -1) {
            System.err.println("[CHAIN BUILDER ERROR] : Already building an entity!");
            System.err.println("|-> Current entity : " + _instance._entityID);

            System.exit(1);
        }

        _instance._ecs = system;
        _instance._entityID = entityID;

        return _instance;
    }
    /**
     * Finishes the creation and returns the created entity.
     *
     * @return the created entity or -1 if no creation process is running
     *
     * @author Tim Kloepper
     */
    public int finish() {
        int id;

        id = _entityID;

        _ecs = null;
        _entityID = -1;

        return id;
    }


    // -+- ADDITION -+- //

    /**
     * Adds a component to the currently set entity.
     *
     * @param component the component that is to be added
     *
     * @return this chain builder for chain creation
     *
     * @author Tim Kloepper
     */
    public ChainBuilder add(ECS_Component component) {
        if (_entityID == -1) {
            System.err.println("[CHAIN BUILDER ERROR] : Initialization not started!");

            System.exit(1);
        }

        _ecs.addComponent(_entityID, component);

        return this;
    }
    /**
     * Adds a component system to the currently set entity component system.
     *
     * @param componentSystem the component system that is to be added
     * @param componentClass the subclass of the component which the specified component system will manage
     *
     * @return this chain builder for chain creation
     *
     * @author Tim Kloepper
     */
    public ChainBuilder addSys(ECS_ComponentSystem<?> componentSystem, Class<? extends ECS_Component> componentClass) {
        if (_entityID == -1) {
            System.err.println("[CHAIN BUILDER ERROR] : Initialization not started!");

            System.exit(1);
        }

        _ecs.addComponentSystem(componentSystem, componentClass);

        return this;
    }


    // -+- REMOVAL -+- //

    /**
     * Removes a component from the currently set entity.
     *
     * @param component the component that is to be removed
     *
     * @return this chain builder for chain creation
     *
     * @author Tim Kloepper
     */
    public ChainBuilder rmv(ECS_Component component) {
        if (_entityID == -1) {
            System.err.println("[CHAIN BUILDER ERROR] : Initialization not started!");

            System.exit(1);
        }

        _ecs.rmvComponent(_entityID, component);

        return this;
    }
    /**
     * Removes a component system from the currently set entity component system.
     *
     * @param componentClass the component class that is to be removed
     *
     * @return this chain builder for chain creation
     *
     * @author Tim Kloepper
     */
    public ChainBuilder rmvSys(Class<? extends ECS_Component> componentClass) {
        if (_entityID == -1) {
            System.err.println("[CHAIN BUILDER ERROR] : Initialization not started!");

            System.exit(1);
        }

        _ecs.rmvComponentSystem(componentClass);

        return this;
    }


    // -+- GETTERS -+- //#

    /**
     * Returns the currently set entity without finishing the creation process.
     *
     * @return the entity or -1 if no creation process is running
     *
     * @author Tim Kloepper
     */
    public int get() {
        return _entityID;
    }


    // -+- CHECKERS -+- //

    public boolean isBuilding() {
        return (_entityID != -1);
    }


}