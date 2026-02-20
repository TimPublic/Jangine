package internal.entity_component_system;


import internal.entity_component_system.events.ProcessorAddedEvent;
import internal.entity_component_system.events.ProcessorRemovedEvent;
import internal.rendering.container.A_Scene;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;


public class System {


    // -+- CREATION -+- //

    public System(A_Scene scene) {
        _processorsPerComponent = new HashMap<>();
        _SCENE = scene;
        _activeEntities = new HashSet<>();
        _freeEntityIds = new HashSet<>();

        _nextEntity = 0;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final HashMap<Class<? extends A_Component>, A_Processor> _processorsPerComponent;
    private final A_Scene _SCENE;
    private final HashSet<Integer> _activeEntities;
    private final HashSet<Integer> _freeEntityIds;

    // NON-FINALS //

    private int _nextEntity;


    // -+- UPDATE LOOP -+- //

    /**
     * Is called every frame by the owning scene.
     * Updates all processors that this system owns,
     * by calling their update method.
     *
     * @author Tim Kloepper
     */
    public void update() {
        for (A_Processor processor : _processorsPerComponent.values()) {
            processor.p_update(this, _SCENE);
        }
    }


    // -+- PROCESSOR MANAGEMENT -+-  //

    /**
     * Adds a processor to the system.
     * If any of the components this processor would like to manage
     * is already taken, the method currently crashes and returns {@code false}.
     * <p></p>
     * If this system does not contain the requirements of this processor,
     * the method also fails and returns {@code false}.
     *
     * @param processor The processor that is to be added
     *
     * @return success
     *
     * @author Tim Kloepper
     */
    public boolean addProcessor(A_Processor processor) {
        if (processor.p_getProcessedComponentClasses().isEmpty()) return false;
        if (!_meetsRequirementsOf(processor)) return false;

        // Needs to be an own loop, in order to not have the possibility to add the processor
        // to some extends only to then crash and have an incomplete implementation.
        for (Object componentClass : processor.p_getProcessedComponentClasses()) {
            if (_processorsPerComponent.containsKey(componentClass)) return false;
        }

        for (Object componentClass : processor.p_getProcessedComponentClasses()) {
            _processorsPerComponent.put((Class<? extends A_Component>) componentClass, processor);
        }

        HashMap<Class<? extends A_Component>, A_Processor> requirements;

        requirements = new HashMap<>();
        for (Object componentClass : processor.p_getRequiredComponentClasses()) {
            requirements.put((Class<? extends A_Component>) componentClass, _processorsPerComponent.get(componentClass));
        }

        processor.p_init(this, _SCENE);
        processor.p_receiveRequiredProcessors(requirements);

        _onProcessorAdded(processor);

        return true;
    }
    /**
     * Removes a processor from the system.
     * The function returns false, if the processor
     * was not part of this system.
     *
     * @param processor The processor that is to be removed
     *
     * @return success
     *
     * @author Tim Kloepper
     */
    public boolean rmvProcessor(A_Processor processor) {
        if (!_processorsPerComponent.containsValue(processor)) return false;

        for (Object componentClass : processor.p_getProcessedComponentClasses()) {
            if (_processorsPerComponent.get(componentClass) != processor) continue;

            _processorsPerComponent.remove(componentClass);
        }

        processor.p_kill(this, _SCENE);

        _onProcessorRemoved(processor);

        return true;
    }

    /**
     * Gets called when a processor got added.
     * Is responsible for notifications.
     *
     * @param processor Processor that got added
     *
     * @author Tim Kloepper
     */
    private void _onProcessorAdded(A_Processor processor) {
        _SCENE.SYSTEMS.EVENT_HANDLER.push(new ProcessorAddedEvent(processor));
    }
    /**
     * Gets called, when a processor got removed.
     * Is responsible for notifications.
     *
     * @param processor Processor that got removed
     *
     * @author Tim Kloepper
     */
    private void _onProcessorRemoved(A_Processor processor) {
        _SCENE.SYSTEMS.EVENT_HANDLER.push(new ProcessorRemovedEvent(processor));
    }


    // -+- ENTITY MANAGEMENT -+- //

    public int addEntity() {
        int id;

        if (!_freeEntityIds.isEmpty()) {
            id = _freeEntityIds.iterator().next();
        } else {
            id = _nextEntity++;
        }

        _activeEntities.add(id);

        return id;
    }
    public boolean rmvEntity(int id) {
        if (!_activeEntities.contains(id)) {return false;}

        for (A_Processor<?> processor : _processorsPerComponent.values()) {
            processor.rmvComponent(id);
        }

        _activeEntities.remove(id);
        _freeEntityIds.add(id);

        return true;
    }


    // -+- COMPONENT MANAGEMENT -+- //

    /**
     * Adds a component to the specified entity.
     * The correct processor is automatically found,
     * but if none is found, {@code false} is returned.
     * The same is the case for a not existing entity id.
     * If the processor already has a component for that entity
     * and {@code overwrite} is set to {@code false}, {@code false}
     * is returned.
     *
     * @param id The id of the entity that the specified component is to be added
     * @param component The component that is to be added to the specified entity
     * @param overwrite Determines whether an already existing component should be overwritten
     *
     * @return Success
     */
    public boolean addComponentToEntity(int id, A_Component component, boolean overwrite) {
        Class<? extends A_Component> componentClass;
        A_Processor processor;

        if (!_activeEntities.contains(id)) return false;
        if (component.owningEntity != -1) return false;

        componentClass = component.getClass();

        processor = _processorsPerComponent.get(componentClass);
        if (processor == null) return false;

        component.owningEntity = id;

        return processor.addComponent(id, component, overwrite);
    }
    /**
     * Removes a component from the specified entity.
     * The correct processor is automatically found,
     * but if none is found, {@code false} is returned.
     * The same is the case for a not existing entity id.
     * <p></p>
     * You only need to specify the class of the component,
     * not the actual component.
     *
     * @param id The id of the entity from which the component should be removed
     * @param componentClass The class of the component that should be removed from the specified entity
     *
     * @return Success
     */
    public boolean rmvComponentFromEntity(int id, Class<? extends A_Component> componentClass) {
        A_Processor processor;

        processor = _processorsPerComponent.get(componentClass);
        if (processor == null) return false;

        A_Component component;

        component = processor.rmvComponent(id);
        if (component == null) return false;

        component.owningEntity = -1;

        return true;
    }


    // -+- GETTERS -+- //

    /**
     * Returns the processor, that manages the specified class of the component.
     * This is a simple wrapper for {@link HashMap#get(Object)},
     * so if the processor is registered for that class,
     * {@code null} is returned.
     *
     * @param componentClass Class of the component, from which the processor is requested
     *
     * @return The processor managing the specified class, or {@code null} if no processor
     * is registered for that class
     */
    public A_Processor getProcessorOf(Class<? extends A_Component> componentClass) {
        return _processorsPerComponent.get(componentClass);
    }
    /**
     * Returns all processors that are currently in this system.
     * Is just a wrapper for {@link HashMap#values()}.
     *
     * @return All processors that are currently in this system.
     *
     * @author Tim Kloepper
     */
    public Collection<A_Processor> getProcessors() {
        return _processorsPerComponent.values();
    }

    /**
     * Returns all classes of the component that are currently processed.
     * Is just a wrapper for {@link HashMap#keySet()} with the only difference being,
     * that this method returns a {@link Collection} instead of a {@link java.util.Set}
     * for consistency with {@link System#getProcessors()}.
     *
     * @return All classes of the component that are currently processed.
     *
     * @author Tim Kloepper
     */
    public Collection<Class<? extends A_Component>> getProcessedComponentClasses() {
        return _processorsPerComponent.keySet();
    }


    // -+- CHECKERS -+- //

    private boolean _meetsRequirementsOf(A_Processor processor) {
        for (Object componentClass : processor.p_getRequiredComponentClasses()) {
            if (!_processorsPerComponent.containsKey(componentClass)) return false;
        }

        return true;
    }

    public boolean isProcessing(Class<? extends A_Component> componentClass) {
        return _processorsPerComponent.containsKey(componentClass);
    }


}