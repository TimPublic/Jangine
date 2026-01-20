package internal.ecs;


public class EntityBuilder {


    private ECS _ecs;
    private int _entityID;


    private EntityBuilder() {
        _entityID = -1;
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
    public static EntityBuilder start(ECS system) {
        EntityBuilder instance;

        instance = new EntityBuilder();

        if (system == null) {return null;}

        instance._ecs = system;
        instance._entityID = instance._ecs.addEntity();

        return instance;
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
    public static EntityBuilder start(ECS system, int entityID) {
        EntityBuilder instance;

        instance = new EntityBuilder();

        if (system == null) {return null;}

        instance._ecs = system;
        instance._entityID = entityID;

        return instance;
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
    public EntityBuilder add(ECS_Component component) {
        if (_entityID == -1) {
            System.err.println("[CHAIN BUILDER ERROR] : Initialization not started!");

            System.exit(1);
        }

        _ecs.addComponent(_entityID, component);

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
    public EntityBuilder rmv(ECS_Component component) {
        if (_entityID == -1) {
            System.err.println("[CHAIN BUILDER ERROR] : Initialization not started!");

            System.exit(1);
        }

        _ecs.rmvComponent(_entityID, component);

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