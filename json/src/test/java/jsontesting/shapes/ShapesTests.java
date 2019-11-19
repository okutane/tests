package jsontesting.shapes;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

public class ShapesTests {
    @Test
    public void testViaSameModel() throws JsonParseException, JsonMappingException, IOException {
        Circle circle = new Circle();

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(circle);
        Shape shape = mapper.readValue(json, Shape.class);

        assertEquals(circle, shape);
    }

    @Test
    public void testViaJsonNode() throws JsonParseException, JsonMappingException, IOException {
        Circle circle = new Circle();

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(circle);
        JsonNode actual = mapper.readTree(json);

        ObjectNode expected = mapper.createObjectNode();
        expected.put("kind", "circle");

        assertEquals(expected, actual);
    }

    @Test
    public void testViaJsonNodeAttributes() throws JsonParseException, JsonMappingException, IOException {
        Circle circle = new Circle();

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(circle);
        JsonNode tree = mapper.readTree(json);

        assertEquals(circle.getKind(), tree.get("kind").asText());
        // todo: check unknown fields
    }

    @Test
    public void testViaString() throws JsonParseException, JsonMappingException, IOException {
        Circle circle = new Circle();

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(circle);
        String expected = "{\"kind\":\"circle\"}";

        assertEquals(expected, json);
        // todo: check unknown fields
    }
}