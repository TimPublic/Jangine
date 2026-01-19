package internal.ecs.specific.size;


import internal.ecs.ECS_Component;


public class SizeComponent extends ECS_Component {


    public SizeComponent(double width, double height) {
        this.width = width;
        this.height = height;
    }


    public double width, height;


}