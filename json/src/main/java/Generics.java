import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

class GenericDto<T> {
    public T value;

    public GenericDto() {
    }

    public GenericDto(T value) {
        this.value = value;
    }
}

class OkDto {
    public GenericDto<Boolean> ok;

    public OkDto() {
    }

    public OkDto(Boolean value) {
        ok = new GenericDto<>(value);
    }
}

public class Generics {
    public static void main(String[] args) throws Throwable {
        GenericDto<Boolean> generic = new GenericDto<>(true);
        generic.value = true;
        // OkDto ok = new OkDto();
        // ok.ok = generic;

        ObjectMapper mapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        GenericDto<Boolean> oh = mapper.readValue("{\"value\":\"true\"}", GenericDto.class);
        GenericDto<Boolean> oh2 = mapper.readValue("{\"value\":true}", GenericDto.class);

        Boolean b = oh.value;
        Boolean b2 = oh2.value;

        // mapper.writeValue(System.out, new GenericDto<>(true));
        String str = mapper.writeValueAsString(new OkDto(false));
        mapper.readValue(str, OkDto.class);
    }
}