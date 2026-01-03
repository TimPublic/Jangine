import internal.main.Engine;
import internal.rendering.JangineWindow;


public class Main {


    public static void main(String[] args) {
        Engine engine;

        engine = Engine.get();

        engine.run();
    }


}