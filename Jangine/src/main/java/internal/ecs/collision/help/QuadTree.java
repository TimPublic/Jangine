package internal.ecs.collision.help;


import internal.ecs.collision.component.JECS_CollisionComponent;
import org.joml.Vector2d;

import java.util.Collection;
import java.util.HashSet;


/**
 * The quad tree is an implementation of the {@link I_SpatialPartitioning} interface.
 * It is used by {@link internal.ecs.collision.component_system.JECS_CollisionComponentSystem} to make collision checking more effective.
 * <p>
 * In this version, the nodes that contain children nodes do not contain the objects of their children,
 * making searching in deep trees a bit slower, but with an easy "parent check" (see {@link QuadTreeNode#hasChildren()}) for traversing the tree quickly, this should not really matter.
 * <p>
 * As this class is tightly coupled to the {@link internal.ecs.collision.component_system.JECS_CollisionComponentSystem}, it uses
 * the {@link JECS_CollisionComponent} as the objects with simple AABB positional checking, keeping the structure simple but clean.
 * <p>
 * The quad tree is made up of {@link QuadTreeNode} that do the actual magic.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class QuadTree implements I_SpatialPartitioning {


    public static final int CHILDREN_AMOUNT = 4; // QUAD || WATCH OUT : in the _tryParent() method, this final is not recognized.
    public static final int OBJECTS_THRESHOLD = 4;


    private final HashSet<JECS_CollisionComponent> _components;

    private final QuadTreeNode _rootNode;


    public QuadTree(Vector2d position, double width, double height) {
        _components = new HashSet<>();

        _rootNode = new QuadTreeNode(position, width, height, _components);
    }


    /**
     * Returns all {@link JECS_CollisionComponent} inside this tree, that could collide with the specified component.
     *
     * @param forComponent component for which possible collisions are returned
     *
     * @return possible collisions for the specified component
     *
     * @author Tim Kloepper
     */
    @Override
    public HashSet<JECS_CollisionComponent> getPossibleCollisions(JECS_CollisionComponent forComponent) {
        HashSet<JECS_CollisionComponent> set;

        set = new HashSet<>();
        _rootNode.getPossibleCollision(set, forComponent);
        set.remove(forComponent); // Doing it here is much more efficient.

        return set;
    }

    /**
     * Adds a {@link JECS_CollisionComponent} to the tree.
     * Is actually only recognized after the next {@link QuadTree#update()} call.
     *
     * @param component component to be added
     *
     * @author Tim Kloepper
     */
    @Override
    public void addComponent(JECS_CollisionComponent component) {
        _components.add(component);
        _rootNode.addObject(component);
    }

    /**
     * Removes a {@link JECS_CollisionComponent} from the three.
     * Is actually only recognized after the next {@link QuadTree#update()} call.
     *
     * @param component component to be removed
     *
     * @author Tim Kloepper
     */
    @Override
    public void rmvComponent(JECS_CollisionComponent component) {
        _components.remove(component);
        _rootNode.rmvObject(component);
    }


    /**
     * Recognizes changes of positions and sizes, as well as additions and removals performed
     * with {@link QuadTree#addComponent(JECS_CollisionComponent)} and {@link QuadTree#rmvComponent(JECS_CollisionComponent)}.
     *
     * @author Tim Kloepper
     */
    @Override
    public void update() {
        _rootNode.update(_components);
    }


}


