package internal.entity_component_system;


public abstract class A_Component {


    // -+- CREATION -+- //

    public A_Component() {
        active = true;
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    public boolean active;

    // FINALS //

    public int owningEntity; // TODO : Initialize.


}