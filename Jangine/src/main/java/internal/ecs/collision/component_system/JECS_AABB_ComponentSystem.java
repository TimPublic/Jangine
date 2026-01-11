package internal.ecs.collision.component_system;


import internal.ecs.JangineECS_Component;
import internal.ecs.collision.component.JECS_CollisionComponent;
import internal.ecs.collision.help.I_SpatialPartitioning;
import internal.ecs.collision.help.JangineCollisionData;
import org.joml.Vector2d;


public class JECS_AABB_ComponentSystem<T extends JangineECS_Component> extends JECS_CollisionComponentSystem<T> {


    private I_SpatialPartitioning _partitioner;

    private final double _winWidth, _winHeight;


    public JECS_AABB_ComponentSystem(I_SpatialPartitioning partitioner, double winWidth, double winHeight) {
        _partitioner = partitioner;

        _winWidth = winWidth;
        _winHeight = winHeight;
    }


    @Override
    public void update(double deltaTime) {
        for (T component : _components) {
            _tryWindowCollision((JECS_CollisionComponent) component);

            for (JECS_CollisionComponent withComponent : _partitioner.getPossibleCollisions((JECS_CollisionComponent) component)) {
                _tryComponentCollision((JECS_CollisionComponent) component, withComponent);
            }
        }
    }


    @Override
    public void addComponent(T component) {
        _components.add(component);
        _partitioner.addComponent((JECS_CollisionComponent) component);
    }
    @Override
    public void rmvComponent(T component) {
        _components.remove(component);
        _partitioner.rmvComponent((JECS_CollisionComponent) component);
    }


    private void _tryComponentCollision(JECS_CollisionComponent component, JECS_CollisionComponent withComponent) {
        Vector2d cPos, cPosExtended;
        Vector2d wPos, wPosExtended;

        boolean xCollision, yCollision;

        cPos = component.position;
        cPosExtended = component.position.add(component.width, component.height);

        wPos = withComponent.position;
        wPosExtended = withComponent.position.add(component.width, component.height);

        xCollision = (cPos.x < wPosExtended.x && cPosExtended.x > wPos.x);
        yCollision = (cPos.y < wPosExtended.y && cPosExtended.y > wPos.y);

        if (!(xCollision && yCollision)) {
            return;
        }

        double xOverlap, yOverlap;

        xOverlap = Math.min(cPosExtended.x, wPosExtended.x) - Math.max(cPos.x, wPos.x);
        yOverlap = Math.min(cPosExtended.y, wPosExtended.y) - Math.max(cPos.y, wPos.y);

        if (yOverlap > xOverlap) {
            _pushComponentCollision(JangineCollisionData.COLLISION_AXIS.Y, component, withComponent);
            return;
        }

        _pushComponentCollision(JangineCollisionData.COLLISION_AXIS.X, component, withComponent);
    }
    private void _tryWindowCollision(JECS_CollisionComponent component) {
        boolean onY, onX;

        onY = (component.position.x < 0) || (component.position.x + component.width > _winWidth);

        if (onY) {
            _pushWindowCollision(JangineCollisionData.COLLISION_AXIS.Y, component);
        }

        onX = (component.position.y < 0) || (component.position.y + component.height > _winHeight);

        if (onX) {
            _pushWindowCollision(JangineCollisionData.COLLISION_AXIS.X, component);
        }
    }


}