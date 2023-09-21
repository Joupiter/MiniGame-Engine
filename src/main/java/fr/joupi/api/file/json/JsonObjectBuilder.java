package fr.joupi.api.file.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonObjectBuilder {

    private final JsonObject json = new JsonObject();

    public JsonObjectBuilder addProperty(String property, String value) {
        json.addProperty(property, value);
        return this;
    }

    public JsonObjectBuilder addProperty(String property, Number value) {
        json.addProperty(property, value);
        return this;
    }

    public JsonObjectBuilder addProperty(String property, Boolean value) {
        json.addProperty(property, value);
        return this;
    }

    public JsonObjectBuilder addProperty(String property, Character value) {
        json.addProperty(property, value);
        return this;
    }

    public JsonObjectBuilder add(String property, JsonElement element) {
        json.add(property, element);
        return this;
    }

    public JsonObject get() {
        return json;
    }

}