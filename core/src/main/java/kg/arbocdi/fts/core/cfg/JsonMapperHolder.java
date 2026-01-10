package kg.arbocdi.fts.core.cfg;

import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class JsonMapperHolder {
    private static volatile JsonMapper mapper;

    public JsonMapperHolder(JsonMapper mapper) {
        JsonMapperHolder.mapper = mapper;
    }

    public static JsonMapper get() {
        JsonMapper m = mapper;
        if (m == null) throw new IllegalStateException("JsonMapper is not initialized yet");
        return m;
    }
}
