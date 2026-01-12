package internal.ecs.collision.component_system;


import internal.ecs.JangineECS_Component;
import internal.ecs.JangineECS_ComponentSystem;
import internal.ecs.JangineECS_System;
import internal.ecs.collision.component.JECS_CollisionComponent;
import internal.ecs.collision.help.I_SpatialPartitioning;
import internal.ecs.collision.help.JangineCollisionData;
import internal.ecs.collision.help.JangineComponentCollisionData;
import internal.ecs.collision.help.JangineWindowCollisionData;

import java.util.HashSet;
import java.util.function.Consumer;


/**
 * This is a {@link JangineECS_ComponentSystem} designed for collision checks.
 * To receive a "notification" upon a collision (either window or with another component),
 * you can register a callback of the class {@link Consumer} taking in either
 * {@link JangineWindowCollisionData} or {@link JangineComponentCollisionData}.
 * For the registration, these function stand at your disposal:
 * <ul>
 *     <li>{@link JECS_CollisionComponentSystem#addResolutionCallbackWindow(Consumer)}</li>
 *     <li>{@link JECS_CollisionComponentSystem#rmvResolutionCallbackWindow(Consumer)}</li>
 *     <li>{@link JECS_CollisionComponentSystem#addResolutionCallbackComponent(Consumer)}</li>
 *     <li>{@link JECS_CollisionComponentSystem#rmvResolutionCallbackComponent(Consumer)}</li>
 * </ul>
 *
 * @param <T> the {@link JangineECS_Component} subclass, used. This should be {@link JECS_CollisionComponent}
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public abstract class JECS_CollisionComponentSystem<T extends JangineECS_Component> extends JangineECS_ComponentSystem<T> {


    protected final I_SpatialPartitioning _partitioner;

    protected final double _winWidth, _winHeight;

    private HashSet<Consumer<JangineWindowCollisionData>> _resolutionCallbacksWindow = new HashSet<>();
    private HashSet<Consumer<JangineComponentCollisionData>> _resolutionCallbacksComponent = new HashSet<>();


    public JECS_CollisionComponentSystem(I_SpatialPartitioning partitioner, double winWidth, double winHeight) {
        _partitioner = partitioner;

        _winWidth = winWidth;
        _winHeight = winHeight;
    }


    public final void addResolutionCallbackWindow(Consumer<JangineWindowCollisionData> callback) {
        _resolutionCallbacksWindow.add(callback);
    }
    public final void rmvResolutionCallbackWindow(Consumer<JangineWindowCollisionData> callback) {
        _resolutionCallbacksWindow.remove(callback);
    }
    public final void addResolutionCallbackComponent(Consumer<JangineComponentCollisionData> callback) {
        _resolutionCallbacksComponent.add(callback);
    }
    public final void rmvResolutionCallbackComponent(Consumer<JangineComponentCollisionData> callback) {
        _resolutionCallbacksComponent.remove(callback);
    }


    protected final void _pushWindowCollision(JangineCollisionData.COLLISION_AXIS collisionAxis, JECS_CollisionComponent component) {
        JangineWindowCollisionData data;

        data = new JangineWindowCollisionData(collisionAxis, JangineECS_System.get().findEntityByComponent(component));

        for (Consumer<JangineWindowCollisionData> callback : _resolutionCallbacksWindow) {
            callback.accept(data);
        }
    }
    protected final void _pushComponentCollision(JangineCollisionData.COLLISION_AXIS collisionAxis, JECS_CollisionComponent firstComponent, JECS_CollisionComponent secondComponent) {
        JangineComponentCollisionData data;

        data = new JangineComponentCollisionData(collisionAxis, JangineECS_System.get().findEntityByComponent(firstComponent), JangineECS_System.get().findEntityByComponent(secondComponent));

        for (Consumer<JangineComponentCollisionData> callback : _resolutionCallbacksComponent) {
            callback.accept(data);
        }
    }


}