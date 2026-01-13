package internal.ecs.quick_test;


/**
 * The component is a data container, basically a structure, containing no own logic.
 * They are organized in {@link JangineECS_ComponentSystem} and assigned to an entity.
 * They can also be activated and deactivated.
 * On deactivation, component systems will just skip them.
 * All their data is public and not final and can be accessed and mutated directly.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public abstract class JangineECS_Component {


    public int entityID;

    public boolean active;


    // -+- LIFE CYCLE -+- //

    /**
     * Called after setting everything up,
     * will assign this component to the specified entity.
     * It does not validate the entities' id.
     *
     * @param entityID id of the entity this component is to be assigned to
     *
     * @author Tim Kloepper
     */
    public final void init(int entityID) {
        this.entityID = entityID;
        active = true;
    }

    /**
     * Called after removing this component.
     * WIll make the component invalid and inactive.
     * Will only work if the provided entities' id
     * is equal to the id this component is assigned to.
     * This id can be found at {@link JangineECS_Component#entityID}.
     *
     * @param entityID id of the entity that this component is assigned to
     *
     * @author Tim Kloepper
     */
    public final void kill(int entityID) {
        if (this.entityID != entityID) {return;}

        this.entityID = -1;
        active = false;
    }


    // -+- VALIDATION -+- //

    /**
     * Checks if the component is valid, meaning if it is assigned to an entity.
     *
     * @return whether the component is valid or not
     *
     * @author Tim Kloepper
     */
    public final boolean isValid() {
        return entityID != -1;
    }


}