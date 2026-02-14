package internal.batch;


import java.util.HashSet;

public class FragmentNode {


    // -+- CREATION -+- //

    public FragmentNode(int position, int size) {
        this.position = position;
        this.size = size;
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    int position, size;

    FragmentNode lower, higher;


    // -+- FRAGMENT MANAGEMENT -+- //

    public void addNode(FragmentNode node) {
        if (node == null) throw new IllegalArgumentException("[FRAGMENT ERROR] : Node cannot be null!");
        if (node == this) throw new IllegalArgumentException("[FRAGMENT ERROR] : Node is already in the tree!");

        if (node.size >= size) {
            if (higher == null) higher = node;
            else higher.addNode(node);

            return;
        }

        if (lower == null) lower = node;
        else lower.addNode(node);
    }
    public void rmvNode(FragmentNode node) {
        if (node == null) throw new IllegalArgumentException("[FRAGMENT ERROR] : Node cannot be null!");

        if (lower == node) {
            HashSet<FragmentNode> lowerChildren;

            lowerChildren = node.getChildren();
            node.clearChildren();

            lower = null;

            lowerChildren.forEach(this::addNode);
        }
        if (higher == node) {
            HashSet<FragmentNode> higherChildren;

            higherChildren = node.getChildren();
            node.clearChildren();

            higher = null;

            higherChildren.forEach(this::addNode);
        }

        if (lower != null) lower.rmvNode(node);
        if (higher != null) higher.rmvNode(node);
    }


    // -+- CHILDREN MANAGEMENT -+- //

    public void clearChildren() {
        if (lower != null) lower.clearChildren();
        if (higher != null) higher.clearChildren();

        lower = null;
        higher = null;
    }


    // -+- MEMORY MANAGEMENT -+- //

    public int allocate(int amount) {
        if (!canAllocate(amount)) throw new IllegalArgumentException("[FRAGMENT ERROR] : Cannot allocate that amount!");

        int pointer;

        pointer = position;
        position += amount;
        size -= amount;

        return pointer;
    }

    public void merge(FragmentNode node) {
        if (node == this) throw new IllegalArgumentException("[FRAGMENT ERROR] : Fragment cannot merge with itself!");

        position = Math.min(position, node.position);
        size += node.size;
    }


    // -+- CHECKERS -+- //

    public boolean canAllocate(int amount) {
        return size >= amount;
    }

    public boolean isConnectedWith(FragmentNode node) {
        if (node == this) throw new IllegalArgumentException("[FRAGMENT NODE] : Node cannot merge with itself!");

        return node.position + node.size == position || position + size == node.position;
    }


    // -+- GETTERS -+- //

    public HashSet<FragmentNode> getChildren() {
        HashSet<FragmentNode> result;

        result = new HashSet<>();

        if (lower != null) {
            result.add(lower);
            result.addAll(lower.getChildren());
        }
        if (higher != null) {
            result.add(higher);
            result.addAll(higher.getChildren());
        }

        return result;
    }


}