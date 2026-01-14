import internal.ecs.quick_test.JangineECS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class JangineECS_Tests {


    private JangineECS _ecs;


    @BeforeEach
    public void resetEngine() {
        _ecs = new JangineECS();
    }


    @Test
    public void addAndRmvEntity() {
        int id;

        id = _ecs.addEntity();

        assert _ecs.hasEntity(id) : "Should have the entity!";

        _ecs.rmvEntity(id);

        assert !(_ecs.hasEntity(id)) : "Should not have the entity!";
    }


}