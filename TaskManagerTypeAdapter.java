import com.google.gson.*;
import java.lang.reflect.Type;

public class TaskManagerTypeAdapter implements JsonDeserializer<TaskManager> {
    @Override
    public TaskManager deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new InMemoryTaskManager();
    }
}