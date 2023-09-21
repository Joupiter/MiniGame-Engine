package fr.joupi.api.file.json;

import com.google.gson.*;

import java.lang.reflect.Type;

public interface IAdapter<T> extends JsonSerializer<T>, JsonDeserializer<T>  {

    JsonElement serialize(T type);

    T deserialize(JsonElement jsonElement) throws JsonParseException;

    @Override
    default JsonElement serialize(T t, Type type, JsonSerializationContext jsonSerializationContext) {
        return serialize(t);
    }

    @Override
    default T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return deserialize(jsonElement);
    }

}