/**
 * Makes up the {@link QuadTree} and holds the actual objects in the correct partitioning.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
class QuadTreeNode {


    public final QuadTreeNode[] children;
    public final HashSet<JECS_CollisionComponent> objects;

    private final HashSet<JECS_CollisionComponent> _addObjects;
    private final HashSet<JECS_CollisionComponent> _rmvObjects;

    public final Vector2d position;
    public double width, height;


    public QuadTreeNode(Vector2d position, double width, double height, Collection<JECS_CollisionComponent> objects) {
        children = new QuadTreeNode[QuadTree.CHILDREN_AMOUNT];
        this.objects = new HashSet<>();

        _addObjects = new HashSet<>();
        _rmvObjects = new HashSet<>();

        this.position = new Vector2d(position);

        this.width = width;
        this.height = height;

        for (JECS_CollisionComponent object : objects) {
            addObject(object);
        }

        _tryParent();
    }


    /**
     * Checks, if the object provided, is referenced in this node.
     *
     * @param object object to be checked
     *
     * @return whether the object is referenced in this node or not
     *
     * @author Tim Kloepper
     */
    public boolean containsReferential(JECS_CollisionComponent object) {
        return objects.contains(object);
    }

    /**
     * Checks, if the object provided, is inside this node based on position.
     *
     * @param object object to be checked
     *
     * @return whether the object is inside this node based on position
     *
     * @author Tim Kloepper
     */
    public boolean containsPositional(JECS_CollisionComponent object) {
        double xOverlap, yOverlap;

        xOverlap = Math.min(object.position.x + object.width, position.x + width) - Math.max(object.position.x, position.x);
        yOverlap = Math.min(object.position.y + object.height, position.y + height) - Math.max(object.position.y, position.y);

        return (xOverlap > 0 && yOverlap > 0);
    }

    /**
     * Checks, if this node has any children nodes.
     *
     * @return whether this node as any child nodes
     *
     * @author Tim Kloepper
     */
    public boolean hasChildren() {
        return children[0] != null; // If one is null, all are null
    }


    /**
     * If the node is a leaf node, and it contains the provided object based on position,
     * it will add all objects that it contains to the provided set.
     * <p>
     * If the node is not a leaf node, it will pass the set down to its children.
     *
     * @param set set to be manipulated
     * @param forObject object to be checked for
     *
     * @author Tim Kloepper
     */
    public void getPossibleCollision(HashSet<JECS_CollisionComponent> set, JECS_CollisionComponent forObject) {
        if (hasChildren()) {
            for (int index = 0; index < QuadTree.CHILDREN_AMOUNT; index++) {
                children[index].getPossibleCollision(set, forObject);
            }

            return;
        }

        if (containsPositional(forObject)) { // Is more important and for a large amount of objects, the performance tank is not higher as for referential checking.
            set.addAll(objects);
        }
    }


    /**
     * Checks if every object provided, is correctly treated.
     * Meaning, checking for position and reference and if the reference this node holds to the object or not,
     * is justified.
     *
     * @param checkObjects all objects to be checked
     *
     * @author Tim Kloepper
     */
    public void update(HashSet<JECS_CollisionComponent> checkObjects) {
        if (hasChildren()) {
            for (int index = 0; index < QuadTree.CHILDREN_AMOUNT; index++) {
                children[index].update(checkObjects);
            }

            _tryUnparent(); // Children could now possibly have too few objects.
        }

        if (hasChildren()) { // Still have children, our objects will therefore be empty.
            return;
        }

        for (JECS_CollisionComponent object : checkObjects) {
            boolean pos, ref;

            pos = containsPositional(object);
            ref = containsReferential(object);

            if (pos && ref) {
                continue;
            }

            if (pos) { // Other must be false by this time.
                addObject(object);

                continue;
            }

            rmvObject(object); // Contains only referential.
        }

        _applyChanges(); // First apply changes, so resolve problems we found, then parent, unparent or do nothing.

        // Order does not matter.
        _tryParent();
        _tryUnparent();
    }


    /**
     * Adds an object to the node or its children nodes if it has any.
     * Is only actually applied with the next {@link QuadTreeNode#update(HashSet)} call.
     *
     * @param object object ot be added
     *
     * @author Tim Kloepper
     */
    public void addObject(JECS_CollisionComponent object) {
        if (!containsPositional(object)) {
            return;
        }

        if (hasChildren()) {
            for (int index = 0; index < QuadTree.CHILDREN_AMOUNT; index++) {
                children[index].addObject(object);
            }

            return;
        }

        _addObjects.add(object);
    }

    /**
     * Removes an object from the node or its children nodes if it has any.
     * Is only actually applied with the next {@link QuadTreeNode#update(HashSet)} call.
     *
     * @param object object to be removed
     *
     * @return whether removal was successful or not
     *
     * @author Tim Kloepper
     */
    public boolean rmvObject(JECS_CollisionComponent object) {
        if (hasChildren()) {
            boolean result;

            result = false;

            for (int index = 0; index < QuadTree.CHILDREN_AMOUNT; index++) {
                if (children[index].rmvObject(object)) {
                    result = true;
                }
            }

            return result;
        }

        return _rmvObjects.add(object);
    }


    /**
     * Tries to add children to this node.
     *
     * @author Tim Kloepper
     */
    private void _tryParent() {
        if (hasChildren()) {
            return;
        }
        if (objects.size() < QuadTree.OBJECTS_THRESHOLD) {
            return;
        }

        double widthH, heightH;

        widthH = width / 2;
        heightH = height / 2;

        children[0] = new QuadTreeNode(new Vector2d(position).add(0, 0), widthH, heightH, objects); // Top Left
        children[1] = new QuadTreeNode(new Vector2d(position).add(widthH, 0), widthH, heightH, objects); // Top Right
        children[2] = new QuadTreeNode(new Vector2d(position).add(0, heightH), widthH, heightH, objects); // Bottom Left
        children[3] = new QuadTreeNode(new Vector2d(position).add(widthH, heightH), widthH, heightH, objects); // Bottom Right

        objects.clear();
    }

    /**
     * Tries to remove children from this node.
     *
     * @author Tim Kloepper
     */
    private void _tryUnparent() {
        if (!hasChildren()) {
            return;
        }
        if (children[0].hasChildren()) {
            return;
        }

        int objectsOfChildrenSize;

        objectsOfChildrenSize = 0;

        for (int index = 0; index < QuadTree.CHILDREN_AMOUNT; index++) {
            objectsOfChildrenSize += children[index].objects.size();
        }

        if (objectsOfChildrenSize >= QuadTree.OBJECTS_THRESHOLD) {
            return;
        }

        for (int index = 0; index < QuadTree.CHILDREN_AMOUNT; index++) {
            objects.addAll(children[index].objects);

            children[index] = null;
        }
    }


    /**
     * Applies the changes, made by {@link QuadTreeNode#addObject(JECS_CollisionComponent)} and {@link QuadTreeNode#rmvObject(JECS_CollisionComponent)}.
     *
     * @author Tim Kloepper
     */
    private void _applyChanges() {
        objects.removeAll(_rmvObjects);
        _rmvObjects.clear();

        objects.addAll(_addObjects);
        _addObjects.clear();
    }


}