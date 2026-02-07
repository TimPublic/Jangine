package internal.usable;


public interface I_Usable {


    void use();
    void unuse();

    default void setUse(boolean state) {
        if (state) use();
        else unuse();
    }


}