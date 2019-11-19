package jsontesting.shapes;

public class Circle extends Shape {

    public Circle() {
        super("circle");
    }

    @Override
    public boolean equals(Object obj) {
        return getKind().equals(((Shape) obj).getKind());
    }
}