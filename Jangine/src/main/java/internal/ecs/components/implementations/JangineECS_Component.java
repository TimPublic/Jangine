package internal.ecs.components.implementations;


import internal.ecs.JangineEntity;


/**
 * The component is what makes up a {@link JangineEntity}.
 * It defines behaviour, properties and anything else, important for that entity.
 * <p>
 * In order to be accepted by the {@link internal.ecs.JangineECS_System}, they must have
 * been added by a {@link JangineEntity}, as they initialize the component, with themselves.
 * <p>
 * They are updated every frame by the {@link internal.ecs.JangineECS_System}.
 * They are removed by the entity if the entity gets deleted (see {@link JangineEntity#kill()}).
 *
 * @author Tim Kloepper
 * @version 1.0
 */
abstract public class JangineECS_Component {


    private boolean _hasInitialized = false;


    // -+- UPDATE LOOP -+- //

    /**
     * Called by the {@link internal.ecs.JangineECS_System} every frame,
     * to perform updates.
     *
     * @param deltaTime time passed since the last frame
     *
     * @author Tim Kloepper
     */
    public void update(double deltaTime) {}


    // -+- LIFE CYCLE -+- //

    /**
     * Called upon creation by the owning {@link JangineEntity}.
     * Receives the entity to create all needed connections.
     * It is not recommended to keep a reference of the entity.
     *
     * @param entity owner of this component
     *
     * @author Tim Kloepper
     */
    public final void init(JangineEntity entity) {
        _onInit();

        _hasInitialized = true;
    }

    /**
     * Called upon deletion by the owning {@link JangineEntity}.
     * Receives the entity to remove all connection made in {@link JangineECS_Component#init(JangineEntity)}.
     *
     * @param entity owner of this component
     *
     * @author Tim Kloepper
     */
    public void kill(JangineEntity entity) {

    }

    protected void _onInit() {

    }


    // -+- CHECKERS -+- //

    /**
     * Returns true, if the component has been initialized.
     * And false, if the component has not been initialized.
     *
     * @return whether the component has been initialized or not
     *
     * @author Tim Kloepper
     */
    public final boolean hasBeenInitialized() {
        return _hasInitialized;
    }


}