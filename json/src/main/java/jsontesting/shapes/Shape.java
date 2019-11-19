package jsontesting.shapes;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, property = "kind")
@JsonSubTypes({ @Type(value = Circle.class, name = "circle") })
public abstract class Shape {
    private final String kind;

    public Shape(String kind) {
        this.kind = kind;
    }

    /**
     * @return the kind
     */
    public String getKind() {
        return kind;
    }
}