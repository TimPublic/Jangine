package internal.ecs;


/**
 * Is a data container managed by an {@link ECS_ComponentSystem}.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class ECS_Component {


    public int owningEntity;


    // -+- CREATION -+- //

    public ECS_Component() {
        owningEntity = -1;
    }


    // -+- VALIDATION -+- //

    /**
     * Assigns the specified entity to this component,
     * as long as there is no other entity
     * assigned to the specified entity.
     *
     * @param id the id of the entity
     */
    public void init(int id) {
        if (owningEntity != -1) {return;}

        owningEntity = id;
    }
    /**
     * Removes the assigned entity from this component,
     * only if the specified entity is the one,
     * this component is currently assigned to.
     * Otherwise, nothing happens.
     *
     * @param id the id of the entity
     *
     * @author Tim Kloepper
     */
    public void kill(int id) {
        if (id != owningEntity) {return;}

        owningEntity = -1;
    }


}