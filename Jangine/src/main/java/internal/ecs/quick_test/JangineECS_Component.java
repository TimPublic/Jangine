package internal.ecs.quick_test;


public abstract class JangineECS_Component {


    public int entityID;

    public boolean active;


    // -+- LIFE CYCLE -+- //

    public final void init(int entityID) {
        this.entityID = entityID;
        active = true;
    }
    public final void kill(int entityID) {
        if (this.entityID != entityID) {return;}

        this.entityID = -1;
        active = false;
    }


    // -+- VALIDATION -+- //

    public final boolean isValid() {
        return entityID != -1;
    }


}